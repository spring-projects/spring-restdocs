/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.webtestclient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

/**
 * Tests for {@link WebTestClientRequestConverter}.
 *
 * @author Andy Wilkinson
 * @author Abinesh (AbineshDev01)
 */
class WebTestClientRequestConverterTests {

	private final WebTestClientRequestConverter converter = new WebTestClientRequestConverter();

	@Test
	void httpRequest() {
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.configureClient()
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
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.configureClient()
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
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/"), (req) -> ServerResponse.ok().build()))
			.configureClient()
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
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.configureClient()
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
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.configureClient()
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
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.configureClient()
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
		ExchangeResult result = WebTestClient.bindToRouterFunction(RouterFunctions.route(POST("/foo"), (req) -> {
			req.body(BodyExtractors.toFormData()).block();
			return ServerResponse.ok().build();
		}))
			.configureClient()
			.baseUrl("http://localhost")
			.build()
			.post()
			.uri("/foo")
			.body(BodyInserters.fromFormData(parameters))
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
		ExchangeResult result = WebTestClient.bindToRouterFunction(RouterFunctions.route(POST("/foo"), (req) -> {
			req.body(BodyExtractors.toFormData()).block();
			return ServerResponse.ok().build();
		}))
			.configureClient()
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
		ExchangeResult result = WebTestClient.bindToRouterFunction(RouterFunctions.route(POST("/foo"), (req) -> {
			req.body(BodyExtractors.toFormData()).block();
			return ServerResponse.ok().build();
		}))
			.configureClient()
			.baseUrl("http://localhost")
			.build()
			.post()
			.uri(URI.create("http://localhost/foo?a=alpha&b=br%26vo"))
			.body(BodyInserters.fromFormData(parameters))
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
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(POST("/foo"), (req) -> ServerResponse.ok().build()))
			.configureClient()
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
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(POST("/foo"),
					(req) -> ServerResponse.ok()
						.body(req.body(BodyExtractors.toMultipartData()).map((parts) -> parts.size()), Integer.class)))
			.configureClient()
			.baseUrl("http://localhost")
			.build()
			.post()
			.uri("/foo")
			.body(BodyInserters.fromMultipartData(multipartData))
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
		assertThat(request.getContent()).isEmpty();
		assertThat(request.getHeaders().getContentType()).isEqualTo(MediaType.MULTIPART_FORM_DATA);
		assertThat(request.getHeaders().getContentLength()).isEqualTo(-1);
		assertThat(result.getRequestHeaders().getContentType()).isNotNull()
			.satisfies((contentType) -> assertThat(contentType.getParameter("boundary")).isNotEmpty());
		assertThat(request.getParts()).hasSize(1);
		OperationRequestPart part = request.getParts().iterator().next();
		assertThat(part.getName()).isEqualTo("file");
		assertThat(part.getSubmittedFileName()).isNull();
		assertThat(part.getHeaders().size()).isEqualTo(2);
		assertThat(part.getHeaders().getContentLength()).isEqualTo(4L);
		assertThat(part.getHeaders().getContentDisposition().getName()).isEqualTo("file");
		assertThat(part.getContent()).containsExactly(1, 2, 3, 4);
	}

	@ParameterizedTest
	@ValueSource(ints = { 4, 300_000 })
	void multipartUploadFromResource(int contentLength) {
		byte[] content = new byte[contentLength];
		for (int i = 0; i < content.length; i++) {
			content[i] = (byte) (i % 127);
		}
		MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
		multipartData.add("file", new ByteArrayResource(content) {

			@Override
			public String getFilename() {
				return "image.png";
			}

		});
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(POST("/foo"),
					(req) -> ServerResponse.ok()
						.body(req.body(BodyExtractors.toMultipartData()).map((parts) -> parts.size()), Integer.class)))
			.configureClient()
			.baseUrl("http://localhost")
			.build()
			.post()
			.uri("/foo")
			.body(BodyInserters.fromMultipartData(multipartData))
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getUri()).isEqualTo(URI.create("http://localhost/foo"));
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
		assertThat(request.getContent()).isEmpty();
		assertThat(request.getHeaders().getContentType()).isEqualTo(MediaType.MULTIPART_FORM_DATA);
		assertThat(request.getHeaders().getContentLength()).isEqualTo(-1);
		assertThat(result.getRequestHeaders().getContentType()).isNotNull()
			.satisfies((contentType) -> assertThat(contentType.getParameter("boundary")).isNotEmpty());
		assertThat(request.getParts()).hasSize(1);
		OperationRequestPart part = request.getParts().iterator().next();
		assertThat(part.getName()).isEqualTo("file");
		assertThat(part.getSubmittedFileName()).isEqualTo("image.png");
		assertThat(part.getHeaders().size()).isEqualTo(3);
		assertThat(part.getHeaders().getContentLength()).isEqualTo(contentLength);
		ContentDisposition contentDisposition = part.getHeaders().getContentDisposition();
		assertThat(contentDisposition.getName()).isEqualTo("file");
		assertThat(contentDisposition.getFilename()).isEqualTo("image.png");
		assertThat(part.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_PNG);
		assertThat(part.getContent()).isEqualTo(content);
	}

	@ParameterizedTest
	@ValueSource(strings = { "text/plain", "multipart/form-data;boundary=missing" })
	void requestWithoutParsedPartsRetainsContent(String contentType) {
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(POST("/foo"),
					(req) -> req.bodyToMono(String.class).flatMap((body) -> ServerResponse.ok().build())))
			.configureClient()
			.baseUrl("http://localhost")
			.build()
			.post()
			.uri("/foo")
			.contentType(MediaType.parseMediaType(contentType))
			.bodyValue("raw content")
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getParts()).isEmpty();
		assertThat(request.getContentAsString()).isEqualTo("raw content");
		assertThat(request.getHeaders().getContentType()).isEqualTo(MediaType.parseMediaType(contentType));
	}

	@ParameterizedTest
	@ValueSource(strings = { "boundary", "BOUNDARY" })
	void multipartUploadRetainsContentTypeParametersAndRepeatedParts(String boundaryParameter) {
		String content = "--boundary\r\nContent-Disposition: form-data; name=\"a\"\r\n\r\nalpha\r\n"
				+ "--boundary\r\nContent-Disposition: form-data; name=\"a\"\r\n\r\n\r\n--boundary--\r\n";
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(POST("/foo"),
					(req) -> req.bodyToMono(String.class).flatMap((body) -> ServerResponse.ok().build())))
			.configureClient()
			.baseUrl("http://localhost")
			.build()
			.post()
			.uri("/foo")
			.contentType(
					MediaType.parseMediaType("multipart/form-data;" + boundaryParameter + "=boundary;charset=UTF-8"))
			.bodyValue(content)
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getContent()).isEmpty();
		assertThat(request.getHeaders().getContentType())
			.isEqualTo(MediaType.parseMediaType("multipart/form-data;charset=UTF-8"));
		assertThat(request.getParts()).extracting(OperationRequestPart::getName).containsExactly("a", "a");
		assertThat(request.getParts()).extracting(OperationRequestPart::getContentAsString)
			.containsExactly("alpha", "");
	}

	@Test
	void requestWithCookies() {
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.configureClient()
			.baseUrl("http://localhost")
			.build()
			.get()
			.uri("/foo")
			.cookie("cookieName1", "cookieVal1")
			.cookie("cookieName2", "cookieVal2")
			.exchange()
			.expectBody()
			.returnResult();
		assertThat(result.getRequestHeaders().get(HttpHeaders.COOKIE)).isNotNull();
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
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.configureClient()
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

	@Test
	void requestWithCookieHeaderContainingMultipleCookies() {
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.configureClient()
			.baseUrl("http://localhost")
			.build()
			.get()
			.uri("/foo")
			.header(HttpHeaders.COOKIE, "cookieName1=cookieVal1; cookieName2=cookieVal2")
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getCookies()).hasSize(2);
		assertThat(request.getHeaders().get(HttpHeaders.COOKIE)).isNull();
		assertThat(request.getCookies()).extracting("name").containsExactly("cookieName1", "cookieName2");
		assertThat(request.getCookies()).extracting("value").containsExactly("cookieVal1", "cookieVal2");
	}

	@Test
	void requestWithCookieHeaderWithoutEqualsSign() {
		ExchangeResult result = WebTestClient
			.bindToRouterFunction(RouterFunctions.route(GET("/foo"), (req) -> ServerResponse.ok().build()))
			.configureClient()
			.baseUrl("http://localhost")
			.build()
			.get()
			.uri("/foo")
			.header(HttpHeaders.COOKIE, "cookieName")
			.exchange()
			.expectBody()
			.returnResult();
		OperationRequest request = this.converter.convert(result);
		assertThat(request.getCookies()).hasSize(1);
		assertThat(request.getCookies()).extracting("name").containsExactly("cookieName");
		assertThat(request.getCookies()).extracting("value").containsExactly("");
	}

}
