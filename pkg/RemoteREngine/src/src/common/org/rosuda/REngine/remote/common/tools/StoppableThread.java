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
 * Threads that iterate over the loop method. Before the next iteration, 
 * the thread checks if it needs to be stopped, as can be requested
 * by the requestStop method
 * 
 * @author Romain Francois
 *
 */
public abstract class StoppableThread extends Thread {

	/**
	 * Should the thread stop before the next iteration 
	 */
	protected boolean stop ; 
	
	/**
	 * Constructor 
	 * @param name name for the thread
	 */
	public StoppableThread( String name ){
		super( ); 
		setName(name) ;
		stop = false; 
	}
	
	/**
	 * Iterates over the loop method until stop is 
	 * set to false by the requestStop method
	 */
	public final void run(){
		while( !stop ){
			loop() ; 
		}
		end(); 
	}
	
	/**
	 * Content of the loop
	 */
	public abstract void loop() ;
	
	/**
	 * Requests that this thread stops. This sets the 
	 * stop field and the thread will stop at the next iteration of the loop
	 */
	public void requestStop(){
		stop = true ; 
	}
	
	/**
	 * Executed after the loop has finished
	 */
	public void end(){}
	
}



