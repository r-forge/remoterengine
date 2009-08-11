package org.rosuda.REngine.remote.server.callbacks;

import java.util.LinkedList;
import java.util.Queue;

import org.rosuda.REngine.remote.common.callbacks.RCallback;

/**
 * Simple queue for the callbacks 
 *  
 * @author Romain Francois
 */
public class CallbackQueue {

	/**
	 * Queue of callbacks
	 */
	private Queue<RCallback> callbacks ;
	
	/**
	 * constructor, creates the queue
	 */
	public CallbackQueue( ){
		callbacks = new LinkedList<RCallback>(); 
	}
	
	/**
	 * Waits until a callback is available and return it
	 * @return the next callback to send to the client
	 */
	public synchronized RCallback next(){
		while ( callbacks.isEmpty() ){
			try {
				wait(100);
			} catch (InterruptedException e) { }
		}
		return callbacks.poll() ; 
	}
	
	/**
	 * Adds the callbacks to the queue
	 * @param callback the callback to add to the queue
	 */
	public synchronized void push( RCallback callback ){
		callbacks.add( callback ) ;
		notifyAll() ;
	}
	
	
}
