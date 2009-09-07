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
package org.rosuda.REngine.remote.common.history;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Stack;

/**
 * Remote interface for dealing with the history of commands typed in the 
 * server's real eval print loop
 * 
 * @author Romain Francois
 *
 */
public interface CommandHistory extends Remote {
	
	/**
	 * Get the entire stack of commands from the command history
	 * @return the stack of commands
	 * @throws RemoteException 
	 */
	public Stack<String> getCommandHistory() throws RemoteException ; 
	
	/**
	 * The stack of commands that are accepted by the matcher
	 * @param matcher command matcher
	 * @return the set of commands that are accepted by the matcher
	 * @throws RemoteException
	 */
	public Stack<String> getMatchingCommandHistory(CommandMatcher matcher) throws RemoteException ;
	
	/**
	 * Adds a command to the command history
	 * 
	 * @param command
	 * @throws RemoteException
	 */
	public void add( String command ) throws RemoteException ; 
	
	/**
	 * Saves the history into the server-side file
	 * @param filename name of the file (on the server-side)
	 * @throws RemoteException
	 */
	public void saveAs( String filename ) throws RemoteException ;
	
	/**
	 * Load a server side file into the command history
	 * @param filename name of a file in the server
	 * @throws RemoteException
	 */
	public void load( String filename ) throws RemoteException ;
}
