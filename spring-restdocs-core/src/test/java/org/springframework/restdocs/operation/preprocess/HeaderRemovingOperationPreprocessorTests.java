/*
 * Copyright 2014-2015 the original author or authors.
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.operation.StandardOperationRequest;
import org.springframework.restdocs.operation.StandardOperationResponse;

/**
 * Tests for {@link HeaderRemovingOperationPreprocessorTests}
 *
 * @author Andy Wilkinson
 *
 */
public class HeaderRemovingOperationPreprocessorTests {

	private final HeaderRemovingOperationPreprocessor preprocessor = new HeaderRemovingOperationPreprocessor(
			"b");

	@Test
	public void modifyRequestHeaders() {
		StandardOperationRequest request = new StandardOperationRequest(
				URI.create("http://localhost"), HttpMethod.GET, new byte[0],
				getHttpHeaders(), new Parameters(),
				Collections.<OperationRequestPart> emptyList());
		OperationRequest preprocessed = this.preprocessor.preprocess(request);
		assertThat(preprocessed.getHeaders().size(), is(equalTo(1)));
		assertThat(preprocessed.getHeaders(), hasEntry("a", Arrays.asList("alpha")));
	}

	@Test
	public void modifyResponseHeaders() {
		StandardOperationResponse response = new StandardOperationResponse(HttpStatus.OK,
				getHttpHeaders(), new byte[0]);
		OperationResponse preprocessed = this.preprocessor.preprocess(response);
		assertThat(preprocessed.getHeaders().size(), is(equalTo(1)));
		assertThat(preprocessed.getHeaders(), hasEntry("a", Arrays.asList("alpha")));
	}

	private HttpHeaders getHttpHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("a", "alpha");
		httpHeaders.add("b", "bravo");
		httpHeaders.add("b", "banana");
		return httpHeaders;
	}

}
