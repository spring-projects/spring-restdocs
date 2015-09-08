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

import org.springframework.restdocs.snippet.Snippet;

/**
 * Static factory methods for documenting a RESTful API that utilizes Hypermedia.
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
	 */
	public static LinkDescriptor linkWithRel(String rel) {
		return new LinkDescriptor(rel);
	}

	/**
	 * Returns a handler that will produce a snippet documenting the links in the API
	 * call's response. Links will be extracted from the response automatically based on
	 * its content type.
	 * 
	 * @param descriptors The descriptions of the response's links
	 * @return the handler
	 */
	public static Snippet links(LinkDescriptor... descriptors) {
		return new LinksSnippet(new ContentTypeLinkExtractor(),
				Arrays.asList(descriptors));
	}

	/**
	 * Returns a handler that will produce a snippet documenting the links in the API
	 * call's response. The given {@code attributes} will be available during snippet
	 * generation. Links will be extracted from the response automatically based on its
	 * content type.
	 * 
	 * @param attributes Attributes made available during rendering of the links snippet
	 * @param descriptors The descriptions of the response's links
	 * @return the handler
	 */
	public static Snippet links(Map<String, Object> attributes,
			LinkDescriptor... descriptors) {
		return new LinksSnippet(new ContentTypeLinkExtractor(), Arrays.asList(descriptors),
				attributes);
	}

	/**
	 * Returns a handler that will produce a snippet documenting the links in the API
	 * call's response. Links will be extracted from the response using the given
	 * {@code linkExtractor}.
	 * 
	 * @param linkExtractor Used to extract the links from the response
	 * @param descriptors The descriptions of the response's links
	 * @return the handler
	 */
	public static Snippet links(LinkExtractor linkExtractor,
			LinkDescriptor... descriptors) {
		return new LinksSnippet(linkExtractor, Arrays.asList(descriptors));
	}

	/**
	 * Returns a handler that will produce a snippet documenting the links in the API
	 * call's response. The given {@code attributes} will be available during snippet
	 * generation. Links will be extracted from the response using the given
	 * {@code linkExtractor}.
	 * 
	 * @param attributes Attributes made available during rendering of the links snippet
	 * @param linkExtractor Used to extract the links from the response
	 * @param descriptors The descriptions of the response's links
	 * @return the handler
	 */
	public static Snippet links(LinkExtractor linkExtractor,
			Map<String, Object> attributes, LinkDescriptor... descriptors) {
		return new LinksSnippet(linkExtractor, Arrays.asList(descriptors),
				attributes);
	}

	/**
	 * Returns a {@code LinkExtractor} capable of extracting links in Hypermedia
	 * Application Language (HAL) format where the links are found in a map named
	 * {@code _links}.
	 *
	 * @return The extract for HAL-style links
	 */
	public static LinkExtractor halLinks() {
		return new HalLinkExtractor();
	}

	/**
	 * Returns a {@code LinkExtractor} capable of extracting links in Atom format where
	 * the links are found in an array named {@code links}.
	 *
	 * @return The extractor for Atom-style links
	 */
	public static LinkExtractor atomLinks() {
		return new AtomLinkExtractor();
	}
}
