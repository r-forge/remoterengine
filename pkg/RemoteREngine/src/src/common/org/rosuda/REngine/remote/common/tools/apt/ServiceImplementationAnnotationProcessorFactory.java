/*
 * Copyright (c) 2009, Romain Francois <francoisromain@free.fr>
 *
 * This file is part of the RemoteREngine project
 *
 * The RemoteREngine project is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * The RemoteREngine project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the RemoteREngine project. If not, see <http://www.gnu.org/licenses/>.
 */
package org.rosuda.REngine.remote.common.tools.apt;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.rosuda.REngine.remote.common.tools.ServiceImplementation;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

public class ServiceImplementationAnnotationProcessorFactory implements
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
			result = new ServiceImplementationAnnotationProcessor(env); 
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
