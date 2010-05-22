package de.el.swtTools;

import java.util.prefs.Preferences;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class PreferencesCheckButtonSelectionListener extends SelectionAdapter{
	public PreferencesCheckButtonSelectionListener(String prefName, Preferences prefs){
		this.prefName = prefName;
		this.prefs = prefs;
	}
	
	private String prefName;
	private Preferences prefs;
	
	public void widgetSelected(SelectionEvent e){
		prefs.putBoolean(prefName, ((Button)e.getSource()).getSelection());
	}
}
