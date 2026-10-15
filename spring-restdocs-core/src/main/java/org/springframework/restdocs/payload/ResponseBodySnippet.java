/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.payload;

import java.io.IOException;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.Snippet;

/**
 * A {@link Snippet} that documents the body of a response.
 *
 * @author Andy Wilkinson
 */
public class ResponseBodySnippet extends AbstractBodySnippet {

	/**
	 * Creates a new {@code ResponseBodySnippet}.
	 */
	public ResponseBodySnippet() {
		this(null, null);
	}

	/**
	 * Creates a new {@code ResponseBodySnippet} that will document the subsection of the
	 * response body extracted by the given {@code subsectionExtractor}. If the extractor
	 * is {@code null} the entire response body will be documented.
	 * @param subsectionExtractor the subsection extractor, or {@code null} to document
	 * the entire response body
	 */
	public ResponseBodySnippet(@Nullable PayloadSubsectionExtractor<?> subsectionExtractor) {
		this(subsectionExtractor, null);
	}

	/**
	 * Creates a new {@code ResponseBodySnippet} with the given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 * @param attributes the additional attributes
	 */
	public ResponseBodySnippet(@Nullable Map<String, Object> attributes) {
		this(null, attributes);
	}

	/**
	 * Creates a new {@code ResponseBodySnippet} that will document the subsection of the
	 * response body extracted by the given {@code subsectionExtractor}. If the extractor
	 * is {@code null} the entire response body will be documented. The given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 * @param subsectionExtractor the subsection extractor, or {@code null} to document
	 * the entire response body
	 * @param attributes the additional attributes
	 */
	public ResponseBodySnippet(@Nullable PayloadSubsectionExtractor<?> subsectionExtractor,
			@Nullable Map<String, Object> attributes) {
		super("response", subsectionExtractor, attributes);
	}

	@Override
	protected byte[] getContent(Operation operation) throws IOException {
		return operation.getResponse().getContent();
	}

	@Override
	protected @Nullable MediaType getContentType(Operation operation) {
		return operation.getResponse().getHeaders().getContentType();
	}

}
