package org.rosuda.REngine.remote.common.callbacks;

/**
 * Callback to write information on the console 
 * 
 * @author Romain Francois
 *
 */
@SuppressWarnings("serial")
public class RWriteConsoleCallback extends RCallback {

	/** 
	 * regular message
	 */
	public static final int REGULAR = 0 ; 
	
	/**
	 * Warning/Error message
	 */
	public static final int ERROR = 1; 
	
	/**
	 * The type of message, can be REGULAR or ERROR
	 */
	public int type ; 
	
	/**
	 * The message
	 */
	public String message; 
	
	/**
	 * Default constructor
	 * 
	 * @param message message
	 * @param type type of message
	 */
	public RWriteConsoleCallback( String message, int type){
		super();
		this.message = message;
		this.type = type; 
	}
	
	/**
	 * Constructor, regular message
	 *  
	 * @param message message
	 */
	public RWriteConsoleCallback( String message ){
		this( message, REGULAR ) ;
	}
	
	public int getType(){
		return type ; 
	}
	
	public String getMessage(){
		return message; 
	}
	
}
