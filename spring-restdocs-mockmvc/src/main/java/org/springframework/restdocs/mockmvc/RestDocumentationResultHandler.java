/*
 * Copyright 2014-2017 the original author or authors.
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

import java.util.HashMap;
import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
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
	 * Creates a new {@link RestDocumentationResultHandler} to be passed into
	 * {@link ResultActions#andDo(ResultHandler)} that will produce documentation using
	 * the given {@code snippets}. For example:
	 *
	 * <pre>
	 * this.mockMvc.perform(MockMvcRequestBuilders.get("/search"))
	 *     .andExpect(status().isOk())
	 *     .andDo(this.documentationHandler.document(responseFields(
	 *          fieldWithPath("page").description("The requested Page")
	 *     ));
	 * </pre>
	 *
	 * @param snippets the snippets
	 * @return the new result handler
	 */
	public RestDocumentationResultHandler document(Snippet... snippets) {
		return new RestDocumentationResultHandler(this.delegate.withSnippets(snippets)) {

			@Override
			public void handle(MvcResult result) throws Exception {
				@SuppressWarnings("unchecked")
				Map<String, Object> configuration = new HashMap<>(
						(Map<String, Object>) result.getRequest()
								.getAttribute(ATTRIBUTE_NAME_CONFIGURATION));
				configuration.remove(
						RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_SNIPPETS);
				getDelegate().handle(result.getRequest(), result.getResponse(),
						configuration);
			}

		};
	}

	/**
	 * Returns the {@link RestDocumentationGenerator} that is used as a delegate.
	 *
	 * @return the delegate
	 */
	protected final RestDocumentationGenerator<MockHttpServletRequest, MockHttpServletResponse> getDelegate() {
		return this.delegate;
	}

}
