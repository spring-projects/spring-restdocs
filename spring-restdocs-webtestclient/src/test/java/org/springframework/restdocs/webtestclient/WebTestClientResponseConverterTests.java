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

import java.util.Collections;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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
				.bindToRouterFunction(RouterFunctions.route(GET("/foo"),
						(req) -> ServerResponse.ok().syncBody("Hello, World!")))
				.configureClient().baseUrl("http://localhost").build().get().uri("/foo")
				.exchange().expectBody().returnResult();
		OperationResponse response = this.converter.convert(result);
		assertThat(response.getStatus(), is(HttpStatus.OK));
		assertThat(response.getContentAsString(), is(equalTo("Hello, World!")));
		assertThat(response.getHeaders().getContentType(),
				is(MediaType.parseMediaType("text/plain;charset=UTF-8")));
		assertThat(response.getHeaders().getContentLength(), is(13L));
	}

	@Test
	public void responseWithCookie() {
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(GET("/foo"),
						(req) -> ServerResponse.ok()
								.cookie(ResponseCookie.from("name", "value")
										.domain("localhost").httpOnly(true).build())
								.build()))
				.configureClient().baseUrl("http://localhost").build().get().uri("/foo")
				.exchange().expectBody().returnResult();
		OperationResponse response = this.converter.convert(result);
		assertThat(response.getHeaders().size(), is(1));
		assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
		assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE), equalTo(
				Collections.singletonList("name=value; Domain=localhost; HttpOnly")));
	}

}
