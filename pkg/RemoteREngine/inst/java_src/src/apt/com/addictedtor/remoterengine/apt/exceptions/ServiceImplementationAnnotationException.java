package com.addictedtor.remoterengine.apt.exceptions;

import com.sun.mirror.declaration.Declaration;

public class ServiceImplementationAnnotationException extends
		RemoteREngineAnnotationException {

	private static final long serialVersionUID = 1L;

	public ServiceImplementationAnnotationException( Declaration declaration, String message ){
		super(declaration, message); 
	}
}
