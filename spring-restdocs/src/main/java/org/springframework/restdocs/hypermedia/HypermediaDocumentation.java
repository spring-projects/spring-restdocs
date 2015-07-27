/*
 * Copyright 2014-2015 the original author or authors.
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

package org.springframework.restdocs.hypermedia;

import java.util.Arrays;
import java.util.Map;

import org.springframework.restdocs.RestDocumentationResultHandler;

/**
 * Static factory methods for documenting a RESTful API that utilises Hypermedia.
 * 
 * @author Andy Wilkinson
 */
public abstract class HypermediaDocumentation {

	private HypermediaDocumentation() {

	}

	/**
	 * Creates a {@code LinkDescriptor} that describes a link with the given {@code rel}.
	 * 
	 * @param rel The rel of the link
	 * @return a {@code LinkDescriptor} ready for further configuration
	 * @see RestDocumentationResultHandler#withLinks(LinkDescriptor...)
	 * @see RestDocumentationResultHandler#withLinks(LinkExtractor, LinkDescriptor...)
	 */
	public static LinkDescriptor linkWithRel(String rel) {
		return new LinkDescriptor(rel);
	}

	/**
	 * Creates a {@code LinkSnippetResultHandler} that will produce a documentation
	 * snippet for a response's links.
	 * 
	 * @param outputDir The directory to which the snippet should be written
	 * @param attributes Attributes made available during rendering of the links snippet
	 * @param linkExtractor Used to extract the links from the response
	 * @param descriptors The descriptions of the response's links
	 * @return the handler
	 * @see RestDocumentationResultHandler#withLinks(LinkDescriptor...)
	 * @see RestDocumentationResultHandler#withLinks(LinkExtractor, LinkDescriptor...)
	 */
	public static LinkSnippetResultHandler documentLinks(String outputDir,
			Map<String, Object> attributes, LinkExtractor linkExtractor,
			LinkDescriptor... descriptors) {
		return new LinkSnippetResultHandler(outputDir, attributes, linkExtractor,
				Arrays.asList(descriptors));
	}
}
