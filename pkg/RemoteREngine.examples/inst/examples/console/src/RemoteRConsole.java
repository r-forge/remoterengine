import java.rmi.*; 
import org.rosuda.REngine.remote.client.RemoteREngine ;
import org.rosuda.REngine.remote.common.callbacks.*;
import org.rosuda.REngine.remote.common.console.* ;
 
public class RemoteRConsole {
 
	public static void main( String[] args) {
		try{
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new RMISecurityManager());
			}
			RemoteREngine r = new RemoteREngine( "RemoteREngine" , "localhost", 1099 );
			r.addCallbackListener( new ConsoleCallbackListener() ) ; 
			ConsoleThread console = new ConsoleThread( r ) ; 
			console.start( ) ; 
	  } catch( Exception e){
	  	e.printStackTrace(); 
	  }
	}
	
	private static class ConsoleCallbackListener implements CallbackListener {
		public void handleCallback( RCallback callback ){
			if( callback instanceof RWriteConsoleCallback ){
				System.out.print( ( (RWriteConsoleCallback)callback).getMessage()  ) ;
			} else if( callback instanceof RShowMessageCallback){
				System.out.print( ( (RShowMessageCallback)callback).getMessage()  ) ;
			} else if( callback instanceof ReadConsoleCallback){
				System.out.print( ( (ReadConsoleCallback)callback).getPrompt()  ) ;
			} else if( callback instanceof InputCallback ){
				System.out.print( ((InputCallback)callback).getCommand() + "\n" ) ;
			}
		}
	}
	
	private static class ConsoleThread extends Thread {
		
		private DefaultConsoleReadLine readline ;
		private RemoteREngine engine; 
		
		private ConsoleThread( RemoteREngine engine){
			super() ; 
			this.engine = engine ; 
			this.readline = new DefaultConsoleReadLine( ); 
		}
		
		public void run(){
			/* just the first time */
			System.out.print( "> " ) ;
			
			while( true ){
				String line = readline.readLine(); 
				engine.sendToConsole( line ) ; 
			}
		}
		
	}
}

