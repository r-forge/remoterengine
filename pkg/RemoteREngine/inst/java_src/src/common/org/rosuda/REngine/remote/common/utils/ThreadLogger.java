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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to write out information about all the threads within the JVM to a logger
 * @author $Author$
 * @version $Rev$ as of $Date$
 * <p>URL : $HeadURL$
 */
public class ThreadLogger {
	private final Logger logger = LoggerFactory.getLogger(org.rosuda.REngine.remote.common.utils.ThreadLogger.class);

	/**
	 * Log information about all the current threads
	 * @param eventDetails Message to be written into the log prior to the thread information
	 */
	public void logAllThreads(String eventDetails) {
		if (!logger.isDebugEnabled()) return; // All details written to debug so don't execute if not going to be logged
		logger.debug(eventDetails);
		ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
		while (root.getParent() != null) {
			root = root.getParent();
		}
		visit(root, 0);
	}

	/**
	 * Loop over all the threads within the thread group and sub groups and write out
	 * information about them
	 * @param group Thread group to be reported
	 * @param level Current level of the thread group
	 */
	private void visit(ThreadGroup group, int level) {
		logger.debug("ThreadGroup: {}",Integer.toString(level));
		int numThreads = group.activeCount();
		Thread[] threads = new Thread[numThreads*2];	// Allow head room on the number of threads returned
		numThreads = group.enumerate(threads, false);
		while (numThreads >= threads.length) {
			threads = new Thread[threads.length + 5];
			numThreads = group.enumerate(threads, false);
		}
		
		for (int i=0; i<numThreads; i++) {
			// Get thread
			Thread thread = threads[i];
			logger.debug(reportThread(thread));
		}
		
		int numGroups = group.activeGroupCount();
		ThreadGroup[] groups = new ThreadGroup[numGroups*2];
		numGroups = group.enumerate(groups, false);
		while (numGroups >= groups.length) {
			groups = new ThreadGroup[groups.length + 5];
			numGroups = group.enumerate(groups, false);
		}
		for (int i=0; i<numGroups; i++) {
			visit(groups[i], level+1);
		}
	}

	/**
	 * Helper method to return information about a thread. Returns the following information
	 * <p>
	 * <ul>
	 * <li>Name</li>
	 * <li>Alive</li>
	 * <li>State</li>
	 * <li>isDaemon</li>
	 * <li>Class.Method(line number)</li>
	 * <ul>
	 * @param thread
	 * @return
	 */
	private String reportThread(Thread thread) {
		StringBuilder details = new StringBuilder(thread.getName());
		details.append(": Alive? " + Boolean.toString(thread.isAlive()));
		details.append(" State: " + thread.getState().toString());
		details.append(" isDaemon? " + Boolean.toString(thread.isDaemon()));
		StackTraceElement[] stack = thread.getStackTrace();
		if (stack.length > 0) {
			details.append(" Location: ");
			details.append(stack[0].getClassName());
			details.append(".");
			details.append(stack[0].getMethodName());
			details.append("(" + stack[0].getLineNumber() + ")");
		}
		return details.toString();
	}

}
