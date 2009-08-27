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

import org.rosuda.REngine.remote.common.console.ConsoleReadLine;
import org.rosuda.REngine.remote.common.tools.ServiceException;
import org.rosuda.REngine.remote.common.tools.ServiceManager;
import org.rosuda.REngine.remote.server.RemoteREngine_Server;

/**
 * The console thread, that allows control of the server on the server side 
 * 
 * @author Romain Francois
 *
 */
public class ConsoleThread extends Thread {

	/**
	 * The server associated with this thread
	 */
	private RemoteREngine_Server server ; 

	/**
	 * Should the thread stop
	 */
	private boolean stop; 

	/**
	 * the reader used to get lines 
	 */
	private ConsoleReadLine reader ; 

	/**
	 * The constructor. 
	 * @param server R engine associated with this console 
	 */
	public ConsoleThread( RemoteREngine_Server server ){
		/* TODO: add the name of the consolereadline service as a parameter */ 
		this.server = server ; 
		stop = false; 
		try {
			reader = ServiceManager.getInstance(ConsoleReadLine.class, "default") ;
		} catch (ServiceException e) {	e.printStackTrace() ; }
	}
	
	/**
	 * Prompts continually 
	 */
	public void run(){
		while (!stop) {
			try {
				sleep(100); 
			} catch (InterruptedException e1) {
				stop = true;
				break; 
			}
			if( stop ){
				break; 
			}
			String line = reader.readLine() ;
			if ("Quit".equalsIgnoreCase(line)) {
				stop = true; /* stop this thread */
				server.shutdown() ; /* let clients know */
			} else {
				server.getConsoleSync().add( line ) ;
			}

		}
		System.out.println( "console thread stopped" ) ;
	}

	/**
	 * Request that this thread stops
	 */
	public void requestStop(){
		stop = true ;
	}

}
