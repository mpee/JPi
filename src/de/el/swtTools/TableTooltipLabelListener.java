package de.el.swtTools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class TableTooltipLabelListener implements Listener {
	
	public TableTooltipLabelListener(Table t){
		table = t;
	}

	private Label label;
	private Table table;
	private Shell shell;
	@Override
	public void handleEvent(Event event) {
		label = (Label) event.widget;
		shell = label.getShell();
		switch (event.type) {
		case SWT.MouseDown:
			Event e = new Event();
			e.item = (TableItem) label.getData("_TABLEITEM");
			// Assuming table is single select, set the selection as if
			// the mouse down event went through to the table
			table.setSelection(new TableItem[] { (TableItem) e.item });
			table.notifyListeners(SWT.Selection, e);
			// fall through
		case SWT.MouseExit:
			shell.dispose();
			break;
		}
	}

}
