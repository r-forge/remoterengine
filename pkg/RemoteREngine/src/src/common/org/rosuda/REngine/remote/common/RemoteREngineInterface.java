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
import org.rosuda.REngine.REXPNull;
import org.rosuda.REngine.REXPReference;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.remote.common.callbacks.RCallback;
import org.rosuda.REngine.remote.common.exceptions.FileAlreadyExistsException;
import org.rosuda.REngine.remote.common.exceptions.ServerSideIOException;

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
	 * @param where environment to evaluate in (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine)
	 * @param resolve resolve the resulting REXP or just return a reference
	 * @return the result of the evaluation of the last expression 
	 */
  public REXP eval(REXP what, REXP where, boolean resolve) throws REngineException, REXPMismatchException, RemoteException;

	/**
	 * assign into an environment
	 *
   * @param symbol symbol name
   * @param value value to assign
   * @param env environment to assign to (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine
   */
   public void assign(String symbol, REXP value, REXP env) throws REngineException, REXPMismatchException, RemoteException;

	/**
	 * get a value from an environment
	 * @param symbol symbol name
	 * @param env environment (use <code>null</code> for the global environemnt and/or if environments are not supported by the engine)
	 * @param resolve resolve the resulting REXP or just return a reference		
	 * @return value
	 */
   public REXP get(String symbol, REXP env, boolean resolve) throws REngineException, REXPMismatchException, RemoteException;

	/** 
	 * fetch the contents of the given reference. 
	 * The resulting REXP may never be REXPReference. 
	 * The engine should raise a {@link #REngineException} exception
	 * if {@link #supportsReferences()} returns <code>false</code>.
	 * 
	 * @param ref reference to resolve
	 * @return resolved reference
	 */
	public REXP resolveReference(REXP ref) throws REngineException, REXPMismatchException, RemoteException;

	/** 
	 * create a reference by pushing local data to R and returning a reference to the data. If ref is a reference it is returned as-is. 
	 * The engine should raise a {@link #REngineException} exception if {@link #supportsReferences()} returns <code>false</code>.
	 * 
	 * @param value to create reference to
	 * @return reference to the value
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
	 * get the parent environemnt of an environment
	 *
	 * @param env environment to query
	 * @param resolve whether to resolve the resulting environment reference
	 * @return parent environemnt of env
	 */
	public REXP getParentEnvironment(REXP env, boolean resolve) throws REngineException, REXPMismatchException, RemoteException;
	
	/**
	 * create a new environemnt
	 *
	 * @param parent parent environment
	 * @param resolve whether to resolve the reference to the environemnt (usually <code>false</code> since the returned environment will be empty)
	 * 
	 * @return resulting environment
	 */
	public REXP newEnvironment(REXP parent, boolean resolve) throws REngineException, REXPMismatchException, RemoteException;

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
	 * Waits for the next callback to be available and send it to the client
	 */
	public RCallback nextCallback() throws RemoteException; 
	
	/* FIXME: these are used only once at the setup of the engine to mirror some of the 
	 * settings of the JRIEngine, there probably is a better way to deal with that. 
	 */
	public REXPReference getGlobalEnv() throws RemoteException ; 
	public REXPReference getEmptyEnv() throws RemoteException ; 
	public REXPReference getBaseEnv() throws RemoteException ; 
	public REXPReference getNullValueRef() throws RemoteException ; 
	public REXPNull getNullValue() throws RemoteException; 
	
	/* this is only used to key the engine, need something better? */
	public int getEngineHashCode() throws RemoteException; 
	
}

