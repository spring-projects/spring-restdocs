/*
 * Copyright 2014-2022 the original author or authors.
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

package com.example.restassured;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class HttpCookies {

	private RequestSpecification spec;

	public void cookies() {
		// tag::cookies[]
		RestAssured.given(this.spec).filter(document("cookies", requestCookies(// <1>
				cookieWithName("JSESSIONID").description("Saved session token")), // <2>
				responseCookies(// <3>
						cookieWithName("logged_in").description("If user is logged in"),
						cookieWithName("JSESSIONID").description("Updated session token"))))
				.cookie("JSESSIONID", "ACBCDFD0FF93D5BB") // <4>
				.when().get("/people").then().assertThat().statusCode(is(200));
		// end::cookies[]
	}

}
