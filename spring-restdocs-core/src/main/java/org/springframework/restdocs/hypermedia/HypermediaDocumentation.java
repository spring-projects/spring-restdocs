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

package org.springframework.restdocs.hypermedia;

import java.util.Arrays;
import java.util.Map;

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
	 * Returns a new {@code Snippet} that will document the links in the API operation's
	 * response. Links will be extracted from the response automatically based on its
	 * content type and will be documented using the given {@code descriptors}.
	 * <p>
	 * If a link is present in the response, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a link
	 * is documented, is not marked as optional, and is not present in the response, a
	 * failure will also occur.
	 * <p>
	 * If you do not want to document a link, a link descriptor can be marked as
	 * {@link LinkDescriptor#ignored}. This will prevent it from appearing in the
	 * generated snippet while avoiding the failure described above.
	 * <p>
	 * If a descriptor does not have a {@link LinkDescriptor#description(Object)
	 * description}, the {@link Link#getTitle() title} of the link will be used. If the
	 * link does not have a title a failure will occur.
	 *
	 * @param descriptors the descriptions of the response's links
	 * @return the snippet that will document the links
	 */
	public static LinksSnippet links(LinkDescriptor... descriptors) {
		return new LinksSnippet(new ContentTypeLinkExtractor(),
				Arrays.asList(descriptors));
	}

	/**
	 * Returns a new {@code Snippet} that will document the links in the API operation's
	 * response. Links will be extracted from the response automatically based on its
	 * content type and will be documented using the given {@code descriptors}.
	 * <p>
	 * If a link is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented links will be ignored.
	 * <p>
	 * If a descriptor does not have a {@link LinkDescriptor#description(Object)
	 * description}, the {@link Link#getTitle() title} of the link will be used. If the
	 * link does not have a title a failure will occur.
	 *
	 * @param descriptors the descriptions of the response's links
	 * @return the snippet that will document the links
	 */
	public static LinksSnippet relaxedLinks(LinkDescriptor... descriptors) {
		return new LinksSnippet(new ContentTypeLinkExtractor(),
				Arrays.asList(descriptors), true);
	}

	/**
	 * Returns a new {@code Snippet} that will document the links in the API call's
	 * response. The given {@code attributes} will be available during snippet generation.
	 * Links will be extracted from the response automatically based on its content type
	 * and will be documented using the given {@code descriptors}.
	 * <p>
	 * If a link is present in the response, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a link
	 * is documented, is not marked as optional, and is not present in the response, a
	 * failure will also occur.
	 * <p>
	 * If you do not want to document a link, a link descriptor can be marked as
	 * {@link LinkDescriptor#ignored}. This will prevent it from appearing in the
	 * generated snippet while avoiding the failure described above.
	 * <p>
	 * If a descriptor does not have a {@link LinkDescriptor#description(Object)
	 * description}, the {@link Link#getTitle() title} of the link will be used. If the
	 * link does not have a title a failure will occur.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response's links
	 * @return the snippet that will document the links
	 */
	public static LinksSnippet links(Map<String, Object> attributes,
			LinkDescriptor... descriptors) {
		return new LinksSnippet(new ContentTypeLinkExtractor(),
				Arrays.asList(descriptors), attributes);
	}

	/**
	 * Returns a new {@code Snippet} that will document the links in the API call's
	 * response. The given {@code attributes} will be available during snippet generation.
	 * Links will be extracted from the response automatically based on its content type
	 * and will be documented using the given {@code descriptors}.
	 * <p>
	 * If a link is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented links will be ignored.
	 * <p>
	 * If a descriptor does not have a {@link LinkDescriptor#description(Object)
	 * description}, the {@link Link#getTitle() title} of the link will be used. If the
	 * link does not have a title a failure will occur.
	 *
	 * @param attributes the attributes
	 * @param descriptors the descriptions of the response's links
	 * @return the snippet that will document the links
	 */
	public static LinksSnippet relaxedLinks(Map<String, Object> attributes,
			LinkDescriptor... descriptors) {
		return new LinksSnippet(new ContentTypeLinkExtractor(),
				Arrays.asList(descriptors), attributes, true);
	}

	/**
	 * Returns a new {@code Snippet} that will document the links in the API operation's
	 * response. Links will be extracted from the response using the given
	 * {@code linkExtractor} and will be documented using the given {@code descriptors}.
	 * <p>
	 * If a link is present in the response, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a link
	 * is documented, is not marked as optional, and is not present in the response, a
	 * failure will also occur.
	 * <p>
	 * If you do not want to document a link, a link descriptor can be marked as
	 * {@link LinkDescriptor#ignored}. This will prevent it from appearing in the
	 * generated snippet while avoiding the failure described above.
	 * <p>
	 * If a descriptor does not have a {@link LinkDescriptor#description(Object)
	 * description}, the {@link Link#getTitle() title} of the link will be used. If the
	 * link does not have a title a failure will occur.
	 *
	 * @param linkExtractor used to extract the links from the response
	 * @param descriptors the descriptions of the response's links
	 * @return the snippet that will document the links
	 */
	public static LinksSnippet links(LinkExtractor linkExtractor,
			LinkDescriptor... descriptors) {
		return new LinksSnippet(linkExtractor, Arrays.asList(descriptors));
	}

	/**
	 * Returns a new {@code Snippet} that will document the links in the API operation's
	 * response. Links will be extracted from the response using the given
	 * {@code linkExtractor} and will be documented using the given {@code descriptors}.
	 * <p>
	 * If a link is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented links will be ignored.
	 * <p>
	 * If a descriptor does not have a {@link LinkDescriptor#description(Object)
	 * description}, the {@link Link#getTitle() title} of the link will be used. If the
	 * link does not have a title a failure will occur.
	 *
	 * @param linkExtractor used to extract the links from the response
	 * @param descriptors the descriptions of the response's links
	 * @return the snippet that will document the links
	 */
	public static LinksSnippet relaxedLinks(LinkExtractor linkExtractor,
			LinkDescriptor... descriptors) {
		return new LinksSnippet(linkExtractor, Arrays.asList(descriptors), true);
	}

	/**
	 * Returns a new {@code Snippet} that will document the links in the API operation's
	 * response. The given {@code attributes} will be available during snippet generation.
	 * Links will be extracted from the response using the given {@code linkExtractor} and
	 * will be documented using the given {@code descriptors}.
	 * <p>
	 * If a link is present in the response, but is not documented by one of the
	 * descriptors, a failure will occur when the snippet is invoked. Similarly, if a link
	 * is documented, is not marked as optional, and is not present in the response, a
	 * failure will also occur.
	 * <p>
	 * If you do not want to document a link, a link descriptor can be marked as
	 * {@link LinkDescriptor#ignored}. This will prevent it from appearing in the
	 * generated snippet while avoiding the failure described above.
	 * <p>
	 * If a descriptor does not have a {@link LinkDescriptor#description(Object)
	 * description}, the {@link Link#getTitle() title} of the link will be used. If the
	 * link does not have a title a failure will occur.
	 *
	 * @param attributes the attributes
	 * @param linkExtractor used to extract the links from the response
	 * @param descriptors the descriptions of the response's links
	 * @return the snippet that will document the links
	 */
	public static LinksSnippet links(LinkExtractor linkExtractor,
			Map<String, Object> attributes, LinkDescriptor... descriptors) {
		return new LinksSnippet(linkExtractor, Arrays.asList(descriptors), attributes);
	}

	/**
	 * Returns a new {@code Snippet} that will document the links in the API operation's
	 * response. The given {@code attributes} will be available during snippet generation.
	 * Links will be extracted from the response using the given {@code linkExtractor} and
	 * will be documented using the given {@code descriptors}.
	 * <p>
	 * If a link is documented, is not marked as optional, and is not present in the
	 * response, a failure will occur. Any undocumented links will be ignored.
	 * <p>
	 * If a descriptor does not have a {@link LinkDescriptor#description(Object)
	 * description}, the {@link Link#getTitle() title} of the link will be used. If the
	 * link does not have a title a failure will occur.
	 *
	 * @param attributes the attributes
	 * @param linkExtractor used to extract the links from the response
	 * @param descriptors the descriptions of the response's links
	 * @return the snippet that will document the links
	 */
	public static LinksSnippet relaxedLinks(LinkExtractor linkExtractor,
			Map<String, Object> attributes, LinkDescriptor... descriptors) {
		return new LinksSnippet(linkExtractor, Arrays.asList(descriptors), attributes,
				true);
	}

	/**
	 * Returns a {@code LinkExtractor} capable of extracting links in Hypermedia
	 * Application Language (HAL) format where the links are found in a map named
	 * {@code _links}. For example:
	 *
	 * <pre>
	 * {
	 *     "_links": {
	 *         "self": {
	 *             "href": "http://example.com/foo"
	 *         }
	 *     }
	 * }
	 * </pre>
	 *
	 * @return The extractor for HAL-style links
	 */
	public static LinkExtractor halLinks() {
		return new HalLinkExtractor();
	}

	/**
	 * Returns a {@code LinkExtractor} capable of extracting links in Atom format where
	 * the links are found in an array named {@code links}. For example:
	 *
	 * <pre>
	 * {
	 *     "links": [
	 *         {
	 *             "rel": "self",
	 *             "href": "http://example.com/foo"
	 *         }
	 *     ]
	 * }
	 * </pre>
	 *
	 * @return The extractor for Atom-style links
	 */
	public static LinkExtractor atomLinks() {
		return new AtomLinkExtractor();
	}
}
