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

package org.springframework.restdocs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.test.StubMvcResult.result;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.ResponseModifier.ResponseModifyingRestDocumentationResultHandler;
import org.springframework.restdocs.response.ResponsePostProcessor;

/**
 * Tests for {@link ResponseModifier}
 * 
 * @author Andy Wilkinson
 *
 */
public class ResponseModifierTests {

	@Test
	public void postProcessorsAreApplied() throws Exception {
		ResponsePostProcessor first = mock(ResponsePostProcessor.class);
		ResponsePostProcessor second = mock(ResponsePostProcessor.class);

		MockHttpServletResponse original = new MockHttpServletResponse();
		MockHttpServletResponse afterFirst = new MockHttpServletResponse();
		MockHttpServletResponse afterSecond = new MockHttpServletResponse();

		given(first.postProcess(original)).willReturn(afterFirst);
		given(second.postProcess(afterFirst)).willReturn(afterSecond);

		RestDocumentationResultHandler resultHandler = new ResponseModifier(first, second)
				.andDocument("test");
		assertThat(
				afterSecond,
				is(equalTo(((ResponseModifyingRestDocumentationResultHandler) resultHandler)
						.postProcessResponse(result(original)).getResponse())));
	}

}
