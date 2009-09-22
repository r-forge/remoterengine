package org.rosuda.REngine.remote.client;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPFactor;
import org.rosuda.REngine.RFactor;
import org.rosuda.REngine.remote.common.RemoteREngineTestBase;

/*
 * Copyright Stoat Software & Services 2009 <ilong@stoatsoftware.com>
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
 * @author $Author: ian_long $
 * @version $Rev: 129 $ as of $Date: 2009-09-15 12:18:19 +0200 (Tue, 15 Sep 2009) $
 * <p>URL : $HeadURL: svn+ssh://romain@svn.r-forge.r-project.org/svnroot/remoterengine/pkg/RemoteREngine/inst/java_src/test/org/rosuda/REngine/remote/client/TestFactors.java $
 */
public class TestFactors extends RemoteREngineTestBase {

	public TestFactors() throws Exception {
		super();
	}
	
	@Test
	public void testFactors() throws Exception {
		System.out.println("* Test support of factors");
		REXP f = r.parseAndEval("factor(paste('F',as.integer(runif(20)*5),sep=''))");
		System.out.println("  f="+f);
		System.out.println("  isFactor: "+f.isFactor()+", asFactor: "+f.asFactor());
		Assert.assertNotNull( f.isFactor(), "Returned a null factor");
		assert( f.isFactor()) : "Failed to return a factor";

/*		System.out.println("  singe-level factor used to degenerate:");
		f = r.parseAndEval("factor('foo')");
		System.out.println("  isFactor: "+f.isFactor()+", asFactor: "+f.asFactor());
		if (!f.isFactor() || f.asFactor() == null) throw new TestException("single factor test failed (not a factor)");
		if (!f.asFactor().at(0).equals("foo")) throw new TestException("single factor test failed (wrong value)");
		System.out.println("  test factors with null elements contents:");
		r.assign("f", new REXPFactor(new RFactor(new String[] { "foo", "bar", "foo", "foo", null, "bar" })));
		f = r.parseAndEval("f");
		if (!f.isFactor() || f.asFactor() == null) throw new TestException("factor assign-eval test failed (not a factor)");
		System.out.println("  f = "+f.asFactor());
		f = r.parseAndEval("as.factor(c(1,'a','b',1,'b'))");
		System.out.println("  f = "+f);
		if (!f.isFactor() || f.asFactor() == null) throw new TestException("factor test failed (not a factor)");
		System.out.println("PASSED");
*/
	}
}
