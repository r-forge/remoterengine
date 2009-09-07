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
package org.rosuda.REngine.remote.common.tools;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Simple synchronization mechanism. This wraps a {@link Queue} 
 * and provides a next method that waits until an element gets
 * added to the queue
 * 
 * @author Romain Francois
 *
 * @param <T> 
 */
public class Synchronizer<T> {

	/**
	 * The command queue
	 */
	protected Queue<T> queue ; 
	
	/**
	 * Constructor
	 */
	public Synchronizer() {
		queue = new LinkedList<T>();
	}

	/**
	 * Waits until the queue is not empty and returns its head
	 * @return the head of the command queue
	 */
	public synchronized T next() {
		while ( queue.isEmpty() ){
			try {
				wait(100);
				afterWaiting(); 
			} catch (InterruptedException e) { }
		}
		return queue.poll() ; 
	}

	/**
	 * Adds an object to the queue
	 * @param o object to add in the tail of the queue
	 */
	public synchronized void add(T o) {
		queue.add(o); 
		notifyAll();
	}
	
	/**
	 * Hook method executed after the call to wait
	 */
	public void afterWaiting(){} ;

}
