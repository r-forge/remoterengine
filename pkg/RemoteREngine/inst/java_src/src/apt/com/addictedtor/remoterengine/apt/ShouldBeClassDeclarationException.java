package com.addictedtor.remoterengine.apt;

import com.sun.mirror.declaration.Declaration;


public class ShouldBeClassDeclarationException extends
		ServiceImplementationAnnotationException {

	private static final long serialVersionUID = 1L;

	public ShouldBeClassDeclarationException(Declaration declaration) {
		super( declaration, "the @ServiceImplementation annotation should only be used used on class declaration" );
	}

}
