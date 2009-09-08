package org.rosuda.REngine.remote.common.callbacks;

/**
 * Signed callback
 * 
 * @author Romain Francois <francoisromain@free.fr>
 *
 */
public class SignedCallback extends RCallbackNoResponse {

	/**
	 * The callback signature
	 */
	protected String signature ;
	
	/**
	 * Builds a signed callback 
	 *  
	 * @param signature callback signature
	 */
	public SignedCallback( String signature){
		this.signature = signature ;
	}
	
	private static final long serialVersionUID = 1L;

	/**
	 * @return the signature of the callback
	 */
	public String getSignature(){
		return signature ; 
	}
	
}
