/*
 * Copyright (c) 2009, Ian Long <ilong@stoatsoftware.com>
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

package org.rosuda.REngine.remote.client;

import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.rosuda.REngine.remote.common.CommandLineArgs;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author $Author$
 * @version $Rev$ as of $Date$
 * <p>URL : $HeadURL$
 */
public class TestCommandLineArgs {
	Vector<String>propertiesChanged = new Vector<String>();
	Properties originalSystemProperties = null;
	
	/**
	 * Cache the System properties so we can set them back to their original
	 * settings after the tests
	 */
	@BeforeTest
	public void cacheProperties() {
		originalSystemProperties = System.getProperties();
	}
	
	/**
	 * Restore the System Properties
	 */
	@AfterTest
	public void returnProperties() {
		for (String property : propertiesChanged) {
			System.setProperty(property, originalSystemProperties.getProperty(property));
		}
	}
	
	@Test
	public void testArguments() {
		Map<String, String> args = CommandLineArgs.arguments(new String[] {"--key1","--key2","value"});
		Assert.assertNotNull(args,"Null map returned");
		Assert.assertEquals(args.size(),2,"Incorrect number of entries returned");
		Assert.assertNotNull(args.get("key1"),"No entry for key1");
		Assert.assertEquals(args.get("key1"),"yes","Boolean type not inferred correctly");
		Assert.assertEquals(args.get("key2"),"value","value not returned correctly");
				
		Map<String, String> args2 = CommandLineArgs.arguments(new String[] {"--key","value","--key","value2"});
		Assert.assertEquals(args2.size(),1,"Repeat entries not handled correctly");
		Assert.assertEquals(args2.get("key"),"value","value not returned correctly");
	}

}
