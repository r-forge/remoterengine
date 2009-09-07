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

/**
 * Indicates that a file already exists. This exception 
 * is used if the client wants to push a file to the server 
 * while making sure that the file does not already exist 
 * 
 * @author Romain Francois
 *
 */
public class FileAlreadyExistsException extends ServerSideException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The name of the file 
	 */
	private String filename ; 
	
	/**
	 * Constructor
	 * @param filename the name of the file
	 */
	public FileAlreadyExistsException( String filename ){
		super( filename + " already exists" ) ;
		this.filename = filename;  
	}
	
	/**
	 * @return the name of the file
	 */
	public String getFilename(){
		return filename; 
	}
}
