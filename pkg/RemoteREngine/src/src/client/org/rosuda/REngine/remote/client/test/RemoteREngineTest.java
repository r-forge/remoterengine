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

package org.rosuda.REngine.remote.client.test ;

/*{{{ imports */
import java.io.File;
import java.util.Map;

import org.rosuda.REngine.*;
import org.rosuda.REngine.remote.client.RemoteREngine ;
import org.rosuda.REngine.remote.common.CommandLineArgs;

import static org.rosuda.REngine.remote.common.RemoteREngineConstants.* ;
/*}}} */

public class RemoteREngineTest {

	/*{{{ main */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		/*{{{ security manager */
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		/*}}}*/

		try { 

			String name = DEFAULTNAME ;
			String host = RMIHOSTNAME ;
			int port = 1099; 
			
			
			Map<String,String> arguments = CommandLineArgs.arguments(args) ;
			if( arguments.containsKey("host")){
				host = arguments.get("host") ;
			}
			if( arguments.containsKey("name")){
				name = arguments.get("name");
			}
			if( arguments.containsKey("port")){
				try{
					port = Integer.parseInt(arguments.get("port")) ;
				} catch( NumberFormatException ex){
					System.out.println("Could not parse port into a integer, falling back on default port : 1099 ");
				}
			}
			
			
			/*{{{ Init */
				RemoteREngine eng = new RemoteREngine( name, host, port );
				System.out.println("R Version: " + eng.parseAndEval("R.version.string").asString());
				/*}}}*/

				/*{{{ * Testing functionality of assembled S3 objects ... */
				{ // regression: object bit was not set for Java-side generated objects before 0.5-3
					System.out.println("* Testing functionality of assembled S3 objects ...");
					// we have already assigned the data.frame in previous test, so we jsut re-use it
					REXP x = eng.parseAndEval("z[2,2]");
					System.out.println("  z[2,2] = " + x);
					if (x == null || x.length() != 1 || x.asDouble() != 1.2)
						throw new TestException("S3 object bit regression test failed");
					System.out.println("PASSED");
				}
				/*}}}*/

				/*{{{ * Testing pass-though capability for data.frames ... */
				{ // this test does a pull and push of a data frame. It will fail when the S3 test above failed.
					System.out.println("* Testing pass-though capability for data.frames ...");
					REXP df = eng.parseAndEval("{data(iris); iris}");
					eng.assign("df", df);
					REXP x = eng.parseAndEval("identical(df, iris)");
					System.out.println("  identical(df, iris) = "+x);
					if (x == null || !x.isLogical() || x.length() != 1 || !((REXPLogical)x).isTrue()[0])
						throw new TestException("Pass-through test for a data.frame failed");
					System.out.println("PASSED");
				}
				/*}}}*/

				/*{{{ * Test support of factors */
				{ // factors
					System.out.println("* Test support of factors");
					REXP f = eng.parseAndEval("factor(paste('F',as.integer(runif(20)*5),sep=''))");
					System.out.println("  f="+f);
					System.out.println("  isFactor: "+f.isFactor()+", asFactor: "+f.asFactor());
					if (!f.isFactor() || f.asFactor() == null) throw new TestException("factor test failed");
					System.out.println("  singe-level factor used to degenerate:");
					f = eng.parseAndEval("factor('foo')");
					System.out.println("  isFactor: "+f.isFactor()+", asFactor: "+f.asFactor());
					if (!f.isFactor() || f.asFactor() == null) throw new TestException("single factor test failed (not a factor)");
					if (!f.asFactor().at(0).equals("foo")) throw new TestException("single factor test failed (wrong value)");
					System.out.println("  test factors with null elements contents:");
					eng.assign("f", new REXPFactor(new RFactor(new String[] { "foo", "bar", "foo", "foo", null, "bar" })));
					f = eng.parseAndEval("f");
					if (!f.isFactor() || f.asFactor() == null) throw new TestException("factor assign-eval test failed (not a factor)");
					System.out.println("  f = "+f.asFactor());
					f = eng.parseAndEval("as.factor(c(1,'a','b',1,'b'))");
					System.out.println("  f = "+f);
					if (!f.isFactor() || f.asFactor() == null) throw new TestException("factor test failed (not a factor)");
					System.out.println("PASSED");
				}
				/*}}}*/

				/*{{{ * Lowess test */
				{
					System.out.println("* Lowess test");
					double x[] = eng.parseAndEval("rnorm(100)").asDoubles();
					double y[] = eng.parseAndEval("rnorm(100)").asDoubles();
					eng.assign("x", x);
					eng.assign("y", y);
					RList l = eng.parseAndEval("lowess(x,y)").asList();
					System.out.println("  "+l);
					x = l.at("x").asDoubles();
					y = l.at("y").asDoubles();
					System.out.println("PASSED");
				}
				/*}}}*/

				/*{{{ * Test multi-line expressions */
				{
					// multi-line expressions
					System.out.println("* Test multi-line expressions");
					if (eng.parseAndEval("{ a=1:10\nb=11:20\nmean(b-a) }\n").asInteger()!=10)
						throw new TestException("multi-line test failed.");
					System.out.println("PASSED");
				}
				/*}}}*/

				/*{{{ * Matrix tests\n  matrix: create a matrix */
				{
					System.out.println("* Matrix tests\n  matrix: create a matrix");
					int m=100, n=100;
					double[] mat=new double[m*n];
					int i=0;
					while (i<m*n) mat[i++]=i/100;
					System.out.println("  matrix: assign a matrix");
					eng.assign("m", mat);
					eng.parseAndEval("m<-matrix(m,"+m+","+n+")", null, false); // don't return the result - it has a similar effect to voidEval in Rserve
					System.out.println("matrix: cross-product");
					@SuppressWarnings("unused")
					double[][] mr=eng.parseAndEval("crossprod(m,m)").asDoubleMatrix();
					System.out.println("PASSED");
				}
				/*}}}*/

				/*{{{ * Test serialization and raw vectors */
				{
					System.out.println("* Test serialization and raw vectors");
					byte[] b = eng.parseAndEval("serialize(ls, NULL, ascii=FALSE)").asBytes();
					System.out.println("  serialized ls is "+b.length+" bytes long");
					eng.assign("r", new REXPRaw(b));
					String[] s = eng.parseAndEval("unserialize(r)()").asStrings();
					System.out.println("  we have "+s.length+" items in the workspace");
					System.out.println("PASSED");
				}
				/*}}}*/

				/*{{{ * Test enviroment support */
				{
					System.out.println("* Test enviroment support");
					REXP x = eng.parseAndEval("new.env(parent=baseenv())");
					System.out.println("  new.env() = " + x);
					if (x == null || !x.isEnvironment()) throw new TestException("pull of an environemnt failed");
					System.out.println( x.getClass() ) ; 
					REXPEnvironment e = (REXPEnvironment) x;
					e.assign("foo", new REXPString("bar"));
					x = e.get("foo");
					System.out.println("  get(\"foo\") = " + x);
					if (x == null || !x.isString() || !x.asString().equals("bar")) throw new TestException("assign/get in an environemnt failed");
					x = eng.newEnvironment(e, true);
					System.out.println("  eng.newEnvironment() = " + x);
					if (x == null || !x.isEnvironment()) throw new TestException("Java-side environment creation failed");
					x = ((REXPEnvironment)x).parent(true);
					System.out.println("  parent = " + x);
					if (x == null || !x.isEnvironment()) throw new TestException("parent environment pull failed");
					x = e.get("foo");
					System.out.println("  get(\"foo\",parent) = " + x);
					if (x == null || !x.isString() || !x.asString().equals("bar")) throw new TestException("get in the parent environemnt failed");
					System.out.println("PASSED");
				}
				/*}}}*/
				
				/*{{{ * Test file transfer */
				{
					System.out.println("* Test file transfer");
					System.out.println("  generating pdf file in the server side");
					eng.parseAndEval( "pdf('test.pdf'); plot( rnorm(10) ) ;dev.off()" ) ;
					System.out.println("  bring the file to the client");
					eng.fetchFile("test.pdf", "test.pdf", true) ;
					File clientfile = new File( "test.pdf" ) ;
					if( !clientfile.exists() ){
						 throw new TestException("Could not bring file from server");
					}
					System.out.println("  check that the server file has been deleted");
					boolean ok = ( (REXPLogical)eng.parseAndEval(" ! file.exists( 'test.pdf' ) ") ).isTRUE()[0] ;
					if( !ok ){
						 throw new TestException("server file was not deleted");
					}
					System.out.println("  push file to the server");
					eng.pushFile( "test.pdf", "filefromclient.pdf" , true ) ;
					ok = ( (REXPLogical)eng.parseAndEval("file.exists( 'filefromclient.pdf' ) ") ).isTRUE()[0] ;
					if( !ok ){
						 throw new TestException("  file was not transferred to the server");
					}
					/* clean */
					eng.parseAndEval( "unlink( 'filefromclient.pdf' )" ) ;
					clientfile.delete(); 
					System.out.println("PASSED");
				}
				/*}}} */
				

				System.out.println("Done.");

		} catch (REXPMismatchException me) {
			// some type is different from what you (the programmer) expected
			System.err.println("Type mismatch: "+me);
			me.printStackTrace();
			System.exit(1);
		} catch (REngineException ee) {
			// something went wring in the engine
			System.err.println("REngine exception: "+ee);
			ee.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			// some other exception ...
			System.err.println("Exception: "+e);
			e.printStackTrace();
			System.exit(1);
		}

	}
	/*}}}*/

	/*{{{ class TestException */
	@SuppressWarnings("serial")
	private static class TestException extends Exception {
		public TestException(String msg) {	
			super(msg);
		}
	}
	/*}}}*/

	// :tabSize=4:indentSize=4:noTabs=false:folding=explicit:collapseFolds=1:
}
