/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import org.jspecify.annotations.Nullable;

import org.springframework.http.HttpHeaders;

/**
 * Standard implementation of {@code OperationRequestPart}.
 *
 * @author Andy Wilkinson
 */
class StandardOperationRequestPart extends AbstractOperationMessage implements OperationRequestPart {

	private final String name;

	private final @Nullable String submittedFileName;

	/**
	 * Creates a new {@code StandardOperationRequestPart} with the given {@code name}.
	 * @param name the name of the part
	 * @param submittedFileName the name of the file being uploaded by this part
	 * @param content the contents of the part
	 * @param headers the headers of the part
	 */
	StandardOperationRequestPart(String name, @Nullable String submittedFileName, byte[] content, HttpHeaders headers) {
		super(content, headers);
		this.name = name;
		this.submittedFileName = submittedFileName;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public @Nullable String getSubmittedFileName() {
		return this.submittedFileName;
	}

}
