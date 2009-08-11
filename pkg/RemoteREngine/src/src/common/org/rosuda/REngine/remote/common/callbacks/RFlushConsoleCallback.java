package org.rosuda.REngine.remote.common.callbacks;

import org.rosuda.JRI.RMainLoopCallbacks;

/**
 * Callback to flush the console
 * 
 * @author Romain Francois
 * @see RMainLoopCallbacks#rFlushConsole(org.rosuda.JRI.Rengine)
 */
@SuppressWarnings("serial")
public class RFlushConsoleCallback extends RCallback {

	/**
	 * Default constructor
	 */
	public RFlushConsoleCallback(){
		super(); 
	}
}
