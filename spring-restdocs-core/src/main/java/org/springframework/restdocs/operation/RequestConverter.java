/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

/**
 * A {@code RequestConverter} is used to convert an implementation-specific request into
 * an {@link OperationRequest}.
 *
 * @param <R> the implementation-specific request type
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public interface RequestConverter<R> {

	/**
	 * Converts the given {@code request} into an {@code OperationRequest}.
	 * @param request the request
	 * @return the operation request
	 * @throws ConversionException if the conversion fails
	 */
	OperationRequest convert(R request);

}
