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
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.ResponseConverter;
import org.springframework.restdocs.operation.ResponseCookie;
import org.springframework.test.web.reactive.server.ExchangeResult;

/**
 * A {@link ResponseConverter} for creating an {@link OperationResponse} derived from an
 * {@link ExchangeResult}.
 *
 * @author Andy Wilkinson
 */
class WebTestClientResponseConverter implements ResponseConverter<ExchangeResult> {

	@Override
	public OperationResponse convert(ExchangeResult result) {
		HttpHeaders headers = result.getResponseHeaders();
		Collection<ResponseCookie> cookies = extractCookies(result, headers);
		return new OperationResponseFactory().create(result.getStatus(), headers,
				result.getResponseBodyContent(), cookies);
	}

	private Collection<ResponseCookie> extractCookies(ExchangeResult result,
			HttpHeaders headers) {
		List<String> cookieHeaders = headers.get(HttpHeaders.COOKIE);
		if (cookieHeaders == null) {
			return result.getResponseCookies().values().stream().flatMap(List::stream)
					.map(this::createResponseCookie).collect(Collectors.toSet());
		}
		headers.remove(HttpHeaders.COOKIE);
		return cookieHeaders.stream().map(this::createResponseCookie)
				.collect(Collectors.toList());
	}

	private ResponseCookie createResponseCookie(
			org.springframework.http.ResponseCookie original) {
		return new ResponseCookie(original.getName(), original.getValue());
	}

	private ResponseCookie createResponseCookie(String header) {
		String[] components = header.split("=");
		return new ResponseCookie(components[0], components[1]);
	}

}
