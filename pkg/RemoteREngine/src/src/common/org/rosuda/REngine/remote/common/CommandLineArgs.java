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

package org.rosuda.REngine.remote.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Very simple command line argument utility
 * 
 * @author Romain Francois <francoisromain@free.fr>
 */
public class CommandLineArgs {

	/**
	 * Turns args into a map (key/value) or the command line arguments. 
	 * The expected format is :
	 * --key value
	 * This sequence: --key1 --key2 value will create the map
	 * (key1->yes, key2->value)
	 * A key may be used only once, ie these arguments: 
	 * --key value --key value2
	 * will result in this mapping: 
	 * (key->value)
	 * 
	 * @param args command line arguments
	 * @return map (key,value) of the arguments
	 */
	public static Map<String,String> arguments(String[] args){
		HashMap<String,String> result = new HashMap<String,String>();
		
		String flag = null; 
		for( int i=0; i<args.length; i++){
			
			String arg = args[i] ;
			if( arg.startsWith("-") ){
				if( flag != null ) {
					result.put( flag, "yes" ) ;
				} else if( i == ( args.length -1) ){
					result.put( unflag( args[i] ) , "yes" ) ;
				}
				flag = unflag( arg ) ;
			} else {
				if( !result.containsKey(flag)){
					result.put( flag, arg) ;
				}
				flag = null; 
			}
		}
		
		return result; 
	}
	
	/**
	 * Removes the leading "-" from the string
	 * 
	 * @param flag a command line flag, i.e. --flag
	 *  
	 * @return the flag without its leading "-" signs
	 */
	private static String unflag( String flag ){
		return flag.replaceFirst( "^-+", "" ) ; 
	}
	
}
