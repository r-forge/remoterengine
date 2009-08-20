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

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rosuda.REngine.remote.common.callbacks.RCallback;

/**
 * A client of a RemoteREngine. The RemoteREngine server keeps a reference of 
 * all its clients and use the methods of this class to call back the client 
 * 
 * @author Romain Francois
 *
 */
public interface RemoteREngineClient extends Remote {

	/**
	 * Send an RCallback to a client 
	 * 
	 * @param callback a callback
	 */
	public void callback( RCallback callback ) throws RemoteException ; 
	
	/**
	 * Indicates to the client that the callback was responded by another client
	 * @param callback
	 */
	public void cancelCallback( int id ) throws RemoteException ;
	
	/**
	 * Indicates to the client that the server is dying 
	 * @throws RemoteException
	 */
	public void serverDying() throws RemoteException;  
	
}
