package org.rosuda.REngine.remote.common.exceptions;

import java.io.Serializable;

/**
 * An exception on the server side of the Remote R engine
 *
 * @author Romain Francois
 */
public class ServerSideException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The default message
	 */
	public static final String DEFAULT_MESSAGE = "server side exception"  ; 
	
	/**
	 * Constructor
	 * @param message the message of the exception
	 */
	public ServerSideException(String message){
		super( message ) ;
	}
	
	/**
	 * Constructor using the default message
	 */
	public ServerSideException(){
		this( DEFAULT_MESSAGE ) ;
	}
	
	
}
