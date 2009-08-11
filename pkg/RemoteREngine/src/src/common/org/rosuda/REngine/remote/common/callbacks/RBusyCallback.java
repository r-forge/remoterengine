package org.rosuda.REngine.remote.common.callbacks;

import org.rosuda.JRI.RMainLoopCallbacks;

/**
 * Callback used to indicate to the client if R is busy
 * 
 * @author Romain Francois
 * @see RMainLoopCallbacks#rBusy(org.rosuda.JRI.Rengine, int)
 */
@SuppressWarnings("serial")
public class RBusyCallback extends RCallback {

	/**
	 * Is R busy
	 */
	private boolean busy; 
	
	/**
	 * Constructor 
	 * @param busy is R busy
	 */
	public RBusyCallback( boolean busy){
		super(); 
		this.busy = busy ;
	}
	
	/**
	 * Constructor
	 * @param busy_int is R busy (1) or not (0)
	 */
	public RBusyCallback( int busy_int){
		this( busy_int == 1 ) ; 
	}
	
	/**
	 * Indicates if R is busy
	 * @return true if R is busy
	 */
	public boolean isBusy(){
		return busy;
	}
	
}
