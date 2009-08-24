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

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class ConsoleReadLineFactory {

	private static HashMap<String,Class<? extends ConsoleReadLine>> implementations ;
	static{
		implementations = new HashMap<String,Class<? extends ConsoleReadLine>>(); 
		implementations.put ("default", DefaultConsoleReadLine.class ) ;
	}

	public static ConsoleReadLine getConsoleReadLine( String name ){

		Class<?extends ConsoleReadLine> clazz = implementations.get(name) ;
		Constructor<?extends ConsoleReadLine> cons = null; 
		try{
			cons = clazz.getConstructor( (Class[])null ) ;
		} catch( NoSuchMethodException nsm){}
		Object o = null ; 
		try{
			o =  cons.newInstance( (Object[]) null ) ;	
		} catch( Exception e ){
			/* none should happen */
		}
		return (ConsoleReadLine)o; 
	}

}
