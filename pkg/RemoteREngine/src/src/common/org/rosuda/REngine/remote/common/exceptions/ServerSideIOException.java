package org.rosuda.REngine.remote.common.exceptions;

import java.io.IOException;

/**
 * Represents an IO exception that happened on the server side. This 
 * is mainly used to distinguish server side and client side IO exceptions
 * 
 * @author Romain Francois
 * @see IOException
 */
public class ServerSideIOException extends ServerSideException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The actual exception
	 */
	private IOException source; 
	
	/**
	 * Constructor
	 * @param ioe the IO exception to embed
	 */
	public ServerSideIOException( IOException ioe){
		super( "IO exception on the server: " + ioe.getMessage() ) ;
		this.source = ioe ;
	}
	
	/**
	 * @return the source
	 */
	public IOException getSource( ){
		return source; 
	}
	
}
