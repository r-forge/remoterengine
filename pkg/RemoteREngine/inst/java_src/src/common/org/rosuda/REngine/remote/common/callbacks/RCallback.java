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

import java.io.Serializable;

/**
 * A callback encapsulates the information that is generated by the 
 * R main loop callback class.  
 * 
 * @author Romain Francois
 */
@SuppressWarnings("serial")
public abstract class RCallback implements Serializable{
	
	/** 
	 * counter
	 */
	private static int counter = 0 ; 
	
	/**
	 * id of this callback
	 */
	private int id;
	
	/**
	 * constructor, increments the id
	 */
	public RCallback(){
		id = counter++ ;
	}
	
	/**
	 * @return the id of the callback
	 */
	public int getId(){
		return id; 
	}
	
	/**
	 * @return true if this callback requires a response. 
	 */
	public abstract boolean needsResponse() ;
	
	/**
	 * Is this callback restricted to a subset of clients
	 * 
	 * @return true if the callback is restricted
	 */
	public boolean isRestricted(){
		return false; 
	}
	
}
