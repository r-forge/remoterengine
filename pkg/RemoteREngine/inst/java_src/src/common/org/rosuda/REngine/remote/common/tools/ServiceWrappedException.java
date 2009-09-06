package org.rosuda.REngine.remote.common.tools;

public class ServiceWrappedException extends ServiceException {

	private static final long serialVersionUID = 1L;
	
	protected Exception actual ;
	
	public ServiceWrappedException( Exception e){
		super( "Problem instanciating service : " + e.getMessage() ) ;
		this.actual = e; 
	}
	
}
