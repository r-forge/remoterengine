package org.rosuda.REngine.remote.client.test;

import org.junit.Test;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.remote.client.RemoteREngine;
import static org.junit.Assert.*;

public class Doubles_Test {

	private static RemoteREngine r = TestEnvironment.r ;
	
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
