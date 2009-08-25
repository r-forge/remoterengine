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
import java.util.Map;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.util.SourcePosition;

public class ServiceImplementationAnnotationProcessor implements
		AnnotationProcessor {

	private AnnotationProcessorEnvironment environment;
	 
	private AnnotationTypeDeclaration noteDeclaration;

	public ServiceImplementationAnnotationProcessor( AnnotationProcessorEnvironment env){
		environment = env ;
		// get the annotation type declaration for our 'ServiceImplementation' annotation.
		// Note, this is also passed in to our annotation factory - this 
		// is just an alternate way to do it.
		noteDeclaration = (AnnotationTypeDeclaration) environment
				.getTypeDeclaration("org.rosuda.REngine.remote.common.tools.ServiceImplementation");

	}
	
	public void process() {		 
		// Get all declarations that use the note annotation.
		Collection<Declaration> declarations = environment
				.getDeclarationsAnnotatedWith(noteDeclaration);
		for (Declaration declaration : declarations) {
			processNoteAnnotations(declaration);
		}
	}
 
	private void processNoteAnnotations(Declaration declaration) {
		
		// Get all of the annotation usage for this declaration.
		// the annotation mirror is a reflection of what is in the source.
		Collection<AnnotationMirror> annotations = declaration
				.getAnnotationMirrors();
		
		// iterate over the mirrors.
		for (AnnotationMirror mirror : annotations) {
			
			// if the mirror in this iteration is for our service implementation declaration...
			if(mirror.getAnnotationType().getDeclaration().equals(noteDeclaration)) {
				
				// print out the goodies.
				SourcePosition position = mirror.getPosition();
				Map<AnnotationTypeElementDeclaration, AnnotationValue> values = mirror
						.getElementValues();
 
				System.out.println("Declaration: " + declaration.toString());
				System.out.println("Position: " + position);
				System.out.println("Values:");
				for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> entry : values
						.entrySet()) {
					AnnotationTypeElementDeclaration elemDecl = entry.getKey();
					AnnotationValue value = entry.getValue();
					System.out.println("    " + elemDecl + "=" + value);
				}
			}
		}
	}

}
