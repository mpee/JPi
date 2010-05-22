package de.pe.jpi.calcmethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

/**
 *
 * @author PEH
 */
public class PerformanceTest extends CalcMethod {

	public PerformanceTest(Display d, ProgressBar pb, Text text) {
		super(d, pb, text);
		time = 0;
		intervall = 0;
		threadCount = 0;
	}

	public PerformanceTest() {
		time = 0;
		intervall = 0;
		threadCount = 0;
	}
	private long time;
	private int intervall;
	private List<PerformanceThread> threads = new ArrayList<PerformanceThread>();

	@Override
	public Double doInBackground() {
		LOG.debug("Building threads");
		for (int i = 0; i < threadCount; i++) {
			threads.add(new PerformanceThread());
		}

		time = time * 1000;

		double stepsPerSecondAll = 0;
		double pi = 0;
		long start = System.currentTimeMillis();
		long stepsOverall = 0;

		Timer timer = new Timer();
		timer.schedule(new PerformanceTimerTask(start), 0, intervall);

		LOG.debug("starting threads");
		for (PerformanceThread p : threads) {
			p.start();
		}

		while ((System.currentTimeMillis() - start < time) && !isCancelled()) {
			try {
				Thread.sleep(intervall / 10);
			} catch (InterruptedException ex) {
				LOG.debug("", ex);
			}
		}

		timer.cancel();
		setProgress(100);
		
		LOG.debug("Interrupting threads");
		for (PerformanceThread p : threads) {
			p.interrupt();
			stepsOverall += p.getSteps();
			pi += p.getPi();
		}

		pi = pi / threadCount;
		stepsPerSecondAll = stepsOverall / (System.currentTimeMillis() - start);
		publish(pi + " ( " + stepsPerSecondAll + " steps per millisecond )");
		return pi;
	}

	@Override
	public Map<String, Object> getParameters() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("Time (seconds)", new Long(0));
		ret.put("Update Intervall (milliseconds)", new Integer(0));
		ret.put("Threads", new Integer(0));
		return ret;
	}

	@Override
	public boolean setParameters(Map<String, Object> parameters) {
		if (parameters.containsKey("Time (seconds)")) {
			this.time = Long.parseLong((String) parameters.get("Time (seconds)"));
		}
		if (parameters.containsKey("Update Intervall (milliseconds)")) {
			this.intervall = Integer.parseInt((String) parameters.get("Update Intervall (milliseconds)"));
		}
		if (parameters.containsKey("Threads")) {
			this.threadCount = Integer.parseInt((String) parameters.get("Threads"));
		}
		return allParametersFilled();
	}

	@Override
	boolean allParametersFilled() {
		if (this.time == 0) {
			return false;
		}
		if (this.intervall == 0) {
			return false;
		}
		if (this.threadCount == 0) {
			return false;
		}
		return true;
	}

	private class PerformanceThread extends Thread {

		private double pi;
		private long steps;
		private long lastSteps;

		@Override
		public void run() {
			while (!isInterrupted()) {
				pi = pi + (Math.pow(-1, steps) / (2 * steps + 1));
				steps++;
				lastSteps++;
			}
		}

		public long getSteps() {
			return steps;
		}

		public long getLastSteps() {
			long ret = lastSteps;
			lastSteps = 0;
			return ret;
		}

		public double getPi() {
			return pi * 4;
		}
	}

	private class PerformanceTimerTask extends TimerTask {

		public PerformanceTimerTask(long start){
			this.start = start;
		}

		private long start;
		private long stepsCurrent;
		private double pi;

		@Override
		public void run() {
			setProgress((int) ((System.currentTimeMillis() - start) * 100 / time));
			stepsCurrent = 0;
			pi = 0;

			for (PerformanceThread p : threads) {
				pi += p.getPi();
				stepsCurrent += p.getLastSteps();
			}

			pi = pi / threadCount;
			publish(pi + " @ " + (stepsCurrent / intervall) + " steps / ms");
		}
	}
}
