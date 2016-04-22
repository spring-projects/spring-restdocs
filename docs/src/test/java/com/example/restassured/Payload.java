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

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

public class Payload {

	private RequestSpecification spec;

	public void response() throws Exception {
		// tag::response[]
		RestAssured.given(this.spec).accept("application/json")
			.filter(document("user", responseFields( // <1>
					fieldWithPath("contact").description("The user's contact details"), // <2>
					fieldWithPath("contact.email").description("The user's email address")))) // <3>
			.when().get("/user/5")
			.then().assertThat().statusCode(is(200));
		// end::response[]
	}

	public void explicitType() throws Exception {
		RestAssured.given(this.spec).accept("application/json")
			// tag::explicit-type[]
			.filter(document("user", responseFields(
					fieldWithPath("contact.email")
							.type(JsonFieldType.STRING) // <1>
							.description("The user's email address"))))
			// end::explicit-type[]
			.when().get("/user/5")
			.then().assertThat().statusCode(is(200));
	}

	public void constraints() throws Exception {
		RestAssured.given(this.spec).accept("application/json")
			// tag::constraints[]
			.filter(document("create-user", requestFields(
					attributes(key("title").value("Fields for user creation")), // <1>
					fieldWithPath("name").description("The user's name")
							.attributes(key("constraints")
									.value("Must not be null. Must not be empty")), // <2>
					fieldWithPath("email").description("The user's email address")
							.attributes(key("constraints")
									.value("Must be a valid email address"))))) // <3>
		// end::constraints[]
		.when().post("/users")
		.then().assertThat().statusCode(is(200));
	}

	public void descriptorReuse() throws Exception {
		FieldDescriptor[] book = new FieldDescriptor[] {
				fieldWithPath("title").description("Title of the book"),
				fieldWithPath("author").description("Author of the book") };

		// tag::single-book[]
		RestAssured.given(this.spec).accept("application/json")
			.filter(document("book", responseFields(book))) // <1>
			.when().get("/books/1")
			.then().assertThat().statusCode(is(200));
		// end::single-book[]

		// tag::book-array[]
		RestAssured.given(this.spec).accept("application/json")
			.filter(document("books", responseFields(
				fieldWithPath("[]").description("An array of books")) // <1>
				.andWithPrefix("[].", book))) // <2>
		.when().get("/books/1")
		.then().assertThat().statusCode(is(200));
		// end::book-array[]

	}

}
