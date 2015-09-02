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

import org.springframework.restdocs.config.RestDocumentationConfigurer;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;

/**
 * Static factory methods for documenting RESTful APIs using Spring MVC Test
 * 
 * @author Andy Wilkinson
 */
public abstract class RestDocumentation {

	private RestDocumentation() {

	}

	/**
	 * Provides access to a {@link MockMvcConfigurer} that can be used to configure the
	 * REST documentation when building a {@link MockMvc} instance.
	 * 
	 * @see ConfigurableMockMvcBuilder#apply(MockMvcConfigurer)
	 * @return the configurer
	 */
	public static RestDocumentationConfigurer documentationConfiguration() {
		return new RestDocumentationConfigurer();
	}

	/**
	 * Documents the API call with the given {@code identifier} using the given
	 * {@code snippets}.
	 * 
	 * @param identifier an identifier for the API call that is being documented
	 * @param snippets the snippets that will document the API call
	 * @return a Mock MVC {@code ResultHandler} that will produce the documentation
	 * @see MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
	 * @see ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
	 */
	public static RestDocumentationResultHandler document(String identifier,
			Snippet... snippets) {
		return new RestDocumentationResultHandler(identifier, snippets);
	}

	/**
	 * Documents the API call with the given {@code identifier} using the given
	 * {@code snippets}. The given {@code requestPreprocessor} is applied to the request
	 * before it is documented.
	 * 
	 * @param identifier an identifier for the API call that is being documented
	 * @param requestPreprocessor the request preprocessor
	 * @param snippets the snippets that will document the API call
	 * @return a Mock MVC {@code ResultHandler} that will produce the documentation
	 * @see MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
	 * @see ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
	 */
	public static RestDocumentationResultHandler document(String identifier,
			OperationRequestPreprocessor requestPreprocessor, Snippet... snippets) {
		return new RestDocumentationResultHandler(identifier, requestPreprocessor,
				snippets);
	}

	/**
	 * Documents the API call with the given {@code identifier} using the given
	 * {@code snippets}. The given {@code responsePreprocessor} is applied to the request
	 * before it is documented.
	 * 
	 * @param identifier an identifier for the API call that is being documented
	 * @param responsePreprocessor the response preprocessor
	 * @param snippets the snippets that will document the API call
	 * @return a Mock MVC {@code ResultHandler} that will produce the documentation
	 * @see MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
	 * @see ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
	 */
	public static RestDocumentationResultHandler document(String identifier,
			OperationResponsePreprocessor responsePreprocessor, Snippet... snippets) {
		return new RestDocumentationResultHandler(identifier, responsePreprocessor,
				snippets);
	}

	/**
	 * Documents the API call with the given {@code identifier} using the given
	 * {@code snippets}. The given {@code requestPreprocessor} and
	 * {@code responsePreprocessor} are applied to the request and response respectively
	 * before they are documented.
	 * 
	 * @param identifier an identifier for the API call that is being documented
	 * @param requestPreprocessor the request preprocessor
	 * @param responsePreprocessor the response preprocessor
	 * @param snippets the snippets that will document the API call
	 * @return a Mock MVC {@code ResultHandler} that will produce the documentation
	 * @see MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
	 * @see ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
	 */
	public static RestDocumentationResultHandler document(String identifier,
			OperationRequestPreprocessor requestPreprocessor,
			OperationResponsePreprocessor responsePreprocessor, Snippet... snippets) {
		return new RestDocumentationResultHandler(identifier, requestPreprocessor,
				responsePreprocessor, snippets);
	}

}
