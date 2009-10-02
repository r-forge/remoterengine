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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.AccessException;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to return the contents of a file as a String
 * @author $Author$
 * @version $Rev$ as of $Date$
 * <p>URL : $HeadURL$
 */
public class FileParser {
	private final Logger logger = LoggerFactory.getLogger(org.rosuda.REngine.remote.common.utils.FileParser.class);
	
	/**
	 * Read the content of the init script as a single string
	 * @param filePath Path to a local file on the server
	 * @return String containing the contents of the file
	 * @throws AccessException
	 */
	public String readFile(String filePath) throws AccessException{
		String[] lines = readLines(filePath);
		StringBuilder completeFile = new StringBuilder();
		for (String line : lines) {
			completeFile.append(line);
		}
		return completeFile.toString();
	}
	
	/**
	 * Read the content of the init script as an array of Strings
	 * @param filePath Path to a local file on the server
	 * @return The contents of the file
	 * @throws AccessException
	 */
	public String[] readLines(String filePath) throws AccessException {
		File file = new File(filePath);
		logger.debug("Executing: {}",file.getAbsolutePath());
		InputStream in = null;
		Vector<String> lines = new Vector<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line=reader.readLine()) != null) {
				lines.add(line);
			}
			return lines.toArray(new String[0]);
		} catch (FileNotFoundException e) {
			String msg = "Init script '"+filePath+"' not found.";
			logger.error(msg,e);
			throw new AccessException(msg, e);
		} catch (IOException e) {
			String msg = "Error in reading Init script '"+filePath+"', "+e.getMessage();
			logger.error(msg,e);
			throw new AccessException(msg, e);
		} finally{
			closeInputStream(in);
		}
	}	
	
	/**
	 * Close the input stream silently.
	 * 
	 * @param in Stream to be closed
	 */
	private void closeInputStream(InputStream in) {
		if (in == null){
			return;
		}
		try {
			in.close();
		} catch (IOException e) {
			logger.error("Error in close inputstream :"+e.getMessage(),e);
		} finally {
			in = null;
		}
	}

}
