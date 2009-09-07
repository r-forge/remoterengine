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
 
package org.rosuda.REngine.remote.common.exceptions;

import java.io.Serializable;

/**
 * An exception on the server side of the Remote R engine
 *
 * @author Romain Francois
 */
public class ServerSideException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The default message
	 */
	public static final String DEFAULT_MESSAGE = "server side exception"  ; 
	
	/**
	 * Constructor
	 * @param message the message of the exception
	 */
	public ServerSideException(String message){
		super( message ) ;
	}
	
	/**
	 * Constructor using the default message
	 */
	public ServerSideException(){
		this( DEFAULT_MESSAGE ) ;
	}
	
	
}
