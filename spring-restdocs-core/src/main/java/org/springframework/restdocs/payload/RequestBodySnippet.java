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
 * A {@link Snippet} that documents the body of a request.
 *
 * @author Andy Wilkinson
 */
public class RequestBodySnippet extends AbstractBodySnippet {

	/**
	 * Creates a new {@code RequestBodySnippet}.
	 */
	public RequestBodySnippet() {
		this(null, null);
	}

	/**
	 * Creates a new {@code RequestBodySnippet} that will document the subsection of the
	 * request body extracted by the given {@code subsectionExtractor}. If the extractor
	 * is {@code null}, the fields of the entire payload will be documented.
	 * @param subsectionExtractor the subsection extractor, or {@code null} to document
	 * the entire body.
	 */
	public RequestBodySnippet(@Nullable PayloadSubsectionExtractor<?> subsectionExtractor) {
		this(subsectionExtractor, null);
	}

	/**
	 * Creates a new {@code RequestBodySnippet} with the given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 * @param attributes the additional attributes
	 */
	public RequestBodySnippet(@Nullable Map<String, Object> attributes) {
		this(null, attributes);
	}

	/**
	 * Creates a new {@code RequestBodySnippet} that will document the subsection of the
	 * request body extracted by the given {@code subsectionExtractor}. If the extractor
	 * is {@code null}, the entire body will be documented.
	 *
	 * The given additional {@code attributes} that will be included in the model during
	 * template rendering.
	 * @param subsectionExtractor the subsection extractor or {@code null} to document the
	 * entire body
	 * @param attributes the additional attributes
	 */
	public RequestBodySnippet(@Nullable PayloadSubsectionExtractor<?> subsectionExtractor,
			@Nullable Map<String, Object> attributes) {
		super("request", subsectionExtractor, attributes);
	}

	@Override
	protected byte[] getContent(Operation operation) throws IOException {
		return operation.getRequest().getContent();
	}

	@Override
	protected @Nullable MediaType getContentType(Operation operation) {
		return operation.getRequest().getHeaders().getContentType();
	}

}
