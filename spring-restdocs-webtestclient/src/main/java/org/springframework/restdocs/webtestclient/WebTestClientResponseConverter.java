/*
 * Copyright 2014-2018 the original author or authors.
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

package org.springframework.restdocs.webtestclient;

import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.ResponseConverter;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.util.StringUtils;

/**
 * A {@link ResponseConverter} for creating an {@link OperationResponse} derived from an
 * {@link ExchangeResult}.
 *
 * @author Andy Wilkinson
 */
class WebTestClientResponseConverter implements ResponseConverter<ExchangeResult> {

	@Override
	public OperationResponse convert(ExchangeResult result) {
		return new OperationResponseFactory().create(result.getStatus(),
				extractHeaders(result), result.getResponseBodyContent());
	}

	private HttpHeaders extractHeaders(ExchangeResult result) {
		HttpHeaders headers = result.getResponseHeaders();
		if (result.getResponseCookies().isEmpty()
				|| headers.containsKey(HttpHeaders.SET_COOKIE)) {
			return headers;
		}
		result.getResponseCookies().values().stream().flatMap(Collection::stream)
				.forEach((cookie) -> headers.add(HttpHeaders.SET_COOKIE,
						generateSetCookieHeader(cookie)));
		return headers;
	}

	private String generateSetCookieHeader(ResponseCookie cookie) {
		StringBuilder header = new StringBuilder();
		header.append(cookie.getName());
		header.append('=');
		appendIfAvailable(header, cookie.getValue());
		long maxAge = cookie.getMaxAge().getSeconds();
		if (maxAge > -1) {
			header.append("; Max-Age=");
			header.append(maxAge);
		}
		appendIfAvailable(header, "; Domain=", cookie.getDomain());
		appendIfAvailable(header, "; Path=", cookie.getPath());
		if (cookie.isSecure()) {
			header.append("; Secure");
		}
		if (cookie.isHttpOnly()) {
			header.append("; HttpOnly");
		}
		return header.toString();
	}

	private void appendIfAvailable(StringBuilder header, String value) {
		if (StringUtils.hasText(value)) {
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
