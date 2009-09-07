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
package org.rosuda.REngine.remote.client.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.remote.client.RemoteREngine;

import static org.junit.Assert.*;

public class S3Objects_Test {

	private RemoteREngine r ; 
	
	@Before
	public void getR(){
		r = TestEnvironment.cleanR(); 
	}
	
	@After
	public void forgetR(){
		r = null  ;
	}
	
	@Test
	public void objectBit() throws REngineException, REXPMismatchException{
		
		int[] la = new int[]{ 0,1,2,3 } ;
		double[] lb = new double[]{0.5,1.2,2.3,3.0} ;
		
		RList l = new RList();
		l.put("a",new REXPInteger(la));
		l.put("b",new REXPDouble(lb));
		r.assign("z", REXP.createDataFrame(l) );
		// regression: object bit was not set for Java-side generated objects before 0.5-3
		REXP x = r.parseAndEval("z[2,2]");
		assertEquals( "length of result" , 1, x.length() ) ;
		assertEquals( "content of result", 1.2, x.asDouble(), 0.0 ) ;
		
		REXP z = r.get( "z", null, true) ;
		assertEquals( "test class attribute of the data frame", 
				"data.frame", z.getAttribute("class").asString() ) ;
		
	}
}
