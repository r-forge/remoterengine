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

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.JRI.JRIEngine;
import org.rosuda.REngine.remote.common.JRIEngineGlobalVariables;
import org.rosuda.REngine.remote.common.RemoteREngineClient;
import org.rosuda.REngine.remote.common.RemoteREngineInterface;
import org.rosuda.REngine.remote.common.callbacks.RCallback;
import org.rosuda.REngine.remote.common.callbacks.ServerDownCallback;
import org.rosuda.REngine.remote.common.exceptions.AlreadyRegisteredException;
import org.rosuda.REngine.remote.common.exceptions.FileAlreadyExistsException;
import org.rosuda.REngine.remote.common.exceptions.NotRegisteredException;
import org.rosuda.REngine.remote.common.exceptions.ServerSideIOException;
import org.rosuda.REngine.remote.common.files.RemoteFileInputStream;
import org.rosuda.REngine.remote.common.files.RemoteFileOutputStream;
import org.rosuda.REngine.remote.server.callbacks.CallbackQueue;
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
	
	/** 
	 * The local R engine this server is shadowing
	 */ 
	private JRIEngine r ; 

	/**
	 * The callback queue
	 */
	private CallbackQueue callbackQueue ;

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
	 * Constructor. Initiates the local R engine that this engine shadows
	 * @throws REngineException Error creating the R instance
	 */ 
	public RemoteREngine_Server() throws REngineException {
		super();
		clients = new Vector<RemoteREngineClient>(); 
		callbackQueue = new CallbackQueue(); 
		// String[] args = new String[]{ "--no-save" } ; 
		// TODO: control parameters passed to R
		// r  = new JRIEngine( args , new RemoteRMainLoopCallbacks(this) ) ;
		r = (JRIEngine) JRIEngine.createEngine() ;
		
		/* TODO: forbid the q function */
		
		/* capture global variables of the JRIEngine */
		variables = new JRIEngineGlobalVariables( 
				r.globalEnv, r.emptyEnv, r.baseEnv, 
				r.nullValueRef, r.nullValue,r.hashCode() ) ;
		
		/* inform the clients that the jvm of the server is dying */
		shutdownHook = new RemoteREngineServerShutdownHook() ;
		Runtime.getRuntime().addShutdownHook( shutdownHook ); 
	}

	
	/**
	 * Shutdown hook that indicates to clients of this server 
	 * that the server is dying 
	 * 
	 * @author Romain Francois
	 */
	private class RemoteREngineServerShutdownHook extends Thread{
		public void run(){
			for( RemoteREngineClient client: clients){
				try{
					/* client.serverDying(); */
					client.callback( new ServerDownCallback() ) ;
				} catch( RemoteException e){
					/* TODO: handle RemoteException when calling serverDying */
				}
			}
			/* TODO: maybe also unbind the object from the registry instead
			 * of doing it in the REngineServer class*/
			
			/* TODO: shutdown R cleanly as well */
		}
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
		debug( ">> assign" ) ;
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
		debug( ">> parseAndEval" ) ;
		return r.parseAndEval( text, where, resolve ); 
	}


	/**
	 * open a stream to read a file from the server
	 * @param filename file name on the server to read from 
	 * 
	 * @return the stream used to read the file
	 * @throws RemoteException 
	 * @throws IOException when the stream cannot be create
	 */
	@Override
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
	@Override
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
	@Override
	public synchronized JRIEngineGlobalVariables subscribe(RemoteREngineClient client) throws RemoteException, AlreadyRegisteredException {
		debug( "registering client" ) ;
		if( clients.contains( client ) ){
			throw new AlreadyRegisteredException(); 
		}
		if( clients.isEmpty() ){
			/* r.getRni().addMainLoopCallbacks( new RemoteRMainLoopCallbacks( this ) ) ; */
		}
		clients.add( client ) ;
		return variables ; 
	}
	
	/**
	 * Unsubscribe a client
	 * @throws NotRegisteredException if the client is not registered with this server
	 */
	@Override
	public synchronized void close(RemoteREngineClient client) throws RemoteException, NotRegisteredException {
		debug( "unregister client" ) ;
		if( !clients.contains( client) ){
			throw new NotRegisteredException() ; 
		}
		clients.remove( client ) ;
	}

	
	/**
	 * Adds a callback to the queue
	 * @param callback the callback
	 */
	public void addCallback(RCallback callback){
		callbackQueue.push(callback) ;
	}


}

