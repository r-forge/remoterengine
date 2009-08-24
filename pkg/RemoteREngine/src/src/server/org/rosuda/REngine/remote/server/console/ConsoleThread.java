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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.rosuda.REngine.remote.common.callbacks.RCallback;
import org.rosuda.REngine.remote.common.callbacks.RShowMessageCallback;
import org.rosuda.REngine.remote.common.callbacks.RWriteConsoleCallback;
import org.rosuda.REngine.remote.server.RemoteREngine_Server;
import org.rosuda.REngine.remote.server.callbacks.CallbackListener;

/**
 * The console thread, that allows control of the server on the server side 
 * 
 * @author Romain Francois
 *
 */
public class ConsoleThread extends Thread implements CallbackListener {
	
	/**
	 * The server associated with this thread
	 */
	private RemoteREngine_Server server ; 
	
	/**
	 * Should the thread stop
	 */
	private boolean stop; 
	
	private BufferedReader rdr ;
	
	/**
	 * The constructor. 
	 * @param server R engine associated with this console 
	 */
	public ConsoleThread( RemoteREngine_Server server){
		this.server = server ; 
		stop = false; 
	}
	
	/**
	 * Prompts continually 
	 */
	public void run(){
		
		rdr = new BufferedReader(new InputStreamReader(System.in));
    	
    	while (!stop) {
			System.out.println("Type \"Quit\" to shutdown the server.");
			try {
				while( !stop && !rdr.ready() ){
					sleep(100); 
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				stop = true;
				break; 
			}
			if( stop ){
				break; 
			}
			try {
				/* TODO: need to find a way to interrupt this blocking call */
				String line = rdr.readLine() ;
				if ("Quit".equalsIgnoreCase(line)) {
					stop = true; /* stop this thread */
					server.shutdown() ; /* let clients know */
				} else {
					server.getConsoleSync().addInput( line ) ;
				}
			} catch (IOException e) {
				System.out.println(e.getClass().getName() + " : " + e.getMessage());
			}
			
		}
		try {
			rdr.close();
		} catch (IOException e) {
			// don't care
		} 
		System.out.println( "console thread stopped" ) ;
	}
	
	/**
	 * Request that this thread stops
	 */
	public void requestStop(){
		stop = true ;
		try {
			rdr.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Handles a callback sent by the server
	 */
	@Override
	public void handleCallback(RCallback callback) {
		/* TODO: this need to be handled asynchronously */
		if( callback instanceof RWriteConsoleCallback ){
			System.out.println( ((RWriteConsoleCallback)callback).getMessage() ) ;
		} else if( callback instanceof RShowMessageCallback ) {
			System.out.println( ((RShowMessageCallback)callback).getMessage() ) ;
		}
	}
	
}
