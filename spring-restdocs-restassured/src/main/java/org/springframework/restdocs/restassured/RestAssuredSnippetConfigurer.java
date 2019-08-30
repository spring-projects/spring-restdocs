/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.restassured;

import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.filter.FilterContext;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.FilterableResponseSpecification;

import org.springframework.restdocs.config.SnippetConfigurer;

/**
 * A configurer that can be used to configure the generated documentation snippets when
 * using REST Assured.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 * @deprecated since 1.2.0 in favor of
 * {@link org.springframework.restdocs.restassured3.RestAssuredSnippetConfigurer}
 */
@Deprecated
public final class RestAssuredSnippetConfigurer extends
		SnippetConfigurer<RestAssuredRestDocumentationConfigurer, RestAssuredSnippetConfigurer> implements Filter {

	RestAssuredSnippetConfigurer(RestAssuredRestDocumentationConfigurer parent) {
		super(parent);
	}

	@Override
	public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec,
			FilterContext context) {
		return and().filter(requestSpec, responseSpec, context);
	}

}
