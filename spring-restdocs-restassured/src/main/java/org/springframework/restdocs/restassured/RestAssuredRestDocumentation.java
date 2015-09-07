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

package org.springframework.restdocs.restassured;

import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.snippet.Snippet;

import com.jayway.restassured.filter.Filter;

/**
 * Static factory methods for documenting RESTful APIs using REST Assured
 * 
 * @author Andy Wilkinson
 */
public abstract class RestAssuredRestDocumentation {

	private RestAssuredRestDocumentation() {

	}

	/**
	 * Documents the API call with the given {@code identifier} using the given
	 * {@code snippets}.
	 * 
	 * @param identifier an identifier for the API call that is being documented
	 * @param snippets the snippets that will document the API call
	 * @return a REST Assured {@link Filter} that will produce the documentation
	 */
	// TODO RestDocumentation should not be passed to this method
	public static RestDocumentationFilter document(RestDocumentation restDocumentation,
			String identifier, Snippet... snippets) {
		return new RestDocumentationFilter(restDocumentation, identifier, snippets);
	}

}
