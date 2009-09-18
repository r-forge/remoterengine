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

import org.rosuda.REngine.REXPReference;
import org.rosuda.REngine.UniqueID;

/**
 * Encapsulates global variables of the JRIEngine 
 * 
 * @author Romain Francois
 *
 */
public class JRIEngineGlobalVariables implements Serializable {

	private static final long serialVersionUID = 1L;

	/** reference to the global environment */
	public long p_globalEnv ; 
	
	/** reference to the empty environment */
	public long p_emptyEnv ; 
	
	/** reference to the base environment */
	public long p_baseEnv ; 
	
	/** reference to the NULL value */
	public long p_nullValueRef;
		
	/** identifier of the server side (real) engine */
	public UniqueID engine_id; 
	
	/**
	 * Constructor. Holds the values of the engine
	*/
	public JRIEngineGlobalVariables( 
			long p_globalEnv, 
			long p_emptyEnv, 
			long p_baseEnv, 
			long p_nullValueRef, 
			UniqueID engine_id
			){
		this.p_globalEnv = p_globalEnv ;
		this.p_emptyEnv = p_emptyEnv ;
		this.p_baseEnv = p_baseEnv ;
		this.p_nullValueRef = p_nullValueRef ;
		this.engine_id = engine_id ;
	}
	
}
