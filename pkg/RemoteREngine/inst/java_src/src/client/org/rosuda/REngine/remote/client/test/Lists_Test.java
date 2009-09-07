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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.remote.client.RemoteREngine;

public class Lists_Test {

	private RemoteREngine r; 
	
	@Before
	public void setR(){
		r = TestEnvironment.cleanR() ;
	}
	
	@After
	public void forgetR(){
		r = null ; 
	}
	
	@Test
	public void assignList() throws REngineException, REXPMismatchException {
		int[] la = new int[]{ 0,1,2,3 } ;
		double[] lb = new double[]{0.5,1.2,2.3,3.0} ;
		
		RList l = new RList();
		l.put("a",new REXPInteger(la));
		l.put("b",new REXPDouble(lb));
		r.assign("x", new REXPList(l));
		assertTrue( "does the list exist",
			( (REXPLogical)r.parseAndEval("exists('x') && is.list(x)" ) ).isTRUE()[0] );
		REXP x = r.parseAndEval("x");
		assertEquals( "test java class of list" , "REXPList" , x.getClass().getSimpleName() ) ;
		RList xl = x.asList(); 
		assertEquals( "test length of list back in java", 2 , xl.size() ) ;
		assertEquals( "test class of list element (1)", "REXPInteger", xl.at(0).getClass().getSimpleName() ) ;
		assertArrayEquals("test values of list element (1)", la , xl.at(0).asIntegers() ) ;
		
		assertEquals( "test class of list element (2)", "REXPDouble", xl.at(1).getClass().getSimpleName() ) ;
		assertArrayEquals("test values of list element (2)", lb , xl.at(1).asDoubles(), 0.0 ) ;
		
		/* TODO: more tests on lists */
		
		/*
		r.assign("y", new REXPGenericVector(l) );
		r.assign("z", REXP.createDataFrame(l) );
		
		REXP y = r.parseAndEval("y");
		REXP z = r.parseAndEval("z");
		
		assertEquals( "test classes of generic vector" , "REXPList" , x.getClass().getSimpleName() ) ;
		*/
		
	}

}
