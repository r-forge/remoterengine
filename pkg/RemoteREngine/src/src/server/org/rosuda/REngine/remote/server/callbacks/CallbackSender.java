package org.rosuda.REngine.remote.server.callbacks;

import org.rosuda.REngine.remote.common.callbacks.CallbackListener;
import org.rosuda.REngine.remote.common.callbacks.RCallback;
import org.rosuda.REngine.remote.common.tools.Synchronizer;
import org.rosuda.REngine.remote.server.RemoteREngine_Server;

public class CallbackSender extends Thread {

	private RemoteREngine_Server server ;
	private boolean stop ;
	private Synchronizer<RCallback> synchronizer ; 
	
	public CallbackSender( RemoteREngine_Server server ){
		this.server = server ; 
		this.stop = false; 
	}
	
	public void run(){
		while( !stop ){
			RCallback callback = synchronizer.next() ; 
			for( CallbackListener listener: server.getCallbackListeners() ){
				listener.handleCallback( callback ) ;
			}
		}
	}
	
	public void addCallback( RCallback callback ){
		synchronizer.add(callback ) ;
	}
	
}
