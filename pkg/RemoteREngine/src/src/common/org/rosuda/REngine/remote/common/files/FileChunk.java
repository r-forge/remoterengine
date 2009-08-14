package org.rosuda.REngine.remote.common.files;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Represents a chunk of file
 * 
 * @author Romain Francois
 *
 */
public class FileChunk implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 
	 * The number of relevant bytes 
	 */
	public int size; 
	
	/**
	 * The buffer
	 */
	public byte[] buffer ;
	
	/**
	 * Empty file chunk
	 */
	public FileChunk(){
		size = 0; 
		buffer = null; 
	}
	
	/**
	 * Constructor
	 * @param size the number of relevant bytes
	 * @param buffer the buffer
	 */
	public FileChunk(int size, byte[] buffer){
		this.size = size; 
		this.buffer = buffer ;
	}
	
	/**
	 * Is this an empty chunk
	 * @return true if this is an empty chunk
	 */
	public boolean isEmpty(){
		return size == 0; 
	}
	
	/* custom serialization so that only the relevant bytes are sent */
	private void writeObject(ObjectOutputStream out) throws IOException{
		out.writeInt( size )  ; 
		if( !isEmpty() ){
			out.write( buffer, 0, size ) ;
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		size = in.readInt() ;
		if( size == 0){
			buffer = null ;
		} else{
			buffer = new byte[size] ; 
			in.readFully(buffer, 0, size) ;
		}
	}
	
	
}
