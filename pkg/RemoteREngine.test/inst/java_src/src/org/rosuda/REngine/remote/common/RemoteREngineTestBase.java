package org.rosuda.REngine.remote.common;

import java.util.Enumeration;
import java.util.Properties;

import org.rosuda.REngine.remote.client.RemoteREngine;
import org.testng.annotations.BeforeSuite;

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
 * <p>URL : $HeadURL: svn+ssh://romain@svn.r-forge.r-project.org/svnroot/remoterengine/pkg/RemoteREngine/inst/java_src/test/org/rosuda/REngine/remote/common/RemoteREngineTestBase.java $
 */
public abstract class RemoteREngineTestBase {
	/** Instance of the Remote R Engine available for tests */
	protected static RemoteREngine r = null;

	/**
	 * Call init() to connect to an instance of the RemoteREngine
	 */
	protected RemoteREngineTestBase() {
		super();
		init();
	}
	
	/**
	 * Create a connection to a running remote server
	 * @return Reference to a remote engine
	 */
	protected static RemoteREngine init() {

		String name = "RemoteREngineTest";
		int registryPort = RemoteREngineConstants.RMIPORT;
		try{
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
			r = new RemoteREngine( name , RemoteREngineConstants.RMIHOSTNAME , registryPort );
			 
			System.out.println(((r != null) ? "Connected to " : "Failed to connect to ") + name + " via " + RemoteREngineConstants.RMIHOSTNAME + 
					" registry on port " + registryPort);
		} catch( Exception e ){
			System.err.println( "cannot connect to R engine" ) ;
			System.exit(1); 
		}
		return r; 
	}
	
	protected static RemoteREngine cleanR(){
		try{
			r.parseAndEval( "rm( list = ls() )" ) ;
		} catch(Exception e){
			// should not happen
		}
		return r; 
	}

}
