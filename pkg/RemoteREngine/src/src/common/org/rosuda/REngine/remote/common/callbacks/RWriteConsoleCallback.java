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

package org.rosuda.REngine.remote.common.callbacks;

/**
 * Callback to write information on the console 
 * 
 * @author Romain Francois
 *
 */
@SuppressWarnings("serial")
public class RWriteConsoleCallback extends RCallback {

	/** 
	 * regular message
	 */
	public static final int REGULAR = 0 ; 
	
	/**
	 * Warning/Error message
	 */
	public static final int ERROR = 1; 
	
	/**
	 * The type of message, can be REGULAR or ERROR
	 */
	public int type ; 
	
	/**
	 * The message
	 */
	public String message; 
	
	/**
	 * Default constructor
	 * 
	 * @param message message
	 * @param type type of message
	 */
	public RWriteConsoleCallback( String message, int type){
		super();
		this.message = message;
		this.type = type; 
	}
	
	/**
	 * Constructor, regular message
	 *  
	 * @param message message
	 */
	public RWriteConsoleCallback( String message ){
		this( message, REGULAR ) ;
	}
	
	public int getType(){
		return type ; 
	}
	
	public String getMessage(){
		return message; 
	}
	
}
