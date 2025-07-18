/*
 * Copyright 2014-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
