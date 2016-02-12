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

import java.net.URI;
import java.util.Collections;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;
import org.springframework.restdocs.operation.Parameters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ContentModifyingOperationPreprocessor}.
 *
 * @author Andy Wilkinson
 *
 */
public class ContentModifyingOperationPreprocessorTests {

	private final OperationRequestFactory requestFactory = new OperationRequestFactory();

	private final OperationResponseFactory responseFactory = new OperationResponseFactory();

	private final ContentModifyingOperationPreprocessor preprocessor = new ContentModifyingOperationPreprocessor(
			new ContentModifier() {

				@Override
				public byte[] modifyContent(byte[] originalContent, MediaType mediaType) {
					return "modified".getBytes();
				}

			});

	@Test
	public void modifyRequestContent() {
		OperationRequest request = this.requestFactory.create(
				URI.create("http://localhost"), HttpMethod.GET, "content".getBytes(),
				new HttpHeaders(), new Parameters(),
				Collections.<OperationRequestPart>emptyList());
		OperationRequest preprocessed = this.preprocessor.preprocess(request);
		assertThat(preprocessed.getContent(), is(equalTo("modified".getBytes())));
	}

	@Test
	public void modifyResponseContent() {
		OperationResponse response = this.responseFactory.create(HttpStatus.OK,
				new HttpHeaders(), "content".getBytes());
		OperationResponse preprocessed = this.preprocessor.preprocess(response);
		assertThat(preprocessed.getContent(), is(equalTo("modified".getBytes())));
	}

	@Test
	public void contentLengthIsUpdated() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentLength(7);
		OperationRequest request = this.requestFactory.create(
				URI.create("http://localhost"), HttpMethod.GET, "content".getBytes(),
				httpHeaders, new Parameters(),
				Collections.<OperationRequestPart>emptyList());
		OperationRequest preprocessed = this.preprocessor.preprocess(request);
		assertThat(preprocessed.getHeaders().getContentLength(), is(equalTo(8L)));
	}

}
