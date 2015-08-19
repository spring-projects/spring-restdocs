package org.springframework.restdocs.payload;

/**
 * Thrown to indicate that a failure has occurred during payload handling
 * 
 * @author Andy Wilkinson
 *
 */
@SuppressWarnings("serial")
class PayloadHandlingException extends RuntimeException {

	/**
	 * Creates a new {@code PayloadHandlingException} with the given cause
	 * @param cause the cause of the failure
	 */
	PayloadHandlingException(Throwable cause) {
		super(cause);
	}

}
