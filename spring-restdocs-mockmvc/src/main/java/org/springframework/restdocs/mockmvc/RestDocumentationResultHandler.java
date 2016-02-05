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

import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.util.Assert;

/**
 * A Spring MVC Test {@code ResultHandler} for documenting RESTful APIs.
 *
 * @author Andy Wilkinson
 * @author Andreas Evers
 * @see MockMvcRestDocumentation#document(String, Snippet...)
 */
public class RestDocumentationResultHandler implements ResultHandler {

	static final String ATTRIBUTE_NAME_CONFIGURATION = "org.springframework.restdocs.configuration";

	private final RestDocumentationGenerator<MockHttpServletRequest, MockHttpServletResponse> delegate;

	RestDocumentationResultHandler(
			RestDocumentationGenerator<MockHttpServletRequest, MockHttpServletResponse> delegate) {
		Assert.notNull(delegate, "delegate must be non-null");
		this.delegate = delegate;
	}

	@Override
	public void handle(MvcResult result) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> configuration = (Map<String, Object>) result.getRequest()
				.getAttribute(ATTRIBUTE_NAME_CONFIGURATION);
		this.delegate.handle(result.getRequest(), result.getResponse(), configuration);
	}

	/**
	 * Adds the given {@code snippets} such that they are documented when this result
	 * handler is called.
	 *
	 * @param snippets the snippets to add
	 * @return this {@code RestDocumentationResultHandler}
	 */
	public RestDocumentationResultHandler snippets(Snippet... snippets) {
		this.delegate.addSnippets(snippets);
		return this;
	}

}
