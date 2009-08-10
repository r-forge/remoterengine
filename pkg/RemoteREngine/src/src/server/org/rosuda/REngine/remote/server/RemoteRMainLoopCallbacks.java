package org.rosuda.REngine.remote.server;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

/**
 * Remote main loop callbacks 
 */
public class RemoteRMainLoopCallbacks implements RMainLoopCallbacks {

	/**
	 * R server associated with these callbacks
	 */
	@SuppressWarnings("unused")
	private RemoteREngine_Server server ; 
	
	/**
	 * Constructor, holds the server instance
	 * @param server R server associated with this callback  
	 */
	public RemoteRMainLoopCallbacks(RemoteREngine_Server server){
		this.server = server ;
	}

	/* TODO: the first few methods inform the clients of this server about the state of R 
	 * and should be easy to implement 
	 * 
	 * create a Callback class that encapsulates the information 
	 * and send this (or make it available somehow) to the clients
	 * 
	 */
	
    /**
     *  called when R prints output to the console
     *  
	 * @param re calling engine
	 * @param text text to display in the console
	*  @param oType output type (0=regular, 1=error/warning)
    */
	@Override
	public void rWriteConsole(Rengine re, String text, int oType) {
		// TODO: broadcast the message to the clients
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
		// TODO: dispatch the information to the clients
	}
	
    /** 
     * called when R requests the console to flush any buffered output
     * 
	 * @param re calling engine
	 */	
	@Override
	public void rFlushConsole(Rengine re) {
		// TODO: dispatch the information to the clients
	}
	
	
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

    /** 
     * called when R want to show a warning/error message
     * (not to be confused with messages displayed in the console output)
     * 
	 * @param re calling engine
	 * @param message message to display
	 */
	@Override
	public void rShowMessage(Rengine re, String message) {
		// TODO: dispatch the information to the clients
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
		return null;
	}


	
}
