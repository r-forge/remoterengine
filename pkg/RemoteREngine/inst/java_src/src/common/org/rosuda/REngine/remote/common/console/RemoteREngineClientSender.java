package org.rosuda.REngine.remote.common.console;

import org.rosuda.REngine.remote.common.RemoteREngineClient;

public class RemoteREngineClientSender extends CommandSender {

	protected RemoteREngineClient client ;

	public RemoteREngineClientSender(RemoteREngineClient client){
		this.client = client ; 
	}
	
	public RemoteREngineClient getClient(){
		return client; 
	}
}
