package org.eclipse.corrosion.wizards.imports;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

public class CargoImportWizard extends Wizard implements IImportWizard {

	private IWizardPage cargoImportWizardPage;

	public CargoImportWizard() {
		super();
		this.setNeedsProgressMonitor(true);
	}

	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.cargoImportWizardPage = new CargoImportWizardPage(selection);
		this.setWindowTitle("Import Existing Cargo Project");
		this.addPages();
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		// TODO Auto-generated method stub
		super.addPages();
		this.addPage(this.cargoImportWizardPage);
	}

}
