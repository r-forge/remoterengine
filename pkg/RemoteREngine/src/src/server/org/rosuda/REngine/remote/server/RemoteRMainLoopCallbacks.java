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

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;
import org.rosuda.REngine.remote.common.callbacks.RBusyCallback;
import org.rosuda.REngine.remote.common.callbacks.RFlushConsoleCallback;
import org.rosuda.REngine.remote.common.callbacks.RShowMessageCallback;
import org.rosuda.REngine.remote.common.callbacks.RWriteConsoleCallback;

/**
 * Remote main loop callbacks 
 */
public class RemoteRMainLoopCallbacks implements RMainLoopCallbacks {

	/**
	 * R server associated with these callbacks
	 */
	private RemoteREngine_Server server ; 
	
	/**
	 * Constructor, holds the server instance
	 * @param server R server associated with this callback  
	 */
	public RemoteRMainLoopCallbacks(RemoteREngine_Server server){
		this.server = server ;
	}
	
    /**
     *  called when R prints output to the console
     *  
	 * @param re calling engine
	 * @param text text to display in the console
	*  @param oType output type (0=regular, 1=error/warning)
    */
	@Override
	public void rWriteConsole(Rengine re, String text, int oType) {
		server.addCallback( new RWriteConsoleCallback( text, oType ) ) ;
	}

    /** 
     * called when R enters or exits a longer evaluation. 
     * It is usually a good idea to signal this state to the user, e.g. by changing the cursor to a "hourglass" and back.
     * 
	 * @param re calling engine
	 * @param which identifies whether R enters (1) or exits (0) the busy state
	 */
	@Override
	public void rBusy(Rengine re, int which) {
		server.addCallback( new RBusyCallback( which == 1 ) ) ;
	}
	
    /** 
     * called when R requests the console to flush any buffered output
     * 
	 * @param re calling engine
	 */	
	@Override
	public void rFlushConsole(Rengine re) {
		server.addCallback( new RFlushConsoleCallback() ) ;
	}
	
    /** 
     * called when R want to show a warning/error message
     * (not to be confused with messages displayed in the console output)
     * 
	 * @param re calling engine
	 * @param message message to display
	 */
	@Override
	public void rShowMessage(Rengine re, String message) {
		server.addCallback( new RShowMessageCallback( message ) ) ;
	}
	
	/* history management */
	
    /** 
     * called to save the contents of the history (the implementation is responsible of keeping track of the history)
     *
     * @param re calling engine
	 * @param filename name of the history file
	 */
    public void   rSaveHistory  (Rengine re, String filename){
    	// TODO: read the history file. Make it available to clients
    }
    
    /** 
     * called to load the contents of the history
     * 
	 * @param re calling engine
	 * @param filename name of the history file
	 */
    public void   rLoadHistory  (Rengine re, String filename){
    	// TODO: load the history file
    }


	/* TODO: the last two callbacks are more tricky, they need interaction between the client and the server */
	
    /** 
     * called when R expects the user to choose a file
     * 
	 * @param re calling engine
	 * @param newFile flag determining whether an existing or new file is to be selected
	 * @return path/name of the selected file 
	 */
	@Override
	public String rChooseFile(Rengine re, int newFile) {
		// TODO: choose a file on the client(s), bring the file back and return the name of the file in the server
		return null;
	}

	/** 
	 * called when R waits for user input. During the duration of this callback it 
	 * is safe to re-enter R, and very often it is also the only time. The implementation 
	 * is free to block on this call until the user hits Enter, but in JRI it is 
	 * a good idea to call {@link Rengine.rniIdle()} occasionally to allow other 
	 * event handlers (e.g graphics device UIs) to run. Implementations should NEVER return 
	 * immediately even if there is no input - such behavior will result in a fast cycling event 
	 * loop which makes the use of R pretty much impossible.
	 * 
	 * @param re calling engine
	 * @param prompt prompt to be displayed at the console prior to user's input
	 * @param addToHistory flag telling the handler whether the input should be considered for adding to history (!=0) or not (0)
	 * @return user's input to be passed to R for evaluation */
	@Override
	public String rReadConsole(Rengine re, String prompt, int addToHistory) {
		// TODO:
		// - setup some background thread that waits for input
		// - dispatch to the clients that R is waiting for input
		// - when some input arrives, send it to R
		return "" ;
	}


	
}
