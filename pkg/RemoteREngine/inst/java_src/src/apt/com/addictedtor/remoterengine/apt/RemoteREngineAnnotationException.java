package com.addictedtor.remoterengine.apt;

import com.sun.mirror.declaration.Declaration;

public abstract class RemoteREngineAnnotationException extends Exception {

	private static final long serialVersionUID = 1L;

	protected Declaration declaration ;
	
	public RemoteREngineAnnotationException(Declaration declaration, String message){
		super( message ) ;
	}
	
	public String getMessage(){
		/* TODO: add information about source location of the problem */
		return super.getMessage() ;	
	}
	
}
