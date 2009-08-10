package org.rosuda.REngine.remote.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Very simple command line argument utility
 * @author romain
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
				if( flag != null || i == args.length ){
					result.put( flag, "yes" ) ;
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
	
	private static String unflag( String flag ){
		return flag.replaceFirst( "^-+", "" ) ; 
	}
	
}
