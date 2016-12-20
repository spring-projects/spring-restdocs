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
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class InvokeService {

	private RequestSpecification spec;

	public void invokeService() throws Exception {
		// tag::invoke-service[]
		RestAssured.given(this.spec) // <1>
				.accept("application/json") // <2>
				.filter(document("index")) // <3>
				.when().get("/") // <4>
				.then().assertThat().statusCode(is(200)); // <5>
		// end::invoke-service[]
	}
}
