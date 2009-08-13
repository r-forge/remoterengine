package org.rosuda.REngine.remote.common.exceptions;

/**
 * Indicates that a file already exists. This exception 
 * is used if the client wants to push a file to the server 
 * while making sure that the file does not already exist 
 * 
 * @author Romain Francois
 *
 */
public class FileAlreadyExistsException extends ServerSideException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The name of the file 
	 */
	private String filename ; 
	
	/**
	 * Constructor
	 * @param filename the name of the file
	 */
	public FileAlreadyExistsException( String filename ){
		super( filename + " already exists" ) ;
		this.filename = filename;  
	}
	
	/**
	 * @return the name of the file
	 */
	public String getFilename(){
		return filename; 
	}
}
