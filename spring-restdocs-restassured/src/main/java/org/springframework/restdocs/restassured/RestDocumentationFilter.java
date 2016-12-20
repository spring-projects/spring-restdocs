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

package org.springframework.restdocs.restassured;

import java.util.HashMap;
import java.util.Map;

import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.filter.FilterContext;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.FilterableResponseSpecification;

import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.util.Assert;

/**
 * A REST Assured {@link Filter} for documenting RESTful APIs.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public class RestDocumentationFilter implements Filter {

	static final String CONTEXT_KEY_CONFIGURATION = "org.springframework.restdocs.configuration";

	private final RestDocumentationGenerator<FilterableRequestSpecification, Response> delegate;

	RestDocumentationFilter(
			RestDocumentationGenerator<FilterableRequestSpecification, Response> delegate) {
		Assert.notNull(delegate, "delegate must be non-null");
		this.delegate = delegate;
	}

	@Override
	public final Response filter(FilterableRequestSpecification requestSpec,
			FilterableResponseSpecification responseSpec, FilterContext context) {
		Response response = context.next(requestSpec, responseSpec);

		Map<String, Object> configuration = getConfiguration(requestSpec, context);

		this.delegate.handle(requestSpec, response, configuration);

		return response;
	}

	/**
	 * Returns the configuration that should be used when calling the delgate. The
	 * configuration is derived from the given {@code requestSpec} and {@code context}.
	 *
	 * @param requestSpec the request specification
	 * @param context the filter context
	 * @return the configuration
	 */
	protected Map<String, Object> getConfiguration(
			FilterableRequestSpecification requestSpec, FilterContext context) {
		Map<String, Object> configuration = new HashMap<>(
				context.<Map<String, Object>>getValue(CONTEXT_KEY_CONFIGURATION));
		configuration.put(RestDocumentationContext.class.getName(),
				context.<RestDocumentationContext>getValue(
						RestDocumentationContext.class.getName()));
		configuration.put(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
				requestSpec.getUserDefinedPath());
		return configuration;
	}

	/**
	 * Adds the given {@code snippets} such that they are documented when this result
	 * handler is called.
	 *
	 * @param snippets the snippets to add
	 * @return this {@code RestDocumentationFilter}
	 * @deprecated since 1.1 in favor of {@link #document(Snippet...)}
	 */
	@Deprecated
	public final RestDocumentationFilter snippets(Snippet... snippets) {
		this.delegate.addSnippets(snippets);
		return this;
	}

	/**
	 * Creates a new {@link RestDocumentationFilter} that will produce documentation using
	 * the given {@code snippets}.
	 *
	 * @param snippets the snippets
	 * @return the new result handler
	 */
	public final RestDocumentationFilter document(Snippet... snippets) {
		return new RestDocumentationFilter(this.delegate.withSnippets(snippets)) {

			@Override
			protected Map<String, Object> getConfiguration(
					FilterableRequestSpecification requestSpec, FilterContext context) {
				Map<String, Object> configuration = super.getConfiguration(requestSpec,
						context);
				configuration.remove(
						RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_SNIPPETS);
				return configuration;
			}

		};
	}

}
