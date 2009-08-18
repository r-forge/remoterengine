package org.rosuda.REngine.remote.client.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPReference;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.remote.client.RemoteREngine;

public class Strings_Test extends RemoteREngineTest {

	private static RemoteREngine r = TestEnvironment.r ; 

	@Test 
	public void singleString() throws REngineException, REXPMismatchException {
		assertEquals( "retrieving one character string", 
				r.parseAndEval( "'hello'" ).asString(), "hello" ) ;

		String a = new String( "world" ) ;
		r.assign( "test" , a ) ;
		boolean success = ((REXPLogical)r.parseAndEval( "exists( 'test' ) && is.character( test ) && test == 'world' ")) .isTRUE()[0] ; 
		assertTrue( "push a java string to R" , success ) ; 
				
		assertEquals( "retrieve a string (parseAndEval)",
				r.parseAndEval("test").asString(), a ) ;
		assertEquals( "retrieve a string (get)",
				r.get("test", r.globalEnv, true).asString(), a ) ;

		REXPReference ref = (REXPReference)r.get("test", r.globalEnv, false) ;
		assertEquals( "retrieve a string (get+resolve)", 
				ref.resolve().asString() , a ) ;

		REXPReference ref2 = (REXPReference)r.get("test", r.globalEnv, false) ;
		assertEquals( "retrieve a string (get+auto resolve)",
				ref2.asString() , a ) ;
		r.parseAndEval( "rm(test)" ) ;
	}

	@Test
	public void stringArray() throws REngineException, REXPMismatchException {
		String[] letters = r.parseAndEval( "letters" ).asStrings() ;
		assertEquals( 26, letters.length ) ;
		assertEquals( "a", letters[0] ) ;
		assertEquals( "z", letters[25] ) ;
		
		String[] fruits = new String[]{ "apple", "banana" } ;
		r.assign("fruits", fruits ) ;
		assertEquals( 2 , r.parseAndEval( "length(fruits)" ).asInteger() ) ;
		
	}

	@Test
	public void dataframeColumn() throws REngineException, REXPMismatchException {
		RList l = r.parseAndEval("{d=data.frame(\"huhu\",c(11:20)); lapply(d,as.character)}").asList();
		int cols = l.size();
		assertEquals( "checking number of columns", cols, 2) ;
		int rows = l.at(0).length();
		assertEquals( "checking number of rows", rows, 10 ) ;

		String[] col1 = new String[ rows ] ;
		for( int i=0; i<rows; i++){
			col1[i] = "huhu" ;
		}
		assertArrayEquals( "checking column 1" , col1, l.at(0).asStrings() ) ;
		
		String[] col2 = new String[ rows ] ;
		for( int i=0; i<rows; i++){
			col2[i] = ""+(11+i) ;
		}
		assertArrayEquals( "checking column 2" , col1, l.at(0).asStrings() ) ;
		
	}

}
