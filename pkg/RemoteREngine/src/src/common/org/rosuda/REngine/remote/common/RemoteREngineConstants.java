package org.rosuda.REngine.remote.common;

/**
 * Set of constants used by the server and the client 
 * 
 * @author Romain Francois <francoisromain@free.fr>
 */
public class RemoteREngineConstants {

	/** Define the default name to register the server process under */
    public static final String DEFAULTNAME = "RemoteREngine";
	
    /** Define the default host to look up to locate the RMI Registry */
	public static final String RMIHOSTNAME = "localhost";
	
	/** Define the default port number to locate the RMI Registry */
	public static final String RMIPORT = "1099";

	/**  The default chunk size for byte[] buffer */
	public static final int CHUNKSIZE = 8192 ; 
	
}
