package org.rosuda.REngine.remote.common.console;

import java.io.Serializable;

public class Command implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String command; 

	protected CommandSender sender ;

	public Command( String command){
		this( command, new UnknownSender() ) ;
	}

	public Command( String command, CommandSender sender){
		this.command = command ; 
		this.sender = sender;  
	}

	public String getCommand( ){
		return command; 
	}

	public CommandSender getSender(){
		return sender; 
	}
}
