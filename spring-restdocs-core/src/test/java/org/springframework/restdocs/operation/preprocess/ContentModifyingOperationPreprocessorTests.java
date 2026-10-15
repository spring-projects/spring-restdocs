/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import java.net.URI;
import java.util.Collections;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.OperationResponseFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ContentModifyingOperationPreprocessor}.
 *
 * @author Andy Wilkinson
 *
 */
class ContentModifyingOperationPreprocessorTests {

	private final OperationRequestFactory requestFactory = new OperationRequestFactory();

	private final OperationResponseFactory responseFactory = new OperationResponseFactory();

	private final ContentModifyingOperationPreprocessor preprocessor = new ContentModifyingOperationPreprocessor(
			new ContentModifier() {

				@Override
				public byte[] modifyContent(byte[] originalContent, @Nullable MediaType mediaType) {
					return "modified".getBytes();
				}

			});

	@Test
	void modifyRequestContent() {
		OperationRequest request = this.requestFactory.create(URI.create("http://localhost"), HttpMethod.GET,
				"content".getBytes(), new HttpHeaders(), Collections.<OperationRequestPart>emptyList());
		OperationRequest preprocessed = this.preprocessor.preprocess(request);
		assertThat(preprocessed.getContent()).isEqualTo("modified".getBytes());
	}

	@Test
	void modifyResponseContent() {
		OperationResponse response = this.responseFactory.create(HttpStatus.OK, new HttpHeaders(),
				"content".getBytes());
		OperationResponse preprocessed = this.preprocessor.preprocess(response);
		assertThat(preprocessed.getContent()).isEqualTo("modified".getBytes());
	}

	@Test
	void contentLengthIsUpdated() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentLength(7);
		OperationRequest request = this.requestFactory.create(URI.create("http://localhost"), HttpMethod.GET,
				"content".getBytes(), httpHeaders, Collections.<OperationRequestPart>emptyList());
		OperationRequest preprocessed = this.preprocessor.preprocess(request);
		assertThat(preprocessed.getHeaders().getContentLength()).isEqualTo(8L);
	}

}
