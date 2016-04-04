/*
 * Copyright 2012-2016 the original author or authors.
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

package org.springframework.restdocs.restassured.operation.preprocess;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationRequestPartFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.Parameters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link UriModifyingOperationPreprocessor}.
 *
 * @author Andy Wilkinson
 */
public class UriModifyingOperationPreprocessorTests {

	private final OperationRequestFactory requestFactory = new OperationRequestFactory();

	private final OperationResponseFactory responseFactory = new OperationResponseFactory();

	private final UriModifyingOperationPreprocessor preprocessor = new UriModifyingOperationPreprocessor();

	@Test
	public void requestUriSchemeCanBeModified() {
		this.preprocessor.scheme("https");
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithUri("http://localhost:12345"));
		assertThat(processed.getUri(),
				is(equalTo(URI.create("https://localhost:12345"))));
	}

	@Test
	public void requestUriHostCanBeModified() {
		this.preprocessor.host("api.example.com");
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithUri("http://api.example.com:12345"));
		assertThat(processed.getUri(),
				is(equalTo(URI.create("http://api.example.com:12345"))));
	}

	@Test
	public void requestUriPortCanBeModified() {
		this.preprocessor.port(23456);
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithUri("http://api.example.com:12345"));
		assertThat(processed.getUri(),
				is(equalTo(URI.create("http://api.example.com:23456"))));
	}

	@Test
	public void requestUriPortCanBeRemoved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithUri("http://api.example.com:12345"));
		assertThat(processed.getUri(), is(equalTo(URI.create("http://api.example.com"))));
	}

	@Test
	public void requestUriPathIsPreserved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithUri("http://api.example.com:12345/foo/bar"));
		assertThat(processed.getUri(),
				is(equalTo(URI.create("http://api.example.com/foo/bar"))));
	}

	@Test
	public void requestUriQueryIsPreserved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithUri("http://api.example.com:12345?foo=bar"));
		assertThat(processed.getUri(),
				is(equalTo(URI.create("http://api.example.com?foo=bar"))));
	}

	@Test
	public void requestUriAnchorIsPreserved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithUri("http://api.example.com:12345#foo"));
		assertThat(processed.getUri(),
				is(equalTo(URI.create("http://api.example.com#foo"))));
	}

	@Test
	public void requestContentUriSchemeCanBeModified() {
		this.preprocessor.scheme("https");
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithContent(
						"The uri 'http://localhost:12345' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'https://localhost:12345' should be used")));
	}

	@Test
	public void requestContentUriHostCanBeModified() {
		this.preprocessor.host("api.example.com");
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithContent(
						"The uri 'http://localhost:12345' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'http://api.example.com:12345' should be used")));
	}

	@Test
	public void requestContentUriPortCanBeModified() {
		this.preprocessor.port(23456);
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithContent(
						"The uri 'http://localhost:12345' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'http://localhost:23456' should be used")));
	}

	@Test
	public void requestContentUriPortCanBeRemoved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithContent(
						"The uri 'http://localhost:12345' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'http://localhost' should be used")));
	}

	@Test
	public void multipleRequestContentUrisCanBeModified() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithContent(
						"Use 'http://localhost:12345' or 'https://localhost:23456' to access the service"));
		assertThat(new String(processed.getContent()), is(equalTo(
				"Use 'http://localhost' or 'https://localhost' to access the service")));
	}

	@Test
	public void requestContentUriPathIsPreserved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithContent(
						"The uri 'http://localhost:12345/foo/bar' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'http://localhost/foo/bar' should be used")));
	}

	@Test
	public void requestContentUriQueryIsPreserved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithContent(
						"The uri 'http://localhost:12345?foo=bar' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'http://localhost?foo=bar' should be used")));
	}

	@Test
	public void requestContentUriAnchorIsPreserved() {
		this.preprocessor.removePort();
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithContent(
						"The uri 'http://localhost:12345#foo' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'http://localhost#foo' should be used")));
	}

	@Test
	public void responseContentUriSchemeCanBeModified() {
		this.preprocessor.scheme("https");
		OperationResponse processed = this.preprocessor
				.preprocess(createResponseWithContent(
						"The uri 'http://localhost:12345' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'https://localhost:12345' should be used")));
	}

	@Test
	public void responseContentUriHostCanBeModified() {
		this.preprocessor.host("api.example.com");
		OperationResponse processed = this.preprocessor
				.preprocess(createResponseWithContent(
						"The uri 'http://localhost:12345' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'http://api.example.com:12345' should be used")));
	}

	@Test
	public void responseContentUriPortCanBeModified() {
		this.preprocessor.port(23456);
		OperationResponse processed = this.preprocessor
				.preprocess(createResponseWithContent(
						"The uri 'http://localhost:12345' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'http://localhost:23456' should be used")));
	}

	@Test
	public void responseContentUriPortCanBeRemoved() {
		this.preprocessor.removePort();
		OperationResponse processed = this.preprocessor
				.preprocess(createResponseWithContent(
						"The uri 'http://localhost:12345' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'http://localhost' should be used")));
	}

	@Test
	public void multipleResponseContentUrisCanBeModified() {
		this.preprocessor.removePort();
		OperationResponse processed = this.preprocessor
				.preprocess(createResponseWithContent(
						"Use 'http://localhost:12345' or 'https://localhost:23456' to access the service"));
		assertThat(new String(processed.getContent()), is(equalTo(
				"Use 'http://localhost' or 'https://localhost' to access the service")));
	}

	@Test
	public void responseContentUriPathIsPreserved() {
		this.preprocessor.removePort();
		OperationResponse processed = this.preprocessor
				.preprocess(createResponseWithContent(
						"The uri 'http://localhost:12345/foo/bar' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'http://localhost/foo/bar' should be used")));
	}

	@Test
	public void responseContentUriQueryIsPreserved() {
		this.preprocessor.removePort();
		OperationResponse processed = this.preprocessor
				.preprocess(createResponseWithContent(
						"The uri 'http://localhost:12345?foo=bar' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'http://localhost?foo=bar' should be used")));
	}

	@Test
	public void responseContentUriAnchorIsPreserved() {
		this.preprocessor.removePort();
		OperationResponse processed = this.preprocessor
				.preprocess(createResponseWithContent(
						"The uri 'http://localhost:12345#foo' should be used"));
		assertThat(new String(processed.getContent()),
				is(equalTo("The uri 'http://localhost#foo' should be used")));
	}

	@Test
	public void urisInRequestHeadersCanBeModified() {
		OperationRequest processed = this.preprocessor.host("api.example.com")
				.preprocess(createRequestWithHeader("Foo", "http://locahost:12345"));
		assertThat(processed.getHeaders().getFirst("Foo"),
				is(equalTo("http://api.example.com:12345")));
		assertThat(processed.getHeaders().getFirst("Host"),
				is(equalTo("api.example.com")));
	}

	@Test
	public void urisInResponseHeadersCanBeModified() {
		OperationResponse processed = this.preprocessor.host("api.example.com")
				.preprocess(createResponseWithHeader("Foo", "http://locahost:12345"));
		assertThat(processed.getHeaders().getFirst("Foo"),
				is(equalTo("http://api.example.com:12345")));
	}

	@Test
	public void urisInRequestPartHeadersCanBeModified() {
		OperationRequest processed = this.preprocessor.host("api.example.com").preprocess(
				createRequestWithPartWithHeader("Foo", "http://locahost:12345"));
		assertThat(processed.getParts().iterator().next().getHeaders().getFirst("Foo"),
				is(equalTo("http://api.example.com:12345")));
	}

	@Test
	public void urisInRequestPartContentCanBeModified() {
		OperationRequest processed = this.preprocessor.host("api.example.com")
				.preprocess(createRequestWithPartWithContent(
						"The uri 'http://localhost:12345' should be used"));
		assertThat(new String(processed.getParts().iterator().next().getContent()),
				is(equalTo("The uri 'http://api.example.com:12345' should be used")));
	}

	@Test
	public void modifiedUriDoesNotGetDoubleEncoded() {
		this.preprocessor.scheme("https");
		OperationRequest processed = this.preprocessor
				.preprocess(createRequestWithUri("http://localhost:12345?foo=%7B%7D"));
		assertThat(processed.getUri(),
				is(equalTo(URI.create("https://localhost:12345?foo=%7B%7D"))));

	}

	private OperationRequest createRequestWithUri(String uri) {
		return this.requestFactory.create(URI.create(uri), HttpMethod.GET, new byte[0],
				new HttpHeaders(), new Parameters(),
				Collections.<OperationRequestPart>emptyList());
	}

	private OperationRequest createRequestWithContent(String content) {
		return this.requestFactory.create(URI.create("http://localhost"), HttpMethod.GET,
				content.getBytes(), new HttpHeaders(), new Parameters(),
				Collections.<OperationRequestPart>emptyList());
	}

	private OperationRequest createRequestWithHeader(String name, String value) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(name, value);
		return this.requestFactory.create(URI.create("http://localhost"), HttpMethod.GET,
				new byte[0], headers, new Parameters(),
				Collections.<OperationRequestPart>emptyList());
	}

	private OperationRequest createRequestWithPartWithHeader(String name, String value) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(name, value);
		return this.requestFactory.create(URI.create("http://localhost"), HttpMethod.GET,
				new byte[0], new HttpHeaders(), new Parameters(),
				Arrays.asList(new OperationRequestPartFactory().create("part", "fileName",
						new byte[0], headers)));
	}

	private OperationRequest createRequestWithPartWithContent(String content) {
		return this.requestFactory.create(URI.create("http://localhost"), HttpMethod.GET,
				new byte[0], new HttpHeaders(), new Parameters(),
				Arrays.asList(new OperationRequestPartFactory().create("part", "fileName",
						content.getBytes(), new HttpHeaders())));
	}

	private OperationResponse createResponseWithContent(String content) {
		return this.responseFactory.create(HttpStatus.OK, new HttpHeaders(),
				content.getBytes());
	}

	private OperationResponse createResponseWithHeader(String name, String value) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(name, value);
		return this.responseFactory.create(HttpStatus.OK, headers, new byte[0]);
	}

}
