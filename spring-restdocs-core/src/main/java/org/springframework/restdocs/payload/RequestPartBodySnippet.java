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
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.SnippetException;

/**
 * A {@link Snippet} that documents the body of a request part.
 *
 * @author Andy Wilkinson
 */
public class RequestPartBodySnippet extends AbstractBodySnippet {

	private final String partName;

	/**
	 * Creates a new {@code RequestPartBodySnippet} that will document the body of the
	 * request part with the given {@code partName}.
	 *
	 * @param partName the name of the request part
	 */
	public RequestPartBodySnippet(String partName) {
		this(partName, null, null);
	}

	/**
	 * Creates a new {@code RequestPartBodySnippet} that will document the subsection of
	 * the body of the request part with the given {@code partName} extracted by the given
	 * {@code subsectionExtractor}.
	 *
	 * @param partName the name of the request part
	 * @param subsectionExtractor the subsection extractor
	 */
	public RequestPartBodySnippet(String partName,
			PayloadSubsectionExtractor<?> subsectionExtractor) {
		this(partName, subsectionExtractor, null);
	}

	/**
	 * Creates a new {@code RequestPartBodySnippet} that will document the body of the
	 * request part with the given {@code partName}. The given additional
	 * {@code attributes} will be included in the model during template rendering.
	 *
	 * @param partName the name of the request part
	 * @param attributes the additional attributes
	 */
	public RequestPartBodySnippet(String partName, Map<String, Object> attributes) {
		this(partName, null, attributes);
	}

	/**
	 * Creates a new {@code RequestPartBodySnippet} that will document the body of the
	 * request part with the given {@code partName}. The subsection of the body extracted
	 * by the given {@code subsectionExtractor} will be documented and the given
	 * additional {@code attributes} that will be included in the model during template
	 * rendering.
	 *
	 * @param partName the name of the request part
	 * @param subsectionExtractor the subsection extractor
	 * @param attributes The additional attributes
	 */
	public RequestPartBodySnippet(String partName,
			PayloadSubsectionExtractor<?> subsectionExtractor,
			Map<String, Object> attributes) {
		super("request-part-" + partName, "request-part", subsectionExtractor,
				attributes);
		this.partName = partName;
	}

	@Override
	protected byte[] getContent(Operation operation) throws IOException {
		return findPart(operation).getContent();
	}

	@Override
	protected MediaType getContentType(Operation operation) {
		return findPart(operation).getHeaders().getContentType();
	}

	private OperationRequestPart findPart(Operation operation) {
		for (OperationRequestPart candidate : operation.getRequest().getParts()) {
			if (candidate.getName().equals(this.partName)) {
				return candidate;
			}
		}
		throw new SnippetException("A request part named '" + this.partName
				+ "' was not found in the request");
	}

}
