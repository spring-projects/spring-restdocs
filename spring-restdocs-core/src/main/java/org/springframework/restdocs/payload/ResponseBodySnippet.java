/*
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
	 * response body extracted by the given {@code subsectionExtractor}.
	 *
	 * @param subsectionExtractor the subsection extractor
	 */
	public ResponseBodySnippet(PayloadSubsectionExtractor<?> subsectionExtractor) {
		this(subsectionExtractor, null);
	}

	/**
	 * Creates a new {@code ResponseBodySnippet} with the given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 *
	 * @param attributes The additional attributes
	 */
	public ResponseBodySnippet(Map<String, Object> attributes) {
		this(null, attributes);
	}

	/**
	 * Creates a new {@code ResponseBodySnippet} that will document the subsection of the
	 * response body extracted by the given {@code subsectionExtractor}. The given
	 * additional {@code attributes} that will be included in the model during template
	 * rendering.
	 *
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes The additional attributes
	 */
	public ResponseBodySnippet(PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes) {
		super("response", subsectionExtractor, attributes);
	}

	@Override
	protected byte[] getContent(Operation operation) throws IOException {
		return operation.getResponse().getContent();
	}

	@Override
	protected MediaType getContentType(Operation operation) {
		return operation.getResponse().getHeaders().getContentType();
	}

}
