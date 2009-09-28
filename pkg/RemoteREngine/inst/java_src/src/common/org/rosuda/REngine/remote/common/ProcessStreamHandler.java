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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @author $Author$
 * @version $Rev$ as of $Date$
 * <p>URL : $HeadURL$
 */
public class ProcessStreamHandler implements Runnable {
	/** Define the stream this process is going to be handling */
	private InputStream inputStream;
	/** Define the stream this process is going to write out to */
	private OutputStream outputStream;
	
	/**
	 * Construct a stream handler, otherwise output is directed to StdOut
	 * @param intputStream
	 */
	ProcessStreamHandler(InputStream intputStream ) {
		this(intputStream, null);
	}

	/**
	 * Construct stream handler defining the source and the output streams
	 * @param inputStream Stream to be handled by this process
	 * @param outputStream Stream to write output to
	 */
	ProcessStreamHandler(InputStream inputStream, OutputStream outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	ProcessStreamHandler(InputStream inputStream, String outputFilename, boolean append) {
		this.inputStream = inputStream;
		try {
			this.outputStream = new FileOutputStream(outputFilename, append);
		} catch (FileNotFoundException e) {
			System.err.println("Unable to locate " + outputFilename + " for output streaming");
		}
	}
	
	/**
	 * Ensure we shut down the outputStream cleanly
	 */
	public void close() {
		if (outputStream == null) return;
		try {
			outputStream.flush();
		} catch (IOException e) {
		} finally {
			try {
				outputStream.close();				
			} catch (IOException io) {}
			finally {
				this.outputStream = null;
			}
		}
	}
	
	/**
	 * Start the stream processing
	 */
	public void run() {
		try {
			PrintWriter printWriter = null;
			if (outputStream != null) printWriter = new PrintWriter(outputStream);
	
			InputStreamReader isr = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(isr);
			String line=null;
			while ( (line = br.readLine()) != null) {
				if (printWriter != null) 
					printWriter.println(line);
				else
					System.out.println(line);    
			}
			if (printWriter != null) printWriter.flush();
		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());  
		}
	}
}
