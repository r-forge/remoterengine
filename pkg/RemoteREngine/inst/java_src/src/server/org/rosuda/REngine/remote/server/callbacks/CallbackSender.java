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

import org.rosuda.REngine.remote.common.RemoteREngineClient;
import org.rosuda.REngine.remote.common.callbacks.CallbackListener;
import org.rosuda.REngine.remote.common.callbacks.InputCallback;
import org.rosuda.REngine.remote.common.callbacks.RCallback;
import org.rosuda.REngine.remote.common.console.CommandSender;
import org.rosuda.REngine.remote.common.console.RemoteREngineClientSender;
import org.rosuda.REngine.remote.common.console.UnknownSender;
import org.rosuda.REngine.remote.common.tools.StoppableThreadWithSynchronizer;
import org.rosuda.REngine.remote.server.RemoteREngine_Server;
import org.rosuda.REngine.remote.server.console.ConsoleCallbackHandler;
import org.rosuda.REngine.remote.server.console.ServerConsoleSender;

/**
 * Thread that sends callbacks to callback listeners
 */
public class CallbackSender extends StoppableThreadWithSynchronizer<RCallback> {

	/**
	 * The associated server
	 */
	private RemoteREngine_Server server ;
		
	public CallbackSender( RemoteREngine_Server server ){
		super( "callback sender" ) ;
		this.server = server ; 
	}
	
	/**
	 * Send the callback to all callback listeners associated
	 * with the server
	 */
	@Override
	public void dealWith(RCallback callback) {
		
		/* FIXME: need to review this and add some filtering mechanism into the callback classes */
		
		if( callback instanceof InputCallback ){
			InputCallback input = (InputCallback)callback ;
			CommandSender sender = input.getSender() ; 
			
			/* it comes from a client */
			if( sender instanceof RemoteREngineClientSender ){
			
				RemoteREngineClientSender origin = (RemoteREngineClientSender)sender ;
				for( CallbackListener listener: server.getCallbackListeners() ){
					if( listener instanceof ClientCallbackListener ){
						/* find if the listener and the sender are working with the same client */
						RemoteREngineClient lis_client = ( (ClientCallbackListener)listener ).getClient() ;
						if( origin.getClient().equals( lis_client ) ){
							continue ;
						}
					}
					listener.handleCallback( callback ) ;
				}
			} else if( sender instanceof UnknownSender){
				sendToAll( callback ); 
			} else if( sender instanceof ServerConsoleSender ){
				
				/* send to all except the console handler */
				for( CallbackListener listener: server.getCallbackListeners() ){
					if( ! (listener instanceof ConsoleCallbackHandler) ){
						listener.handleCallback( callback ) ;
					}
				}
			}			
		} else{
			sendToAll( callback ); 
		}
	}
	
	private void sendToAll( RCallback callback ){
		/* send to all */
		for( CallbackListener listener: server.getCallbackListeners() ){
			listener.handleCallback( callback ) ;
		}
	}
	
	
}
