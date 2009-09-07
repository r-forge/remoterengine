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

package org.rosuda.REngine.remote.common;

/**
 * Set of constants used by the server and the client 
 * 
 * @author Romain Francois <francoisromain@free.fr>
 */
public class RemoteREngineConstants {

	/** Define the default name to register the server process under */
    public static final String DEFAULTNAME = "RemoteREngine";
	
    /** Define the default host to look up to locate the RMI Registry */
	public static final String RMIHOSTNAME = "localhost";
	
	/** Define the default port number to locate the RMI Registry */
	public static final int RMIPORT = 1099 ;

	/**  The default chunk size for byte[] buffer */
	public static final int CHUNKSIZE = 8192 ; 
	
}
