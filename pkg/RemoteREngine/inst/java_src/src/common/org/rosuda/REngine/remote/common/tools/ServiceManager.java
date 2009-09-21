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
package org.rosuda.REngine.remote.common.tools;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simple service manager mechanism.
 * 
 * @author Romain Francois
 *
 */
public class ServiceManager {

	/**
	 * map of factories
	 */
	private static HashMap<Class<?>,GenericFactory<?>> factories = new HashMap<Class<?>,GenericFactory<?>>(); 

	/**
	 * Creates an instance of the service defined by the service class for the given name
	 * 
	 * @param <T> the type of {@link Service}
	 * @param serviceClass service class
	 * @param name name of the implementation of the service
	 * @return an instance of the requested service
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Service> T getInstance( Class<T> serviceClass, String name) throws ServiceException {

		if( !factories.containsKey(serviceClass) ){
			throw new NoFactoryForServiceException( serviceClass ) ;
		}
		GenericFactory<T> factory = (GenericFactory<T>)factories.get( serviceClass ) ;
		if( !factory.defines( name) ){
			throw new ServiceImplementationNotFoundException( serviceClass, name ) ;
		}
		T s = null ;
		try{
			s = factory.getImplementation( name ) ; 
		} catch( Exception e){
			throw new ServiceWrappedException( e ) ;
		}
		return s ;
	}
	
	/**
	 * Store a definition of a service 
	 * 
	 * @param <T> the type of service
	 * @param serviceClass the service class
	 * @param name the name of the implementation
	 * @param implementationClass the class implementing the service
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Service> void addServiceImplementation( Class<T> serviceClass, String name, Class<? extends T> implementationClass){
		if( !factories.containsKey(serviceClass) ){
			factories.put(serviceClass, new GenericFactory<T>() ) ;
		}
		GenericFactory<T> factory = (GenericFactory<T>)factories.get(serviceClass) ;
		factory.addImplementation(name, implementationClass) ;
	}

	/**
	 * Initializes the services by reading the services.xml file
	 */
	public static void init(){

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		domFactory.setValidating(false);

		try {
			DocumentBuilder documentBuilder = domFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(ServiceManager.class.getResourceAsStream("/services.xml")) ;
			NodeList serviceNodes = document.getElementsByTagName("service") ;
			for( int i=0; i<serviceNodes.getLength(); i++ ){
				Node service = serviceNodes.item(i) ;
				NamedNodeMap attrs = service.getAttributes() ;
				load( extract( attrs, "class"), extract( attrs, "name"), extract( attrs, "implementation") ) ;
			}
			
		} catch( Exception e){
			e.printStackTrace();
		}
	
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Service> void load( String clazz, String name, String impl){
		Class<T> serviceClass ; 
		Class<? extends T> implementationClass; 
		
		try{
			// TODO: various checks here. does serviceClass implement Service, ...
			serviceClass = (Class<T>)Class.forName(clazz) ;
			implementationClass = (Class<? extends T>)Class.forName(impl) ;
			addServiceImplementation( serviceClass, name, implementationClass )  ;
		} catch( ClassNotFoundException cnf){}
		
	}
	
	private static String extract( NamedNodeMap map, String attr){
		return ( (Attr)map.getNamedItem( attr ) ).getValue() ;
	}
}
