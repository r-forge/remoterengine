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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import org.rosuda.REngine.remote.common.RemoteREngineConstants;
import org.rosuda.REngine.remote.common.exceptions.ServerSideIOException;
import org.rosuda.REngine.remote.common.files.FileChunk;
import org.rosuda.REngine.remote.common.files.RemoteFileInputStream;

/**
 * Default Remote File Input stream implementation, backed by an 
 * BufferedInputStream
 * 
 * Methods of this classes are similar to methods of the {@link InputStream} 
 * but the {@link IOException} are disguised as {@link ServerSideIOException}
 * to allow the client to distinguish between IO problems 
 * on the client side or the server side
 *  
 * @author Romain Francois
 *
 */
public class RemoteFileInputStream_Server implements RemoteFileInputStream {
	
	/**
	 * The default size of the buffer
	 */
	public static final int DEFAULT_BUFFER_SIZE = RemoteREngineConstants.CHUNKSIZE  ; 
	
	/**
	 * The backend stream
	 */
	private BufferedInputStream stream ; 
	
	/**
	 * The buffer
	 */
	private byte[] buffer ; 
	
	/**
	 * The file
	 */
	private File file ;
	
	/**
	 * Constructor based on a file
	 * @param file file on the server
	 * @throws IOException
	 */
	public RemoteFileInputStream_Server( File file ) throws ServerSideIOException {
		this.file = file ;
		buffer = new byte[DEFAULT_BUFFER_SIZE] ;
		try{
			stream = new BufferedInputStream( new FileInputStream( file ) );
		} catch( IOException ioe ){
			throw new ServerSideIOException( ioe ) ;
		}
	}

	/**
	 * Constructor base on the file name
	 * @param file filename on the server
	 * @throws IOException
	 */
	public RemoteFileInputStream_Server( String file ) throws ServerSideIOException {
		this( new File( file ) ) ; 
	}
	
	
	public int available() throws ServerSideIOException {
		int res = 0; 
		try{
			res = stream.available() ;
		} catch( IOException ioe){
			throw new ServerSideIOException( ioe ) ;
		}
		return res; 
	}

	public void close() throws ServerSideIOException {
		try{
			stream.close() ;
		} catch( IOException ioe){
			throw new ServerSideIOException( ioe ) ;
		}
	}

	public void mark(int readlimit) throws RemoteException {
		stream.mark(readlimit) ;
	}

	public boolean markSupported() throws RemoteException {
		return stream.markSupported() ;
	}

	public void reset() throws ServerSideIOException {
		try{
			stream.reset() ;
		} catch(IOException ioe){
			throw new ServerSideIOException( ioe ) ;
		}
	}

	public long skip(long n) throws ServerSideIOException {
		long res = 0; 
		try{ 
			res = stream.skip( n ) ;
		} catch( IOException ioe){
			throw new ServerSideIOException( ioe ) ;
		}
		return res; 
	}
	
	public void setBufferSize( int n){
		buffer = new byte[n] ;
	}
	
	public FileChunk readNextChunk( ) throws ServerSideIOException {
		int c = 0 ; 
		try{
			c = stream.read( buffer ) ;
		} catch( IOException ioe){
			throw new ServerSideIOException( ioe ) ;
		}
		
		if( c < 0){
			return new FileChunk() ;
		} else{
			return new FileChunk( c, buffer ) ;
		}
	}
	
	public int getBufferSize(){
		return buffer.length ;
	}

	public boolean delete() throws RemoteException {
		return file.delete() ;
	}
	
}
