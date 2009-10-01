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

import org.rosuda.REngine.remote.common.console.Command;
import org.rosuda.REngine.remote.common.console.ConsoleReadLine;
import org.rosuda.REngine.remote.common.tools.ServiceException;
import org.rosuda.REngine.remote.common.tools.ServiceManager;
import org.rosuda.REngine.remote.common.tools.StoppableThread;
import org.rosuda.REngine.remote.server.RemoteREngine_Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The console thread, that allows control of the server on the server side 
 * 
 * @author Romain Francois
 *
 */
public class ConsoleThread extends StoppableThread {
	private final Logger logger = LoggerFactory.getLogger(org.rosuda.REngine.remote.server.console.ConsoleThread.class);

	/**
	 * The server associated with this thread
	 */
	private RemoteREngine_Server server ; 

	/**
	 * the reader used to get lines 
	 */
	private ConsoleReadLine reader ; 

	/**
	 * The constructor. 
	 * @param server R engine associated with this console 
	 */
	public ConsoleThread( RemoteREngine_Server server ){
		super( "console thread" ) ;
		/* TODO: add the name of the consolereadline service as a parameter */ 
		this.server = server ; 
		try {
			reader = ServiceManager.getInstance(ConsoleReadLine.class, "default") ;
		} catch (ServiceException e) {	e.printStackTrace() ; }
	}

	/**
	 * Prompts continually 
	 */
	@Override
	public void loop(){
		logger.debug("Awaiting console input");
		String line = reader.readLine() ;
		logger.debug("Received '{}'",line);
		if ("Quit".equalsIgnoreCase(line)) {
			logger.info("Processing Quit");
			requestStop(); 
			server.shutdown() ; /* let clients know */
		} else {
			Command command =  new Command( line, new ServerConsoleSender(this) )  ;
			server.getConsoleSync().add( command ) ;
		}
	}

	@Override
	public void end() {
		logger.debug( "console thread stopped" ) ;
	}

}
