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
package org.rosuda.REngine.remote.server.console;

import java.util.LinkedList;
import java.util.Queue;

import org.rosuda.REngine.remote.server.RemoteREngine_Server;

public class ConsoleSync {

		/**
		 * The command queue
		 */
		private Queue<String> input ; 

		/**
		 * Server associated with this synchronizer
		 */
		private RemoteREngine_Server server ; 
		
		/**
		 * Constructor
		 */
		public ConsoleSync(RemoteREngine_Server server) {
			input = new LinkedList<String>();
			this.server = server; 
		}

		/**
		 * Waits until the queue is not empty and returns its head
		 * @return the head of the command queue
		 */
		public synchronized String waitForInput() {
			while ( input.isEmpty() ){
				try {
					wait(100);
					server.rniIdle() ;
				} catch (InterruptedException e) { }
			}
			return input.poll() ; 
		}

		/**
		 * Adds a command to the queue
		 * @param msg Command
		 * @param echo should the command be echoed in the console
		 */
		public synchronized void addInput(String msg) {
			input.add(msg); 
			notifyAll();
		}
	
}
