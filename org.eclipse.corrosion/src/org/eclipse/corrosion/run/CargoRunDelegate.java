/*********************************************************************
 * Copyright (c) 2017, 2018 Red Hat Inc. and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Lucas Bullen (Red Hat Inc.) - Initial implementation
 *******************************************************************************/
package org.eclipse.corrosion.run;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.corrosion.CorrosionPlugin;
import org.eclipse.corrosion.CorrosionPreferenceInitializer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

public class CargoRunDelegate extends LaunchConfigurationDelegate implements ILaunchShortcut {

	public static final String PROJECT_ATTRIBUTE = "PROJECT";
	public static final String RUN_COMMAND_ATTRIBUTE = "RUN_COMMAND";

	@Override
	public void launch(ISelection selection, String mode) {

		if (selection instanceof IStructuredSelection) {
			Iterator<?> selectionIterator = ((IStructuredSelection) selection).iterator();
			while (selectionIterator.hasNext()) {
				Object element = selectionIterator.next();
				IResource resource = null;
				if (element instanceof IResource) {
					resource = (IResource) element;
				} else if (element instanceof IAdaptable) {
					resource = ((IAdaptable) element).getAdapter(IResource.class);
				}

				if (resource != null) {
					try {
						ILaunchConfiguration launchConfig = getLaunchConfiguration(mode, resource);
						if (launchConfig != null) {
							launchConfig.launch(mode, new NullProgressMonitor());
						}
					} catch (CoreException e) {
						CorrosionPlugin.logError(e);
					}
					return;
				}
			}
		}
		Display.getDefault().asyncExec(() -> {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Unable to Launch",
					"Unable to launch Rust Project from selection.");
		});
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		IFile file = input.getAdapter(IFile.class);

		try {
			ILaunchConfiguration launchConfig = getLaunchConfiguration(mode, file);
			if (launchConfig != null) {
				launchConfig.launch(mode, new NullProgressMonitor());
			}
		} catch (CoreException e) {
			CorrosionPlugin.logError(e);
		}
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		List<String> cargoRunCommand = new ArrayList<>();
		IPreferenceStore store = CorrosionPlugin.getDefault().getPreferenceStore();
		String cargo = store.getString(CorrosionPreferenceInitializer.cargoPathPreference);
		cargoRunCommand.add(cargo);
		String projectName = configuration.getAttribute(PROJECT_ATTRIBUTE, "");
		String runCommand = configuration.getAttribute(RUN_COMMAND_ATTRIBUTE, "");

		IProject project = null;
		if (!projectName.isEmpty()) {
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		}
		if (project == null || !project.exists()) {
			Display.getDefault().asyncExec(() -> {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"Unable to Launch", "Unable to find project.");
			});
			return;
		}
		IFile cargoManifest = project.getFile("Cargo.toml");
		if (!cargoManifest.exists()) {
			Display.getDefault().asyncExec(() -> {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"Unable to Launch", "Unable to find Cargo.toml file.");
			});
			return;
		}
		if (runCommand.isEmpty()) {
			runCommand = "run";
		}
		IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		runCommand = manager.performStringSubstitution(runCommand);
		cargoRunCommand.addAll(Arrays.asList(runCommand.replace('\n', ' ').split("(\n| )")));

		final String cargoPathString = cargoManifest.getLocation().toPortableString();
		cargoRunCommand.add("--manifest-path");
		cargoRunCommand.add(cargoPathString);

		final List<String> finalRunCommand = cargoRunCommand;
		CompletableFuture.runAsync(() -> {
			try {
				String[] cmdLine = finalRunCommand.toArray(new String[finalRunCommand.size()]);
				Process p = DebugPlugin.exec(cmdLine, null);
				IProcess process = DebugPlugin.newProcess(launch, p, "cargo run");
				process.setAttribute(IProcess.ATTR_CMDLINE, String.join(" ", cmdLine));
			} catch (CoreException e) {
				Display.getDefault().asyncExec(() -> {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Unable to Launch", e.getLocalizedMessage());
				});
			}
		});
	}

	private ILaunchConfiguration getLaunchConfiguration(String mode, IResource resource) {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = launchManager
				.getLaunchConfigurationType("org.eclipse.corrosion.run.CargoRunDelegate");
		try {
			ILaunchConfiguration[] launchConfigurations = launchManager.getLaunchConfigurations(configType);
			final String projectName = resource.getProject().getName();

			for (ILaunchConfiguration iLaunchConfiguration : launchConfigurations) {
				if (iLaunchConfiguration.getAttribute("PROJECT", "").equals(projectName)) {
					return iLaunchConfiguration;
				}
			}
			String configName = launchManager.generateLaunchConfigurationName(projectName);
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, configName);
			wc.setAttribute("PROJECT", projectName);
			return wc;
		} catch (CoreException e) {
			CorrosionPlugin.logError(e);
		}
		return null;
	}
}
