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

package org.rosuda.REngine.remote.common.files;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rosuda.REngine.remote.common.exceptions.ServerSideIOException;

/**
 * Input stream to read a file from the server 
 * @author Romain Francois
 */
public interface RemoteFileInputStream extends Remote {

	/**
	 * Returns an estimate of the number of bytes that can be read (or skipped over) from this input stream 
	 * without blocking by the next invocation of a method for this input stream.
	 * 
	 * @throws ServerSideIOException when an IO exception happens on the server side
	 */
	public int available() throws RemoteException, ServerSideIOException ;
	
	/**
	 * Closes this input stream and releases any system resources associated with the stream.
	 * @throws ServerSideIOException when an IO exception happens on the server side 
	 */
	public void close() throws RemoteException, ServerSideIOException  ;
	
	/**
	 * Marks the current position in this input stream.
	 * @param readlimit 
	 */
	public void mark(int readlimit) throws RemoteException ;
     
	/**
	 * Tests if this input stream supports the mark and reset methods
	 * 
	 * @return
	 */
	public boolean 	markSupported() throws RemoteException ;
    
	/**
	 * Repositions this stream to the position at the time the mark method was last called on this input stream.
	 * @throws ServerSideIOException when an IO exception happens on the server side 
	 */
	public void reset() throws RemoteException, ServerSideIOException ; 
     
    /**
     * Skips over and discards n bytes of data from this input stream.
     * @param n
     * @return
     * @throws ServerSideIOException when an IO exception happens on the server side 
	 */
	public long skip(long n) throws RemoteException, ServerSideIOException ;
   
	/**
	 * Set the buffer size of the stream  
	 * @param n the size of the byte[] used by the stream
	 */
	public void setBufferSize( int n) throws RemoteException ;

	/**
	 * @return the size of the buffer used by the stream
	 */
	public int getBufferSize() throws RemoteException ;

	/**
	 * Reads the next chunk  
	 * @return the next chunk of data as a byte[] or null
	 * @throws ServerSideIOException when an IO exception happens on the server side 
	 */
	public FileChunk readNextChunk() throws ServerSideIOException, RemoteException;

	/**
	 * Delete the file
	 * @throws ServerSideIOException when an IO exception happens on the server side 
	 */
	public boolean delete() throws ServerSideIOException, RemoteException ; 
	
}
