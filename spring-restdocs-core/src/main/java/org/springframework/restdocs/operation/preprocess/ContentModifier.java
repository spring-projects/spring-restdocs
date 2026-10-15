/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import org.jspecify.annotations.Nullable;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationResponse;

/**
 * A {@code ContentModifier} modifies the content of an {@link OperationRequest} or
 * {@link OperationResponse} during the preprocessing that is performed prior to
 * documentation generation.
 *
 * @author Andy Wilkinson
 * @see ContentModifyingOperationPreprocessor
 */
public interface ContentModifier {

	/**
	 * Returns modified content based on the given {@code originalContent}.
	 * @param originalContent the original content
	 * @param contentType the type of the original content, may be {@code null}
	 * @return the modified content
	 */
	byte[] modifyContent(byte[] originalContent, @Nullable MediaType contentType);

}
