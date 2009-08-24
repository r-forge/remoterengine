package org.rosuda.REngine.remote.server.console;

import org.rosuda.REngine.remote.common.callbacks.RCallback;
import org.rosuda.REngine.remote.common.callbacks.RShowMessageCallback;
import org.rosuda.REngine.remote.common.callbacks.RWriteConsoleCallback;
import org.rosuda.REngine.remote.common.callbacks.ReadConsoleCallback;
import org.rosuda.REngine.remote.server.RemoteREngine_Server;
import org.rosuda.REngine.remote.server.callbacks.CallbackListener;

public class ConsoleCallbackHandler extends Thread implements CallbackListener {

	@SuppressWarnings("unused")
	private RemoteREngine_Server server ; 
	
	private boolean stop ; 
	
	private ConsoleCallbackSync sync ; 
	
	public ConsoleCallbackHandler(RemoteREngine_Server server){
		this.server = server ;
		stop = false; 
		sync = new ConsoleCallbackSync(); 
	}
	
	public void run(){
		while( !stop ){
			
			RCallback callback = sync.next(); 
			
			if( callback instanceof RWriteConsoleCallback ){
				System.out.print( ((RWriteConsoleCallback)callback).getMessage() ) ;
			} else if( callback instanceof RShowMessageCallback ){
				System.out.print( ((RShowMessageCallback)callback).getMessage() ) ;
			} else if( callback instanceof ReadConsoleCallback ){
				System.out.print( ((ReadConsoleCallback)callback).getPrompt() ) ;
			}
			
		}
	}
	
	public synchronized void handleCallback(RCallback callback) {
		sync.add( callback ) ;
	}
	
	public void requestStop(){
		stop= true; 
	}

}
