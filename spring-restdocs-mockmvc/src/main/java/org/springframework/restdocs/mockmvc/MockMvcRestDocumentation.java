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

import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;

/**
 * Static factory methods for documenting RESTful APIs using Spring MVC Test.
 *
 * @author Andy Wilkinson
 */
public abstract class MockMvcRestDocumentation {

	private static final MockMvcRequestConverter REQUEST_CONVERTER = new MockMvcRequestConverter();

	private static final MockMvcResponseConverter RESPONSE_CONVERTER = new MockMvcResponseConverter();

	private MockMvcRestDocumentation() {

	}

	/**
	 * Provides access to a {@link MockMvcConfigurer} that can be used to configure a
	 * {@link MockMvc} instance using the given {@code restDocumentation}.
	 *
	 * @param restDocumentation the REST documentation
	 * @return the configurer
	 * @see ConfigurableMockMvcBuilder#apply(MockMvcConfigurer)
	 * @deprecated Since 1.1 in favor of
	 * {@link #documentationConfiguration(RestDocumentationContextProvider)}
	 */
	@Deprecated
	public static MockMvcRestDocumentationConfigurer documentationConfiguration(
			org.springframework.restdocs.RestDocumentation restDocumentation) {
		return documentationConfiguration(
				(RestDocumentationContextProvider) restDocumentation);
	}

	/**
	 * Provides access to a {@link MockMvcConfigurer} that can be used to configure a
	 * {@link MockMvc} instance using the given {@code contextProvider}.
	 *
	 * @param contextProvider the context provider
	 * @return the configurer
	 * @see ConfigurableMockMvcBuilder#apply(MockMvcConfigurer)
	 */
	public static MockMvcRestDocumentationConfigurer documentationConfiguration(
			RestDocumentationContextProvider contextProvider) {
		return new MockMvcRestDocumentationConfigurer(contextProvider);
	}

	/**
	 * Documents the API call with the given {@code identifier} using the given
	 * {@code snippets} in addition to any default snippets.
	 *
	 * @param identifier an identifier for the API call that is being documented
	 * @param snippets the snippets
	 * @return a Mock MVC {@code ResultHandler} that will produce the documentation
	 * @see MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
	 * @see ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
	 */
	public static RestDocumentationResultHandler document(String identifier,
			Snippet... snippets) {
		return new RestDocumentationResultHandler(new RestDocumentationGenerator<>(
				identifier, REQUEST_CONVERTER, RESPONSE_CONVERTER, snippets));
	}

	/**
	 * Documents the API call with the given {@code identifier} using the given
	 * {@code snippets} in addition to any default snippets. The given
	 * {@code requestPreprocessor} is applied to the request before it is documented.
	 *
	 * @param identifier an identifier for the API call that is being documented
	 * @param requestPreprocessor the request preprocessor
	 * @param snippets the snippets
	 * @return a Mock MVC {@code ResultHandler} that will produce the documentation
	 * @see MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
	 * @see ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
	 */
	public static RestDocumentationResultHandler document(String identifier,
			OperationRequestPreprocessor requestPreprocessor, Snippet... snippets) {
		return new RestDocumentationResultHandler(
				new RestDocumentationGenerator<>(identifier, REQUEST_CONVERTER,
						RESPONSE_CONVERTER, requestPreprocessor, snippets));
	}

	/**
	 * Documents the API call with the given {@code identifier} using the given
	 * {@code snippets} in addition to any default snippets. The given
	 * {@code responsePreprocessor} is applied to the request before it is documented.
	 *
	 * @param identifier an identifier for the API call that is being documented
	 * @param responsePreprocessor the response preprocessor
	 * @param snippets the snippets
	 * @return a Mock MVC {@code ResultHandler} that will produce the documentation
	 * @see MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
	 * @see ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
	 */
	public static RestDocumentationResultHandler document(String identifier,
			OperationResponsePreprocessor responsePreprocessor, Snippet... snippets) {
		return new RestDocumentationResultHandler(
				new RestDocumentationGenerator<>(identifier, REQUEST_CONVERTER,
						RESPONSE_CONVERTER, responsePreprocessor, snippets));
	}

	/**
	 * Documents the API call with the given {@code identifier} using the given
	 * {@code snippets} in addition to any default snippets. The given
	 * {@code requestPreprocessor} and {@code responsePreprocessor} are applied to the
	 * request and response respectively before they are documented.
	 *
	 * @param identifier an identifier for the API call that is being documented
	 * @param requestPreprocessor the request preprocessor
	 * @param responsePreprocessor the response preprocessor
	 * @param snippets the snippets
	 * @return a Mock MVC {@code ResultHandler} that will produce the documentation
	 * @see MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
	 * @see ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
	 */
	public static RestDocumentationResultHandler document(String identifier,
			OperationRequestPreprocessor requestPreprocessor,
			OperationResponsePreprocessor responsePreprocessor, Snippet... snippets) {
		return new RestDocumentationResultHandler(new RestDocumentationGenerator<>(
				identifier, REQUEST_CONVERTER, RESPONSE_CONVERTER, requestPreprocessor,
				responsePreprocessor, snippets));
	}

}
