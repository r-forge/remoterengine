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

import java.io.IOException;

/**
 * Represents an IO exception that happened on the server side. This 
 * is mainly used to distinguish server side and client side IO exceptions
 * 
 * @author Romain Francois
 * @see IOException
 */
public class ServerSideIOException extends ServerSideException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The actual exception
	 */
	private IOException source; 
	
	/**
	 * Constructor
	 * @param ioe the IO exception to embed
	 */
	public ServerSideIOException( IOException ioe){
		super( "IO exception on the server: " + ioe.getMessage() ) ;
		this.source = ioe ;
	}
	
	/**
	 * @return the source
	 */
	public IOException getSource( ){
		return source; 
	}
	
}
