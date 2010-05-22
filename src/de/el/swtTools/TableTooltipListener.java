package de.el.swtTools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class TableTooltipListener implements Listener {
	
	public TableTooltipListener(Shell s, Display d, Table t){
		shell = s;
		display = d;
		table = t;
	}
	private Shell shell;
	private Display display;
	private Shell tip = null;
	private Label label = null;
	private Table table;

	public void handleEvent(Event event) {

		switch (event.type) {
			case SWT.Dispose:
			case SWT.KeyDown:
			case SWT.MouseMove: {
				if (tip == null)
					break;
				tip.dispose();
				tip = null;
				label = null;
				break;
			}
			case SWT.MouseHover: {
				TableItem item = table.getItem(new Point(event.x, event.y));
				if (item != null) {
					if (tip != null && !tip.isDisposed())
						tip.dispose();
					tip = new Shell(shell, SWT.ON_TOP | SWT.TOOL);
					tip.setLayout(new FillLayout());
					label = new Label(tip, SWT.NONE);
					label.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
					label.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
					label.setData("_TABLEITEM", item);
					label.setText((String)item.getData("status"));
					label.addListener(SWT.MouseExit, new TableTooltipLabelListener(table));
					label.addListener(SWT.MouseDown, new TableTooltipLabelListener(table));
					Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					Rectangle rect = item.getBounds(0);
					Point pt = table.toDisplay(rect.x, rect.y);
					tip.setBounds(pt.x + 50, pt.y, size.x, size.y);
					tip.setVisible(true);
				}
			}
		}
	}
}
