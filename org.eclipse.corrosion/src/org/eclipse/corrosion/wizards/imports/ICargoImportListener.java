package org.eclipse.corrosion.wizards.imports;

import org.eclipse.swt.events.SelectionListener;

public interface ICargoImportListener extends SelectionListener {
	void handleDirectorySelected();
}
