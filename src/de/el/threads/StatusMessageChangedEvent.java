package de.el.threads;

/**
 * This event indicates that the status message of a XThread has changed.
 * @author PEH & FLN
 */
public class StatusMessageChangedEvent {

	/**
	 * the status message
	 */
	private String statusMessage;

	/**
	 * Creates a new status changed event.
	 * @param s
	 */
	public StatusMessageChangedEvent(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	/**
	 * Returns the status message.
	 * @return status message
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
}
