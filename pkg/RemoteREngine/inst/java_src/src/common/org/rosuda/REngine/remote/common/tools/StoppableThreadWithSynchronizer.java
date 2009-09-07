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

/**
 * StoppableThread whose loop content consists of retrieveing the head 
 * of a synchronizer queue and act on it
 * 
 * @author Romain Francois
 *
 * @param <T>
 */
public abstract class StoppableThreadWithSynchronizer<T> extends StoppableThread {

	/**
	 * Synchronizer associated with this thread
	 */
	protected Synchronizer<T> synchronizer ; 
	
	/**
	 * Constructor
	 * @param name name of the thread
	 */
	public StoppableThreadWithSynchronizer( String name ){
		super( name ); 
		synchronizer = new Synchronizer<T>(); 
	}
	
	/**
	 * Gets the head of the queue and deals with it
	 */
	@Override
	public final void loop() {
		T o = synchronizer.next() ; 
		dealWith( o ) ;
	}
	
	/**
	 * deal with an object
	 * @param o object to deal with
	 */
	public abstract void dealWith( T o ) ;

	/**
	 * Adds an object to the queue
	 * @param o object
	 */
	public void addToQueue( T o ){
		synchronizer.add(o) ;
	}
	
}
