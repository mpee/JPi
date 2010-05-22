package de.pe.jpi;

import de.el.threads.SWTWorker.WorkerState;
import de.el.threads.StatusChangedEvent;
import de.el.threads.StatusListener;
import de.pe.jpi.calcmethod.CalcMethod;
import de.pe.jpi.calcmethod.LeibnitzMethod;
import de.pe.jpi.calcmethod.PerformanceTest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author PEH
 */
public class MainGui {

	public MainGui() {
		display = Display.getDefault();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText("PiCalc");
		initGui();
		addAllMethods();
	}
	private Shell shell;
	private Display display;
	private Composite comp;
	private CalcMethod cm;
	private Combo methodCombo;
	private Label[] parameterLabels;
	private Text[] parameterTexts;
	private Group parameterGroup;
	private int maxParameterCount = 3;
	private Label info;
	private boolean readyToCalc = true;
	private ProgressBar progress;
	private Text resultText, mathPiText;
	private Label resultLabel, mathPiLabel;
	private Button goButton, stopButton;
	private static final Logger LOG = LoggerFactory.getLogger(MainGui.class);

	private void initGui() {
		int offset = 5;
		comp = new Composite(shell, SWT.NONE);
		comp.setLayout(new FormLayout());

		FormData f = new FormData();
		Label methodLabel = new Label(comp, SWT.NONE);
		f.top = new FormAttachment(0, offset + 3);
		f.left = new FormAttachment(0, offset);
		methodLabel.setText("Calculation Method");
		methodLabel.setLayoutData(f);

		f = new FormData();
		methodCombo = new Combo(comp, SWT.NONE);
		f.top = new FormAttachment(0, offset);
		f.left = new FormAttachment(methodLabel, offset);
		f.right = new FormAttachment(100, -5);
		f.width = 150;
		methodCombo.setLayoutData(f);
		methodCombo.addSelectionListener(new MethodComboSelectionListener());

		f = new FormData();
		parameterGroup = new Group(comp, SWT.NONE);
		f.top = new FormAttachment(methodCombo, offset);
		f.left = new FormAttachment(0, offset);
		f.right = new FormAttachment(100, -5);
		parameterGroup.setLayoutData(f);
		parameterGroup.setText("Parameters");
		parameterGroup.setLayout(new FormLayout());
		initParameterGroup();

		f = new FormData();
		progress = new ProgressBar(comp, SWT.NONE);
		f.top = new FormAttachment(parameterGroup, offset);
		f.left = new FormAttachment(0, offset);
		f.right = new FormAttachment(100, -5);
		f.width = 600;
		progress.setLayoutData(f);

		f = new FormData();
		goButton = new Button(comp, SWT.PUSH);
		f.bottom = new FormAttachment(100, -5);
		f.right = new FormAttachment(100, -5);
		goButton.setLayoutData(f);
		goButton.setText("Go");
		goButton.addSelectionListener(new GoButtonSelectionListener());

		f = new FormData();
		stopButton = new Button(comp, SWT.PUSH);
		f.bottom = new FormAttachment(100, -5);
		f.right = new FormAttachment(goButton, -5);
		stopButton.setLayoutData(f);
		stopButton.setText("Stop");
		stopButton.addSelectionListener(new StopButtonSelectionListener());
		stopButton.setEnabled(false);

		f = new FormData();
		info = new Label(comp, SWT.BORDER);
		f.left = new FormAttachment(0, offset);
		f.right = new FormAttachment(goButton, -5);
		f.bottom = new FormAttachment(100, -10);
		info.setLayoutData(f);

		f = new FormData();
		resultLabel = new Label(comp, SWT.NONE);
		f.top = new FormAttachment(progress, offset);
		f.left = new FormAttachment(0, offset);
		f.right = new FormAttachment(20, -5);
		resultLabel.setLayoutData(f);
		resultLabel.setText("Result:");

		f = new FormData();
		resultText = new Text(comp, SWT.NONE);
		f.top = new FormAttachment(progress, offset);
		f.left = new FormAttachment(resultLabel, offset);
		f.right = new FormAttachment(100, -5);
		resultText.setLayoutData(f);
		resultText.setEditable(false);

		f = new FormData();
		mathPiLabel = new Label(comp, SWT.NONE);
		f.top = new FormAttachment(resultText, offset);
		f.left = new FormAttachment(0, offset);
		f.right = new FormAttachment(20, -5);
		mathPiLabel.setLayoutData(f);
		mathPiLabel.setText("Math.Pi:");

		f = new FormData();
		mathPiText = new Text(comp, SWT.NONE);
		f.top = new FormAttachment(resultText, offset);
		f.left = new FormAttachment(mathPiLabel, offset);
		f.right = new FormAttachment(100, -5);
		f.bottom = new FormAttachment(goButton, -5);
		mathPiText.setLayoutData(f);
		mathPiText.setEditable(false);
		mathPiText.setText("" + Math.PI);

	}

	private void initParameterGroup() {
		parameterLabels = new Label[maxParameterCount];
		parameterTexts = new Text[maxParameterCount];

		FormData f = new FormData();
		Label label = new Label(parameterGroup, SWT.NONE);
		f.top = new FormAttachment(0, 8);
		f.left = new FormAttachment(0, 5);
		f.width = 200;
		label.setLayoutData(f);
		parameterLabels[0] = label;

		f = new FormData();
		Text text = new Text(parameterGroup, SWT.BORDER);
		f.top = new FormAttachment(0, 5);
		f.left = new FormAttachment(label, 20);
		f.right = new FormAttachment(100, -5);
		text.setLayoutData(f);

		parameterTexts[0] = text;


		for (int i = 1; i < maxParameterCount; i++) {
			f = new FormData();
			label = new Label(parameterGroup, SWT.NONE);
			f.top = new FormAttachment(parameterLabels[i - 1], 10);
			f.left = new FormAttachment(0, 5);
			f.width = 200;
			label.setLayoutData(f);
			parameterLabels[i] = label;

			f = new FormData();
			text = new Text(parameterGroup, SWT.BORDER);
			f.top = new FormAttachment(parameterTexts[i - 1], 5);
			f.left = new FormAttachment(parameterLabels[i], 20);
			if (i == maxParameterCount - 1) {
				f.bottom = new FormAttachment(100, -5);
			}
			f.right = new FormAttachment(100, -5);
			text.setLayoutData(f);
			parameterTexts[i] = text;
		}
	}

	public void open() {
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void showParameters(CalcMethod m) {
		int i = 0;
		for (Entry e : m.getParameters().entrySet()) {
			parameterLabels[i].setText((String) e.getKey());
			parameterLabels[i].pack();
			parameterTexts[i].setText("");
			parameterTexts[i].setData(e.getValue());
			parameterTexts[i].setEnabled(true);
			parameterTexts[i].addVerifyListener(new ParameterTextKeyListener(e.getValue()));
			i++;
		}
		for (; i < maxParameterCount; i++) {
			parameterLabels[i].setText("");
			parameterTexts[i].setText("");
			parameterTexts[i].setData(null);
			parameterTexts[i].setEnabled(false);
		}
	}

	private CalcMethod getCalcMethodByName(String calcMethodName) {
		if (calcMethodName.contains("Leibnitz")) {
			return new LeibnitzMethod(display, progress, resultText);
		} else if (calcMethodName.contains("Benchmark")) {
			return new PerformanceTest(display, progress, resultText);
		} else {
			return null;
		}
	}

	private void addAllMethods() {
		methodCombo.add("Leibnitz");
		methodCombo.add("Benchmark");
	}

	private void infoAlarm(String msg){
		infoAlarm(msg, false);
	}

	private void infoAlarm(String msg, boolean changeColor){
		info.setText(msg);
		if(changeColor)
			info.setBackground(new Color(display, 255,100,100));
	}

	private void infoReset(){
		infoAlarm("");
		info.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	}

	private class MethodComboSelectionListener extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			showParameters(getCalcMethodByName(((Combo) e.getSource()).getText()));
		}
	}

	private class GoButtonSelectionListener extends SelectionAdapter {

		private Map<String, Object> parameters = new HashMap<String, Object>();

		@Override
		public void widgetSelected(SelectionEvent e) {
			progress.setSelection(0);
			cm = getCalcMethodByName(methodCombo.getText());
			cm.addStatusListener(new StatusListener() {

				public void statusChanged(final StatusChangedEvent e) {
					display.asyncExec(new Runnable() {

						public void run() {
							LOG.debug("" + e.getState());
							if (e.getState() == WorkerState.RUNNING) {
								stopButton.setEnabled(true);
								goButton.setEnabled(false);
							} else {
								stopButton.setEnabled(false);
								goButton.setEnabled(true);
							}
						}
					});
				}
			});
			addAllParameters();
			if (readyToCalc && cm.setParameters(parameters)) {
				cm.execute();
				infoReset();
			} else if(!cm.setParameters(parameters)){
				infoAlarm("not all Parameters filled");
			}

		}

		private void addAllParameters() {
			for (int i = 0; i < maxParameterCount; i++) {
				if (parameterTexts[i].isEnabled()) {
					if (parameterTexts[i].getText().isEmpty()) {
						continue;
					} else if (parameterLabels[i].getText().equals("Threads")) {
						int availibleCores = Runtime.getRuntime().availableProcessors();
						if (!readyToCalc) {
							readyToCalc = true;
						} else if (Integer.parseInt(parameterTexts[i].getText()) > availibleCores) {
							infoAlarm("Info: more threads choosen than cores availible. Click again to start anyway.");
							readyToCalc = false;
						}
					}
					parameters.put(parameterLabels[i].getText(), parameterTexts[i].getText());
				}
			}
		}
	}

	private class StopButtonSelectionListener extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			cm.cancel(true);
		}
	}

	private class ParameterTextKeyListener implements VerifyListener {

		public ParameterTextKeyListener(Object obj) {
			this.o = obj;
		}
		private Object o;

		@Override
		public void verifyText(VerifyEvent e) {
			if (e.keyCode == 8 || e.keyCode == 127) {
				return;
			}
			if (o instanceof Long) {
				try {
					String b = ((Text) e.getSource()).getText() + e.text;
					long l = Long.parseLong(b);
					if (l > Integer.MAX_VALUE) {
						infoAlarm("This could cause trouble ( input > " + Integer.MAX_VALUE + ")");
					} else {
						infoReset();
					}
				} catch (NumberFormatException ex) {
					e.doit = false;
					infoAlarm("Incorrect Input or Number to large", true);
				}
			} else if (o instanceof Integer) {
				try {
					int l = Integer.parseInt(e.text);
					infoReset();
				} catch (NumberFormatException ex) {
					e.doit = false;
					infoAlarm("Only numbers allowed.", true);
				}
			} else {
				e.doit = true;
			}
		}
	}
}

