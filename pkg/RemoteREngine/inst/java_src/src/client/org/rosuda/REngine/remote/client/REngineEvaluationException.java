/* 
 * project	: RemoteREngine
 * package	: org.rosuda.REngine.remote.client
 * created  : 22 Sep 2009
 */
package org.rosuda.REngine.remote.client;

import org.rosuda.REngine.REXP;


/**
 * The R Engine Evaluation Runtime Exception.
 * Class contains the REXP returned from the expression to support debugging
 */
public class REngineEvaluationException extends RuntimeException {

	/**
	 * serialVersionUID : <code>long</code>
	 */
	private static final long serialVersionUID = 1468472935008087370L;
	
	/**
	 * Environment used in R to run the expression
	 * exp : <code>REXP</code>
	 */
	private REXP exp;
	
	/**
	 * Capture the environment in Remote R that was used to run the expression
	 */
	public REngineEvaluationException(REXP exp) {
		super();
		this.exp = exp;
	}

	/**
	 * Throw an Exception due to an error in the connection on the Remote R
	 * @param exp Environment the expression was run in
	 * @param message Information about the exception
	 * @param cause Underlying cause of the exception
	 */
	public REngineEvaluationException(REXP exp, String message, Throwable cause) {
		super(message, cause);
		this.exp = exp;
	}

	/**
	 * Record exception message and the environment from Remote R
	 * @param exp Environment the expression was to be run in
	 * @param message Message regarding the error
	 */
	public REngineEvaluationException(REXP exp, String message) {
		super(message);
		this.exp = exp;
	}

	/**
	 * Throw an exception, recording the underlying cause
	 * @param exp Environment the expression was to be run in
	 * @param cause Underlying cause of the exception
	 */
	public REngineEvaluationException(REXP exp, Throwable cause) {
		super(cause);
		this.exp = exp;
	}
	
	/**
	 * return the expression evaluated.
	 * @return Environment the expression was to be run in
	 */
	public REXP getExp(){
		return exp;
	}
	
	
}
