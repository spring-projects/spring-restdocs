/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;

/**
 * An {@link OperationPreprocessor} that applies a {@link ContentModifier} to the content
 * of the request or response.
 *
 * @author Andy Wilkinson
 */
public class ContentModifyingOperationPreprocessor implements OperationPreprocessor {

	private final OperationRequestFactory requestFactory = new OperationRequestFactory();

	private final OperationResponseFactory responseFactory = new OperationResponseFactory();

	private final ContentModifier contentModifier;

	/**
	 * Create a new {@code ContentModifyingOperationPreprocessor} that will apply the
	 * given {@code contentModifier} to the operation's request or response.
	 * @param contentModifier the contentModifier
	 */
	public ContentModifyingOperationPreprocessor(ContentModifier contentModifier) {
		this.contentModifier = contentModifier;
	}

	@Override
	public OperationRequest preprocess(OperationRequest request) {
		byte[] modifiedContent = this.contentModifier.modifyContent(request.getContent(),
				request.getHeaders().getContentType());
		return this.requestFactory.createFrom(request, modifiedContent);
	}

	@Override
	public OperationResponse preprocess(OperationResponse response) {
		byte[] modifiedContent = this.contentModifier.modifyContent(response.getContent(),
				response.getHeaders().getContentType());
		return this.responseFactory.createFrom(response, modifiedContent);
	}

}
