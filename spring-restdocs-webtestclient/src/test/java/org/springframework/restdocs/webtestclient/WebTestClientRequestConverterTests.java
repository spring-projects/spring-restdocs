/*
 * Copyright 2014-2018 the original author or authors.
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

import java.net.URI;
import java.util.Arrays;

import org.junit.Test;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

/**
 * Tests for {@link WebTestClientRequestConverter}.
 *
 * @author Andy Wilkinson
 */
public class WebTestClientRequestConverterTests {

	private final WebTestClientRequestConverter converter = new WebTestClientRequestConverter();

	@Test
	public void httpRequest() {
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> null))
				.configureClient().baseUrl("http://localhost").build().get().uri("/foo")
				.exchange().expectBody().returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
	}

	@Test
	public void httpRequestWithCustomPort() {
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> null))
				.configureClient().baseUrl("http://localhost:8080").build().get()
				.uri("/foo").exchange().expectBody().returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost:8080/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
	}

	@Test
	public void requestWithHeaders() {
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(GET("/"), (req) -> null))
				.configureClient().baseUrl("http://localhost").build().get().uri("/foo")
				.header("a", "alpha", "apple").header("b", "bravo").exchange()
				.expectBody().returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
		assertThat(request.getHeaders()).containsEntry("a",
				Arrays.asList("alpha", "apple"));
		assertThat(request.getHeaders()).containsEntry("b", Arrays.asList("bravo"));
	}

	@Test
	public void httpsRequest() {
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> null))
				.configureClient().baseUrl("https://localhost").build().get().uri("/foo")
				.exchange().expectBody().returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("https://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
	}

	@Test
	public void httpsRequestWithCustomPort() {
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> null))
				.configureClient().baseUrl("https://localhost:8443").build().get()
				.uri("/foo").exchange().expectBody().returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("https://localhost:8443/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
	}

	@Test
	public void getRequestWithQueryStringPopulatesParameters() throws Exception {
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> null))
				.configureClient().baseUrl("http://localhost").build().get()
				.uri("/foo?a=alpha&b=bravo").exchange().expectBody().returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri())
				.isEqualTo(URI.create("http://localhost/foo?a=alpha&b=bravo"));
		assertThat(request.getParameters()).hasSize(2);
		assertThat(request.getParameters()).containsEntry("a", Arrays.asList("alpha"));
		assertThat(request.getParameters()).containsEntry("b", Arrays.asList("bravo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
	}

	@Test
	public void postRequestWithFormDataParameters() throws Exception {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.addAll("a", Arrays.asList("alpha", "apple"));
		parameters.addAll("b", Arrays.asList("br&vo"));
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(POST("/foo"), (req) -> {
					req.body(BodyExtractors.toFormData()).block();
					return null;
				})).configureClient().baseUrl("http://localhost").build().post()
				.uri("/foo").body(BodyInserters.fromFormData(parameters)).exchange()
				.expectBody().returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
		assertThat(request.getParameters()).hasSize(2);
		assertThat(request.getParameters()).containsEntry("a",
				Arrays.asList("alpha", "apple"));
		assertThat(request.getParameters()).containsEntry("b", Arrays.asList("br&vo"));
	}

	@Test
	public void postRequestWithQueryStringParameters() throws Exception {
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(POST("/foo"), (req) -> {
					req.body(BodyExtractors.toFormData()).block();
					return null;
				})).configureClient().baseUrl("http://localhost").build().post()
				.uri(URI.create("http://localhost/foo?a=alpha&a=apple&b=br%26vo"))
				.exchange().expectBody().returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri())
				.isEqualTo(URI.create("http://localhost/foo?a=alpha&a=apple&b=br%26vo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
		assertThat(request.getParameters()).hasSize(2);
		assertThat(request.getParameters()).containsEntry("a",
				Arrays.asList("alpha", "apple"));
		assertThat(request.getParameters()).containsEntry("b", Arrays.asList("br&vo"));
	}

	@Test
	public void postRequestWithQueryStringAndFormDataParameters() throws Exception {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.addAll("a", Arrays.asList("apple"));
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(POST("/foo"), (req) -> {
					req.body(BodyExtractors.toFormData()).block();
					return null;
				})).configureClient().baseUrl("http://localhost").build().post()
				.uri(URI.create("http://localhost/foo?a=alpha&b=br%26vo"))
				.body(BodyInserters.fromFormData(parameters)).exchange().expectBody()
				.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri())
				.isEqualTo(URI.create("http://localhost/foo?a=alpha&b=br%26vo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
		assertThat(request.getParameters()).hasSize(2);
		assertThat(request.getParameters()).containsEntry("a",
				Arrays.asList("alpha", "apple"));
		assertThat(request.getParameters()).containsEntry("b", Arrays.asList("br&vo"));
	}

	@Test
	public void postRequestWithNoContentType() throws Exception {
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(POST("/foo"),
						(req) -> ServerResponse.ok().build()))
				.configureClient().baseUrl("http://localhost").build().post().uri("/foo")
				.exchange().expectBody().returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
	}

	@Test
	public void multipartUpload() throws Exception {
		MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
		multipartData.add("file", new byte[] { 1, 2, 3, 4 });
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(POST("/foo"), (req) -> {
					req.body(BodyExtractors.toMultipartData()).block();
					return null;
				})).configureClient().baseUrl("http://localhost").build().post()
				.uri("/foo").body(BodyInserters.fromMultipartData(multipartData))
				.exchange().expectBody().returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
		assertThat(request.getParts()).hasSize(1);
		OperationRequestPart part = request.getParts().iterator().next();
		assertThat(part.getName()).isEqualTo("file");
		assertThat(part.getSubmittedFileName()).isNull();
		assertThat(part.getHeaders()).hasSize(2);
		assertThat(part.getHeaders().getContentLength()).isEqualTo(4L);
		assertThat(part.getHeaders().getContentDisposition().getName()).isEqualTo("file");
		assertThat(part.getContent()).containsExactly(1, 2, 3, 4);
	}

	@Test
	public void multipartUploadFromResource() throws Exception {
		MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
		multipartData.add("file", new ByteArrayResource(new byte[] { 1, 2, 3, 4 }) {

			@Override
			public String getFilename() {
				return "image.png";
			}

		});
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(POST("/foo"), (req) -> {
					req.body(BodyExtractors.toMultipartData()).block();
					return null;
				})).configureClient().baseUrl("http://localhost").build().post()
				.uri("/foo").body(BodyInserters.fromMultipartData(multipartData))
				.exchange().expectBody().returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
		assertThat(request.getParts()).hasSize(1);
		OperationRequestPart part = request.getParts().iterator().next();
		assertThat(part.getName()).isEqualTo("file");
		assertThat(part.getSubmittedFileName()).isEqualTo("image.png");
		assertThat(part.getHeaders()).hasSize(3);
		assertThat(part.getHeaders().getContentLength()).isEqualTo(4);
		ContentDisposition contentDisposition = part.getHeaders().getContentDisposition();
		assertThat(contentDisposition.getName()).isEqualTo("file");
		assertThat(contentDisposition.getFilename()).isEqualTo("image.png");
		assertThat(part.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_PNG);
		assertThat(part.getContent()).containsExactly(1, 2, 3, 4);
	}

	@Test
	public void requestWithCookies() throws Exception {
		ExchangeResult result = WebTestClient
				.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> null))
				.configureClient().baseUrl("http://localhost").build().get().uri("/foo")
				.cookie("cookieName1", "cookieVal1").cookie("cookieName2", "cookieVal2")
				.exchange().expectBody().returnResult();
		assertThat(result.getRequestHeaders().get(HttpHeaders.COOKIE)).isNotNull();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
		assertThat(request.getCookies()).hasSize(2);
		assertThat(request.getHeaders().get(HttpHeaders.COOKIE)).isNull();
		assertThat(request.getCookies()).extracting("name").containsExactly("cookieName1",
				"cookieName2");
		assertThat(request.getCookies()).extracting("value").containsExactly("cookieVal1",
				"cookieVal2");
	}

}
