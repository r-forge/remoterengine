package org.rosuda.REngine.remote.common.callbacks;

/**
 * a response to a {@link ReadConsoleCallback} callback
 * 
 * @author Romain Francois
 *
 */
public class ReadConsoleCallbackResponse extends CallbackResponse<ReadConsoleCallback> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The command
	 */
	private String command; 
	
	/**
	 * Constuctor 
	 * @param callbackId the id of the associated callback
	 * @param command the command 
	 */
	public ReadConsoleCallbackResponse(int callbackId, String command) {
		super(callbackId);
		this.command = command; 
	}

	/**
	 * @return the command 
	 */
	public String getCommand(){
		return command ;
	}
}
