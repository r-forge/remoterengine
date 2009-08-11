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

import org.rosuda.REngine.remote.server.callbacks.CallbackQueue ;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPNull;
import org.rosuda.REngine.REXPReference;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.JRI.JRIEngine;
import org.rosuda.REngine.remote.common.RemoteREngineInterface;
import org.rosuda.REngine.remote.common.callbacks.RCallback;

public class RemoteREngine_Server implements RemoteREngineInterface {
  
	private static boolean DEBUG = false; 
	
	/** 
	 * The local R engine this server is shadowing
	 */ 
	private JRIEngine r ; 
	
	/**
	 * The callback queue
	 */
	private CallbackQueue callbackQueue ;
	
	/**
	 * Constructor. Initiates the local R engine that this engine shadows
	 * @throws REngineException Error creating the R instance
	 */ 
	public RemoteREngine_Server() throws REngineException {
		super();
		//      try{
		r = (JRIEngine) JRIEngine.createEngine() ;
		// r  = new JRIEngine( new String[]{ "--no-save" }, new RemoteRMainLoopCallbacks(this) ) ;
		/*      } catch( Exception e ){
      	System.out.println( "Could not create JRIEngine :" ) ;
      	e.printStackTrace( ); 
      }
		 */
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
	 * get the parent environemnt of an environment
	 *
	 * @param env environment to query
	 * @param resolve whether to resolve the resulting environment reference
	 * @return parent environemnt of env
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
	
	public REXP parseAndEval(String text, REXP where, boolean resolve) throws REngineException, REXPMismatchException {
		debug( ">> parseAndEval" ) ;
		return r.parseAndEval( text, where, resolve ); 
	}

	/**
	 * Waits for the next callback to be available and sent it
	 * 
	 * @return the next callback
	 */
	public RCallback nextCallback(){
		return callbackQueue.next(); 
	}
	
	public void addCallback(RCallback callback){
		callbackQueue.push(callback) ;
	}
	
	
	private void debug( String message){
		if( DEBUG ){
			System.out.println( message ); 
		}
	}
  
	public REXPReference getGlobalEnv(){
		return r.globalEnv; 
	}
	public REXPReference getEmptyEnv(){
		return r.emptyEnv ; 
	}
	public REXPReference getBaseEnv(){
		return r.baseEnv; 
	}
	public REXPReference getNullValueRef(){
		return r.nullValueRef ;
	}
	public REXPNull getNullValue(){
		return r.nullValue ;
	}
	public int getEngineHashCode(){
		return r.hashCode(); 
	}
	
	
}

