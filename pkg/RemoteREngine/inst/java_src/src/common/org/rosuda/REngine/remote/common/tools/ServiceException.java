package org.rosuda.REngine.remote.common.tools;

public abstract class ServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public ServiceException(String message){
		super( message ) ;
	}
}
