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
package org.rosuda.REngine.remote.server.callbacks;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.rosuda.REngine.remote.common.RemoteREngineClient;
import org.rosuda.REngine.remote.common.callbacks.RCallback;

/**
 * Listener that sends callbacks to a client
 * 
 * @author Romain Francois
 */
public class ClientCallbackListener implements CallbackListener {

	/**
	 * client
	 */
	private RemoteREngineClient client;
	
	/**
	 * map client -> listener
	 */
	private static HashMap<RemoteREngineClient,CallbackListener> map = 
		new HashMap<RemoteREngineClient,CallbackListener>() ; 
	
	/**
	 * Constructor
	 * @param client the associated client
	 */
	public ClientCallbackListener(RemoteREngineClient client){
		this.client = client; 
		map.put( client, this) ;
	}
	
	/**
	 * Sends the callback to the client
	 * 
	 * @see RemoteREngineClient#callback(RCallback)
	 */
	public void handleCallback(RCallback callback) {
		try{
			client.callback(callback) ;
		} catch( RemoteException e){
			/* TODO: handle remote exception when sending callback to client */ 
		}		
	}
	
	/**
	 * @param client the client
	 * @return The listener associated with the client 
	 */
	public static synchronized CallbackListener popCallbackListener( RemoteREngineClient client){
		return map.remove(client) ;
	}

}
