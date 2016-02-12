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

import java.util.Arrays;

import org.junit.Test;

import org.springframework.restdocs.operation.OperationResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DelegatingOperationResponsePreprocessor}.
 *
 * @author Andy Wilkinson
 */
public class DelegatingOperationResponsePreprocessorTests {

	@Test
	public void delegationOccurs() {
		OperationResponse originalResponse = mock(OperationResponse.class);
		OperationPreprocessor preprocessor1 = mock(OperationPreprocessor.class);
		OperationResponse preprocessedResponse1 = mock(OperationResponse.class);
		OperationPreprocessor preprocessor2 = mock(OperationPreprocessor.class);
		OperationResponse preprocessedResponse2 = mock(OperationResponse.class);
		OperationPreprocessor preprocessor3 = mock(OperationPreprocessor.class);
		OperationResponse preprocessedResponse3 = mock(OperationResponse.class);

		given(preprocessor1.preprocess(originalResponse))
				.willReturn(preprocessedResponse1);
		given(preprocessor2.preprocess(preprocessedResponse1))
				.willReturn(preprocessedResponse2);
		given(preprocessor3.preprocess(preprocessedResponse2))
				.willReturn(preprocessedResponse3);

		OperationResponse result = new DelegatingOperationResponsePreprocessor(
				Arrays.asList(preprocessor1, preprocessor2, preprocessor3))
						.preprocess(originalResponse);

		assertThat(result, is(preprocessedResponse3));
	}
}
