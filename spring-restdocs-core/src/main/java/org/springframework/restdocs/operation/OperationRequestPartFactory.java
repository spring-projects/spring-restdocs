/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation;

import org.jspecify.annotations.Nullable;

import org.springframework.http.HttpHeaders;

/**
 * A factory for creating {@link OperationRequestPart OperationRequestParts}.
 *
 * @author Andy Wilkinson
 */
public class OperationRequestPartFactory {

	/**
	 * Creates a new {@link OperationRequestPart}. The given {@code headers} will be
	 * augmented to ensure that they always include a {@code Content-Length} header if the
	 * part has any content.
	 * @param name the name of the part
	 * @param submittedFileName the name of the file being submitted by the part
	 * @param content the content of the part
	 * @param headers the headers of the part
	 * @return the {@code OperationRequestPart}
	 */
	public OperationRequestPart create(String name, @Nullable String submittedFileName, byte[] content,
			HttpHeaders headers) {
		return new StandardOperationRequestPart(name, submittedFileName, content, augmentHeaders(headers, content));
	}

	private HttpHeaders augmentHeaders(HttpHeaders input, byte[] content) {
		return new HttpHeadersHelper(input).setContentLengthHeader(content).getHeaders();
	}

}
