package com.addictedtor.remoterengine.apt.exceptions;

import com.sun.mirror.declaration.ClassDeclaration;

public class IsNotAServiceException extends ServiceImplementationAnnotationException {

	private static final long serialVersionUID = 1L;

	public IsNotAServiceException(ClassDeclaration dec) {
		super(dec, "The class annotated with @ServiceImplementation should implement an interface that extends the Service interface") ;
	}
	
}
