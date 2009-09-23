/*
 * Copyright (c) 2009, Ian Long <ilong@stoatsoftware.com>
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

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.rosuda.REngine.remote.common.RemoteREngineConstants;
import org.rosuda.REngine.remote.common.RemoteREngineInterface;

/**
 * @author $Author$
 * @version $Rev$ as of $Date$
 * <p>URL : $HeadURL$
 */
public class ServerShutDown extends Thread {
	/** Name of the server within the RMI Registry */
	private String serverName;
	/** Hostname or IP Address of the RMI Registry */
	private String registryHost;
	/** Port number of the RMI Registry */
	private int registryPort;
		
	/**
	 * Create an instance of the ServerShutDown
	 * @param serverName Name of the RMI Service to be shut down
	 * @param registryHost Host name or IP address of the RMI Registry
	 * @param registryPort Port number of the RMI Registy
	 */
	private ServerShutDown(String serverName, String registryHost, int registryPort) {
		super();
		this.serverName =  serverName;
		this.registryHost = registryHost;
		this.registryPort = registryPort;
	}
	
	/**
	 * Try and shut down the server by connecting to the RMI Registry and getting a stub to the server.
	 * Try and call shutdown() against the RMI stub returned
	 */
	public void run() {
		try {
			Registry reg = LocateRegistry.getRegistry(registryHost, registryPort);
			RemoteREngineInterface engine = (RemoteREngineInterface) reg.lookup(serverName);
			if (engine != null) engine.shutdown();
		} catch (NotBoundException nb) {
			System.err.println("Unable to locate " + serverName + " bound within " + registryHost + ":" + registryPort );
		} catch (RemoteException e) {
			// Expect to get a RemoteException here because if the method has succeeded, then
			// it can't return! 
		}
		return;
	}

	/**
	 * Method to provide a clean shutdown for a remote R server. Attempts to locate the server
	 * based on the RMI Registry information provided and then calls shutdown() against it.
	 * @param serverName Name of the service to be shut down
	 * @param registryHost Hostname or IP address for the RMI Registry publishing the service
	 * @param registryPort IP port for the RMI Registry publishing the service
	 * @return true if server has been located and successfully shut down
	 */
	public static void shutDown(String serverName, String registryHost, int registryPort) {
		ServerShutDown shutDown = new ServerShutDown(serverName, registryHost, registryPort);
		Thread shutDownThread = new Thread(shutDown);
		shutDownThread.start();
	}
	
	/**
	 * Calling class to assist with the shutdown of the server. Attempts to connect to the server
	 * and calls shutdown on it. All connected clients will be disconnected by this process. After starting 
	 * the shutdown process this method waits for 5 seconds before shutting down it's virtual machine.
	 * @param args Takes 3 optional arguments ServiceName, RMI Registry Hostname, RMI Registry Portnumber
	 */
	public static void main (String[] args) {
		String serviceName = RemoteREngineConstants.DEFAULTNAME;
		String host = RemoteREngineConstants.RMIHOSTNAME;
		int port = RemoteREngineConstants.DEFAULTSERVERPORT;
		
		if (args.length > 0) serviceName = args[0];
		if (args.length > 1) host = args[1];
		if (args.length > 2) {
			try {
				port = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.err.println("Unable to parse " + args[2] + " as a port number");
			}
		}
		System.out.println("Attempting to shutdown " + serviceName + " registered with " + host + ":" + port);
		ServerShutDown.shutDown(serviceName, host, port);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		System.exit(0);
	}
}
