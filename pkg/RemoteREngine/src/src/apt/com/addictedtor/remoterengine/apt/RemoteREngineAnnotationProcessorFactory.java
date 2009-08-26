package com.addictedtor.remoterengine.apt;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.rosuda.REngine.remote.common.tools.ServiceImplementation;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

public class RemoteREngineAnnotationProcessorFactory implements
		AnnotationProcessorFactory {

	/**
	 * Returns a service implementation annotation processor.
	 * 
	 * @return An annotation processor for service implementation
	 * annotations if requested. 
	 * otherwise, returns the NO_OP annotation processor.
	 */
	public AnnotationProcessor getProcessorFor(
			Set<AnnotationTypeDeclaration> declarations,
			AnnotationProcessorEnvironment env) {
		
		AnnotationProcessor result;
		if(declarations.isEmpty()) {
			result = AnnotationProcessors.NO_OP;
		}
		else {
			result = new RemoteREngineAnnotationProcessor(env); 
		}
		return result;

	}

	/**
	 * This factory only builds processors for the 
	 * {@link ServiceImplementation} annotation.
	 * 
	 * @return a collection containing only the note annotation name.
	 */
	public Collection<String> supportedAnnotationTypes() {
		return Collections.singletonList("org.rosuda.REngine.remote.common.tools.ServiceImplementation");
	}
 
	/**
	 * No options are supported by this annotation processor.
	 * @return an empty list.
	 */
	public Collection<String> supportedOptions() {
		return Collections.emptyList();
	}

	
}
