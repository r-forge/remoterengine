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

package org.rosuda.REngine.remote.common ;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPReference;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.remote.common.callbacks.CallbackResponse;
import org.rosuda.REngine.remote.common.callbacks.RCallbackWithResponse;
import org.rosuda.REngine.remote.common.exceptions.AlreadyRegisteredException;
import org.rosuda.REngine.remote.common.exceptions.FileAlreadyExistsException;
import org.rosuda.REngine.remote.common.exceptions.NotRegisteredException;
import org.rosuda.REngine.remote.common.exceptions.ServerSideIOException;
import org.rosuda.REngine.remote.common.files.RemoteFileInputStream;
import org.rosuda.REngine.remote.common.files.RemoteFileOutputStream;

/**
 * The remote interface that is used to from a client program 
 * to call a remote R engine 
 * 
 * @author Romain Francois
 */
public interface RemoteREngineInterface extends Remote {

	/**                                                    
	 * parse a string into an expression vector         
	 *
	 * @param text string to parse
	 * @param resolve resolve the resulting REXP (<code>true</code>) or just return a reference (<code>false</code>)
	 * @return parsed expression 
	 */
	public REXP parse(String text, boolean resolve) throws REngineException, RemoteException ;

	/** 
	 * evaluate an expression vector
	 * @param what an expression (or vector of such) to evaluate
	 * @param where environment to evaluate in (use <code>null</code> for the global environment and/or if environments are not supported by the engine)
	 * @param resolve resolve the resulting REXP or just return a reference
	 * @return the result of the evaluation of the last expression 
	 */
	public REXP eval(REXP what, REXP where, boolean resolve) throws REngineException, REXPMismatchException, RemoteException;

	/**
	 * assign into an environment
	 *
	 * @param symbol symbol name
	 * @param value value to assign
	 * @param env environment to assign to (use <code>null</code> for the global environment and/or if environments are not supported by the engine
	 */
	public void assign(String symbol, REXP value, REXP env) throws REngineException, REXPMismatchException, RemoteException;

	/**
	 * get a value from an environment
	 * @param symbol symbol name
	 * @param env environment (use <code>null</code> for the global environment and/or if environments are not supported by the engine)
	 * @param resolve resolve the resulting REXP or just return a reference		
	 * @return value
	 */
	public REXP get(String symbol, REXP env, boolean resolve) throws REngineException, REXPMismatchException, RemoteException;

	/** 
	 * fetch the contents of the given reference. 
	 * The resulting REXP may never be REXPReference. 
	 * 
	 * @param ref reference to resolve
	 * @return resolved reference
	 * @throws REngineException if references are not supported
	 */
	public REXP resolveReference(REXP ref) throws REngineException, REXPMismatchException, RemoteException;

	/** 
	 * create a reference by pushing local data to R and returning a reference to the data. If ref is a reference it is returned as-is. 
	 * 
	 * @param value to create reference to
	 * @return reference to the value
	 * @throws REngineException if references are not supported by the engine
	 */
	public REXP createReference(REXP value) throws REngineException, REXPMismatchException, RemoteException;

	/** 
	 * removes reference from the R side. This method is called automatically by the finalizer 
	 * of <code>REXPReference</code> and should never be called directly.
	 *
	 * @param ref reference to finalize 
	 */
	public void finalizeReference(REXP ref) throws REngineException, REXPMismatchException, RemoteException;

	/**
	 * get the parent environment of an environment
	 *
	 * @param env environment to query
	 * @param resolve whether to resolve the resulting environment reference
	 * @return parent environment of env
	 */
	public REXP getParentEnvironment(REXP env, boolean resolve) throws REngineException, REXPMismatchException, RemoteException;

	/**
	 * create a new environment
	 *
	 * @param parent parent environment
	 * @param resolve whether to resolve the reference to the environement (usually <code>false</code> since the returned environment will be empty)
	 * 
	 * @return resulting environment
	 */
	public REXP newEnvironment(REXP parent, boolean resolve) throws REngineException, REXPMismatchException, RemoteException;

	/**
	 * Convenience method to parse and eval. This is overriden so that both
	 * operations happen on the server side
	 * 
	 * @param text text to parse
	 * @param where environment
	 * @param resolve should the reference be resolved
	 * @return a {@link REXPReference} to the result if resolve is false, a {@link REXP} if resolve is true, or null if an error occured
	 *
	 * @throws REngineException
	 * @throws REXPMismatchException
	 * @throws RemoteException
	 */
	public REXP parseAndEval(String text, REXP where, boolean resolve) throws REngineException, REXPMismatchException, RemoteException; 

	/**
	 * open a stream to read a file from the server
	 * @param filename the name of the file in the server
	 * 
	 * @return the stream to read from
	 * 
	 * @throws ServerSideIOException when the stream cannot be opened 
	 */
	public RemoteFileInputStream openFile( String filename ) throws RemoteException, ServerSideIOException; 

	/**
	 * open a stream to write into a file in the server
	 * @param filename filename on the server
	 * @param must_be_new an exception is thrown if this is true and the file already exists
	 * 
	 * @return the stream to write into
	 * 
	 * @throws ServerSideIOException when the stream cannot be created
	 * @throws FileAlreadyExistsException when must_be_new is true and the file already exists on the server
	 */
	public RemoteFileOutputStream createFile( String filename, boolean must_be_new ) throws ServerSideIOException, RemoteException, FileAlreadyExistsException ;

	/**
	 * Subscribe a client to this server. The server uses this subscription 
	 * mechanism to send callbacks to the clients 
	 * 
	 * @param client
	 * @throws RemoteException
	 */
	public JRIEngineGlobalVariables subscribe( RemoteREngineClient client) throws RemoteException, AlreadyRegisteredException ;

	/**
	 * Close the client connection to the engine. Ends the subscription 
	 * @throws RemoteException
	 */
	public void close(RemoteREngineClient client) throws RemoteException, NotRegisteredException ;  

	/**
	 * Sends a response to a callback
	 * 
	 * @param response the response to a callback
	 * @throws RemoteException
	 */
	public void sendResponse( CallbackResponse<? extends RCallbackWithResponse> response ) throws RemoteException ;
	
	/**
	 * Send a command to the server's REPL
	 * @param cmd command to send to the REPL
	 * @throws RemoteException
	 */
	public void sendToConsole( String cmd, RemoteREngineClient client ) throws RemoteException ;
	
	/**
	 * Shut down the RServer and underlying R process. NOTE: This will terminate all the connections
	 * to the R Server and should only be called if the intention is to shut down the whole system.
	 * @throws RemoteException Error executing shutdown
	 */
	public void shutdown() throws RemoteException;
}

