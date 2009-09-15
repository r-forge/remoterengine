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
 * @author $Author$
 * @version $Rev$ as of $Date$
 * <p>URL : $HeadURL$
 */
public class RemoteREngineTestBase {
	/** Instance of the Remote R Engine available for tests */
	protected static RemoteREngine r = init();

	/**
	 * Create a connection to a running remote server
	 * @return Reference to a remote engine
	 */
	@BeforeSuite
	protected static RemoteREngine init(){
/*		Properties props = System.getProperties();
		for (Enumeration<?> enumerator = props.keys(); enumerator.hasMoreElements(); ) {
			String key = (String)enumerator.nextElement();
			String value = props.getProperty(key);
			System.out.println(key + " : " + value);
		}
*/
		RemoteREngine r = null ;
		String name = "RemoteREngineTest";
		int registryPort = RemoteREngineConstants.RMIPORT;
		try{
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
			r = new RemoteREngine( name , RemoteREngineConstants.RMIHOSTNAME , registryPort );
			System.out.println("Connected to " + name + " via " + RemoteREngineConstants.RMIHOSTNAME + 
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
