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

import java.util.HashSet;
import java.util.Set;

import org.rosuda.REngine.remote.common.callbacks.CallbackResponse;
import org.rosuda.REngine.remote.common.callbacks.RCallbackWithResponse;
import org.rosuda.REngine.remote.common.tools.Waiter;

public class CallbackResponseWaiter extends Waiter<Integer,CallbackResponse<? extends RCallbackWithResponse>> {

	private Set<Integer> waiting ; 
	
	public CallbackResponseWaiter(){
		super();
		waiting = new HashSet<Integer>(); 
	}
	
	@Override
	public synchronized CallbackResponse<? extends RCallbackWithResponse> get(Integer key) {
		CallbackResponse<? extends RCallbackWithResponse> response = super.get(key);
		waiting.remove(response.getCallbackId()) ;
		return response; 
	}

	public synchronized void waitingFor(int id) {
		waiting.add( id ) ;
	}
	
	public synchronized boolean isWaitingFor( int id) {
		return waiting.contains( id) ;
	}
	
}
