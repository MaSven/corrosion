/*********************************************************************
 * Copyright (c) 2017 Red Hat Inc. and others.
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
package org.eclipse.corrosion.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.corrosion.builder.AddCargoBuilder;
import org.eclipse.corrosion.builder.RemoveCargoBuilder;
import org.eclipse.corrosion.builder.TestCargoBuilderEnabled;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.tests.harness.util.DisplayHelper;
import org.junit.Test;

public class TestBuilder extends AbstractCorrosionTest {

	@Test
	public void testBuild() throws Exception {
		IProject project = getProject("basic");
		PropertyTester test = new TestCargoBuilderEnabled();
		addBuilder(project);
		assertTrue(test.test(project, "isCargoBuilderEnabled", null, null));
		new DisplayHelper() {
			@Override
			protected boolean condition() {
				try {
					project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
				} catch (CoreException e) {
				}
				return project.getFolder("target").getFolder("debug").getFile("basic").exists();
			}
		}.waitForCondition(getShell().getDisplay(), 10000);
		assertTrue(project.getFolder("target").getFolder("debug").getFile("basic").exists());
		removeBuilder(project);
		assertFalse(test.test(project, "isCargoBuilderEnabled", null, null));
	}

	private static final ICommandService COMMAND_SERVICE = PlatformUI.getWorkbench().getService(ICommandService.class);
	private static final Command ADD_COMMAND = COMMAND_SERVICE.getCommand("org.eclipse.corrosion.builder.AddCargoBuilder");
	private static final Command REMOVE_COMMAND = COMMAND_SERVICE
			.getCommand("org.eclipse.corrosion.builder.RemoveCargoBuilder");

	private void addBuilder(IProject project) throws Exception {
		IEvaluationContext evaluationContext = new EvaluationContext(null, project);
		evaluationContext.addVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME, new StructuredSelection(project));
		ExecutionEvent event = new ExecutionEvent(ADD_COMMAND, new HashMap<>(0), null, evaluationContext);
		AddCargoBuilder addBuilder = new AddCargoBuilder();
		addBuilder.execute(event);
	}

	private void removeBuilder(IProject project) throws Exception {
		IEvaluationContext evaluationContext = new EvaluationContext(null, project);
		evaluationContext.addVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME, new StructuredSelection(project));
		ExecutionEvent event = new ExecutionEvent(REMOVE_COMMAND, new HashMap<>(0), null, evaluationContext);
		RemoveCargoBuilder removeBuilder = new RemoveCargoBuilder();
		removeBuilder.execute(event);
	}
}
