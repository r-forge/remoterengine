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

package org.rosuda.REngine.remote.common.callbacks;

/**
 * Callback used to indicate to the client if R is busy
 * 
 * @author Romain Francois
 */
@SuppressWarnings("serial")
public class RBusyCallback extends RCallbackNoResponse {

	/**
	 * Is R busy
	 */
	private boolean busy; 
	
	/**
	 * Constructor 
	 * @param busy is R busy
	 */
	public RBusyCallback( boolean busy){
		super(); 
		this.busy = busy ;
	}
	
	/**
	 * Constructor
	 * @param busy_int is R busy (1) or not (0)
	 */
	public RBusyCallback( int busy_int){
		this( busy_int == 1 ) ; 
	}
	
	/**
	 * Indicates if R is busy
	 * @return true if R is busy
	 */
	public boolean isBusy(){
		return busy;
	}
	
	
}
