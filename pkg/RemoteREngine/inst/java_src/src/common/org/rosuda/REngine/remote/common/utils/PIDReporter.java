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

package org.rosuda.REngine.remote.common.utils;

import java.lang.management.ManagementFactory;
import java.util.StringTokenizer;

/**
 * Class to return an estimate of the JVM's process Id. This implementation is not guaranteed to work
 * on all OSs - no Java PID solution is, however it appears to work on most common ones.
 * @author $Author$
 * @version $Rev$ as of $Date$
 * <p>URL : $HeadURL$
 */
public class PIDReporter {
	/**
	 * Method to return an estimate of the JVM's process Id. This implementation is not guaranteed to work
	 * on all OSs - no Java PID solution is, however it appears to work on most common ones.
	 * @return ProcessId of the JVM
	 */
	public static String getPID() {
		try {
			String name = ManagementFactory.getRuntimeMXBean().getName();
			if (name.indexOf("@") > 0) {
				StringTokenizer tok = new StringTokenizer(name,"@");
				return tok.nextToken();
			} else  {
				return name;
			}
		} catch (Throwable t) {
			return t.getClass().getName() + ":" + (t.getMessage() != null ? t.getMessage() : "");
		}
	}
}
