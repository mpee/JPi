package de.el.threads;

import java.util.EventListener;

/**
 * This interface is implemented by any class which wants to receive
 * notifications about the status message change of an XThread.
 * @author PEH & FLN
 */
public interface StatusMessageListener extends EventListener {

	/**
	 * This method is invoked when the status message of a XThread has changed.
	 * @param e
	 */
	public void statusMessageChanged(StatusMessageChangedEvent e);
}
