/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.operation.preprocess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import org.springframework.restdocs.operation.OperationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DelegatingOperationResponsePreprocessor}.
 *
 * @author Andy Wilkinson
 */
class DelegatingOperationResponsePreprocessorTests {

	@Test
	void delegationOccurs() {
		OperationResponse originalResponse = mock(OperationResponse.class);
		OperationPreprocessor preprocessor1 = mock(OperationPreprocessor.class);
		OperationResponse preprocessedResponse1 = mock(OperationResponse.class);
		OperationPreprocessor preprocessor2 = mock(OperationPreprocessor.class);
		OperationResponse preprocessedResponse2 = mock(OperationResponse.class);
		OperationPreprocessor preprocessor3 = mock(OperationPreprocessor.class);
		OperationResponse preprocessedResponse3 = mock(OperationResponse.class);

		given(preprocessor1.preprocess(originalResponse)).willReturn(preprocessedResponse1);
		given(preprocessor2.preprocess(preprocessedResponse1)).willReturn(preprocessedResponse2);
		given(preprocessor3.preprocess(preprocessedResponse2)).willReturn(preprocessedResponse3);

		OperationResponse result = new DelegatingOperationResponsePreprocessor(
				Arrays.asList(preprocessor1, preprocessor2, preprocessor3))
			.preprocess(originalResponse);

		assertThat(result).isSameAs(preprocessedResponse3);
	}

}
