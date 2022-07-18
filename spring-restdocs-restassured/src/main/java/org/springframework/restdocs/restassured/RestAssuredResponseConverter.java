/*
 * Copyright 2014-2022 the original author or authors.
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

package org.springframework.restdocs.restassured;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.restassured.http.Header;
import io.restassured.response.Response;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.ResponseConverter;
import org.springframework.restdocs.operation.ResponseCookie;

/**
 * A converter for creating an {@link OperationResponse} from a REST Assured
 * {@link Response}.
 *
 * @author Andy Wilkinson
 * @author Clyde Stubbs
 */
class RestAssuredResponseConverter implements ResponseConverter<Response> {

	@Override
	public OperationResponse convert(Response response) {
		HttpHeaders headers = extractHeaders(response);
		Collection<ResponseCookie> cookies = extractCookies(response, headers);
		return new OperationResponseFactory().create(response.getStatusCode(), extractHeaders(response),
				extractContent(response), cookies);
	}

	private Collection<ResponseCookie> extractCookies(Response response, HttpHeaders headers) {
		if (response.getCookies() == null || response.getCookies().size() == 0) {
			return Collections.emptyList();
		}
		List<ResponseCookie> cookies = new ArrayList<>();
		for (Map.Entry<String, String> cookie : response.getCookies().entrySet()) {
			cookies.add(new ResponseCookie(cookie.getKey(), cookie.getValue()));
		}
		return cookies;
	}

	private HttpHeaders extractHeaders(Response response) {
		HttpHeaders httpHeaders = new HttpHeaders();
		for (Header header : response.getHeaders()) {
			httpHeaders.add(header.getName(), header.getValue());
		}
		return httpHeaders;
	}

	private byte[] extractContent(Response response) {
		return response.getBody().asByteArray();
	}

}
