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

import org.rosuda.REngine.remote.common.callbacks.CallbackListener;
import org.rosuda.REngine.remote.common.callbacks.RCallback;
import org.rosuda.REngine.remote.common.tools.StoppableThread;
import org.rosuda.REngine.remote.common.tools.Synchronizer;
import org.rosuda.REngine.remote.server.RemoteREngine_Server;

/**
 * Thread that sends callbacks to callback listeners
 */
public class CallbackSender extends StoppableThread {

	/**
	 * The associated server
	 */
	private RemoteREngine_Server server ;
	
	/**
	 * callback synchronizer
	 */
	private Synchronizer<RCallback> synchronizer ; 
	
	public CallbackSender( RemoteREngine_Server server ){
		super( "callback sender" ) ;
		this.server = server ; 
	}
	
	/**
	 * Get the next callback and send it to all callback listeners associated
	 * with the server
	 */
	@Override
	public void loop() {
		RCallback callback = synchronizer.next() ; 
		for( CallbackListener listener: server.getCallbackListeners() ){
			listener.handleCallback( callback ) ;
		}
	}
	
	/**
	 * Add a callback to the queue of callbacks to be sent
	 * @param callback callback that is to be sent to listeners
	 */
	public void addCallback( RCallback callback ){
		synchronizer.add(callback ) ;
	}
	
}
