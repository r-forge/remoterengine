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
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.remote.client.RemoteREngine;
import static org.junit.Assert.*;

public class Doubles_Test {

private RemoteREngine r ; 
	
	@Before
	public void getR(){
		r = TestEnvironment.cleanR() ;
	}
	
	@After
	public void forgetR(){
		r = null; 
	}

	@Test
	public void pushNAtoR() throws REngineException, REXPMismatchException {
		double x[] = { 1.0, 0.5, REXPDouble.NA , Double.NaN, 3.5 };
		r.assign("x",x);
		String nas = r.parseAndEval("paste(capture.output(print(x)),collapse='\\n')").asString();
		assertEquals("testing NA support", "[1] 1.0 0.5  NA NaN 3.5", nas) ;
	}
	
	@Test
	public void getNAFromR() throws REngineException, REXPMismatchException{
		double[] y = r.parseAndEval( "c(NA,1.0,NaN,-Inf,Inf)").asDoubles() ;
		assertTrue( "testing NA values with isNA", REXPDouble.isNA(y[0]) );
		assertTrue( "testing NaN", Double.isNaN(y[2]) ) ;
		assertTrue( "testing infinite values + ", Double.isInfinite(y[3]) ) ;
		assertTrue( "testing infinite values - ", Double.isInfinite(y[4]) ) ;
	}
}
