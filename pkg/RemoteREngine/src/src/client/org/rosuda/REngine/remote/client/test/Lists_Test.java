package org.rosuda.REngine.remote.client.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.remote.client.RemoteREngine;
import static org.junit.Assert.*;

public class Lists_Test {

	private RemoteREngine r; 
	
	@Before
	public void setR(){
		r = TestEnvironment.r ;
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
			( (REXPLogical)r.parseAndEval("exists(x) && is.list(x)" ) ).isTRUE()[0] );
		r.assign("y", new REXPGenericVector(l) );
		r.assign("z", REXP.createDataFrame(l) );
		REXP x = r.parseAndEval("x");
		assertEquals( "test java class of list" , "REXPList" , x.getClass().getSimpleName() ) ;
		RList xl = x.asList(); 
		assertEquals( "test length of list back in java", 2 , xl.size() ) ;
		assertEquals( "test class of list element (1)", "REXPInteger", xl.at(0).getClass().getSimpleName() ) ;
		assertArrayEquals("test values of list element (1)", la , xl.at(0).asIntegers() ) ;
		
		assertEquals( "test class of list element (2)", "REXPDouble", xl.at(1).getClass().getSimpleName() ) ;
		assertArrayEquals("test values of list element (2)", lb , xl.at(1).asDoubles(), 0.0 ) ;
		
		REXP z = r.parseAndEval("z");
		assertEquals( "test class attribute of the data frame", 
				"data.frame", z.getAttribute("class") ) ;
		/* TODO: more */
		
		/*
		REXP y = r.parseAndEval("y");
		
		
		assertEquals( "test classes of generic vector" , "REXPList" , x.getClass().getSimpleName() ) ;
		*/
		
	}

}
