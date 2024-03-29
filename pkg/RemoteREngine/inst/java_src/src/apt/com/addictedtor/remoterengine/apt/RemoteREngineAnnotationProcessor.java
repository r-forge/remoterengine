package com.addictedtor.remoterengine.apt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.InterfaceDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.type.InterfaceType;

public class RemoteREngineAnnotationProcessor implements AnnotationProcessor {

	private AnnotationProcessorEnvironment environment;

	private AnnotationTypeDeclaration serviceImplementationDeclaration;

	private static Writer writer ; 

	/**
	 * The fully qualified name of the Service class
	 */
	public static final String SERVICECLASS = "org.rosuda.REngine.remote.common.tools.Service" ; 

	public RemoteREngineAnnotationProcessor( AnnotationProcessorEnvironment env){
		environment = env ;
		// get the annotation type declaration for our 'ServiceImplementation' annotation.
		// Note, this is also passed in to our annotation factory - this 
		// is just an alternate way to do it.
		serviceImplementationDeclaration = (AnnotationTypeDeclaration) environment
		.getTypeDeclaration("org.rosuda.REngine.remote.common.tools.ServiceImplementation");

		try{
			File file = new File("build/services.xml") ;
			System.out.println( file.getAbsolutePath() );
			writer = new BufferedWriter( new FileWriter( file ) );
			writer.append( "<?xml version=\"1.0\"?>\n" ) ;
			writer.append( "<services>\n" ) ;
			writer.append( "  <!-- this is an autogenerated file, do not change -->\n" ) ;
		} catch( IOException ioe){}
	}

	public void process() {		 
		//try{
			// Get all declarations that use the note annotation.
			Collection<Declaration> declarations = environment
			.getDeclarationsAnnotatedWith(serviceImplementationDeclaration);
			for (Declaration declaration : declarations) {
				try{
					processServiceImplementationAnnotations(declaration);
				} catch( RemoteREngineAnnotationException e){
					e.printStackTrace() ;
				}
			}
		// } catch( Exception e){}
	}

	private void processServiceImplementationAnnotations(Declaration declaration) throws RemoteREngineAnnotationException {

		System.out.println( declaration ) ;

		// Get all of the annotation usage for this declaration.
		// the annotation mirror is a reflection of what is in the source.
		Collection<AnnotationMirror> annotations = declaration
		.getAnnotationMirrors();

		boolean ok = false; 

		// iterate over the mirrors.
		for (AnnotationMirror mirror : annotations) {

			// if the mirror in this iteration is for our service implementation declaration...
			if(mirror.getAnnotationType().getDeclaration().equals(serviceImplementationDeclaration)) {


				// name of the service 
				String service = ""; 
				String name = ""; 
				String impl = ""; 

				// print out the goodies.
				Map<AnnotationTypeElementDeclaration, AnnotationValue> values = mirror
				.getElementValues();

				/* the annotation should be used on a class */
				if( ! ( declaration instanceof ClassDeclaration ) ){
					throw new ShouldBeClassDeclarationException( declaration ) ;
				}
				ClassDeclaration decl = (ClassDeclaration)declaration ;
				impl = decl.getQualifiedName() ;

				/* the class should implement an interface that extends Service */ 
				Collection<InterfaceType> interfaces = decl.getSuperinterfaces() ;
				ok = false; 
				for( InterfaceType intf: interfaces) {
					InterfaceDeclaration dec = intf.getDeclaration() ;
					Collection<InterfaceType> superintf = dec.getSuperinterfaces() ;
					for( InterfaceType supf: superintf){
						if( SERVICECLASS.equals(supf.getDeclaration().getQualifiedName()) ){
							ok = true ; 
							service = intf.getDeclaration().getQualifiedName() ;
							break ;
						}
					}
				}
				if( !ok ){
					throw new IsNotAServiceException(decl) ;
				}

				/* the class should have a constructor without argument */
				ok = false; 
				Collection<ConstructorDeclaration> 	constructors = decl.getConstructors() ; 
				for( ConstructorDeclaration constructor: constructors){
					Collection<ParameterDeclaration> parameters = constructor.getParameters() ;
					if( parameters.size() == 0){
						ok = true; 
						break ; 
					}
				}
				if( !ok ){
					throw new ShouldHaveADefaultConstructorException(declaration) ;
				}

				for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> entry : values
						.entrySet()) {
					AnnotationTypeElementDeclaration elemDecl = entry.getKey() ;
					if( ! "value".equals(elemDecl.getSimpleName()) ){
						continue ; 
						/* should not happen */
					}
					name = (String)entry.getValue().getValue();
				}

				System.out.println("========================"  );
				System.out.println("service        : " + service);
				System.out.println("implementation : " + impl );
				System.out.println("name           : " + name );

				try{
					writer.append( "\n  <service \n"); 
					writer.append( "     class=\"") ;
					writer.append( service ) ;
					writer.append( "\" \n     name=\"" ) ;
					writer.append( name ) ;
					writer.append( "\" \n     implementation=\"" ) ;
					writer.append( impl ) ;
					writer.append( "\" />\n\n" ) ;
				} catch( IOException ioe){}


			}

		}
		try{
			writer.append( "</services>\n" ) ;
			writer.close();
		} catch( IOException ioe){}
	}

}
