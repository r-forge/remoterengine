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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

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
	 * @return
	 * @throws NoFactoryForServiceException
	 * @throws ServiceImplementationNotFoundException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Service> T getService( Class<T> serviceClass, String name) throws ServiceException {
		
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
	public static <T extends Service> void addService( Class<T> serviceClass, String name, Class<? extends T> implementationClass){
		if( !factories.containsKey(serviceClass) ){
			factories.put(serviceClass, new GenericFactory<T>() ) ;
		}
		GenericFactory<T> factory = (GenericFactory<T>)factories.get(serviceClass) ;
		factory.addImplementation(name, implementationClass) ;
	}
	
	
}
