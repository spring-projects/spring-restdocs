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

package org.springframework.restdocs.resttestclient;

import java.util.Collections;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.ResponseCookie;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.web.servlet.function.RequestPredicates.GET;

/**
 * Tests for {@link RestTestClientResponseConverter}.
 *
 * @author Andy Wilkinson
 */
class RestTestClientResponseConverterTests {

	private final RestTestClientResponseConverter converter = new RestTestClientResponseConverter();

	@Test
	void basicResponse() {
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(
					RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().body("Hello, World!")))
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
			.isEqualTo(MediaType.parseMediaType("text/plain;charset=ISO-8859-1"));
		assertThat(response.getHeaders().getContentLength()).isEqualTo(13);
	}

	@Test
	void responseWithCookie() {
		ExchangeResult result = RestTestClient.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> {
			Cookie cookie = new Cookie("name", "value");
			cookie.setDomain("localhost");
			cookie.setHttpOnly(true);
			return ServerResponse.ok().cookie(cookie).build();
		})).baseUrl("http://localhost").build().get().uri("/foo").exchange().expectBody().returnResult();
		OperationResponse response = this.converter.convert(result);
		assertThat(response.getHeaders().headerSet()).containsOnly(
				entry(HttpHeaders.SET_COOKIE, Collections.singletonList("name=value; Domain=localhost; HttpOnly")));
		assertThat(response.getCookies()).hasSize(1);
		assertThat(response.getCookies()).first().extracting(ResponseCookie::getName).isEqualTo("name");
		assertThat(response.getCookies()).first().extracting(ResponseCookie::getValue).isEqualTo("value");
	}

	@Test
	void responseWithNonStandardStatusCode() {
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.status(210).build()))
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
