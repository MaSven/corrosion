package org.eclipse.corrosion.wizards.imports;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class CargoImportWizardPage extends WizardPage {

	private final static String FILE_IMPORT_MASK = "*.toml";//$NON-NLS-1$
	private final static String IMPORT_PROJECTS_TITLE = "";
	private final static String IMPORT_PROJECT_DESCRIPTION = "";

	private String initialPath;

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
		workArea.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

		Dialog.applyDialogFont(workArea);
	}

}
