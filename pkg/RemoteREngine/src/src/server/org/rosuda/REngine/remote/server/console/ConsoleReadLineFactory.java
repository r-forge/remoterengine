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
