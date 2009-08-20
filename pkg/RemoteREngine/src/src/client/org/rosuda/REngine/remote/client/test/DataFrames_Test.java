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
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.remote.client.RemoteREngine;

/**
 * Testing pass-though capability for data.frames ...
 * this test does a pull and push of a data frame. It will fail when the S3 test above failed.
 * 
 * @author Romain Francois
 *
 */
public class DataFrames_Test {

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
	public void pullAndPush() throws REngineException, REXPMismatchException {
		REXP df = r.parseAndEval("{data(iris); iris}");
		int ncols = df.asList().size() ;
		assertEquals("ncols of pulled data frame", 5, ncols ); 
		int[] nrows = new int[ncols] ;
		for( int i=0; i<ncols; i++){
			nrows[i] = df.asList().at(i).length() ;
		}
		int[] expected = {150,150,150,150,150}; 
		assertArrayEquals("nrows of pulled data frame", nrows, expected ); 
		
		r.assign("df", df);
		assertEquals("ncols of pushed data frame", 5, r.parseAndEval("ncol(df)").asInteger() ) ;
		assertEquals("nrows of pushed data frame", 5, r.parseAndEval("nrow(df)").asInteger() ) ;
		boolean identical = ((REXPLogical)r.parseAndEval("identical(df, iris)")).isTRUE()[0] ;
		assertTrue( "identical( df, iris )", identical ) ;
	}

}
