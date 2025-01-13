/*
 * Copyright 2014-2025 the original author or authors.
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

package org.springframework.restdocs.mockmvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.Cookie;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.ResponseConverter;
import org.springframework.restdocs.operation.ResponseCookie;
import org.springframework.util.StringUtils;

/**
 * A converter for creating an {@link OperationResponse} derived from a
 * {@link MockHttpServletResponse}.
 *
 * @author Andy Wilkinson
 * @author Clyde Stubbs
 */
class MockMvcResponseConverter implements ResponseConverter<MockHttpServletResponse> {

	@Override
	public OperationResponse convert(MockHttpServletResponse mockResponse) {
		HttpHeaders headers = extractHeaders(mockResponse);
		Collection<ResponseCookie> cookies = extractCookies(mockResponse);
		return new OperationResponseFactory().create(HttpStatusCode.valueOf(mockResponse.getStatus()), headers,
				mockResponse.getContentAsByteArray(), cookies);
	}

	private Collection<ResponseCookie> extractCookies(MockHttpServletResponse mockResponse) {
		if (mockResponse.getCookies() == null || mockResponse.getCookies().length == 0) {
			return Collections.emptyList();
		}
		List<ResponseCookie> cookies = new ArrayList<>();
		for (Cookie cookie : mockResponse.getCookies()) {
			cookies.add(new ResponseCookie(cookie.getName(), cookie.getValue()));
		}
		return cookies;
	}

	private HttpHeaders extractHeaders(MockHttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();
		for (String headerName : response.getHeaderNames()) {
			for (String value : response.getHeaders(headerName)) {
				headers.add(headerName, value);
			}
		}

		if (response.getCookies() != null && !headers.containsHeader(HttpHeaders.SET_COOKIE)) {
			for (Cookie cookie : response.getCookies()) {
				headers.add(HttpHeaders.SET_COOKIE, generateSetCookieHeader(cookie));
			}
		}

		return headers;
	}

	private String generateSetCookieHeader(Cookie cookie) {
		StringBuilder header = new StringBuilder();

		header.append(cookie.getName());
		header.append('=');

		appendIfAvailable(header, cookie.getValue());

		int maxAge = cookie.getMaxAge();
		if (maxAge > -1) {
			header.append(";Max-Age=");
			header.append(maxAge);
		}

		appendIfAvailable(header, "; Domain=", cookie.getDomain());
		appendIfAvailable(header, "; Path=", cookie.getPath());

		if (cookie.getSecure()) {
			header.append("; Secure");
		}

		if (cookie.isHttpOnly()) {
			header.append("; HttpOnly");
		}

		return header.toString();
	}

	private void appendIfAvailable(StringBuilder header, String value) {
		if (StringUtils.hasText(value)) {
			header.append("");
			header.append(value);
		}
	}

	private void appendIfAvailable(StringBuilder header, String name, String value) {
		if (StringUtils.hasText(value)) {
			header.append(name);
			header.append(value);
		}
	}

}
