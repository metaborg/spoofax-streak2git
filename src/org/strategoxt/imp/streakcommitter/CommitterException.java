/**
 * 
 */
package org.strategoxt.imp.streakcommitter;

/**
 * @author Vlad Vergu <v.a.vergu add tudelft.nl>
 * 
 */
public class CommitterException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1568303565712308391L;

	public CommitterException() {
		super();
	}

	public CommitterException(String msg) {
		super(msg);
	}

	public CommitterException(String msg, Throwable t) {
		super(msg, t);
	}

	public CommitterException(Throwable t) {
		super(t);
	}
}
