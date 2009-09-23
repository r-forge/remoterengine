/* 
 * project	: RemoteREngine
 * package	: org.rosuda.REngine.remote.client
 * created  : 22 Sep 2009
 */
package org.rosuda.REngine.remote.client;

/**
 * The R engine connect exception.
 * 
 */
public class REngineConnectException extends RuntimeException {

	/**
	 * serialVersionUID : <code>long</code>
	 */
	private static final long serialVersionUID = -6815651756285432282L;

	/**
	 * 
	 */
	public REngineConnectException() {
		super("Error in connecting to R server : ");
	}

	/**
	 * @param message
	 * @param cause
	 */
	public REngineConnectException(String message, Throwable cause) {
		super("Error connecting to R server : " + message, cause);
	}

	/**
	 * @param message
	 */
	public REngineConnectException(String message) {
		super("Error connecting to R server : " + message);
	}

	/**
	 * @param cause
	 */
	public REngineConnectException(Throwable cause) {
		super("Error connecting to R server ", cause);
	}
}
