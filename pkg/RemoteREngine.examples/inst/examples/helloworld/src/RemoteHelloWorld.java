import java.rmi.*; 
import org.rosuda.REngine.remote.client.RemoteREngine ;

/**
 * Hello world example of a client to the RemoteREngine server
 * 
 * <p>This client connects to the <i>RemoteREngine</i> servant on <i>localhost</i>
 * and grabs the result of <strong>rnorm(5)</strong>
 */
public class RemoteHelloWorld {
 
	public static void main( String[] args) {
		try{
			
			/* standard RMI code, sets the system security manager to a
			   RMI security manager. */
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new RMISecurityManager());
			}
			
			/* creates the connection to the servant called "RemoteREngine" (default)
			   on port 1099 of localhost */
			RemoteREngine r = new RemoteREngine( "RemoteREngine" , "localhost", 1099 );
			
			/* grab five random numbers */
			double[] result = r.parseAndEval( "rnorm( 5 )" ).asDoubles() ; 
	  	
			/* boring java code to print the numbers */
			StringBuilder b = new StringBuilder("five random numbers from R :"); 
	  	for( int i=0; i<result.length; i++){
	  		b.append( " " + result[i]) ;
	  	}
	  	System.out.println( b ) ;
	  	
	  	/* close the connection. 
	  	   This ends the subscription of this client with the server */
	  	r.close( ) ;
	  	
	  	/* quit */
	  	 
	  	System.exit( 0 );
	  } catch( Exception e){
		System.err.println(e.getClass().getName() + ": " + e.getMessage());
	  	e.printStackTrace(); 
	  }
	}
	
}
