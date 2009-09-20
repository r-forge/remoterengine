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
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import org.rosuda.JRI.Rengine;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPReference;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.REngineRegistry;
import org.rosuda.REngine.JRI.JRIEngine;
import org.rosuda.REngine.remote.common.JRIEngineGlobalVariables;
import org.rosuda.REngine.remote.common.RemoteREngineClient;
import org.rosuda.REngine.remote.common.RemoteREngineConstants;
import org.rosuda.REngine.remote.common.RemoteREngineInterface;
import org.rosuda.REngine.remote.common.callbacks.CallbackListener;
import org.rosuda.REngine.remote.common.callbacks.CallbackResponse;
import org.rosuda.REngine.remote.common.callbacks.RCallback;
import org.rosuda.REngine.remote.common.callbacks.RCallbackWithResponse;
import org.rosuda.REngine.remote.common.callbacks.ReadConsoleCallback;
import org.rosuda.REngine.remote.common.callbacks.ServerDownCallback;
import org.rosuda.REngine.remote.common.console.Command;
import org.rosuda.REngine.remote.common.console.CommandSender;
import org.rosuda.REngine.remote.common.console.RemoteREngineClientSender;
import org.rosuda.REngine.remote.common.exceptions.AlreadyRegisteredException;
import org.rosuda.REngine.remote.common.exceptions.FileAlreadyExistsException;
import org.rosuda.REngine.remote.common.exceptions.NotRegisteredException;
import org.rosuda.REngine.remote.common.exceptions.ServerSideIOException;
import org.rosuda.REngine.remote.common.files.RemoteFileInputStream;
import org.rosuda.REngine.remote.common.files.RemoteFileOutputStream;
import org.rosuda.REngine.remote.server.callbacks.CallbackSender;
import org.rosuda.REngine.remote.server.callbacks.ClientCallbackListener;
import org.rosuda.REngine.remote.server.console.ConsoleCallbackHandler;
import org.rosuda.REngine.remote.server.console.ConsoleSync;
import org.rosuda.REngine.remote.server.console.ConsoleThread;
import org.rosuda.REngine.remote.server.files.RemoteFileInputStream_Server;
import org.rosuda.REngine.remote.server.files.RemoteFileOutputStream_Server;

/**
 * The main class of the server side part of the RemoteREngine project 
 *
 * @author Romain Francois
 *
 */
public class RemoteREngine_Server implements RemoteREngineInterface {

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
	 * @param args arguments for R 
	 * 
	 * @throws REngineException Error creating the R instance
	 */ 
	public RemoteREngine_Server(String name, int servicePort, int registryPort, String[] args) throws REngineException, RemoteException, AccessException {
		super();
		this.name = name ; 
		this.registryPort = registryPort ;
		
		/* inform the clients that the jvm of the server is dying */
		shutdownHook = new RemoteREngineServerShutdownHook() ;
		Runtime.getRuntime().addShutdownHook( shutdownHook ); 
		
		clients = new Vector<RemoteREngineClient>(); 
		callbackListeners = new Vector<CallbackListener>(); 
		callbackLoop = new RemoteRMainLoopCallbacks(this) ;
		consoleSync = new ConsoleSync(this) ;
		
		consoleThread = new ConsoleThread(this);
		consoleCallbackHandler = new ConsoleCallbackHandler(this);
		callbackSender = new CallbackSender(this) ;
		addCallbackListener( consoleCallbackHandler ) ;
		
		r = new JRIEngine( args, callbackLoop ) ;
		
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
			System.out.println("Unable to locate registry, so creating a new RMI Registry locally");
		}
		try {
			if (registry==null) registry = LocateRegistry.createRegistry(registryPort);
		} catch (RemoteException e) {
			System.err.println(e.getClass().getName() + ": While trying to create registery on port " + registryPort + 
					": " + e.getMessage());
			System.err.println(e.getCause());
			throw e;
		}

		try {
			stub = (RemoteREngineInterface)UnicastRemoteObject.exportObject(this,servicePort);
		} catch (RemoteException e) {
			System.err.println("Unable to serialize server on " + servicePort + ": " + e.getMessage());
			throw e;
		}

		try {
			registry.bind(name, stub);
		} catch (AlreadyBoundException e) {
			debug(name + " already bound, attempting to rebind");
			try {
				registry.rebind(name, stub);
			} catch (AccessException ae) {
				System.err.println("AccessException while rebinding server to registry: " + ae.getMessage());
				throw ae;
			}catch (RemoteException re) {
				System.err.println("Unable to rebind server to port " + servicePort + ": " + re.getMessage());
				throw re;
			}
		} catch (RemoteException e) {
			System.err.println("Unable to bind server(" + name + ") to port " + servicePort + ": " + e.getMessage());
			throw e;
		}

		System.out.println( "R Engine bound as `"+ name +"` as a service on port " + servicePort + " to local RMIRegistry running on port " + registryPort );
		running = true; 
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
		this( name, 0, port, null) ;
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
		this( name, servicePort, registryPort, null) ;
	}
	/**
	 * Shutdown hook that indicates to clients of this server 
	 * that the server is dying 
	 * 
	 * @author Romain Francois
	 */
	private class RemoteREngineServerShutdownHook extends Thread{
		public void run(){
			if( !running ) shutdown() ;
		}
	}
	
	/**
	 * Shutdown the server
	 */
	public synchronized void shutdown(){
		System.err.println( "shutdown" ) ;
		if( !running ) return; 
		running = false; 
		
		consoleThread.requestStop() ;
		consoleThread.interrupt() ;
		
		if( !clients.isEmpty() ){
			ServerDownCallback dying = new ServerDownCallback() ;
			sendCallbackToListeners(dying) ;
		}
		/* TODO: empty the clients and listeners ? */
		/* TODO: shutdown R cleanly as well */
		
		System.err.println("Unbinding " + name );
		try {
			if (registry != null) registry.unbind( name );
			if (stub != null) {
				if (UnicastRemoteObject.unexportObject(stub, false)) {
					System.out.println(name + " successfully unexported");
				} else {
					System.out.println("Unable to Unexport " + name);
				}
			}
		} catch (NotBoundException e) {
			// don't care
		} catch (RemoteException e) {
			StringBuffer buf = new StringBuffer(e.getClass().getName() + ": " + e.getMessage() + ". While unbinding " + name);
			Throwable cause = e.getCause();
			if (cause != null) {
				buf.append(cause.getClass().getName() + ": " + cause.getMessage());
			}
			System.err.println( buf.toString());
		}
		System.out.println("Stopping the JVM");
		System.exit(0);
		
		
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
		debug( ">> parse" ) ;
		return r.parse( text, resolve ) ; 
	}

	/** 
	 * evaluate an expression vector
	 * @param what an expression (or vector of such) to evaluate
	 * @param where environment to evaluate in (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine)
	 * @param resolve resolve the resulting REXP or just return a reference
	 * @return the result of the evaluation of the last expression 
	 */
	public REXP eval(REXP what, REXP where, boolean resolve) throws REngineException, REXPMismatchException{
		debug( ">> eval" ) ;
		return r.eval( what, where, resolve ); 
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
		r.assign( symbol, value, env ); 
	}


	/**
	 * get a value from an environment
	 * @param symbol symbol name
	 * @param env environment (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine)
	 * @param resolve resolve the resulting REXP or just return a reference		
	 * @return value
	 */
	public REXP get(String symbol, REXP env, boolean resolve) throws REngineException, REXPMismatchException {
		debug( ">> get" ) ;
		return r.get( symbol, env, resolve ); 
	}

	/** 
	 * fetch the contents of the given reference. 
	 * The resulting REXP may never be REXPReference. 
	 * The engine should raise a {@link #REngineException} exception
	 * if {@link #supportsReferences()} returns <code>false</code>.
	 * 
	 * @param ref reference to resolve
	 * @return resolved reference
	 */
	public REXP resolveReference(REXP ref) throws REngineException, REXPMismatchException{ 
		debug( ">> resolveReference" ) ;
		return r.resolveReference( ref ); 
	}

	/** 
	 * create a reference by pushing local data to R and returning a reference to the data. If ref is a reference it is returned as-is. 
	 * The engine should raise a {@link #REngineException} exception if {@link #supportsReferences()} returns <code>false</code>.
	 * 
	 * @param value to create reference to
	 * @return reference to the value
	 */
	public REXP createReference(REXP value) throws REngineException, REXPMismatchException{
		debug( ">> createReference" ) ;
		return r.createReference( value ); 
	}

	/** 
	 * removes reference from the R side. This method is called automatically by the finalizer 
	 * of <code>REXPReference</code> and should never be called directly.
	 *
	 * @param ref reference to finalize 
	 */
	public void finalizeReference(REXP ref) throws REngineException, REXPMismatchException{ 
		debug( ">> finalizeReference" ) ;
		r.finalizeReference( ref ); 
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
		return r.getParentEnvironment( env, resolve ); 
	}

	/**
	 * create a new environemnt
	 *
	 * @param parent parent environment
	 * @param resolve whether to resolve the reference to the environemnt (usually <code>false</code> since the returned environment will be empty)
	 * @return resulting environment
	 */
	public REXP newEnvironment(REXP parent, boolean resolve) throws REngineException, REXPMismatchException{ 
		debug( ">> newEnvironment" ) ;
		return r.newEnvironment( parent, resolve ) ; 
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
			debug(e.getClass().getName() + ": " + e.getMessage() + " while processing " + text);
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
		RemoteFileInputStream_Server stream = new RemoteFileInputStream_Server( filename ) ;
		RemoteFileInputStream stub = (RemoteFileInputStream) UnicastRemoteObject.exportObject(stream);
    	return stub ; 
	}

	/**
	 * Opens a stream to write a file into the server
	 * @param filename filename in which to write 
	 * 
	 * @return the stream to write into 
	 * @throws RemoteException 
	 * @throws IOException when the stream cannot be created 
	 */
	public RemoteFileOutputStream createFile( String filename, boolean must_be_new) throws ServerSideIOException, FileAlreadyExistsException, RemoteException{
		if( must_be_new ){
			if( (new File( filename) ).exists() ){
				throw new FileAlreadyExistsException( filename ) ;
			}
		}
		RemoteFileOutputStream_Server stream = new RemoteFileOutputStream_Server( filename ) ;
		RemoteFileOutputStream stub = (RemoteFileOutputStream)UnicastRemoteObject.exportObject( stream ) ;
		return stub ;
	}
	
	/**
	 * Called when a client wants to send a command to the REPL
	 * 
	 * @param cmd command to send to the REPL
	 * @param client the client that sent the command
	 * 
	 * @throws RemoteException
	 */
	public void sendToConsole( String cmd, RemoteREngineClient origin ){
		consoleSync.add( new Command( cmd, new RemoteREngineClientSender(origin) ) );
	}
		
	private void debug( String message){
		if( DEBUG ){
			System.err.println( message ); 
		}
	}

	/**
	 * Register a client with this server
	 * 
	 * @throws AlreadyRegisteredException if the client is already registered with this server
	 */
	public synchronized JRIEngineGlobalVariables subscribe(RemoteREngineClient client) throws RemoteException, AlreadyRegisteredException {
		debug( "registering client" ) ;
		if( clients.contains( client ) ){
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
		callbackListeners.add( listener ) ;
	}
	
	/**
	 * Removes a callback listener
	 * @param listener the callback listener to remove
	 */
	public synchronized void removeCallbackListener( CallbackListener listener) {
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
			throw new NotRegisteredException() ; 
		}
		clients.remove( client ) ;
		removeCallbackListener( ClientCallbackListener.popCallbackListener(client) ) ;
	}

	/**
	 * Sends a response to a callback
	 */
	public void sendResponse( CallbackResponse<? extends RCallbackWithResponse> response ) throws RemoteException {
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
	 * The listeners associated with this server
	 * @return 
	 */
	public synchronized Vector<CallbackListener> getCallbackListeners() {
		return callbackListeners ;
	}

	
	private long getPointer( REXPReference ref){
		return ( (Long)ref.getHandle() ).longValue() ;
	}
	
}

