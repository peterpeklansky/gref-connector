package org.mule.modules.gref.exception;

public class GrefConnectorAccessDeniedException extends Exception{
	private static final long serialVersionUID = 1L;

	public GrefConnectorAccessDeniedException() {
	}

	public GrefConnectorAccessDeniedException(String message) {
		super(message);
	}

	public GrefConnectorAccessDeniedException(Throwable cause) {
		super(cause);
	}

	public GrefConnectorAccessDeniedException(String message, Throwable cause) {
		super(message, cause);
	}
}
