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

package org.springframework.restdocs.cookies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.ResponseCookie;
import org.springframework.restdocs.snippet.Snippet;

/**
 * A {@link Snippet} that documents the cookies in a response.
 *
 * @author Andreas Evers
 * @author Andy Wilkinson
 * @author Clyde Stubbs
 * @since 2.1
 * @see CookieDocumentation#responseCookies(CookieDescriptor...)
 * @see CookieDocumentation#responseCookies(Map, CookieDescriptor...)
 */
public class ResponseCookiesSnippet extends AbstractCookiesSnippet {

	/**
	 * Creates a new {@code ResponseCookiesSnippet} that will document the cookies in the
	 * response using the given {@code descriptors}.
	 * @param descriptors the descriptors
	 */
	protected ResponseCookiesSnippet(List<CookieDescriptor> descriptors) {
		this(descriptors, null, false);
	}

	/**
	 * Creates a new {@code ResponseCookiesSnippet} that will document the cookies in the
	 * response using the given {@code descriptors}. The given {@code attributes} will be
	 * included in the model during template rendering. Undocumented cookies will cause a
	 * failure.
	 * @param descriptors the descriptors
	 * @param attributes the additional attributes
	 */
	protected ResponseCookiesSnippet(List<CookieDescriptor> descriptors,
			Map<String, Object> attributes) {
		super("response", descriptors, attributes, false);
	}

	/**
	 * Creates a new {@code ResponseCookiesSnippet} that will document the cookies in the
	 * response using the given {@code descriptors}. The given {@code attributes} will be
	 * included in the model during template rendering.
	 * @param descriptors the descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedCookies ignore any cookies that are undocumented
	 */
	protected ResponseCookiesSnippet(List<CookieDescriptor> descriptors,
			Map<String, Object> attributes, boolean ignoreUndocumentedCookies) {
		super("response", descriptors, attributes, ignoreUndocumentedCookies);
	}

	@Override
	protected Set<String> extractActualCookies(Operation operation) {
		return operation.getResponse().getCookies().stream().map(ResponseCookie::getName)
				.collect(Collectors.toSet());
	}

	/**
	 * Returns a new {@code ResponseCookiesSnippet} configured with this snippet's
	 * attributes and its descriptors combined with the given
	 * {@code additionalDescriptors}.
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public final ResponseCookiesSnippet and(CookieDescriptor... additionalDescriptors) {
		return and(Arrays.asList(additionalDescriptors));
	}

	/**
	 * Returns a new {@code ResponseCookiesSnippet} configured with this snippet's
	 * attributes and its descriptors combined with the given
	 * {@code additionalDescriptors}.
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public final ResponseCookiesSnippet and(
			List<CookieDescriptor> additionalDescriptors) {
		List<CookieDescriptor> combinedDescriptors = new ArrayList<>(
				this.getCookieDescriptors());
		combinedDescriptors.addAll(additionalDescriptors);
		return new ResponseCookiesSnippet(combinedDescriptors, getAttributes(),
				ignoreUndocumentedCookies);
	}

}
