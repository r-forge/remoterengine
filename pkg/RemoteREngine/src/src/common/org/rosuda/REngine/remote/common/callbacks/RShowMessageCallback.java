package org.rosuda.REngine.remote.common.callbacks;

import org.rosuda.JRI.RMainLoopCallbacks;

/**
 * Callback created when R needs to display a message 
 *  
 * @author Romain Francois
 * @see RMainLoopCallbacks#rShowMessage(org.rosuda.JRI.Rengine, String)
 */
@SuppressWarnings("serial")
public class RShowMessageCallback extends RCallback {

	/**
	 * The message
	 */
	private String message; 
	
	/**
	 * Constructor
	 * @param message the message 
	 */
	public RShowMessageCallback( String message){
		super(); 
		this.message  = message ; 
	}
	
	/**
	 * @return the message associated to this callback
	 */
	public String getMessage(){
		return message; 
	}
}
