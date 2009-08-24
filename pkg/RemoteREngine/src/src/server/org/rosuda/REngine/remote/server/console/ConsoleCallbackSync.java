package org.rosuda.REngine.remote.server.console;

import org.rosuda.REngine.remote.common.callbacks.RCallback;
import org.rosuda.REngine.remote.common.tools.Synchronizer;

public class ConsoleCallbackSync extends Synchronizer<RCallback> {

	public ConsoleCallbackSync(){
		super(); 
	}

	@Override
	public void afterWaiting() {}
	
}
