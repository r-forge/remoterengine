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

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPNull;
import org.rosuda.REngine.REXPReference;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.REnginePool;
import org.rosuda.REngine.remote.common.RemoteFileInputStream;
import org.rosuda.REngine.remote.common.RemoteFileOutputStream;
import org.rosuda.REngine.remote.common.RemoteREngineInterface;
import org.rosuda.REngine.remote.common.exceptions.FileAlreadyExistsException;
import org.rosuda.REngine.remote.common.exceptions.ServerSideIOException;

/**
 * An implementation of the REngine API that communicates through an 
 * RMI protocol
 */
public class RemoteREngine extends REngine {
	
	/* {{{ fields */
	private RemoteREngineInterface engine;
	
	/** special, global references */
	public REXPReference globalEnv, emptyEnv, baseEnv, nullValueRef;
	
	/** canonical NULL object */
	public REXPNull nullValue;
	
	/* }}} */
	
	/**
	 * Construct a RemoteREngine
	 * @param registryHost Name or IP Address of the host containing the RMI registry
	 * @param name Name of the Remote server registered within the Registry
	 */
	public RemoteREngine(String name, String registryHost, int port){
		if (name == null || name.length()==0) {
			System.err.println("RMI Name of remote engine not defined");
		}
		try{
			Registry reg = LocateRegistry.getRegistry(registryHost, port);
			engine = (RemoteREngineInterface) reg.lookup(name);
			
			/* store this engine in the pool */
			int code      = engine.getEngineHashCode( ) ;
			REnginePool.add( this, code ); 
		} catch ( NotBoundException nb) {
			System.out.println("Unable to locate " + name + " within RMI Registry");
		} catch( Exception e ){
			System.out.println( e.getClass().getName() + " creating the RemoteREngine" ) ;
			e.printStackTrace();
		}
		init(); 
	}
	
	private void init(){
		try{
			globalEnv    = engine.getGlobalEnv() ;
			emptyEnv     = engine.getEmptyEnv(); 
			baseEnv      = engine.getBaseEnv(); 
			nullValueRef = engine.getNullValueRef();
			nullValue    = engine.getNullValue(); 
		} catch (RemoteException e){
			RemoteExceptionManager.send( e, "init" ) ; 
		}
	}
	/* }}} */
	
	/* {{{ implementation of REngine through the RemoteREngineInterface */
	
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
	 * create a new environemnt
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

	public REXP parseAndEval(String text, REXP where, boolean resolve) throws REngineException, REXPMismatchException {
		REXP res = null; 
		try{
			res = engine.parseAndEval( text, where, resolve ) ;
		} catch( RemoteException e){
			RemoteExceptionManager.send( e, "parseAndEval" );
		}
		return res ; 
	}

	 /* }}} */
	
	
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
		
		byte [] b = new byte[8192];
	
		/* typical java IO stuff */
		int c = client_in.read(b) ; 
		while( c >= 0 ){
			server_out.write( b ) ;
			c = client_in.read(b) ;
		}
		server_out.close();
		client_in.close(); 
	}
	
	/**
	 * Fetch a file from the server
	 * @param client_file the name of the file in the client to write into
	 * @param server_file the name of the server file to fetch
	 * @param delete delete the file after fetching it ?
	 * @throws IOException 
	 */
	public void fetchFile( String client_file, String server_file, boolean delete ) throws IOException, ServerSideIOException {
		BufferedOutputStream client_out = new BufferedOutputStream( new FileOutputStream( client_file ) ) ;
		RemoteFileInputStream server_in = engine.openFile(server_file) ;
		
		byte[] chunk = server_in.readNextChunk() ;
		while( chunk != null){
			client_out.write(chunk) ;
			chunk = server_in.readNextChunk() ;
		}
		server_in.close() ;
		client_out.close(); 
		if( delete ){
			server_in.delete(); 
		}
	}
	
	
	
	/* {{{ capabilities */
	/** check whether this engine supports references to R objects
	 @return <code>true</code> if this engine supports references, <code>false/code> otherwise */
	public boolean supportsReferences() { return true ; }
	/** check whether this engine supports handing of environments (if not, {@link #eval} and {@link #assign} only support the global environment denoted by <code>null</code>).
	 @return <code>true</code> if this engine supports environments, <code>false/code> otherwise */
	public boolean supportsEnvironemnts() { return true ; }
	/** check whether this engine supports REPL (Read-Evaluate-Print-Loop) and corresponding callbacks.
	 @return <code>true</code> if this engine supports REPL, <code>false/code> otherwise */
	public boolean supportsREPL() { return true ; }
	/** check whether this engine supports locking ({@link #lock}, {@link #tryLock} and {@link #unlock}).
	 @return <code>true</code> if this engine supports REPL, <code>false/code> otherwise */
	public boolean supportsLocking() { return true ; }
	/* }}} */

    // :tabSize=4:indentSize=4:noTabs=false:folding=explicit:collapseFolds=1:
}

