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
 * Call back sent to the clients to indicate that a previous 
 * callback has already been reply to from another client 
 * 
 * @author Romain Francois
 *
 */
public class CancelCallback extends RCallbackNoResponse {

	private static final long serialVersionUID = 1L;

	/**
	 * The id of the callback to cancel
	 */
	private int cancelledId ;  
	
	/**
	 * Constructor
	 * @param cancelledId the id of the callback to cancel
	 */
	public CancelCallback( int cancelledId ) {
		this.cancelledId = cancelledId; 
	}
	
	/**
	 * Constructor using the id of the specified callback
	 * @param original original callback to cancel
	 */
	public CancelCallback( RCallback original ){
		this( original.getId() ) ;
	}
	
	/**
	 * @return the id of the callback to cancel
	 */
	public int getCancelledId(){
		return cancelledId ;
	}

}
