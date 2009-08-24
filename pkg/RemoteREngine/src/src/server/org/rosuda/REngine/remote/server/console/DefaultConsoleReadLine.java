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

/**
 * Default implementation for the readline. Uses only regular java 
 * classes 
 * 
 * @author Romain Francois
 */
public class DefaultConsoleReadLine implements ConsoleReadLine {

	private static final BufferedReader in = new BufferedReader(
			new InputStreamReader(System.in) );

	public DefaultConsoleReadLine(){
	}

	public String readLine() {
		String res = null ; 
		try{
			in.readLine() ;
		} catch( IOException e){
		}
		return res; 
	}

}
