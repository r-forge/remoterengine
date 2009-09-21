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

package org.rosuda.REngine.remote.client ;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPNull;
import org.rosuda.REngine.REXPReference;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.REngineRegistry;
import org.rosuda.REngine.UniqueID;
import org.rosuda.REngine.remote.client.callbacks.ClientCallbackDispatcher;
import org.rosuda.REngine.remote.common.JRIEngineGlobalVariables;
import org.rosuda.REngine.remote.common.RemoteREngineClient;
import org.rosuda.REngine.remote.common.RemoteREngineConstants;
import org.rosuda.REngine.remote.common.RemoteREngineInterface;
import org.rosuda.REngine.remote.common.callbacks.CallbackListener;
import org.rosuda.REngine.remote.common.callbacks.CallbackResponse;
import org.rosuda.REngine.remote.common.callbacks.RCallback;
import org.rosuda.REngine.remote.common.callbacks.RCallbackWithResponse;
import org.rosuda.REngine.remote.common.exceptions.FileAlreadyExistsException;
import org.rosuda.REngine.remote.common.exceptions.NotRegisteredException;
import org.rosuda.REngine.remote.common.exceptions.ServerSideIOException;
import org.rosuda.REngine.remote.common.files.FileChunk;
import org.rosuda.REngine.remote.common.files.RemoteFileInputStream;
import org.rosuda.REngine.remote.common.files.RemoteFileOutputStream;

/**
 * An implementation of the REngine API that communicates through an 
 * RMI protocol
 */
public class RemoteREngine extends REngine implements RemoteREngineClient {

	/**
	 * The remote interface through which this client calls the server
	 */
	private RemoteREngineInterface engine;

	/** 
	 * reference to the global environment of the remote engine
	 */
	public REXPReference globalEnv ; 
	
	/**
	 * reference to the empty environment of the remote engine
	 */
	public REXPReference emptyEnv ; 
	
	/**
	 * Reference to the base environment of the remote engine
	 */
	public REXPReference baseEnv ; 
	
	/**
	 * Reference to the NULL value of the remote engine
	 */
	public REXPReference nullValueRef;

	/**
	 * canonical NULL object (obtained from the server)
	 */
	public REXPNull nullValue;

	/** 
	 * the hashcode of the server this shadows
	 */
	private UniqueID engine_id ; 
	
	/**
	 * Is this engine valid ?
	 */
	private boolean valid = false; 

	/**
	 * Shutdown hook for this client
	 */
	private RemoteREngineShutdownHook shutdownHook ; 
	
	private ClientCallbackDispatcher callbackDispatcher ;
	
	/**
	 * Construct a RemoteREngine
	 * @param registryHost Name or IP Address of the host containing the RMI registry
	 * @param name Name of the Remote server registered within the Registry
	 * @param port port to use to connect to the server
	 */
	public RemoteREngine(String name, String registryHost, int port){
		if (name == null || name.length()==0) {
			System.err.println("RMI Name of remote engine not defined");
		}
		nullValue    = new REXPNull()  ; 
		
		try{
			Registry reg = LocateRegistry.getRegistry(registryHost, port);
			engine = (RemoteREngineInterface) reg.lookup(name);

			/* subscribe to the server and populate the fields */
			/* System.out.println( "subscribe to the server" ) ; */
			UnicastRemoteObject.exportObject(this) ;
			JRIEngineGlobalVariables variables = engine.subscribe(this) ;
			callbackDispatcher = new ClientCallbackDispatcher(this) ;
			engine_id    = variables.engine_id ;
			
			globalEnv    = new REXPReference( this, new Long( variables.p_globalEnv ) ) ;
			emptyEnv     = new REXPReference( this, new Long( variables.p_emptyEnv ) ); 
			baseEnv      = new REXPReference( this, new Long( variables.p_baseEnv)) ; 
			nullValueRef = new REXPReference( this, new Long( variables.p_nullValueRef ) );
			
			/* store this engine in the pool */
			REngineRegistry.add( this, engine_id ); 
			callbackDispatcher.start();
			
			valid = testConnection(); 
		} catch ( NotBoundException nb) {
			System.out.println("Unable to locate " + name + " within RMI Registry");
		} catch( Exception e ){
			System.out.println( e.getClass().getName() + " creating the RemoteREngine" ) ;
			e.printStackTrace();
		}
		
		shutdownHook = new RemoteREngineShutdownHook(); 
		
		/* let the server know when the jvm of this client closes */
		Runtime.getRuntime().addShutdownHook( shutdownHook ) ;
		
		
	}
	
	/**
	 * Shutdown hook for the client. This is used when the jvm of the client
	 * is about to shutdown so that the server is informed and can stop
	 * sending callbacks to this client
	 * 
	 * @see Runtime#addShutdownHook(Thread)
	 * @author Romain Francois
	 *
	 */
	private class RemoteREngineShutdownHook extends Thread {
		public void run(){
			if( isValid() ){
				close( ) ; 
			}
		}
	}

	/**
	 * Indicates if this engine is valid
	 */
	public boolean isValid(){
		/* TODO: maybe ping the engine */
		return valid; 
	}
	
	/**
	 * Constructor using the default port
	 * 
	 * @see RemoteREngineConstants#RMIPORT for the default value
	 * @param name name of the remote object in the registry
	 * @param registryHost host name
	 */
	public RemoteREngine(String name, String registryHost ){
		this( name, registryHost, RemoteREngineConstants.RMIPORT ) ;
	}

	/**                                                    
	 * parse a string into an expression vector         
	 *
	 * @param text string to parse
	 * @param resolve resolve the resulting REXP (<code>true</code>) or just return a reference (<code>false</code>)
	 * @return parsed expression 
	 */
	public REXP parse(String text, boolean resolve) throws REngineException{
		REXP res = null ; 
		try{
			res = engine.parse( text, resolve );
		} catch( RemoteException e){
			RemoteExceptionManager.send( e, "parse" ); 
		}
		return res ;
	}

	/** 
	 * evaluate an expression vector
	 * @param what an expression (or vector of such) to evaluate
	 * @param where environment to evaluate in (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine)
	 * @param resolve resolve the resulting REXP or just return a reference
	 * @return the result of the evaluation of the last expression 
	 */
	public REXP eval(REXP what, REXP where, boolean resolve) throws REngineException, REXPMismatchException{
		REXP res = null ; 
		try{
			res = engine.eval( what, where, resolve );  
		} catch( RemoteException e){
			RemoteExceptionManager.send( e, "eval" ); 
		}
		return res ;
	}

	/**
	 * assign into an environment
	 *
	 * @param symbol symbol name
	 * @param value value to assign
	 * @param env environment to assign to (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine
	 */
	public void assign(String symbol, REXP value, REXP env) throws REngineException, REXPMismatchException{
		try{
			engine.assign( symbol, value, env );  
		} catch( RemoteException e){
			RemoteExceptionManager.send( e, "assign" ); 
		}
	}

	/**
	 * get a value from an environment
	 * @param symbol symbol name
	 * @param env environment (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine)
	 * @param resolve resolve the resulting REXP or just return a reference		
	 * @return value
	 */
	public REXP get(String symbol, REXP env, boolean resolve) throws REngineException, REXPMismatchException{
		REXP res = null ; 
		try{
			res = engine.get( symbol, env, resolve );  
		} catch( RemoteException e){
			RemoteExceptionManager.send( e, "get" ); 
		}
		return res ;
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
		REXP res = null ; 
		try{
			res = engine.resolveReference(ref) ;  
		} catch( RemoteException e){
			RemoteExceptionManager.send( e, "resolveReference" ); 
		}
		return res ;
	}

	/** 
	 * create a reference by pushing local data to R and returning a reference to the data. If ref is a reference it is returned as-is. 
	 * The engine should raise a {@link #REngineException} exception if {@link #supportsReferences()} returns <code>false</code>.
	 * 
	 * @param value to create reference to
	 * @return reference to the value
	 */
	public REXP createReference(REXP value) throws REngineException, REXPMismatchException{
		REXP res = null ; 
		try{
			res = engine.createReference( value );    
		} catch( RemoteException e){
			RemoteExceptionManager.send( e, "createReference" ); 
		}
		return res ;
	}

	/** 
	 * removes reference from the R side. This method is called automatically by the finalizer 
	 * of <code>REXPReference</code> and should never be called directly.
	 *
	 * @param ref reference to finalize 
	 */
	public void finalizeReference(REXP ref) throws REngineException, REXPMismatchException{
		try{
			engine.finalizeReference( ref );     
		} catch( RemoteException e){
			RemoteExceptionManager.send( e, "finalizeReference" ); 
		}
	}

	/**
	 * get the parent environemnt of an environment
	 *
	 * @param env environment to query
	 * @param resolve whether to resolve the resulting environment reference
	 * @return parent environemnt of env
	 */
	public REXP getParentEnvironment(REXP env, boolean resolve) throws REngineException, REXPMismatchException{
		REXP res = null ; 
		try{
			res = engine.getParentEnvironment( env, resolve );    
		} catch( RemoteException e){
			RemoteExceptionManager.send( e, "getParentEnvironment" ); 
		}
		return res ;
	}

	/**
	 * create a new environment
	 *
	 * @param parent parent environment
	 * @param resolve whether to resolve the reference to the environemnt (usually <code>false</code> since the returned environment will be empty)
	 * @return resulting environment
	 */
	public REXP newEnvironment(REXP parent, boolean resolve) throws REngineException, REXPMismatchException{
		REXP res = null ; 
		try{
			res = engine.newEnvironment( parent, resolve ) ;    
		} catch( RemoteException e){
			RemoteExceptionManager.send( e, "newEnvironment" ); 
		}
		return res ;
	}

	/**
	 * Convenience method to parse and eval
	 */
	public REXP parseAndEval(String text, REXP where, boolean resolve) throws REngineException, REXPMismatchException {
		REXP res = null; 
		try{
			res = engine.parseAndEval( text, where, resolve ) ;
		} catch( RemoteException e){
			RemoteExceptionManager.send( e, "parseAndEval" );
		}
		return res ; 
	}

	/**
	 * Push a file from the client side to the server side
	 * 
	 * @param client_file name of the file in the client 
	 * @param server_file name of the file in the server
	 * @param must_be_new if true, throw an exception if the file already exists 
	 */
	public void pushFile( String client_file, String server_file, boolean must_be_new ) throws IOException, ServerSideIOException, FileAlreadyExistsException {
		BufferedInputStream client_in = new BufferedInputStream( new FileInputStream( client_file ) ) ;
		RemoteFileOutputStream server_out = engine.createFile(server_file, must_be_new) ;

		byte [] b = new byte[ RemoteREngineConstants.CHUNKSIZE ];

		/* typical java IO stuff */
		int c = client_in.read(b) ; 
		while( c >= 0 ){
			server_out.write( b, 0, c ) ;
			c = client_in.read(b) ;
		}
		server_out.close();
		client_in.close(); 
	}

	/**
	 * Fetch a file from the server
	 * 
	 * @param client_file the name of the file in the client to write into
	 * @param server_file the name of the server file to fetch
	 * @param delete delete the file after fetching it ?
	 * @return true if the file transfer has completed successfully
	 * @throws IOException 
	 */
	public boolean fetchFile( String client_file, String server_file, boolean delete ) throws IOException, ServerSideIOException {
		BufferedOutputStream client_out = null;
		RemoteFileInputStream server_in = null;
		boolean success = false;
		try {
			client_out = new BufferedOutputStream( new FileOutputStream( client_file ) ) ;
			server_in = engine.openFile(server_file) ;
		
			FileChunk chunk = server_in.readNextChunk() ;
			while( ! chunk.isEmpty() ){
				client_out.write(chunk.buffer, 0, chunk.size ) ;
				chunk = server_in.readNextChunk() ;
			}
			success = true;
		} finally {
			try {
				server_in.close() ;
			} catch (Exception e) {}
			try {
				client_out.close(); 
			} catch (Exception e) {}
			if( delete ){
				server_in.delete(); 
			}
			server_in = null;
			client_out = null;
		}
		return success;
	}

	/** 
	 * References are supported since this mirrors a JRIEngine
	 */
	public boolean supportsReferences() { 
		return true ;
	}

	/** 
	 * Environments are supported since this mirrors a JRIEngine
	 */
	public boolean supportsEnvironemnts() {
		return true ;
	}

	/** 
	 * REPL is supported since this mirrors a JRIEngine 
	 */
	public boolean supportsREPL() {
		return true ;
	}

	/* TODO: implement the locking mechanism in the server side */
	/** 
	 * locking is supported since this mirrors a JRIEngine
	 */
	public boolean supportsLocking() { 
		return true ; 
	}
	
	/**
	 * End the subscription of this client with the R engine on the server side
	 * This method invalidates fields previously set by the subscription mechanism
	 * and makes this engine useless
	 */
	@Override
	public boolean close() {
		try{
			engine.close( this ) ;
		} catch( NotRegisteredException e){
			/* TODO: handle NotRegisteredException when closing the client */
		} catch( RemoteException e){
			/* TODO: handle RemoteException when closing the client */
		}
		invalidate(); 
		return true;  
	}

	private void invalidate() {
		valid = false; 
		engine = null ; 
		globalEnv = null; 
		emptyEnv = null;
		baseEnv = null ; 
		nullValueRef = null; 
		nullValue = null; 
		engine_id = null; 
	}
	
	/**
	 * Receives a callback from the server and dispatchs it to listeners
	 */
	public void callback(RCallback callback) throws RemoteException {
		callbackDispatcher.addToQueue( callback ) ;
	}
		
	/**
	 * Unregister this client from the server before usual finalization
	 */
	@Override
	protected void finalize() throws Throwable {
		close() ;
		super.finalize();
	}

	/**
	 * Adds a new callback listener
	 * 
	 * @param listener callback listener
	 * @see ClientCallbackDispatcher#addCallbackListener(CallbackListener)
	 */
	public void addCallbackListener( CallbackListener listener){
		callbackDispatcher.addCallbackListener( listener ) ;
	}
	
	/**
	 * Removes a callback listener
	 * @param listener callback listener
	 * @see ClientCallbackDispatcher#removeCallbackListener(CallbackListener)
	 */
	public void removeCallbackListener( CallbackListener listener ){
		callbackDispatcher.removeCallbackListener( listener ) ;
	}

	/**
	 * Send a callback response to the server
	 * @param response a callback response
	 */
	public void sendResponse( CallbackResponse<? extends RCallbackWithResponse> response){
		try{
			engine.sendResponse(response) ;
		} catch( RemoteException re){
			/*  what */
		}
	}
	
	/**
	 * Send a command to the console
	 * @param command command to send 
	 */
	public void sendToConsole( String command ){
		try {
			engine.sendToConsole(command, this ) ;
		} catch (RemoteException e) {
			/* what now */
		} 
	}
	
	/**
	 * Check to see if we can run a command against the server
	 * @return true if the command returns successfully
	 */
	private boolean testConnection() {
		try {
			REXP rexp = parseAndEval("R.version$version.string");
			return rexp != null;
		} catch (Throwable t) {
			System.err.println(t.getClass().getName() + ": while testing connection to engine; " + t.getMessage());
			return false;
		}
	}
}

