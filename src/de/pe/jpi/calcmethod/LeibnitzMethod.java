package de.pe.jpi.calcmethod;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

/**
 *
 * @author PEH
 */
public class LeibnitzMethod extends CalcMethod {

	public LeibnitzMethod() {
		super();
	}

	public LeibnitzMethod(Display d, ProgressBar pb, Text text) {
		super(d, pb, text);
	}
	private long steps;

	@Override
	public Double doInBackground() {
		if (!allParametersFilled()) {
			return new Double(0);
		}
		double pi = 0;
		for (long i = 0; i <= this.steps; i++) {
			if (!isCancelled()) {
				pi = pi + (Math.pow(-1, i) / (2 * i + 1));
				setProgress((int) (i * 100 / steps));
				if (i % (steps / (steps / 100)) == 0) {
					publish("" + pi * 4);
				}
			}
		}
		return pi * 4;
	}

	@Override
	public Map<String, Object> getParameters() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("Steps", new Long(0));
		return ret;
	}

	@Override
	public boolean setParameters(Map<String, Object> parameters) {
		if (parameters.containsKey("Steps")) {
			this.steps = Long.parseLong((String) parameters.get("Steps"));
		}
		return allParametersFilled();
	}

	public void setSteps(long steps) {
		this.steps = steps;
	}

	@Override
	boolean allParametersFilled() {
		if (this.steps == 0) {
			return false;
		}
		return true;
	}
}
