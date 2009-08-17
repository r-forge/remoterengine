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
 * Object sent by the client in response to a callback
 * 
 * @author Romain Francois
 */
public abstract class CallbackResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The id of the callback this is responding to
	 */
	private int callbackId ; 
	
	/**
	 * Constructor. Holds the id of the associated callback
	 * @param callbackId the id of the callback this is responding to
	 */
	public CallbackResponse( int callbackId){
		this.callbackId = callbackId ;
	}
	
	/**
	 * @return the id of the associated callback
	 */
	public int getCallbackId(){
		return callbackId ; 
	}
}
