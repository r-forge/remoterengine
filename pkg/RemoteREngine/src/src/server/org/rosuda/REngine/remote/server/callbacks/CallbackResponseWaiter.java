package org.rosuda.REngine.remote.server.callbacks;

import java.util.HashSet;
import java.util.Set;

import org.rosuda.REngine.remote.common.callbacks.CallbackResponse;
import org.rosuda.REngine.remote.common.callbacks.RCallbackWithResponse;
import org.rosuda.REngine.remote.common.tools.Waiter;

public class CallbackResponseWaiter extends Waiter<Integer,CallbackResponse<? extends RCallbackWithResponse>> {

	private Set<Integer> waiting ; 
	
	public CallbackResponseWaiter(){
		super();
		waiting = new HashSet<Integer>(); 
	}
	
	public synchronized CallbackResponse<? extends RCallbackWithResponse> get(int key) {
		return get(new Integer(key));
	}
	
	@Override
	public synchronized CallbackResponse<? extends RCallbackWithResponse> get(Integer key) {
		CallbackResponse<? extends RCallbackWithResponse> response = super.get(key);
		waiting.remove(response.getCallbackId()) ;
		return response; 
	}

	public void waitingFor(int id) {
		waiting.add( id ) ;
	} 
	
}
