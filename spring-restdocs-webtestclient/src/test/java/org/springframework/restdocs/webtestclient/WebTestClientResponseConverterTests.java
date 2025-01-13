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

package org.springframework.restdocs.webtestclient;

import java.util.Collections;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.ResponseCookie;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * Tests for {@link WebTestClientResponseConverter}.
 *
 * @author Andy Wilkinson
 */
public class WebTestClientResponseConverterTests {

	private final WebTestClientResponseConverter converter = new WebTestClientResponseConverter();

	@Test
	public void basicResponse() {
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(
					RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().bodyValue("Hello, World!")))
			.configureClient()
			.baseUrl("http://localhost")
			.build()
			.get()
			.uri("/foo")
			.exchange()
			.expectBody()
			.returnResult();
		OperationResponse response = this.converter.convert(result);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
		assertThat(response.getContentAsString()).isEqualTo("Hello, World!");
		assertThat(response.getHeaders().getContentType())
			.isEqualTo(MediaType.parseMediaType("text/plain;charset=UTF-8"));
		assertThat(response.getHeaders().getContentLength()).isEqualTo(13);
	}

	@Test
	public void responseWithCookie() {
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"),
					(req) -> ServerResponse.ok()
						.cookie(org.springframework.http.ResponseCookie.from("name", "value")
							.domain("localhost")
							.httpOnly(true)
							.build())
						.build()))
			.configureClient()
			.baseUrl("http://localhost")
			.build()
			.get()
			.uri("/foo")
			.exchange()
			.expectBody()
			.returnResult();
		OperationResponse response = this.converter.convert(result);
		assertThat(response.getHeaders().headerSet()).containsOnly(
				entry(HttpHeaders.SET_COOKIE, Collections.singletonList("name=value; Domain=localhost; HttpOnly")));
		assertThat(response.getCookies()).hasSize(1);
		assertThat(response.getCookies()).first().extracting(ResponseCookie::getName).isEqualTo("name");
		assertThat(response.getCookies()).first().extracting(ResponseCookie::getValue).isEqualTo("value");
	}

	@Test
	public void responseWithNonStandardStatusCode() {
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.status(210).build()))
			.configureClient()
			.baseUrl("http://localhost")
			.build()
			.get()
			.uri("/foo")
			.exchange()
			.expectBody()
			.returnResult();
		OperationResponse response = this.converter.convert(result);
		assertThat(response.getStatus()).isEqualTo(HttpStatusCode.valueOf(210));
	}

}
