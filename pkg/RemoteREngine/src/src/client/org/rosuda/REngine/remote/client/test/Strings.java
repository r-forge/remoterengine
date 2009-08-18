package org.rosuda.REngine.remote.client.test;

import org.junit.Test;
import static org.junit.Assert.* ;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.remote.client.RemoteREngine;

public class Strings extends RemoteREngineTest {

	private static RemoteREngine r = TestSuite.r ; 

	@Test
	public void strings(){
		RList l;
		try {
			l = r.parseAndEval("{d=data.frame(\"huhu\",c(11:20)); lapply(d,as.character)}").asList();
			int cols = l.size();
			assertEquals( cols, 2) ;
			int rows = l.at(0).length();
			assertEquals( rows, 10 ) ;
			
			String[][] s = new String[cols][];
			for (int i=0; i<cols; i++){
				s[i]=l.at(i).asStrings();
			}
			assertEquals( s[0][0], "huhu" ); 
			int size = s[1].length ;
			assertEquals( size, 10 ) ;
			for( int i=0; i<size; i++){
				assertEquals( s[1][i], ""+(11+i)) ;
			}
			
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
