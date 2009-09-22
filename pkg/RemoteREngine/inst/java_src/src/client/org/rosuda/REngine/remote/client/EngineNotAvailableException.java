package org.rosuda.REngine.remote.client;
/**
 * Exception thrown if the client is unable to connect through to a
 * remote engine.
 * @author $Author$
 * @version $Rev$ as of $Date$
 * <p>URL : $HeadURL$
 */
/*
 * Copyright Stoat Software & Services 2009 <ilong@stoatsoftware.com>
 */
public class EngineNotAvailableException extends Exception {

	/**
	 * Construct exception without a cause
	 * @param message Message about this exception
	 */
	public EngineNotAvailableException(String message) {
		this(message,null);
	}

	/**
	 * Construct exception with the provided cause
	 * @param message Message about this exception
	 * @param cause The lower level exception that triggered this exception
	 */
	public EngineNotAvailableException(String message, Throwable cause) {
		super(message,cause);
	}
}
