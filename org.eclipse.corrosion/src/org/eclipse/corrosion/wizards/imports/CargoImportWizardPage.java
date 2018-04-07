package org.eclipse.corrosion.wizards.imports;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.wizards.datatransfer.WizardProjectsImportPage.ProjectRecord;

public class CargoImportWizardPage extends WizardPage implements ICargoImportListener {

	private final static String FILE_IMPORT_MASK = "*.toml";//$NON-NLS-1$
	private final static String IMPORT_PROJECTS_TITLE = "";
	private final static String IMPORT_PROJECT_DESCRIPTION = "";

	private String initialPath;
	private Combo directoryPathField;

	private static String previouslyBrowsedDirectory = "";

	private ProjectRecord selectedProject;

	protected CargoImportWizardPage(final IStructuredSelection selection) {
		super(CargoImportWizardPage.class.getName());
		File initialPathBuffer = null;
		// Start search for toml in home dir
		if (selection != null) {
			final Object firstElement = selection.getFirstElement();
			if (firstElement instanceof File) {
				initialPathBuffer = ((File) firstElement);
			} else if (firstElement instanceof IResource) {
				initialPathBuffer = ((IResource) firstElement).getLocation().toFile();
			} else if ((firstElement instanceof String)) {
				initialPathBuffer = new File((String) firstElement);
			}
		}
		this.setInitialPath(initialPathBuffer);
		this.setPageComplete(true);
		this.setTitle(CargoImportWizardPage.IMPORT_PROJECTS_TITLE);
		this.setDescription(CargoImportWizardPage.IMPORT_PROJECT_DESCRIPTION);

	}

	/**
	 * Set initial path only if the file exists
	 *
	 * @param file
	 *            File whom path is set to {@link #initialPath} only if it exists
	 */
	private void setInitialPath(final File file) {
		if ((file != null) && file.exists()) {
			this.initialPath = file.getAbsolutePath();
		}
	}

	@Override
	public void createControl(final Composite parent) {
		this.initializeDialogUnits(parent);

		final Composite workArea = new Composite(parent, SWT.NONE);
		this.setControl(workArea);

		workArea.setLayout(new GridLayout());
		workArea.setLayoutData(new GridData());
		this.createProjectSelection(workArea);
		Dialog.applyDialogFont(workArea);
	}

	private void createProjectSelection(final Composite workArea) {
		final Composite projectGroup = new Composite(workArea, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		layout.marginWidth = 0;
		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.directoryPathField = new Combo(projectGroup, SWT.BORDER);
		final GridData directoryPathData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		directoryPathData.widthHint = new PixelConverter(this.directoryPathField).convertWidthInCharsToPixels(25);
		this.directoryPathField.setLayoutData(directoryPathData);

		// browse button
		final Button browseDirectoriesButton = new Button(projectGroup, SWT.PUSH);
		browseDirectoriesButton.setText("Browse Directories");
		this.setButtonLayoutData(browseDirectoriesButton);

		browseDirectoriesButton.addSelectionListener(this);

	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		final DirectoryDialog dialog = new DirectoryDialog(this.directoryPathField.getShell(), SWT.SHEET);
		dialog.setMessage("Choose toml");
		String dirName = this.directoryPathField.getText().trim();
		if (dirName.length() == 0) {
			dirName = CargoImportWizardPage.previouslyBrowsedDirectory;
		}

		if (dirName.length() == 0) {
			dialog.setFilterPath(IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getLocation().toOSString());
		} else {
			final File path = new File(dirName);
			if (path.exists()) {
				dialog.setFilterPath(new Path(dirName).toOSString());
			}
		}
		final String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			CargoImportWizardPage.previouslyBrowsedDirectory = selectedDirectory;
			this.directoryPathField.setText(CargoImportWizardPage.previouslyBrowsedDirectory);
			this.setMessage("");
			this.selectedProject = new ProjectRecord[0];

		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDirectorySelected() {
		// TODO Auto-generated method stub

	}

}
