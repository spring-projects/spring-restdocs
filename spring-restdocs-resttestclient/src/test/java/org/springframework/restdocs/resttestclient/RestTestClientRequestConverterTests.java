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

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RequestPredicates.POST;

/**
 * Tests for {@link RestTestClientRequestConverter}.
 *
 * @author Andy Wilkinson
 */
class RestTestClientRequestConverterTests {

	private final RestTestClientRequestConverter converter = new RestTestClientRequestConverter();

	@Test
	void httpRequest() {
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.baseUrl("http://localhost")
			.build()
			.get()
			.uri("/foo")
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
	}

	@Test
	void httpRequestWithCustomPort() {
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.baseUrl("http://localhost:8080")
			.build()
			.get()
			.uri("/foo")
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost:8080/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
	}

	@Test
	void requestWithHeaders() {
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/"), (req) -> ServerResponse.ok().build()))
			.baseUrl("http://localhost")
			.build()
			.get()
			.uri("/foo")
			.header("a", "alpha", "apple")
			.header("b", "bravo")
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
		assertThat(request.getHeaders().headerSet()).contains(entry("a", Arrays.asList("alpha", "apple")),
				entry("b", Arrays.asList("bravo")));
	}

	@Test
	void httpsRequest() {
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.baseUrl("https://localhost")
			.build()
			.get()
			.uri("/foo")
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("https://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
	}

	@Test
	void httpsRequestWithCustomPort() {
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))

			.baseUrl("https://localhost:8443")
			.build()
			.get()
			.uri("/foo")
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("https://localhost:8443/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
	}

	@Test
	void getRequestWithQueryString() {
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.baseUrl("http://localhost")
			.build()
			.get()
			.uri("/foo?a=alpha&b=bravo")
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo?a=alpha&b=bravo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
	}

	@Test
	void postRequestWithFormDataParameters() {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.addAll("a", Arrays.asList("alpha", "apple"));
		parameters.addAll("b", Arrays.asList("br&vo"));
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(RouterFunctions.route(POST("/foo"), (req) -> ServerResponse.ok().build()))
			.baseUrl("http://localhost")
			.build()
			.post()
			.uri("/foo")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(parameters)
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
		assertThat(request.getContentAsString()).isEqualTo("a=alpha&a=apple&b=br%26vo");
		assertThat(request.getHeaders().getContentType()).satisfiesAnyOf(
				(mediaType) -> assertThat(mediaType)
					.isEqualTo(new MediaType(MediaType.APPLICATION_FORM_URLENCODED, StandardCharsets.UTF_8)),
				(mediaType) -> assertThat(mediaType).isEqualTo(new MediaType(MediaType.APPLICATION_FORM_URLENCODED)));
	}

	@Test
	void postRequestWithQueryStringParameters() {
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(RouterFunctions.route(POST("/foo"), (req) -> ServerResponse.ok().build()))
			.baseUrl("http://localhost")
			.build()
			.post()
			.uri(URI.create("http://localhost/foo?a=alpha&a=apple&b=br%26vo"))
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo?a=alpha&a=apple&b=br%26vo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
	}

	@Test
	void postRequestWithQueryStringAndFormDataParameters() {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.addAll("a", Arrays.asList("apple"));
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(RouterFunctions.route(POST("/foo"), (req) -> ServerResponse.ok().build()))
			.baseUrl("http://localhost")
			.build()
			.post()
			.uri(URI.create("http://localhost/foo?a=alpha&b=br%26vo"))
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(parameters)
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo?a=alpha&b=br%26vo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
		assertThat(request.getContentAsString()).isEqualTo("a=apple");
		assertThat(request.getHeaders().getContentType()).satisfiesAnyOf(
				(mediaType) -> assertThat(mediaType)
					.isEqualTo(new MediaType(MediaType.APPLICATION_FORM_URLENCODED, StandardCharsets.UTF_8)),
				(mediaType) -> assertThat(mediaType).isEqualTo(new MediaType(MediaType.APPLICATION_FORM_URLENCODED)));
	}

	@Test
	void postRequestWithNoContentType() {
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(RouterFunctions.route(POST("/foo"), (req) -> ServerResponse.ok().build()))

			.baseUrl("http://localhost")
			.build()
			.post()
			.uri("/foo")
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
	}

	@Test
	void multipartUpload() {
		MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
		multipartData.add("file", new byte[] { 1, 2, 3, 4 });
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(
					RouterFunctions.route(POST("/foo"), (req) -> ServerResponse.ok().body(req.multipartData().size())))
			.baseUrl("http://localhost")
			.build()
			.post()
			.uri("/foo")
			.body(multipartData)
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
		assertThat(request.getParts()).hasSize(1);
		OperationRequestPart part = request.getParts().iterator().next();
		assertThat(part.getName()).isEqualTo("file");
		assertThat(part.getSubmittedFileName()).isNull();
		assertThat(part.getHeaders().size()).isEqualTo(3);
		assertThat(part.getHeaders().getContentLength()).isEqualTo(4L);
		assertThat(part.getHeaders().getContentDisposition().getName()).isEqualTo("file");
		assertThat(part.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
		assertThat(part.getContent()).containsExactly(1, 2, 3, 4);
	}

	@Test
	void multipartUploadFromResource() {
		MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
		multipartData.add("file", new ByteArrayResource(new byte[] { 1, 2, 3, 4 }) {

			@Override
			public String getFilename() {
				return "image.png";
			}

		});
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(
					RouterFunctions.route(POST("/foo"), (req) -> ServerResponse.ok().body(req.multipartData().size())))
			.baseUrl("http://localhost")
			.build()
			.post()
			.uri("/foo")
			.body(multipartData)
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
		assertThat(request.getParts()).hasSize(1);
		OperationRequestPart part = request.getParts().iterator().next();
		assertThat(part.getName()).isEqualTo("file");
		assertThat(part.getSubmittedFileName()).isEqualTo("image.png");
		assertThat(part.getHeaders().size()).isEqualTo(3);
		assertThat(part.getHeaders().getContentLength()).isEqualTo(4);
		ContentDisposition contentDisposition = part.getHeaders().getContentDisposition();
		assertThat(contentDisposition.getName()).isEqualTo("file");
		assertThat(contentDisposition.getFilename()).isEqualTo("image.png");
		assertThat(part.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_PNG);
		assertThat(part.getContent()).containsExactly(1, 2, 3, 4);
	}

	@Test
	void requestWithCookies() {
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.baseUrl("http://localhost")
			.build()
			.get()
			.uri("/foo")
			.cookie("cookieName1", "cookieVal1")
			.cookie("cookieName2", "cookieVal2")
			.exchange()
			.expectBody()
			.returnResult();
		List<String> cookieHeaders = result.getRequestHeaders().get(HttpHeaders.COOKIE);
		assertThat(cookieHeaders).isNotNull();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
		assertThat(request.getCookies()).hasSize(2);
		assertThat(request.getHeaders().get(HttpHeaders.COOKIE)).isNull();
		assertThat(request.getCookies()).extracting("name").containsExactly("cookieName1", "cookieName2");
		assertThat(request.getCookies()).extracting("value").containsExactly("cookieVal1", "cookieVal2");
	}

	@Test
	void requestWithCookieValueContainingEqualsSign() {
		ExchangeResult result = RestTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.baseUrl("http://localhost")
			.build()
			.get()
			.uri("/foo")
			.cookie("sessionId", "YWJjZGVm==")
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getCookies()).hasSize(1);
		assertThat(request.getCookies()).extracting("name").containsExactly("sessionId");
		assertThat(request.getCookies()).extracting("value").containsExactly("YWJjZGVm==");
	}

}
