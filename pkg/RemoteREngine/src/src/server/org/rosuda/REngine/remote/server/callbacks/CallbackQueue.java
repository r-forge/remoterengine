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

import java.util.LinkedList;
import java.util.Queue;

import org.rosuda.REngine.remote.common.callbacks.RCallback;

/**
 * Simple queue for the callbacks 
 *  
 * @author Romain Francois
 */
public class CallbackQueue {

	/**
	 * Queue of callbacks
	 */
	private Queue<RCallback> callbacks ;
	
	/**
	 * constructor, creates the queue
	 */
	public CallbackQueue( ){
		callbacks = new LinkedList<RCallback>(); 
	}
	
	/**
	 * Waits until a callback is available and return it
	 * @return the next callback to send to the clients
	 */
	public synchronized RCallback next(){
		while ( callbacks.isEmpty() ){
			try {
				wait(100);
			} catch (InterruptedException e) { }
		}
		return callbacks.poll() ; 
	}
	
	/**
	 * Adds the callbacks to the queue
	 * @param callback the callback to add to the queue
	 */
	public synchronized void push( RCallback callback ){
		System.err.println( "callback ["+callback.getId() + "] : " + callback.getClass().getName() );
		callbacks.add( callback ) ;
		notifyAll() ;
	}
	
	
}
