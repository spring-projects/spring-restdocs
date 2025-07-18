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

package org.springframework.restdocs.operation.preprocess;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationRequestPartFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.RequestCookie;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link UriModifyingOperationPreprocessor}.
 *
 * @author Andy Wilkinson
 */
class UriModifyingOperationPreprocessorTests {

	private final OperationRequestFactory requestFactory = new OperationRequestFactory();

	private final OperationResponseFactory responseFactory = new OperationResponseFactory();

	private final UriModifyingOperationPreprocessor preprocessor = new UriModifyingOperationPreprocessor();

	@Test
	void requestUriSchemeCanBeModified() {
		this.preprocessor.scheme("https");
		OperationRequest processed = this.preprocessor.preprocess(createRequestWithUri("http://localhost:12345"));
		assertThat(processed.getUri()).isEqualTo(URI.create("https://localhost:12345"));
	}

	@Test
	void requestUriHostCanBeModified() {
		this.preprocessor.host("api.example.com");
		OperationRequest processed = this.preprocessor.preprocess(createRequestWithUri("https://api.foo.com:12345"));
		assertThat(processed.getUri()).isEqualTo(URI.create("https://api.example.com:12345"));
		assertThat(processed.getHeaders().getFirst(HttpHeaders.HOST)).isEqualTo("api.example.com:12345");
	}

	@Test
	void requestUriPortCanBeModified() {
		this.preprocessor.port(23456);
		OperationRequest processed = this.preprocessor
			.preprocess(createRequestWithUri("https://api.example.com:12345"));
		assertThat(processed.getUri()).isEqualTo(URI.create("https://api.example.com:23456"));
		assertThat(processed.getHeaders().getFirst(HttpHeaders.HOST)).isEqualTo("api.example.com:23456");
	}

	@Test
	void requestUriPortCanBeRemoved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
			.preprocess(createRequestWithUri("https://api.example.com:12345"));
		assertThat(processed.getUri()).isEqualTo(URI.create("https://api.example.com"));
		assertThat(processed.getHeaders().getFirst(HttpHeaders.HOST)).isEqualTo("api.example.com");
	}

	@Test
	void requestUriPathIsPreserved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
			.preprocess(createRequestWithUri("https://api.example.com:12345/foo/bar"));
		assertThat(processed.getUri()).isEqualTo(URI.create("https://api.example.com/foo/bar"));
	}

	@Test
	void requestUriQueryIsPreserved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
			.preprocess(createRequestWithUri("https://api.example.com:12345?foo=bar"));
		assertThat(processed.getUri()).isEqualTo(URI.create("https://api.example.com?foo=bar"));
	}

	@Test
	void requestUriAnchorIsPreserved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
			.preprocess(createRequestWithUri("https://api.example.com:12345#foo"));
		assertThat(processed.getUri()).isEqualTo(URI.create("https://api.example.com#foo"));
	}

	@Test
	void requestContentUriSchemeCanBeModified() {
		this.preprocessor.scheme("https");
		OperationRequest processed = this.preprocessor.preprocess(createRequestWithContent(
				"The uri 'https://localhost:12345' should be used. foo:bar will be unaffected"));
		assertThat(new String(processed.getContent()))
			.isEqualTo("The uri 'https://localhost:12345' should be used. foo:bar will be unaffected");
	}

	@Test
	void requestContentUriHostCanBeModified() {
		this.preprocessor.host("api.example.com");
		OperationRequest processed = this.preprocessor.preprocess(createRequestWithContent(
				"The uri 'https://localhost:12345' should be used. foo:bar will be unaffected"));
		assertThat(new String(processed.getContent()))
			.isEqualTo("The uri 'https://api.example.com:12345' should be used. foo:bar will be unaffected");
	}

	@Test
	void requestContentHostOfUriWithoutPortCanBeModified() {
		this.preprocessor.host("api.example.com");
		OperationRequest processed = this.preprocessor.preprocess(
				createRequestWithContent("The uri 'https://localhost' should be used. foo:bar will be unaffected"));
		assertThat(new String(processed.getContent()))
			.isEqualTo("The uri 'https://api.example.com' should be used. foo:bar will be unaffected");
	}

	@Test
	void requestContentUriPortCanBeAdded() {
		this.preprocessor.port(23456);
		OperationRequest processed = this.preprocessor.preprocess(
				createRequestWithContent("The uri 'http://localhost' should be used. foo:bar will be unaffected"));
		assertThat(new String(processed.getContent()))
			.isEqualTo("The uri 'http://localhost:23456' should be used. foo:bar will be unaffected");
	}

	@Test
	void requestContentUriPortCanBeModified() {
		this.preprocessor.port(23456);
		OperationRequest processed = this.preprocessor.preprocess(createRequestWithContent(
				"The uri 'http://localhost:12345' should be used. foo:bar will be unaffected"));
		assertThat(new String(processed.getContent()))
			.isEqualTo("The uri 'http://localhost:23456' should be used. foo:bar will be unaffected");
	}

	@Test
	void requestContentUriPortCanBeRemoved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor.preprocess(createRequestWithContent(
				"The uri 'http://localhost:12345' should be used. foo:bar will be unaffected"));
		assertThat(new String(processed.getContent()))
			.isEqualTo("The uri 'http://localhost' should be used. foo:bar will be unaffected");
	}

	@Test
	void multipleRequestContentUrisCanBeModified() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor.preprocess(createRequestWithContent(
				"Use 'http://localhost:12345' or 'https://localhost:23456' to access the service"));
		assertThat(new String(processed.getContent()))
			.isEqualTo("Use 'http://localhost' or 'https://localhost' to access the service");
	}

	@Test
	void requestContentUriPathIsPreserved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
			.preprocess(createRequestWithContent("The uri 'http://localhost:12345/foo/bar' should be used"));
		assertThat(new String(processed.getContent())).isEqualTo("The uri 'http://localhost/foo/bar' should be used");
	}

	@Test
	void requestContentUriQueryIsPreserved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
			.preprocess(createRequestWithContent("The uri 'http://localhost:12345?foo=bar' should be used"));
		assertThat(new String(processed.getContent())).isEqualTo("The uri 'http://localhost?foo=bar' should be used");
	}

	@Test
	void requestContentUriAnchorIsPreserved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
			.preprocess(createRequestWithContent("The uri 'http://localhost:12345#foo' should be used"));
		assertThat(new String(processed.getContent())).isEqualTo("The uri 'http://localhost#foo' should be used");
	}

	@Test
	void responseContentUriSchemeCanBeModified() {
		this.preprocessor.scheme("https");
		OperationResponse processed = this.preprocessor
			.preprocess(createResponseWithContent("The uri 'http://localhost:12345' should be used"));
		assertThat(new String(processed.getContent())).isEqualTo("The uri 'https://localhost:12345' should be used");
	}

	@Test
	void responseContentUriHostCanBeModified() {
		this.preprocessor.host("api.example.com");
		OperationResponse processed = this.preprocessor
			.preprocess(createResponseWithContent("The uri 'https://localhost:12345' should be used"));
		assertThat(new String(processed.getContent()))
			.isEqualTo("The uri 'https://api.example.com:12345' should be used");
	}

	@Test
	void responseContentUriPortCanBeModified() {
		this.preprocessor.port(23456);
		OperationResponse processed = this.preprocessor
			.preprocess(createResponseWithContent("The uri 'http://localhost:12345' should be used"));
		assertThat(new String(processed.getContent())).isEqualTo("The uri 'http://localhost:23456' should be used");
	}

	@Test
	void responseContentUriPortCanBeRemoved() {
		this.preprocessor.removePort();
		OperationResponse processed = this.preprocessor
			.preprocess(createResponseWithContent("The uri 'http://localhost:12345' should be used"));
		assertThat(new String(processed.getContent())).isEqualTo("The uri 'http://localhost' should be used");
	}

	@Test
	void multipleResponseContentUrisCanBeModified() {
		this.preprocessor.removePort();
		OperationResponse processed = this.preprocessor.preprocess(createResponseWithContent(
				"Use 'http://localhost:12345' or 'https://localhost:23456' to access the service"));
		assertThat(new String(processed.getContent()))
			.isEqualTo("Use 'http://localhost' or 'https://localhost' to access the service");
	}

	@Test
	void responseContentUriPathIsPreserved() {
		this.preprocessor.removePort();
		OperationResponse processed = this.preprocessor
			.preprocess(createResponseWithContent("The uri 'http://localhost:12345/foo/bar' should be used"));
		assertThat(new String(processed.getContent())).isEqualTo("The uri 'http://localhost/foo/bar' should be used");
	}

	@Test
	void responseContentUriQueryIsPreserved() {
		this.preprocessor.removePort();
		OperationResponse processed = this.preprocessor
			.preprocess(createResponseWithContent("The uri 'http://localhost:12345?foo=bar' should be used"));
		assertThat(new String(processed.getContent())).isEqualTo("The uri 'http://localhost?foo=bar' should be used");
	}

	@Test
	void responseContentUriAnchorIsPreserved() {
		this.preprocessor.removePort();
		OperationResponse processed = this.preprocessor
			.preprocess(createResponseWithContent("The uri 'http://localhost:12345#foo' should be used"));
		assertThat(new String(processed.getContent())).isEqualTo("The uri 'http://localhost#foo' should be used");
	}

	@Test
	void urisInRequestHeadersCanBeModified() {
		OperationRequest processed = this.preprocessor.host("api.example.com")
			.preprocess(createRequestWithHeader("Foo", "https://locahost:12345"));
		assertThat(processed.getHeaders().getFirst("Foo")).isEqualTo("https://api.example.com:12345");
		assertThat(processed.getHeaders().getFirst("Host")).isEqualTo("api.example.com");
	}

	@Test
	void urisInResponseHeadersCanBeModified() {
		OperationResponse processed = this.preprocessor.host("api.example.com")
			.preprocess(createResponseWithHeader("Foo", "https://locahost:12345"));
		assertThat(processed.getHeaders().getFirst("Foo")).isEqualTo("https://api.example.com:12345");
	}

	@Test
	void urisInRequestPartHeadersCanBeModified() {
		OperationRequest processed = this.preprocessor.host("api.example.com")
			.preprocess(createRequestWithPartWithHeader("Foo", "https://locahost:12345"));
		assertThat(processed.getParts().iterator().next().getHeaders().getFirst("Foo"))
			.isEqualTo("https://api.example.com:12345");
	}

	@Test
	void urisInRequestPartContentCanBeModified() {
		OperationRequest processed = this.preprocessor.host("api.example.com")
			.preprocess(createRequestWithPartWithContent("The uri 'https://localhost:12345' should be used"));
		assertThat(new String(processed.getParts().iterator().next().getContent()))
			.isEqualTo("The uri 'https://api.example.com:12345' should be used");
	}

	@Test
	void modifiedUriDoesNotGetDoubleEncoded() {
		this.preprocessor.scheme("https");
		OperationRequest processed = this.preprocessor
			.preprocess(createRequestWithUri("http://localhost:12345?foo=%7B%7D"));
		assertThat(processed.getUri()).isEqualTo(URI.create("https://localhost:12345?foo=%7B%7D"));

	}

	@Test
	void resultingRequestHasCookiesFromOriginalRequst() {
		List<RequestCookie> cookies = Arrays.asList(new RequestCookie("a", "alpha"));
		OperationRequest request = this.requestFactory.create(URI.create("http://localhost:12345"), HttpMethod.GET,
				new byte[0], new HttpHeaders(), Collections.<OperationRequestPart>emptyList(), cookies);
		OperationRequest processed = this.preprocessor.preprocess(request);
		assertThat(processed.getCookies().size()).isEqualTo(1);
	}

	private OperationRequest createRequestWithUri(String uri) {
		return this.requestFactory.create(URI.create(uri), HttpMethod.GET, new byte[0], new HttpHeaders(),
				Collections.<OperationRequestPart>emptyList());
	}

	private OperationRequest createRequestWithContent(String content) {
		return this.requestFactory.create(URI.create("http://localhost"), HttpMethod.GET, content.getBytes(),
				new HttpHeaders(), Collections.<OperationRequestPart>emptyList());
	}

	private OperationRequest createRequestWithHeader(String name, String value) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(name, value);
		return this.requestFactory.create(URI.create("http://localhost"), HttpMethod.GET, new byte[0], headers,
				Collections.<OperationRequestPart>emptyList());
	}

	private OperationRequest createRequestWithPartWithHeader(String name, String value) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(name, value);
		return this.requestFactory.create(URI.create("http://localhost"), HttpMethod.GET, new byte[0],
				new HttpHeaders(),
				Arrays.asList(new OperationRequestPartFactory().create("part", "fileName", new byte[0], headers)));
	}

	private OperationRequest createRequestWithPartWithContent(String content) {
		return this.requestFactory.create(URI.create("http://localhost"), HttpMethod.GET, new byte[0],
				new HttpHeaders(), Arrays.asList(new OperationRequestPartFactory().create("part", "fileName",
						content.getBytes(), new HttpHeaders())));
	}

	private OperationResponse createResponseWithContent(String content) {
		return this.responseFactory.create(HttpStatus.OK, new HttpHeaders(), content.getBytes());
	}

	private OperationResponse createResponseWithHeader(String name, String value) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(name, value);
		return this.responseFactory.create(HttpStatus.OK, headers, new byte[0]);
	}

}
