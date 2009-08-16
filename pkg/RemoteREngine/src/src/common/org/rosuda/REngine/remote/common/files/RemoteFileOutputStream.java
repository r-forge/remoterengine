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

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rosuda.REngine.remote.common.exceptions.ServerSideIOException;

public interface RemoteFileOutputStream extends Remote {

	/**
	 * Closes this output stream and releases any system resources associated with this stream.
	 * @throws ServerSideIOException 
	 */
	public void 	close() throws RemoteException, ServerSideIOException ; 

	/**
	 * Flushes this output stream and forces any buffered output bytes to be written out.
	 * @throws IOException 
	 */
	public void 	flush() throws RemoteException, ServerSideIOException ; 

	/**
	 * Writes b.length bytes from the specified byte array to this output stream.
	 * @throws IOException 
	 */
	public void 	write(byte[] b) throws RemoteException, ServerSideIOException  ; 

	/** Writes len bytes from the specified byte array starting at offset off to this output stream.
	 * @param b
	 * @param off
	 * @param len
	 * @throws IOException 
	 */
	public void 	write(byte[] b, int off, int len) throws RemoteException, ServerSideIOException ;

}
