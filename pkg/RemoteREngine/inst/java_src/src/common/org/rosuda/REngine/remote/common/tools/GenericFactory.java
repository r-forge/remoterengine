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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Generic factory 
 * 
 * @author Romain Francois
 *
 * @param <T>
 */
public class GenericFactory<T extends Service> {
	
	/**
	 * implementations
	 */
	private HashMap<String,Class<? extends T>> implementations; 

	/**
	 * Constructor. Initializes the map of implementations
	 */
	public GenericFactory(){
		implementations = new HashMap<String, Class<? extends T>>(); 
	}
	
	/**
	 * Gets an instance of the implementation class associated with the name
	 * 
	 * @param name name of the implementation
	 * @return an instance of the class that implements the generic type <T> 
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public T getImplementation( String name ) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException{
		Class<? extends T> clazz = implementations.get(name); 
		Constructor<? extends T> cons = clazz.getConstructor((Class[])null) ; 
		return cons.newInstance( (Object[])null ) ; 
	}

	/**
	 * Adds an implementation to the map
	 * @param name name of the implementation
	 * @param clazz class that extends <T> and has an empty constructor
	 */
	public void addImplementation(String name, Class<? extends T> clazz ){
		implementations.put( name, clazz ) ;
	}
	
	/**
	 * @param name name of one implementation
	 * @return true if the service is defined for that implementation
	 */
	public boolean defines( String name){
		return implementations.containsKey(name) ;
	}
	
}
