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

package com.example.restassured;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class RequestParameters {

	private RequestSpecification spec;

	public void getQueryStringSnippet() throws Exception {
		// tag::request-parameters-query-string[]
		RestAssured.given(this.spec)
			.filter(document("users", requestParameters( // <1>
					parameterWithName("page").description("The page to retrieve"), // <2>
					parameterWithName("per_page").description("Entries per page")))) // <3>
			.when().get("/users?page=2&per_page=100") // <4>
			.then().assertThat().statusCode(is(200));
		// end::request-parameters-query-string[]
	}

	public void postFormDataSnippet() throws Exception {
		// tag::request-parameters-form-data[]
		RestAssured.given(this.spec)
			.filter(document("create-user", requestParameters(
					parameterWithName("username").description("The user's username"))))
			.formParam("username", "Tester") // <1>
			.when().post("/users") // <2>
			.then().assertThat().statusCode(is(200));
		// end::request-parameters-form-data[]
	}

}
