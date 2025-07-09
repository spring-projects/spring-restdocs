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

package org.springframework.restdocs.cookies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.RequestCookie;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.SnippetException;

/**
 * A {@link Snippet} that documents the cookies in a request.
 *
 * @author Clyde Stubbs
 * @author Andy Wilkinson
 * @since 3.0
 * @see CookieDocumentation#requestCookies(CookieDescriptor...)
 * @see CookieDocumentation#requestCookies(Map, CookieDescriptor...)
 */
public class RequestCookiesSnippet extends AbstractCookiesSnippet {

	/**
	 * Creates a new {@code RequestCookiesSnippet} that will document the cookies in the
	 * request using the given {@code descriptors}.
	 * @param descriptors the descriptors
	 */
	protected RequestCookiesSnippet(List<CookieDescriptor> descriptors) {
		this(descriptors, null, false);
	}

	/**
	 * Creates a new {@code RequestCookiesSnippet} that will document the cookies in the
	 * request using the given {@code descriptors}. If {@code ignoreUndocumentedCookies}
	 * is {@code true}, undocumented cookies will be ignored and will not trigger a
	 * failure.
	 * @param descriptors the descriptors
	 * @param ignoreUndocumentedCookies whether undocumented cookies should be ignored
	 */
	protected RequestCookiesSnippet(List<CookieDescriptor> descriptors, boolean ignoreUndocumentedCookies) {
		this(descriptors, null, ignoreUndocumentedCookies);
	}

	/**
	 * Creates a new {@code RequestCookiesSnippet} that will document the cookies in the
	 * request using the given {@code descriptors}. The given {@code attributes} will be
	 * included in the model during template rendering. Undocumented cookies will not be
	 * ignored.
	 * @param descriptors the descriptors
	 * @param attributes the additional attributes
	 */
	protected RequestCookiesSnippet(List<CookieDescriptor> descriptors, @Nullable Map<String, Object> attributes) {
		this(descriptors, attributes, false);
	}

	/**
	 * Creates a new {@code RequestCookiesSnippet} that will document the cookies in the
	 * request using the given {@code descriptors}. The given {@code attributes} will be
	 * included in the model during template rendering.
	 * @param descriptors the descriptors
	 * @param attributes the additional attributes
	 * @param ignoreUndocumentedCookies whether undocumented cookies should be ignored
	 */
	protected RequestCookiesSnippet(List<CookieDescriptor> descriptors, @Nullable Map<String, Object> attributes,
			boolean ignoreUndocumentedCookies) {
		super("request", descriptors, attributes, ignoreUndocumentedCookies);
	}

	@Override
	protected Set<String> extractActualCookies(Operation operation) {
		HashSet<String> actualCookies = new HashSet<>();
		for (RequestCookie cookie : operation.getRequest().getCookies()) {
			actualCookies.add(cookie.getName());
		}
		return actualCookies;
	}

	@Override
	protected void verificationFailed(Set<String> undocumentedCookies, Set<String> missingCookies) {
		String message = "";
		if (!undocumentedCookies.isEmpty()) {
			message += "Cookies with the following names were not documented: " + undocumentedCookies;
		}
		if (!missingCookies.isEmpty()) {
			if (message.length() > 0) {
				message += ". ";
			}
			message += "Cookies with the following names were not found in the request: " + missingCookies;
		}
		throw new SnippetException(message);
	}

	/**
	 * Returns a new {@code RequestCookiesSnippet} configured with this snippet's
	 * attributes and its descriptors combined with the given
	 * {@code additionalDescriptors}.
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public final RequestCookiesSnippet and(CookieDescriptor... additionalDescriptors) {
		return and(Arrays.asList(additionalDescriptors));
	}

	/**
	 * Returns a new {@code RequestCookiesSnippet} configured with this snippet's
	 * attributes and its descriptors combined with the given
	 * {@code additionalDescriptors}.
	 * @param additionalDescriptors the additional descriptors
	 * @return the new snippet
	 */
	public final RequestCookiesSnippet and(List<CookieDescriptor> additionalDescriptors) {
		List<CookieDescriptor> combinedDescriptors = new ArrayList<>(this.getCookieDescriptors().values());
		combinedDescriptors.addAll(additionalDescriptors);
		return new RequestCookiesSnippet(combinedDescriptors, getAttributes(), isIgnoreUndocumentedCookies());
	}

}
