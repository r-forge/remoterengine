package org.rosuda.REngine.remote.client.test;

import org.rosuda.REngine.remote.client.RemoteREngine;

public class TestEnvironment {
	
	public static RemoteREngine r = init() ;

	private static RemoteREngine init(){
		RemoteREngine r = null ;
		try{
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
			r = new RemoteREngine( "test" , "localhost" , 1099 );
		} catch( Exception e ){
			System.err.println( "cannot create R engine" ) ;
			System.exit(1); 
		}
		return r; 
	}

}
