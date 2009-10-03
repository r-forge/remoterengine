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

package org.rosuda.REngine.remote.server.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.rosuda.REngine.remote.common.exceptions.ServerSideIOException;
import org.rosuda.REngine.remote.common.files.RemoteFileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link RemoteFileOutputStream} backed by 
 * a {@link FileOutputStream}
 * 
 * @author Romain Francois
 *
 */
public class RemoteFileOutputStream_Server implements RemoteFileOutputStream {

	private final Logger logger = LoggerFactory.getLogger(org.rosuda.REngine.remote.server.files.RemoteFileOutputStream_Server.class);

	/**
	 * The back end {@link FileOutputStream}
	 */
	private FileOutputStream stream ; 
	
	/**
	 * Constructor
	 * @param file the file to open 
	 * @throws ServerSideIOException
	 */
	public RemoteFileOutputStream_Server( File file) throws ServerSideIOException{
		try{
			stream = new FileOutputStream( file ) ;
		} catch( IOException ioe){
			logger.error(ioe.getClass().getName() + ": " + ioe.getMessage(),ioe);
			throw new ServerSideIOException( ioe ) ;
		}
	}
	
	/**
	 * Constructor based on the file name
	 * @param filename name of the file to open
	 * @throws ServerSideIOException
	 */
	public RemoteFileOutputStream_Server( String filename) throws ServerSideIOException{
		this( new File( filename ) ) ;
	}
	
	public void close() throws ServerSideIOException {
		try{
			stream.close() ; 	
		} catch( IOException ioe){
			logger.error(ioe.getClass().getName() + ": " + ioe.getMessage(),ioe);
			throw new ServerSideIOException( ioe ) ;
		} finally {
			stream = null;
		}
	}

	public void flush() throws ServerSideIOException {
		try{
			stream.flush(); 
		} catch( IOException ioe){
			logger.error(ioe.getClass().getName() + ": " + ioe.getMessage(),ioe);
			throw new ServerSideIOException( ioe ) ;
		}
	}

	public void write(byte[] b) throws ServerSideIOException {
		try{
			stream.write( b ) ;
		} catch( IOException ioe){
			logger.error(ioe.getClass().getName() + ": " + ioe.getMessage(),ioe);
			throw new ServerSideIOException( ioe ); 
		}
	}

	public void write(byte[] b, int off, int len) throws ServerSideIOException {
		try{
			stream.write( b, off, len) ;
		} catch( IOException ioe){
			logger.error(ioe.getClass().getName() + ": " + ioe.getMessage(),ioe);
			throw new ServerSideIOException( ioe ) ;
		}
	}

}
