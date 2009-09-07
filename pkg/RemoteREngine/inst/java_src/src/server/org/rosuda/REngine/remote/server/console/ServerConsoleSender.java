package org.rosuda.REngine.remote.server.console;

import org.rosuda.REngine.remote.common.console.CommandSender;

public class ServerConsoleSender extends CommandSender {

	protected ConsoleThread console; 
	public ServerConsoleSender(ConsoleThread console){
		this.console = console; 
	}
	public ConsoleThread getConsoleThread(){
		return console; 
	}
}
