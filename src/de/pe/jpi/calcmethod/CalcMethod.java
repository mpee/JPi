package de.pe.jpi.calcmethod;

import de.el.threads.ProgressChangedEvent;
import de.el.threads.ProgressListener;
import de.el.threads.SWTWorker;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author PEH
 */
public abstract class CalcMethod extends SWTWorker<Double, String> implements Serializable, ProgressListener {

	public CalcMethod() {
		super(null);
	}

	public CalcMethod(Display d, ProgressBar pb, Text text) {
		super(d);
		this.pb = pb;
		this.text = text;
		addProgressListener(this);
	}

	int threadCount;
	private ProgressBar pb;
	private Text text;
	final static Logger LOG = LoggerFactory.getLogger(CalcMethod.class);

	public abstract Double doInBackground();

	public abstract Map<String, Object> getParameters();

	public abstract boolean setParameters(Map<String, Object> parameters);

	abstract boolean allParametersFilled();

	public void progressChanged(ProgressChangedEvent e) {
		pb.setSelection(e.getValue());
	}

	@Override
	protected void process(List<String> chunks) {
		text.setText(chunks.get(chunks.size() - 1));
	}
}
