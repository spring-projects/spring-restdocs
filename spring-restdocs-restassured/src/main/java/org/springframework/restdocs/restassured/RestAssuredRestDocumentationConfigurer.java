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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.config.RestDocumentationConfigurer;

/**
 * A REST Assured-specific {@link RestDocumentationConfigurer}.
 *
 * @author Andy Wilkinson
 */
public final class RestAssuredRestDocumentationConfigurer extends
		RestDocumentationConfigurer<RestAssuredSnippetConfigurer, RestAssuredRestDocumentationConfigurer>
		implements Filter {

	private final RestAssuredSnippetConfigurer snippetConfigurer = new RestAssuredSnippetConfigurer(
			this);

	private final RestDocumentationContextProvider contextProvider;

	RestAssuredRestDocumentationConfigurer(
			RestDocumentationContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	@Override
	public RestAssuredSnippetConfigurer snippets() {
		return this.snippetConfigurer;
	}

	@Override
	public Response filter(FilterableRequestSpecification requestSpec,
			FilterableResponseSpecification responseSpec, FilterContext filterContext) {
		RestDocumentationContext context = this.contextProvider.beforeOperation();
		filterContext.setValue(RestDocumentationContext.class.getName(), context);
		Map<String, Object> configuration = new HashMap<>();
		filterContext.setValue(RestDocumentationFilter.CONTEXT_KEY_CONFIGURATION,
				configuration);
		apply(configuration, context);
		return filterContext.next(requestSpec, responseSpec);
	}
}
