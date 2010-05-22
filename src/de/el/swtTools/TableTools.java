package de.el.swtTools;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 
 * @author philipp
 *
 */
public class TableTools {

	/**
	 * packs all columns of the given Table.
	 * @param table
	 */
	public static void pack(Table table){
		for(TableColumn cc : table.getColumns()){
			cc.pack();
		}
	}
}
