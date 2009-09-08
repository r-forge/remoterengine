package org.rosuda.REngine.remote.common.callbacks;

import org.rosuda.REngine.REXP;

/**
 * Signed callback that carries an R object
 * 
 * @author Romain Francois <francoisromain@free.fr>
 *
 */
public class ObjectCallback extends SignedCallback {

	private static final long serialVersionUID = 1L;

	/**
	 * R object associated with the callback
	 */
	protected REXP object ; 
	
	/**
	 * Constructor 
	 * @param signature callback signature
	 * @param object R object 
	 */
	public ObjectCallback( String signature, REXP object){
		super( signature ) ;
		this.object = object ; 
	}
	
	/**
	 * Constructor using empty signature 
	 * @param object R object
	 */
	public ObjectCallback( REXP object){
		this( "", object ) ;
	}
	
	/**
	 * @return The R object associated with the callback
	 */
	public REXP getObject(){
		return object ; 
	}
}
