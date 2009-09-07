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
package org.rosuda.REngine.remote.common;

import java.io.Serializable;

import org.rosuda.REngine.REXPNull;
import org.rosuda.REngine.REXPReference;

/**
 * Encapsulates global variables of the JRIEngine 
 * 
 * @author Romain Francois
 *
 */
public class JRIEngineGlobalVariables implements Serializable {

	private static final long serialVersionUID = 1L;

	/** reference to the global environment */
	public REXPReference globalEnv ; 
	
	/** reference to the empty environment */
	public REXPReference emptyEnv ; 
	
	/** reference to the base environment */
	public REXPReference baseEnv ; 
	
	/** reference to the NULL value */
	public REXPReference nullValueRef;
	
	/** canonical NULL object */
	public REXPNull nullValue;
	
	/** hash code of the engine */
	public int hashCode ; 
	
	/**
	 * Constructor. Holds the values of the engine
	 * @param r Real server side R engine
	 */
	public JRIEngineGlobalVariables( 
			REXPReference globalEnv, 
			REXPReference emptyEnv, 
			REXPReference baseEnv, 
			REXPReference nullValueRef, 
			REXPNull nullValue, 
			int hashCode
			){
		this.globalEnv = globalEnv ;
		this.emptyEnv = emptyEnv ;
		this.baseEnv = baseEnv ;
		this.nullValue = nullValue ;
		this.nullValueRef = nullValueRef ;
		this.hashCode = hashCode ;
	}
	
}
