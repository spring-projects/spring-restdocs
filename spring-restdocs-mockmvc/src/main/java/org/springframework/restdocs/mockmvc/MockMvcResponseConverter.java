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

package org.springframework.restdocs.mockmvc;

import javax.servlet.http.Cookie;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.ResponseConverter;

/**
 * A converter for creating an {@link OperationResponse} derived from a
 * {@link MockHttpServletResponse}.
 *
 * @author Andy Wilkinson
 */
class MockMvcResponseConverter implements ResponseConverter<MockHttpServletResponse> {

	@Override
	public OperationResponse convert(MockHttpServletResponse mockResponse) {
		return new OperationResponseFactory().create(
				HttpStatus.valueOf(mockResponse.getStatus()),
				extractHeaders(mockResponse), mockResponse.getContentAsByteArray());
	}

	private HttpHeaders extractHeaders(MockHttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();
		for (String headerName : response.getHeaderNames()) {
			for (String value : response.getHeaders(headerName)) {
				headers.add(headerName, value);
			}
		}

		if (response.getCookies() != null && !headers.containsKey(HttpHeaders.SET_COOKIE)) {
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

		String value = cookie.getValue();
		if (value != null && value.length() > 0) {
			header.append(value);
		}

		int maxAge = cookie.getMaxAge();
		if (maxAge > -1) {
			header.append(";Max-Age=");
			header.append(maxAge);
		}

		String domain = cookie.getDomain();
		if (domain != null && domain.length() > 0) {
			header.append(";domain=");
			header.append(domain);
		}

		String path = cookie.getPath();
		if (path != null && path.length() > 0) {
			header.append(";path=");
			header.append(path);
		}

		if (cookie.getSecure()) {
			header.append(";Secure");
		}

		if (cookie.isHttpOnly()) {
			header.append(";HttpOnly");
		}

		return header.toString();
	}

}
