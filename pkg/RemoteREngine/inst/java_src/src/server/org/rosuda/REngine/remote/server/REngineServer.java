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

package org.rosuda.REngine.remote.server ;

import static org.rosuda.REngine.remote.common.RemoteREngineConstants.DEFAULTNAME;
import static org.rosuda.REngine.remote.common.RemoteREngineConstants.RMIPORT;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.remote.common.CommandLineArgs;
import org.rosuda.REngine.remote.common.Launcher;
import org.rosuda.REngine.remote.common.RemoteREngineConstants;
import org.rosuda.REngine.remote.common.tools.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an utility class to start the server
 */
public class REngineServer {

	final Logger logger = LoggerFactory.getLogger(org.rosuda.REngine.remote.server.REngineServer.class);

	
	/**
	 * Main method to start the R server
	 */
	public static void main(String[] args) {
		// Set the logging configuration properties
		if (System.getProperty(RemoteREngineConstants.LOG4JCONFIGURATIONKEY) == null || System.getProperty(RemoteREngineConstants.LOG4JCONFIGURATIONKEY).equals("")) {
			System.setProperty(RemoteREngineConstants.LOG4JCONFIGURATIONKEY,RemoteREngineConstants.DEFAULTLOGCONFIGURATION);
		}
		Logger logger = LoggerFactory.getLogger(org.rosuda.REngine.remote.server.REngineServer.class);
		
		/* init the services */
		ServiceManager.init(); 
		
		String rmiName = DEFAULTNAME;
		int rmiPort = RMIPORT;
		int servicePort = RemoteREngineConstants.DEFAULTSERVERPORT;
		
		/* print the help if see the -h or --help flags */
		if (args.length > 0) {
			if ( args[0].startsWith("-h") || args[0].startsWith( "--help" )) {
				printMenu();
				System.exit(0);
			}
		}
		Map<String,String> arguments = CommandLineArgs.arguments(args) ;
		
		if( arguments.containsKey( "registryPort" )){
			try {
			rmiPort = Integer.parseInt( arguments.get("registryPort") )  ;
			} catch (NumberFormatException e) {
				System.err.println("Unable to parse " + arguments.get("registryPort") + " as an Integer");
				logger.error("Unable to parse {} as an Integer",arguments.get("registryPort"));
				printMenu();
			}
		}
		if( arguments.containsKey( "servicePort" )){
			try {
				servicePort = Integer.parseInt( arguments.get("servicePort") )  ;
			} catch (NumberFormatException e) {
				System.err.println("Unable to parse " + arguments.get("servicePort") + " as an Integer");
				logger.error("Unable to parse {} as an Integer",arguments.get("servicePort"));
				printMenu();
			}
		}
		if( arguments.containsKey( "name" )){
			rmiName = arguments.get("name") ;
		}
		if( arguments.containsKey( "debug" ) ){
			RemoteREngine_Server.setDebug( arguments.get( "debug" ).equals("yes") ) ;
		}
		
	    if (System.getSecurityManager() == null) {
	        System.setSecurityManager(new SecurityManager());
	    }

	    Registry registry = null ; 
	    RemoteREngine_Server engine = null;
	    try {
	    	logger.debug("About to start R Server using: rmiName {}; servicePort {}; rmiPort {}; args null",
	    			new Object[] {rmiName,servicePort,new Integer(rmiPort)});
	    	engine = new RemoteREngine_Server(rmiName, servicePort, rmiPort, arguments.get("init"), null);
	    } catch (REngineException e) {
	    	System.err.println(e.getClass().getName() +": While creating the R Engine, " + e.getMessage());
	    	logger.error(e.getClass().getName() + " while creating the R Engine",e);
	    	e.printStackTrace();
	    } catch (AccessException e) {
    		System.err.println("Access to RMI Registry denied, " + e.getMessage());
    		logger.error("Access to RMI Registry denied",e);
    		e.printStackTrace();
    	} catch (RemoteException e) {
    		/* TODO: factor rmi tools in a separate class */ 
    		System.err.println(e.getClass().getName() + ": while binding to the RMI registry on " + 
    				" port " + rmiPort + ", " + e.getMessage());
    		logger.error(e.getClass().getName() + ": while binding to the RMI registry on " + 
    				" port " + rmiPort,e);
    		e.printStackTrace();
    		String[] names = new String[0];
    		try {
    			registry = LocateRegistry.getRegistry( null, rmiPort ) ;
    			names = registry.list();
    		} catch (RemoteException re) {
    			if (names.length == 0) {
    				System.err.println("Is RMI Registry running on port " + rmiPort + "?");
    				logger.error("Is RMI Registry running on port {}?", rmiPort);
    			}
    		}
    		if (names.length > 0) {
	    		System.err.println("Existing bound services are:");
	    		logger.error("Existing bound services are:");
	    		for (String name : names) {
	    			System.err.println(name);
	    			logger.error(name);
	    		}
    		} else {
    			System.err.println("No existing services located within RMI Registry");
    			logger.error("No existing services located within RMI Registry");
    		}
    	}
    	
    	if (engine != null) {
    		engine.startConsoleThread();
    	} else {
    		System.err.println("Null RemoteREngine_Server returned from startup.");
    		logger.error("Null RemoteREngine_Server returned from startup.");
    		System.exit(1);
    	}
    	
	}
	
	/**
	 * Helper method to print out the expected arguments onto the console
	 */
	private static void printMenu() {
		System.out.println("Expected arguments are:");
		
		System.out.println("  [--name name]    		: RMI name to register server under");
		System.out.println("                     	default: '" + RemoteREngineConstants.DEFAULTNAME+ "' ");
		
		System.out.println("  [--servicePort port]	: Service port number");
		System.out.println("                     	default: '" + RemoteREngineConstants.DEFAULTSERVERPORT + "' ");
		
		System.out.println("  [--registryPort port] : RMI Registry port number");
		System.out.println("                     	default: '" + RemoteREngineConstants.RMIPORT + "' ");
		
		System.out.println("  [--debug]    			: Print out additional debug information");
		System.out.println("                     	default: 'No' ");
		
		System.out.println("  [--init]    			: R server init script");
		System.out.println("                     	default: '' (no init script) ");

	}
	
    
}
