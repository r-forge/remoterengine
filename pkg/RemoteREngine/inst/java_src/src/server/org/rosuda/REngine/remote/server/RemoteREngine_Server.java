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

package org.rosuda.REngine.remote.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;

import org.rosuda.JRI.Rengine;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPReference;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.REngineRegistry;
import org.rosuda.REngine.JRI.JRIEngine;
import org.rosuda.REngine.remote.common.JRIEngineGlobalVariables;
import org.rosuda.REngine.remote.common.RemoteREngineClient;
import org.rosuda.REngine.remote.common.RemoteREngineInterface;
import org.rosuda.REngine.remote.common.callbacks.CallbackListener;
import org.rosuda.REngine.remote.common.callbacks.CallbackResponse;
import org.rosuda.REngine.remote.common.callbacks.RCallback;
import org.rosuda.REngine.remote.common.callbacks.RCallbackWithResponse;
import org.rosuda.REngine.remote.common.callbacks.ServerDownCallback;
import org.rosuda.REngine.remote.common.console.Command;
import org.rosuda.REngine.remote.common.console.RemoteREngineClientSender;
import org.rosuda.REngine.remote.common.exceptions.AlreadyRegisteredException;
import org.rosuda.REngine.remote.common.exceptions.FileAlreadyExistsException;
import org.rosuda.REngine.remote.common.exceptions.NotRegisteredException;
import org.rosuda.REngine.remote.common.exceptions.ServerSideIOException;
import org.rosuda.REngine.remote.common.files.RemoteFileInputStream;
import org.rosuda.REngine.remote.common.files.RemoteFileOutputStream;
import org.rosuda.REngine.remote.common.utils.FileParser;
import org.rosuda.REngine.remote.common.utils.PIDReporter;
import org.rosuda.REngine.remote.common.utils.ThreadLogger;
import org.rosuda.REngine.remote.server.callbacks.CallbackSender;
import org.rosuda.REngine.remote.server.callbacks.ClientCallbackListener;
import org.rosuda.REngine.remote.server.console.ConsoleCallbackHandler;
import org.rosuda.REngine.remote.server.console.ConsoleSync;
import org.rosuda.REngine.remote.server.console.ConsoleThread;
import org.rosuda.REngine.remote.server.files.RemoteFileInputStream_Server;
import org.rosuda.REngine.remote.server.files.RemoteFileOutputStream_Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class of the server side part of the RemoteREngine project 
 *
 * @author Romain Francois
 *
 */
public class RemoteREngine_Server implements RemoteREngineInterface {

	private final Logger logger = LoggerFactory.getLogger(org.rosuda.REngine.remote.server.RemoteREngine_Server.class);
	
	private static boolean DEBUG = false; 
	
	public static void setDebug( boolean debug){
		DEBUG = debug; 
	}

	private Vector<RemoteREngineClient> clients ;
	private Vector<CallbackListener> callbackListeners ; 
	private CallbackSender callbackSender ; 
	
	/** 
	 * The local R engine this server is shadowing
	 */ 
	private JRIEngine r ; 

	/**
	 * Captures some of the global variables that are sent to the clients
	 * when they subscribe
	 */
	private JRIEngineGlobalVariables variables ;
	
	/**
	 * Shutdown hook
	 */
	private RemoteREngineServerShutdownHook shutdownHook; 
	
	/**
	 * Name of this object in the RMI registry
	 */
	private String name; 
	
	/**
	 * Port of the RMI registry
	 */
	private int registryPort ;
	
	/**
	 * The console thread associated with this engine
	 */
	private ConsoleThread consoleThread ; 
	
	private ConsoleCallbackHandler consoleCallbackHandler; 
	
	
	private Registry registry ; 
	private RemoteREngineInterface stub = null;
	
	private boolean running = false; 
	
	private ConsoleSync consoleSync ;
	
	
	/**
	 * The callback loop
	 */
	private RemoteRMainLoopCallbacks callbackLoop; 
	
	/**
	 * Constructor. Initiates the local R engine that this engine shadows
	 * 
	 * @param name name of this engine in the RMI registry
	 * @param servicePort Port number for this service to use for recieving requests
	 * @param registryPort port used by the RMI registry
	 * @param startUpOptions the client start up options
	 * @param args arguments for R 
	 * 
	 * @throws REngineException Error creating the R instance
	 */ 
	public RemoteREngine_Server(String name, int servicePort, int registryPort, String initScript, String[] args) throws REngineException, RemoteException, AccessException {
		super();
		this.name = name ; 
		this.registryPort = registryPort ;
		
		/* inform the clients that the jvm of the server is dying */
		shutdownHook = new RemoteREngineServerShutdownHook() ;
		logger.debug("Adding shutdownhook for R server process");
		Runtime.getRuntime().addShutdownHook( shutdownHook ); 
		
		clients = new Vector<RemoteREngineClient>(); 
		callbackListeners = new Vector<CallbackListener>(); 
		callbackLoop = new RemoteRMainLoopCallbacks(this) ;
		consoleSync = new ConsoleSync(this) ;
		
		consoleThread = new ConsoleThread(this);
		consoleCallbackHandler = new ConsoleCallbackHandler(this);
		callbackSender = new CallbackSender(this) ;
		addCallbackListener( consoleCallbackHandler ) ;

		logger.debug("About to construct JRIEngine");
		r = new JRIEngine( args, callbackLoop ) ;

		// Execute R prepare script before R server is available via RMI
		runInitScript(r, initScript);
		
		/* TODO: forbid the q function */
		
		/* capture global variables of the JRIEngine */
		variables = new JRIEngineGlobalVariables( 
				getPointer( r.globalEnv ), 
				getPointer( r.emptyEnv ) , 
				getPointer( r.baseEnv ), 
				getPointer( r.nullValueRef) , 
				REngineRegistry.getId(r) ) ;
		
		try {
			// Locate a local registry as RMI Servers can't register with Remote Registries
			registry = LocateRegistry.getRegistry(null, registryPort);
		} catch (RemoteException r) {
			// Unable to locate the registry, so try and create one
			logger.error("Unable to locate registry, so creating a new RMI Registry locally",r);
		}
		try {
			if (registry==null) registry = LocateRegistry.createRegistry(registryPort);
		} catch (RemoteException e) {
			logger.error(e.getClass().getName() + ": While trying to create registry on port " + registryPort,e);
			throw e;
		}

		try {
			stub = (RemoteREngineInterface)UnicastRemoteObject.exportObject(this,servicePort);
		} catch (RemoteException e) {
			logger.error("Unable to serialize server on " + servicePort + ": " + e.getMessage(),e);
			throw e;
		}

		try {
			registry.bind(name, stub);
		} catch (AlreadyBoundException e) {
//			debug(name + " already bound, attempting to shut down previous server");
//			logger.info(name + " already bound, attempting to shut down previous server");
//			
//			try {
//				RemoteREngineInterface previousServer = (RemoteREngineInterface)registry.lookup(name);
//				previousServer.shutdown();
//			} catch (RemoteException re) {
//				System.err.println(re.getClass().getName() + " while trying to shut down previous server");
//			} catch (NotBoundException nbe) { 
//				// Do nothing - it has just unbound!	
//			} 

			try {
				// Note, by automatically rebinding we risk orphaning the previous server and leaving
				// process leaks
				logger.info("Trying to rebind {}",name);
				registry.rebind(name, stub);
			} catch (AccessException ae) {
				logger.error("AccessException while rebinding server to registry: " + ae.getMessage(),ae);
				throw ae;
			}catch (RemoteException re) {
				logger.error("Unable to rebind server to port " + servicePort + ": " + re.getMessage(),re);
				throw re;
			}
		} catch (RemoteException e) {
			logger.error("Unable to bind server(" + name + ") to port " + servicePort + ": " + e.getMessage(),e);
			throw e;
		}

		String msg = "R Engine bound as `"+ name +"` as a service on port " + servicePort + 
		" to local RMIRegistry running on port " + registryPort + ", running under Id: " + PIDReporter.getPID();
		System.out.println( msg );
		logger.info(msg);
		running = true; 
	}

	/**
	 * Run the initialisation script.
	 * 
	 * <p>
	 * If the script path is not provided or empty, return without action.
	 * 
	 * @param r the JRI engine
	 * @param initScript the path to the initial script
	 * @throws REngineException
	 * @throws RemoteException
	 * @throws AccessException
	 */
	private void runInitScript(JRIEngine r, String initScript) throws REngineException, RemoteException, AccessException{
		
		// if no init script provided
		if (initScript == null || initScript.trim().length() == 0){
			return;
		}
		
		logger.info("REngine execute init script '"+initScript+"' ...");
		
		// read the whole content of the init script
		String[] scriptCommands = (new FileParser()).readLines(initScript);
		
		// parse and evaluate the script
		try {
			for (String command : scriptCommands) {
				r.parseAndEval(command);
			}
		} catch (REngineException e) {
			logger.error("Exception running initialisation script: " + e.getMessage(),e);
			throw e;
		} catch (REXPMismatchException e) {
			logger.error("Unable to run init script '"+initScript+"', "+e.getMessage(),e);
			throw new REngineException(REngine.getLastEngine(), "Unable to run init script '"+initScript+"', "+e.getMessage());
		}
	}
	

	/** 
	 * utility to extract the long pointer from a reference. This is only used when making the variables 
	 * object because it needs to transfer to the client before the client can resolve references
	 */
	private long getPointer( REXPReference ref){
		return ( (Long)ref.getHandle() ).longValue() ;
	}
	
	
	/**
	 * Constructor initializing R with the default arguments - service will run on a randomly assigned port
	 * 
	 * @param name name of this engine in the MRI registry
 	 * @param port port used by the RMI registry
 	 * 
	 * @throws REngineException
	 * @throws RemoteException
	 * @throws AccessException
	 */
	public RemoteREngine_Server(String name, int port) throws REngineException, RemoteException, AccessException{
		this( name, 0, port, null, null) ;
	}
	
	/**
	 * Constructor initializing R with the default arguments - service will run on a randomly assigned port
	 * 
	 * @param name name of this engine in the MRI registry
	 * @param servicePort Port used by the service to receive requests
 	 * @param registryPort port used by the RMI registry
 	 * 
	 * @throws REngineException
	 * @throws RemoteException
	 * @throws AccessException
	 */

	public RemoteREngine_Server(String name, int servicePort, int registryPort) throws REngineException, RemoteException, AccessException{
		this( name, servicePort, registryPort, null, null) ;
	}
	/**
	 * Shutdown hook that indicates to clients of this server 
	 * that the server is dying 
	 * 
	 * @author Romain Francois
	 */
	private class RemoteREngineServerShutdownHook extends Thread{
		public void run(){
			if( !running ) {
				logger.info("Calling shutdown");
				shutdown() ;
			}
		}
	}
	
	/**
	 * Shutdown the server
	 */
	public synchronized void shutdown(){
		logger.info( "R Server: shutdown" ) ;
		ThreadLogger threadLogger = new ThreadLogger();
		threadLogger.logAllThreads("About to shutdown");
		
		if( !running ) return; 
		running = false; 
		
		// TODO implement a version of consoleThread that won't block on input
		if (consoleThread != null) consoleThread.requestStop() ;
		if (consoleThread != null) consoleThread.interrupt() ;
		
		if( !clients.isEmpty() ){
			ServerDownCallback dying = new ServerDownCallback() ;
			sendCallbackToListeners(dying) ;
		}
		/* TODO: empty the clients and listeners ? */
		
		logger.info("Unbinding " + name );
		try {
			if (registry != null) {
				registry.unbind( name );
				logger.debug(name + " unbound from registry ");
			}
			
			if (stub != null) {
				if (UnicastRemoteObject.unexportObject(stub, false)) {
					logger.debug(name + " successfully unexported");
				} else {
					logger.debug("Unable to Unexport " + name);
				}
			}
		} catch (NotBoundException e) {
			// don't care
		} catch (NoSuchObjectException e) {
			// don't care about this either - we are just trying to clean up
		} catch (RemoteException e) {
			StringBuffer buf = new StringBuffer(e.getClass().getName() + ": " + e.getMessage() + ". While unbinding " + name);
			Throwable cause = e.getCause();
			if (cause != null) {
				buf.append(cause.getClass().getName() + ": " + cause.getMessage());
			}
			logger.error( buf.toString(),e);
		}
		System.out.println("Stopping R");
		logger.info("Stopping R");
		/* : shutdown R cleanly as well */
		try {
			RTermination rterminator = new RTermination();
			logger.debug("Start R termination thread");
			rterminator.start();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + " while closing R session; " + e.getMessage());
		}
		System.out.println("\n" + Calendar.getInstance().getTime() + ": Stopping the JVM in 3 seconds.");
		int seconds = 3000;
		for (int i=0; i < seconds; i+=100) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			System.out.print(".");
		}
		threadLogger.logAllThreads("Finished shut down");
		System.out.println("\nCalling System.exit(0)");
		logger.info("Calling System.exit(0)");
		System.exit(0);
	}


	/**
	 * Thread to shut down the R process underneath this engine.
	 */
	private class RTermination extends Thread{
		public void run(){
			try {
				logger.debug("Terminating R");
				Rengine theEngine = r.getRni();
				if (theEngine != null && theEngine.isAlive()) theEngine.end();
				logger.debug("R Terminated");
			} catch (Exception e) {
				logger.error(e.getClass().getName() + " while closing R session; " + e.getMessage(),e);
			}
			return;
		}
	}

	
	/**
	 * @return the name of this engine in the RMI registry
	 */
	public String getName(){
		return name; 
	}
	
	/**
	 * @return the port on which the RMI registry runs
	 */
	public int getPort(){
		return registryPort ; 
	}
	
	/**                                                    
	 * parse a string into an expression vector         
	 *
	 * @param text string to parse
	 * @param resolve resolve the resulting REXP (<code>true</code>) or just return a reference (<code>false</code>)
	 * @return parsed expression 
	 */
	public REXP parse(String text, boolean resolve) throws REngineException {
		debug( ">> parse '" + text + "'" ) ;
		try {
			REXP rexp =  r.parse( text, resolve ) ;
			return rexp;
		} catch (REngineException e) {
			logger.error("Parsing '" + text + "'",e);
			throw e;
		}
	}

	/** 
	 * evaluate an expression vector
	 * @param what an expression (or vector of such) to evaluate
	 * @param where environment to evaluate in (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine)
	 * @param resolve resolve the resulting REXP or just return a reference
	 * @return the result of the evaluation of the last expression 
	 */
	public REXP eval(REXP what, REXP where, boolean resolve) throws REngineException, REXPMismatchException{
		if (what.isString()) {
			logger.debug("eval {}",what.toString());
		} else {
			debug( ">> eval" ) ;
		}
		try {
			REXP rexp =  r.eval( what, where, resolve ); 
			return rexp;
		} catch (REngineException e) {
			logger.error("eval '" + (what.isString() ? what.toString() : what) + "'",e);
			throw e;
		}
	}

	/**
	 * assign into an environment
	 *
	 * @param symbol symbol name
	 * @param value value to assign
	 * @param env environment to assign to (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine
	 */
	public void assign(String symbol, REXP value, REXP env) throws REngineException, REXPMismatchException{ 
		debug( ">> assign(" + symbol + ")") ;
		logger.debug("assign {}",symbol);
		try {
			r.assign( symbol, value, env );
		} catch (REngineException e) {
			logger.error("Assign " + symbol,e);
			throw e;
		} catch (REXPMismatchException e) {
			logger.error("Assign " + symbol,e);
			throw e;
		}
	}


	/**
	 * get a value from an environment
	 * @param symbol symbol name
	 * @param env environment (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine)
	 * @param resolve resolve the resulting REXP or just return a reference		
	 * @return value
	 */
	public REXP get(String symbol, REXP env, boolean resolve) throws REngineException, REXPMismatchException {
		debug( ">> get '" + symbol + "'" ) ;
		try {
			REXP rexp = r.get( symbol, env, resolve );
			return rexp;
		} catch (REngineException e) {
			logger.error("Get " + symbol,e);
			throw e;
		} catch (REXPMismatchException e) {
			logger.error("Get " + symbol,e);
			throw e;
		}
	}

	/** 
	 * fetch the contents of the given reference. 
	 * The resulting REXP may never be REXPReference. 
	 * 
	 * @param ref reference to resolve
	 * @return resolved reference
	 * @throws REngineException when references are not supported
	 */
	public REXP resolveReference(REXP ref) throws REngineException, REXPMismatchException{ 
		debug( ">> resolveReference" + (ref.isString() ? ref.toString() : "")) ;
		try {
			return r.resolveReference( ref ); 
		} catch (REngineException e) {
			logger.error("resolveReference " ,e);
			throw e;
		} catch (REXPMismatchException e) {
			logger.error("resolveReference" ,e);
			throw e;
		}
	}

	/** 
	 * create a reference by pushing local data to R and returning a reference to the data. 
	 * If <code>value</code> is a reference it is returned as-is. 
	 * 
	 * @param value to create reference to
	 * @return reference to the value
	 * @throws REngineException if references are not supported
	 */
	public REXP createReference(REXP value) throws REngineException, REXPMismatchException{
		debug( ">> createReference" + (value.isString() ? " '" + value.toString() + "'" : "")) ;
		try {
			return r.createReference( value ); 
		} catch (REngineException e) {
			logger.error("createReference " ,e);
			throw e;
		} catch (REXPMismatchException e) {
			logger.error("createReference" ,e);
			throw e;
		}
	}

	/** 
	 * removes reference from the R side. This method is called automatically by the finalizer 
	 * of <code>REXPReference</code> and should never be called directly.
	 *
	 * @param ref reference to finalize 
	 */
	public void finalizeReference(REXP ref) throws REngineException, REXPMismatchException{ 
		debug( ">> finalizeReference" + (ref.isString() ? "'" + ref.toString() + "'" : "") ) ;
		try {
			r.finalizeReference( ref ); 
		} catch (REngineException e) {
			logger.error("finalizeReference " ,e);
			throw e;
		} catch (REXPMismatchException e) {
			logger.error("finalizeReference" ,e);
			throw e;
		}
	}

	/**
	 * get the parent environment of an environment
	 *
	 * @param env environment to query
	 * @param resolve whether to resolve the resulting environment reference
	 * @return parent environment of env
	 */
	public REXP getParentEnvironment(REXP env, boolean resolve) throws REngineException, REXPMismatchException {
		debug( ">> getParentEnvironment" ) ;
		try {
			return r.getParentEnvironment( env, resolve ); 
		} catch (REngineException e) {
			logger.error("getParentEnvironment " ,e);
			throw e;
		} catch (REXPMismatchException e) {
			logger.error("getParentEnvironment" ,e);
			throw e;
		}
	}

	/**
	 * create a new environment
	 *
	 * @param parent parent environment
	 * @param resolve whether to resolve the reference to the environemnt (usually <code>false</code> since the returned environment will be empty)
	 * @return resulting environment
	 */
	public REXP newEnvironment(REXP parent, boolean resolve) throws REngineException, REXPMismatchException{ 
		debug( ">> newEnvironment" ) ;
		try {
			return r.newEnvironment( parent, resolve ) ; 
		} catch (REngineException e) {
			logger.error("newEnvironment " ,e);
			throw e;
		} catch (REXPMismatchException e) {
			logger.error("newEnvironment" ,e);
			throw e;
		}
	}

	/**
	 * Parse and eval text
	 */
	public REXP parseAndEval(String text, REXP where, boolean resolve) throws REngineException, REXPMismatchException {
		debug( ">> parseAndEval(" + text + ")" ) ;
		REXP result = null;
		try {
			result = r.parseAndEval( text, where, resolve );
		} catch (REngineException e) {
			logger.error(e.getClass().getName() + ": " + e.getMessage() + " while processing " + text,e);
			throw e;
		}
		return result;
	}


	/**
	 * open a stream to read a file from the server
	 * @param filename file name on the server to read from 
	 * 
	 * @return the stream used to read the file
	 * @throws RemoteException 
	 * @throws IOException when the stream cannot be create
	 */
	public RemoteFileInputStream openFile( String filename) throws ServerSideIOException, RemoteException{
		try {
			RemoteFileInputStream_Server stream = new RemoteFileInputStream_Server( filename ) ;
			RemoteFileInputStream stub = (RemoteFileInputStream) UnicastRemoteObject.exportObject(stream);
	    	return stub ; 
		} catch (ServerSideIOException e) {
			logger.error("ServerSideIOException",e);
			throw e;
		} catch (RemoteException e) {
			logger.error("RemoteException",e);
			throw e;
		}
	}

	/**
	 * Opens a stream to write a file into the server
	 * @param filename filename in which to write 
	 * 
	 * @return the stream to write into 
	 * @throws RemoteException 
	 * @throws FileAlreadyExistsException
	 * @throws IOException when the stream cannot be created 
	 */
	public RemoteFileOutputStream createFile( String filename, boolean must_be_new) throws ServerSideIOException, FileAlreadyExistsException, RemoteException{
		File targetFile = null;
		try {
			if( must_be_new ){
				targetFile = new File(filename);
				if( targetFile.exists() ){
					throw new FileAlreadyExistsException( filename ) ;
				}
			}
			RemoteFileOutputStream_Server stream = new RemoteFileOutputStream_Server( filename ) ;
			RemoteFileOutputStream stub = (RemoteFileOutputStream)UnicastRemoteObject.exportObject( stream ) ;
			return stub ;
		} catch (FileAlreadyExistsException e) {
			logger.error("FileAlreadyExistsException: " + 
					(targetFile != null ? targetFile.getAbsolutePath() : filename),e);
			throw e;
		} catch (ServerSideIOException e) {
			logger.error("ServerSideIOException",e);
			throw e;
		} catch (RemoteException e) {
			logger.error("RemoteException",e);
			throw e;
		}
	}
	
	/**
	 * Called when a client wants to send a command to the REPL
	 * 
	 * @param cmd command to send to the REPL
	 * @param origin the client that sent the command
	 * 
	 * @throws RemoteException
	 */
	public void sendToConsole( String cmd, RemoteREngineClient origin ){
		logger.debug(cmd);
		consoleSync.add( new Command( cmd, new RemoteREngineClientSender(origin) ) );
	}
		
	private void debug( String message){
		if( DEBUG ){
			System.err.println( message ); 
		}
		if (logger.isDebugEnabled()) logger.debug(message);
	}

	/**
	 * Register a client with this server
	 * 
	 * @throws AlreadyRegisteredException if the client is already registered with this server
	 */
	public synchronized JRIEngineGlobalVariables subscribe(RemoteREngineClient client) throws RemoteException, AlreadyRegisteredException {
		debug( "registering client" ) ;
		if( clients.contains( client ) ){
			logger.error("Client already exists");
			throw new AlreadyRegisteredException(); 
		}
		clients.add( client ) ;
		addCallbackListener( new ClientCallbackListener( client ) ) ;
		return variables ; 
	}
	
	/**
	 * Adds a callback listener
	 * @param listener the callback listener
	 */
	public synchronized void addCallbackListener( CallbackListener listener) {
		logger.debug("Adding CallbackListener");
		callbackListeners.add( listener ) ;
	}
	
	/**
	 * Removes a callback listener
	 * @param listener the callback listener to remove
	 */
	public synchronized void removeCallbackListener( CallbackListener listener) {
		logger.debug("Removing CallbackListener");
		callbackListeners.remove( listener ) ;
	}

	/**
	 * Unsubscribe a client
	 * 
	 * @throws NotRegisteredException if the client is not registered with this server
	 */
	public synchronized void close(RemoteREngineClient client) throws RemoteException, NotRegisteredException {
		debug( "unregister client" ) ;
		if( !clients.contains( client) ){
			logger.error("Client wasn't registered");
			throw new NotRegisteredException() ; 
		}
		clients.remove( client ) ;
		removeCallbackListener( ClientCallbackListener.popCallbackListener(client) ) ;
	}

	/**
	 * Sends a response to a callback
	 */
	public void sendResponse( CallbackResponse<? extends RCallbackWithResponse> response ) throws RemoteException {
		logger.debug("Sending callback response");
		callbackLoop.addResponse(response) ;
	}
	
	/**
	 * Send a callback to all the listeners associated with this server
	 * 
	 * @param callback the callback
	 */
	public void sendCallbackToListeners(RCallback callback){
		callbackSender.addToQueue( callback ) ;
	}
	
	/** 
	 * start the console thread
	 */
	public void startConsoleThread() {
		consoleThread.start(); 
		consoleCallbackHandler.start(); 
		callbackSender.start(); 
		logger.debug("Starting console thread");
	}

	/**
	 * convenience that calls rniIdle on the Rengine
	 * 
	 * @see Rengine#rniIdle()
	 */
	public void rniIdle(){
		if ((r != null) && (r.getRni() != null ))r.getRni().rniIdle() ;
	}

	/**
	 * @return the console synchronizer
	 */
	public ConsoleSync getConsoleSync() {
		return consoleSync ;
	}

	/**
	 * @return The listeners associated with this server  
	 */
	public synchronized Vector<CallbackListener> getCallbackListeners() {
		return callbackListeners ;
	}
}

