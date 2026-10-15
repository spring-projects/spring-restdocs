/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

/**
 * A {@code ResponseConverter} is used to convert an implementation-specific response into
 * an {@link OperationResponse}.
 *
 * @param <R> the implementation-specific response type
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public interface ResponseConverter<R> {

	/**
	 * Converts the given {@code response} into an {@code OperationResponse}.
	 * @param response the response
	 * @return the operation response
	 * @throws ConversionException if the conversion fails
	 */
	OperationResponse convert(R response);

}
