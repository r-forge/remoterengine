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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.remote.common.RemoteREngineInterface;
import static org.rosuda.REngine.remote.common.RemoteREngineConstants.* ;

/**
 * This is an utility class to start the server
 */
public class REngineServer {

	/**
	 * Main method to start the R server
	 */
	public static void main(String[] args) {
		
		String rmiName = DEFAULTNAME;
		String rmiHost = RMIHOSTNAME;
		String rmiPort = RMIPORT;
		@SuppressWarnings("unused")
		String registeredName = DEFAULTNAME;
		
		if (args.length > 0) {
			if (args[0].startsWith("-h")) {
				printMenu();
				System.exit(0);
			}
			rmiName = args[0];
		} else {
			System.out.println("Use -help for options");
		}
		if (args.length > 1) rmiHost = args[1];
		if (args.length > 2) rmiPort = args[2];
		
	    if (System.getSecurityManager() == null) {
	        System.setSecurityManager(new SecurityManager());
	    }

	    RemoteREngineInterface engine = null;
	    try {
	    	engine = new RemoteREngine_Server();
	    } catch (REngineException e) {
	    	System.err.println(e.getClass().getName() +": While creating the R Engine, " + e.getMessage());
	    	e.printStackTrace();
	    }
        RemoteREngineInterface stub = null;
        Registry registry = null;
        try {
            stub = (RemoteREngineInterface) UnicastRemoteObject.exportObject(engine, 0);
        	registry = LocateRegistry.getRegistry(rmiHost, Integer.parseInt(rmiPort));
        } catch (NumberFormatException e) {
        	System.err.println("Unable to parse RMI port number from " + rmiPort);
        	printMenu();
        } catch (RemoteException e) {
        	System.err.println(e.getClass().getName() + " while trying to connect to the RMIRegistry on " + rmiHost + ":" +
        			rmiPort + ". Is the registry running?");
        	System.err.println(e.getMessage());
        	printMenu();
        }

    	try {
    		registry.rebind(rmiName, stub);	// registry is never null but may not be valid
	        System.out.println("R Engine bound as " + rmiName);
    	} catch (AccessException e) {
    		System.err.println("Access to RMI Registry denied, " + e.getMessage());
    		e.printStackTrace();
    	} catch (RemoteException e) {
    		System.err.println(e.getClass().getName() + ": while binding to the RMI registry on " + 
    				rmiHost + ":" + rmiPort + ", " + e.getMessage());
    		e.printStackTrace();
    		String[] names = new String[0];
    		try {
    			names = registry.list();
    		} catch (RemoteException re) {
    			if (names.length == 0) System.err.println("Is RMI Registry running on " + rmiHost + ":" + rmiPort + "?");
    		}
    		if (names.length > 0) {
	    		System.err.println("Existing bound services are:");
	    		for (String name : names) {
	    			System.err.println(name);
	    		}
    		} else {
    			System.err.println("No existing services located within RMI Registry");
    		}
    	}
    	// don't kill the process otherwise you kill the server
		BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("Type \"Quit\" to shutdown the server.");
			try {
				if ("Quit".equalsIgnoreCase(rdr.readLine())) {
					System.out.println("Unbinding " + rmiName);
					try {
						registry.unbind(rmiName);
					} catch (NotBoundException e) {
						// don't care
					} catch (RemoteException e) {
						System.err.println(e.getClass().getName() + ": " + e.getMessage() + ". While unbinding " + rmiName);
					}
					System.out.println("Stopping the JVM");
					System.exit(0);
				}
			} catch (IOException e) {
				System.out.println(e.getClass().getName() + ": " + e.getMessage());
			}
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Helper method to print out the expected arguments onto the console
	 */
	private static void printMenu() {
		System.out.println("Expected arguments are:");
		System.out.println("RMI name to register server under, e.g. RemoteREngine");
		System.out.println("RMI Registry hostname or IP address, e.g. localhost");
		System.out.println("RMI Registry port number, e.g. 1099");
	}
    
}
