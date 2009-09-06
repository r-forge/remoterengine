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

import org.rosuda.REngine.remote.common.console.CommandSender;

/**
 * Callback sent when another client has sent a command 
 * 
 * @author Romain Francois
 *
 */
public class InputCallback extends RestrictedCallback {

	private static final long serialVersionUID = 1L;

	protected String command ;
	
	protected transient CommandSender sender; 
	
	public InputCallback( String command, CommandSender sender){
		this.command = command; 
		this.sender = sender; 
	}
	
	public String getCommand(){
		return command; 
	}
	
	public CommandSender getSender(){
		return sender; 
	}
	
}
