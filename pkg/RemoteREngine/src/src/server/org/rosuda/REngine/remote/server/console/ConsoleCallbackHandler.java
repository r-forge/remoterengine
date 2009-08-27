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
package org.rosuda.REngine.remote.server.console;

import org.rosuda.REngine.remote.common.callbacks.CallbackListener;
import org.rosuda.REngine.remote.common.callbacks.InputCallback;
import org.rosuda.REngine.remote.common.callbacks.RCallback;
import org.rosuda.REngine.remote.common.callbacks.RShowMessageCallback;
import org.rosuda.REngine.remote.common.callbacks.RWriteConsoleCallback;
import org.rosuda.REngine.remote.common.callbacks.ReadConsoleCallback;
import org.rosuda.REngine.remote.common.tools.StoppableThreadWithSynchronizer;
import org.rosuda.REngine.remote.server.RemoteREngine_Server;

public class ConsoleCallbackHandler extends StoppableThreadWithSynchronizer<RCallback> implements CallbackListener {

	/**
	 * The associated server
	 */
	@SuppressWarnings("unused")
	private RemoteREngine_Server server ; 

	/**
	 * Constructor. 
	 * @param server associated R server
	 */
	public ConsoleCallbackHandler(RemoteREngine_Server server){
		super( "console callback handler" ) ;
		this.server = server ;
	}

	/**
	 * - get the next callback
	 * - handle it
	 */
	public void dealWith(RCallback callback){
		
		if( callback instanceof RWriteConsoleCallback ){
			System.out.print( ((RWriteConsoleCallback)callback).getMessage() ) ;
		} else if( callback instanceof RShowMessageCallback ){
			System.out.print( ((RShowMessageCallback)callback).getMessage() ) ;
		} else if( callback instanceof ReadConsoleCallback ){
			System.out.print( ((ReadConsoleCallback)callback).getPrompt() ) ;
		} else if( callback instanceof InputCallback ){
			System.out.print( ((InputCallback)callback).getCommand() + "\n") ;
		}
		/* else if( callback instanceof ReadConsoleCallback ){
				System.out.print( "\nEnter file name: " ) ;
			} */
		/* TODO: should this handle ChooseFileCallback */
	}

	/**
	 * CallbackListener implementation. adds the callback to the queue of 
	 * callbacks to be handled
	 */
	public synchronized void handleCallback(RCallback callback) {
		addToQueue( callback ) ;
	}

}
