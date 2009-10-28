/*
 * Copyright (c) 2009, Romain Francois <francoisromain@free.fr>
 *
 * This file is part of the RemoteREngine project
 *
 * The RemoteREngine project is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * The RemoteREngine project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the RemoteREngine project. If not, see <http://www.gnu.org/licenses/>.
 */

package org.rosuda.REngine.remote.server;

import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineCallbacks;
import org.rosuda.REngine.REngineConsoleHistoryInterface;
import org.rosuda.REngine.REngineInputInterface;
import org.rosuda.REngine.REngineOutputInterface;
import org.rosuda.REngine.REngineUIInterface;
import org.rosuda.REngine.remote.common.callbacks.CallbackResponse;
import org.rosuda.REngine.remote.common.callbacks.CancelCallback;
import org.rosuda.REngine.remote.common.callbacks.ChooseFileCallback;
import org.rosuda.REngine.remote.common.callbacks.ChooseFileCallbackResponse;
import org.rosuda.REngine.remote.common.callbacks.InputCallback;
import org.rosuda.REngine.remote.common.callbacks.RBusyCallback;
import org.rosuda.REngine.remote.common.callbacks.RCallbackWithResponse;
import org.rosuda.REngine.remote.common.callbacks.RFlushConsoleCallback;
import org.rosuda.REngine.remote.common.callbacks.RShowMessageCallback;
import org.rosuda.REngine.remote.common.callbacks.RWriteConsoleCallback;
import org.rosuda.REngine.remote.common.callbacks.ReadConsoleCallback;
import org.rosuda.REngine.remote.common.console.Command;
import org.rosuda.REngine.remote.server.callbacks.CallbackResponseWaiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Remote main loop callbacks 
 */
public class RemoteRMainLoopCallbacks implements REngineCallbacks, REngineInputInterface, 
REngineConsoleHistoryInterface, REngineUIInterface, REngineOutputInterface {
	final Logger logger = LoggerFactory.getLogger(org.rosuda.REngine.remote.server.RemoteRMainLoopCallbacks.class);

	/**
	 * R server associated with these callbacks
	 */
	private RemoteREngine_Server server ; 
	
	/**
	 * waiter for the callback response
	 */
	/* TODO: maybe this is not the best place for the CallbackResponseWaiter */
	private CallbackResponseWaiter waiter ; 
	
	/**
	 * Constructor, holds the server instance
	 * @param server R server associated with this callback  
	 */
	public RemoteRMainLoopCallbacks(RemoteREngine_Server server){
		this.server = server ;
		waiter = new CallbackResponseWaiter() ;
	}
	
 
	/** 
	 * Adds a response to a callback 
	 * @param response the response to a callback
	 */
	public void addResponse( CallbackResponse<? extends RCallbackWithResponse> response){
		logger.debug("addResponse");
		int id = response.getCallbackId() ; 
		if( waiter.isWaitingFor( id )){
			
			/* send a cancel callback */
			CancelCallback cancel = new CancelCallback( id ) ;
			server.sendCallbackToListeners( cancel ) ;
			
			/* put the response in the queue */
			waiter.put( new Integer(response.getCallbackId()), response) ;
		}
	}

	/**
	 * called when R enters the read stage of the event loop.
	 * 
	 *  @param eng calling engine
	 *  @param prompt prompt to display in the console
	 *  @param addToHistory flag indicating whether the input is transient (<code>false</code>) or to be recorded in the command history (<code>true</code>).
	 *  @return string to be processed as console input
	 */
	public String RReadConsole(REngine eng, String prompt, int addToHistory){
		logger.debug("rReadConsole");
		/* send the callback to clients (they might be interested in the prompt) */
		ReadConsoleCallback callback = new ReadConsoleCallback( prompt) ;
		server.sendCallbackToListeners( callback ) ; 
		
		/* wait for the next available command: this blocks */
		Command cmd = server.getConsoleSync().next() ;
		String result = cmd.getCommand() ;
		if (result == null) logger.warn("Null command returned from R Console");
		
		InputCallback input = new InputCallback( result, cmd.getSender() ) ;
		server.sendCallbackToListeners( input ) ; 
		
		if( result != null && !result.endsWith( "\n" ) ){
			result += "\n" ;
		}
		logger.debug("rReadConsole sending '{}'", result);
		return result ;
		
	}

	/** called when R wants to save the history content.
	 *  @param eng calling engine
	 *  @param filename name of the file to save command history to
	 */
	public void   RSaveHistory  (REngine eng, String filename){
		logger.debug("rSaveHistory {}",filename);
		// TODO: implement RSaveHistory
    }
	
	/** called when R wants to load the history content.
	 *  @param eng calling engine
	 *  @param filename name of the file to load the command history from
	 */
	public void   RLoadHistory  (REngine eng, String filename){
		logger.debug("rSaveHistory {}",filename);
    	// TODO: implement RLoadHistory
	}
	
	
	/**
	 *  called when the busy state of R changes - usual response is to change the shape of the cursor
	 *  
	 *  @param eng calling engine
	 *  @param state busy state of R (0 = not busy)
	 */
	public void RBusyState(REngine eng, int which) {
		logger.debug("rBusy: " + (which == 1? "entered" : "exited"));
		server.sendCallbackToListeners( new RBusyCallback( which == 1 ) ) ;
	}

	/** 
	 * called when R wants the user to choose a file.
	 * 
	 *  @param re calling engine
	 *  @param newFile if <code>true</code> then the user can specify a non-existing file to be created, otherwise an existing file must be selected.
	 *  @return full path and name of the selected file or <code>null</code> if the selection was cancelled.
	 */
	public String RChooseFile(REngine re, boolean newFile) {
		logger.debug("RChooseFile");
		// TODO: choose a file on the client(s), bring the file back and return the name of the file in the server
		ChooseFileCallback callback = new ChooseFileCallback( newFile  ) ;
		waiter.waitingFor( callback.getId() ) ; 
		
		server.sendCallbackToListeners(callback) ;
		
		/* TODO: if a new file has to be returned, maybe it is not worth sending the callback to the client */
		ChooseFileCallbackResponse response = (ChooseFileCallbackResponse)waiter.get( callback.getId() ) ;
		
		return response.getFilename() ;

	}

	/** 
     * called when R requests the console to flush any buffered output
     * 
	 * @param re calling engine
	 */	
	public void RFlushConsole(REngine re) {
		logger.debug("RFlushConsole");
		server.sendCallbackToListeners( new RFlushConsoleCallback() ) ;
	}

    /** 
     * called when R want to show a warning/error message
     * (not to be confused with messages displayed in the console output)
     * 
	 * @param re calling engine
	 * @param message message to display
	 */
	public void RShowMessage(REngine re, String message) {
		logger.debug("RShowMessage: {}",message);
		server.sendCallbackToListeners( new RShowMessageCallback( message ) ) ;
	}

	/**
	 *  called when R prints output to the console
	 *  
	 * @param re calling engine
	 * @param text text to display in the console
	 *  @param oType output type (0=regular, 1=error/warning)
	 */
	public void RWriteConsole(REngine re, String text, int oType) {
		logger.debug("RWriteConsole: {}",text);
		server.sendCallbackToListeners( new RWriteConsoleCallback( text, oType ) ) ;
	}
	
}
