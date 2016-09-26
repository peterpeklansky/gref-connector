package org.mule.modules.gref.exception;

public class GrefConnectorException extends Exception{

	private static final long serialVersionUID = 1L;

	public GrefConnectorException() {
	}

	public GrefConnectorException(String message) {
		super(message);
	}

	public GrefConnectorException(Throwable cause) {
		super(cause);
	}

	public GrefConnectorException(String message, Throwable cause) {
		super(message, cause);
	}
}
