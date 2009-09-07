package com.addictedtor.remoterengine.apt;

import com.sun.mirror.declaration.Declaration;

public class ShouldHaveADefaultConstructorException extends
		ServiceImplementationAnnotationException {

	private static final long serialVersionUID = 1L;

	public ShouldHaveADefaultConstructorException(Declaration declaration) {
		super( declaration, "the class annotated by @ServiceImplementation should have a constructor with no parameters");
	}

}
