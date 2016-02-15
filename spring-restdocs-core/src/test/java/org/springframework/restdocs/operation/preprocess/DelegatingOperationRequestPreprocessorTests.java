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

import org.springframework.restdocs.operation.OperationRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DelegatingOperationRequestPreprocessor}.
 *
 * @author Andy Wilkinson
 */
public class DelegatingOperationRequestPreprocessorTests {

	@Test
	public void delegationOccurs() {
		OperationRequest originalRequest = mock(OperationRequest.class);
		OperationPreprocessor preprocessor1 = mock(OperationPreprocessor.class);
		OperationRequest preprocessedRequest1 = mock(OperationRequest.class);
		OperationPreprocessor preprocessor2 = mock(OperationPreprocessor.class);
		OperationRequest preprocessedRequest2 = mock(OperationRequest.class);
		OperationPreprocessor preprocessor3 = mock(OperationPreprocessor.class);
		OperationRequest preprocessedRequest3 = mock(OperationRequest.class);

		given(preprocessor1.preprocess(originalRequest)).willReturn(preprocessedRequest1);
		given(preprocessor2.preprocess(preprocessedRequest1))
				.willReturn(preprocessedRequest2);
		given(preprocessor3.preprocess(preprocessedRequest2))
				.willReturn(preprocessedRequest3);

		OperationRequest result = new DelegatingOperationRequestPreprocessor(
				Arrays.asList(preprocessor1, preprocessor2, preprocessor3))
						.preprocess(originalRequest);

		assertThat(result, is(preprocessedRequest3));
	}

}
