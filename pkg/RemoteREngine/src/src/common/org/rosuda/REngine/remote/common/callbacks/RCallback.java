package org.rosuda.REngine.remote.common.callbacks;

import java.io.Serializable;

/**
 * A callback encapsulates the information that is generated by the 
 * R main loop callback class.  
 * 
 * @author Romain Francois
 */
@SuppressWarnings("serial")
public abstract class RCallback implements Serializable{
	
	private static int counter = 0 ; 
	
	private int id;
	
	public RCallback(){
		id = counter++ ;
	}
	
	public int getId(){
		return id; 
	}
	
}
