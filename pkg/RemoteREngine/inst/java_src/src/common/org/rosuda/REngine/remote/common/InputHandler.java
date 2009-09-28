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
package org.rosuda.REngine.remote.common;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Class to intercept console input and redirect it to an internal process 
 * @author Ian
 */
public class InputHandler implements Runnable {
	/** Internal flag to stop the thread */
	private boolean run = true;
	/** Reader for the console */
	private BufferedReader in = null;
	/** Stream to direct any input to */
	private OutputStream output = null;
	
	/**
	 * Initialise the handler, defining the input stream that this thread
	 * redirects any StdIn input to
	 * @param output Stream to pass the console input to
	 */
	public InputHandler(OutputStream output) {
		in = new BufferedReader(	new InputStreamReader(System.in) );
		this.output = output;
	}

	/**
	 * Main method for the handler which passes console input into the main processing
	 */
	public void run() {
	
		String res = null;
		while (run) {
			try{
				res = in.readLine() ;
				if (res.equals("Quit")) {
					System.out.println(this.getClass().getName() + " preparing to close");
					run = false;
				}
				res = res + "\n";	// Put on the line feed because readLine strips it off
				if (output != null) {	// output may be null if there is a start up problem
					output.write(res.getBytes());
					output.flush();
				}
			} catch( IOException e){
				System.err.println(this.getClass().getName() + " ERROR: " + e.getMessage());
			}
		}
		close();
	}

	/**
	 * Shut down this thread cleanly
	 */
	public void close() {
		System.out.println(this.getClass().getName() + " stop requested");
		run = false;
		try {
			output.flush();
			output.close();
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + " Error closing stream");
		} finally {
			output = null;
			System.out.println(this.getClass().getName() + " shut down");
		}
	}
}