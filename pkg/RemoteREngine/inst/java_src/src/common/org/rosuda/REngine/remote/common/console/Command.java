package org.rosuda.REngine.remote.common.console;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Command implements Serializable {

	private static final long serialVersionUID = -2117203565343969787L;

	private final Logger logger = LoggerFactory.getLogger(org.rosuda.REngine.remote.common.console.Command.class);
	
	protected String command; 

	protected CommandSender sender ;

	public Command( String command){
		this( command, new UnknownSender() ) ;
	}

	public Command( String command, CommandSender sender){
		logger.debug("{} set '{}'",sender.getClass().getName(),command);
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
