package org.blockchain.node.exception;

/**
 * @author Yulian Yordanov
 * Created: Jun 12, 2018
 */
public class TransactionValidationException extends Throwable {
	private static final long serialVersionUID = 5799983786129972784L;	
	private String errorMessage;
	
	public TransactionValidationException() {
	}
	
	public TransactionValidationException(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
