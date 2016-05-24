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

package org.springframework.restdocs.mockmvc;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.Matchers;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link RestDocumentationResultHandler}.
 *
 * @author Andy Wilkinson
 */
public class RestDocumentationResultHandlerTests {

	@Test
	public void additionalSnippetsAreOnlyCalledOnce() throws Exception {
		Snippet snippet = mock(Snippet.class);
		RestDocumentationResultHandler resultHandler = new RestDocumentationResultHandler(
				"test", snippet);
		Snippet defaultSnippet = mock(Snippet.class);
		Snippet additionalSnippet = mock(Snippet.class);
		resultHandler.snippets(additionalSnippet);
		MvcResult result = mock(MvcResult.class);
		MockHttpServletRequest request = new MockHttpServletRequest("GET",
				"http://localhost");
		request.setAttribute("org.springframework.restdocs.mockmvc.defaultSnippets",
				Arrays.asList(defaultSnippet));
		given(result.getRequest()).willReturn(request);
		given(result.getResponse()).willReturn(new MockHttpServletResponse());
		resultHandler.handle(result);
		resultHandler.handle(result);
		verify(defaultSnippet, times(2)).document(Matchers.any(Operation.class));
		verify(snippet, times(2)).document(Matchers.any(Operation.class));
		verify(additionalSnippet, times(1)).document(Matchers.any(Operation.class));
	}

}
