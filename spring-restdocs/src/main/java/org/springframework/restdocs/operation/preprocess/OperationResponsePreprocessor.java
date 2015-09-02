package org.springframework.restdocs.operation.preprocess;

import org.springframework.restdocs.operation.OperationResponse;

/**
 * An {@code OperationRequestPreprocessor} is used to modify an {@code OperationRequest}
 * prior to it being documented.
 * 
 * @author Andy Wilkinson
 */
public interface OperationResponsePreprocessor {

	/**
	 * Processes and potentially modifies the given {@code response} before it is
	 * documented.
	 * 
	 * @param response the response
	 * @return the modified response
	 */
	OperationResponse preprocess(OperationResponse response);

}
