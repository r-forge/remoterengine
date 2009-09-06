package org.rosuda.REngine.remote.common.callbacks;

public class RestrictedCallback extends RCallbackNoResponse {

	private static final long serialVersionUID = 1L;

	public final boolean isRestricted(){
		return true; 
	}
	
}
