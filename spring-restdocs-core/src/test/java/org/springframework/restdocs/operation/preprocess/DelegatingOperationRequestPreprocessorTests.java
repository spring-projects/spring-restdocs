/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import org.springframework.restdocs.operation.OperationRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DelegatingOperationRequestPreprocessor}.
 *
 * @author Andy Wilkinson
 */
class DelegatingOperationRequestPreprocessorTests {

	@Test
	void delegationOccurs() {
		OperationRequest originalRequest = mock(OperationRequest.class);
		OperationPreprocessor preprocessor1 = mock(OperationPreprocessor.class);
		OperationRequest preprocessedRequest1 = mock(OperationRequest.class);
		OperationPreprocessor preprocessor2 = mock(OperationPreprocessor.class);
		OperationRequest preprocessedRequest2 = mock(OperationRequest.class);
		OperationPreprocessor preprocessor3 = mock(OperationPreprocessor.class);
		OperationRequest preprocessedRequest3 = mock(OperationRequest.class);

		given(preprocessor1.preprocess(originalRequest)).willReturn(preprocessedRequest1);
		given(preprocessor2.preprocess(preprocessedRequest1)).willReturn(preprocessedRequest2);
		given(preprocessor3.preprocess(preprocessedRequest2)).willReturn(preprocessedRequest3);

		OperationRequest result = new DelegatingOperationRequestPreprocessor(
				Arrays.asList(preprocessor1, preprocessor2, preprocessor3))
			.preprocess(originalRequest);

		assertThat(result).isSameAs(preprocessedRequest3);
	}

}
