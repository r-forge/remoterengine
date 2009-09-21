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
package org.rosuda.REngine.remote.client.callbacks;

import java.util.Vector;

import org.rosuda.REngine.remote.client.RemoteREngine;
import org.rosuda.REngine.remote.common.callbacks.CallbackListener;
import org.rosuda.REngine.remote.common.callbacks.RCallback;
import org.rosuda.REngine.remote.common.tools.StoppableThreadWithSynchronizer;

/**
 * Dispatches the callback on the client side
 * @author Romain Francois
 *
 */
public class ClientCallbackDispatcher extends StoppableThreadWithSynchronizer<RCallback> {

	/**
	 * The client 
	 */
	protected RemoteREngine engine; 
	
	/**
	 * List of listeners
	 */
	protected Vector<CallbackListener> listeners ; 
	
	/**
	 * @param engine associated remote R engine
	 */
	public ClientCallbackDispatcher(RemoteREngine engine) {
		super("client callback dispatcher");
		this.engine = engine  ; 
		listeners = new Vector<CallbackListener>(); 
	}

	/**
	 * Forwards the callback to each listener of the client
	 */
	@Override
	public void dealWith(RCallback callback) {
		for( CallbackListener listener: listeners ){
			listener.handleCallback(callback) ;
		}
	}

	/**
	 * Adds a new listener
	 * @param listener callback listener
	 */
	public synchronized void addCallbackListener(CallbackListener listener) {
		listeners.add( listener ) ;
	}
	
	/**
	 * Removes a callback listener
	 * @param listener callback listener to remove
	 */
	public synchronized void removeCallbackListener( CallbackListener listener ){
		listeners.remove( listener ) ;
	}
	

}
