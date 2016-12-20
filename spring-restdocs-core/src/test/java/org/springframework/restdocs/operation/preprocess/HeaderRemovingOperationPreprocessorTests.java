/*
 * Copyright 2014-2016 the original author or authors.
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

package org.springframework.restdocs.operation.preprocess;

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
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.Parameters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link HeaderRemovingOperationPreprocessorTests}.
 *
 * @author Andy Wilkinson
 * @author Roland Huss
 */
public class HeaderRemovingOperationPreprocessorTests {

	private final OperationRequestFactory requestFactory = new OperationRequestFactory();

	private final OperationResponseFactory responseFactory = new OperationResponseFactory();

	private final HeaderRemovingOperationPreprocessor preprocessor = new HeaderRemovingOperationPreprocessor(
			new ExactMatchHeaderFilter("b"));

	@Test
	public void modifyRequestHeaders() {
		OperationRequest request = this.requestFactory.create(
				URI.create("http://localhost"), HttpMethod.GET, new byte[0],
				getHttpHeaders(), new Parameters(),
				Collections.<OperationRequestPart>emptyList());
		OperationRequest preprocessed = this.preprocessor.preprocess(request);
		assertThat(preprocessed.getHeaders().size(), is(equalTo(2)));
		assertThat(preprocessed.getHeaders(), hasEntry("a", Arrays.asList("alpha")));
		assertThat(preprocessed.getHeaders(),
				hasEntry("Host", Arrays.asList("localhost")));
	}

	@Test
	public void modifyResponseHeaders() {
		OperationResponse response = createResponse();
		OperationResponse preprocessed = this.preprocessor.preprocess(response);
		assertThat(preprocessed.getHeaders().size(), is(equalTo(1)));
		assertThat(preprocessed.getHeaders(), hasEntry("a", Arrays.asList("alpha")));
	}

	@Test
	public void modifyWithPattern() {
		OperationResponse response = createResponse("content-length", "1234");
		HeaderRemovingOperationPreprocessor processor = new HeaderRemovingOperationPreprocessor(
				new PatternMatchHeaderFilter("co.*le(.)gth]"));
		OperationResponse preprocessed = processor.preprocess(response);
		assertThat(preprocessed.getHeaders().size(), is(equalTo(2)));
		assertThat(preprocessed.getHeaders(), hasEntry("a", Arrays.asList("alpha")));
		assertThat(preprocessed.getHeaders(),
				hasEntry("b", Arrays.asList("bravo", "banana")));
	}

	@Test
	public void removeAllHeaders() {
		HeaderRemovingOperationPreprocessor processor = new HeaderRemovingOperationPreprocessor(
				new PatternMatchHeaderFilter(".*"));
		OperationResponse preprocessed = processor.preprocess(createResponse());
		assertThat(preprocessed.getHeaders().size(), is(equalTo(0)));
	}

	private OperationResponse createResponse(String... extraHeaders) {
		return this.responseFactory.create(HttpStatus.OK, getHttpHeaders(extraHeaders),
				new byte[0]);
	}

	private HttpHeaders getHttpHeaders(String... extraHeaders) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("a", "alpha");
		httpHeaders.add("b", "bravo");
		httpHeaders.add("b", "banana");
		for (int i = 0; i < extraHeaders.length; i += 2) {
			httpHeaders.add(extraHeaders[i], extraHeaders[i + 1]);
		}
		return httpHeaders;
	}

}
