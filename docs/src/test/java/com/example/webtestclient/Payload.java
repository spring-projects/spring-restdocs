/*
 * Copyright 2014-2017 the original author or authors.
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

package com.example.webtestclient;

import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

public class Payload {

	// @formatter:off

	private WebTestClient webTestClient;

	public void response() throws Exception {
		// tag::response[]
		this.webTestClient.get().uri("user/5").accept(MediaType.APPLICATION_JSON)
			.exchange().expectStatus().isOk().expectBody()
			.consumeWith(document("user",
				responseFields( // <1>
					fieldWithPath("contact.email").description("The user's email address"), // <2>
					fieldWithPath("contact.name").description("The user's name")))); // <3>
		// end::response[]
	}

	public void subsection() throws Exception {
		// tag::subsection[]
		this.webTestClient.get().uri("user/5").accept(MediaType.APPLICATION_JSON)
			.exchange().expectStatus().isOk().expectBody()
			.consumeWith(document("user",
				responseFields(
					subsectionWithPath("contact").description("The user's contact details")))); // <1>
		// end::subsection[]
	}

	public void explicitType() throws Exception {
		this.webTestClient.get().uri("user/5").accept(MediaType.APPLICATION_JSON)
			.exchange().expectStatus().isOk().expectBody()
			// tag::explicit-type[]
			.consumeWith(document("user",
				responseFields(
					fieldWithPath("contact.email")
						.type(JsonFieldType.STRING) // <1>
						.description("The user's email address"))));
		// end::explicit-type[]
	}

	public void constraints() throws Exception {
		this.webTestClient.get().uri("user/5").accept(MediaType.APPLICATION_JSON)
			.exchange().expectStatus().isOk().expectBody()
			// tag::constraints[]
			.consumeWith(document("create-user",
				requestFields(
					attributes(key("title").value("Fields for user creation")), // <1>
					fieldWithPath("name")
						.description("The user's name")
						.attributes(key("constraints").value("Must not be null. Must not be empty")), // <2>
					fieldWithPath("email")
						.description("The user's email address")
						.attributes(key("constraints").value("Must be a valid email address"))))); // <3>
			// end::constraints[]
	}

	public void descriptorReuse() throws Exception {
		FieldDescriptor[] book = new FieldDescriptor[] {
				fieldWithPath("title").description("Title of the book"),
				fieldWithPath("author").description("Author of the book") };

		// tag::single-book[]
		this.webTestClient.get().uri("/books/1").accept(MediaType.APPLICATION_JSON)
			.exchange().expectStatus().isOk().expectBody()
			.consumeWith(document("book",
				responseFields(book))); // <1>
		// end::single-book[]

		// tag::book-array[]
		this.webTestClient.get().uri("/books").accept(MediaType.APPLICATION_JSON)
			.exchange().expectStatus().isOk().expectBody()
			.consumeWith(document("books",
				responseFields(
					fieldWithPath("[]")
						.description("An array of books")) // <1>
						.andWithPrefix("[].", book))); // <2>
		// end::book-array[]
	}

	public void fieldsSubsection() throws Exception {
		// tag::fields-subsection[]
		this.webTestClient.get().uri("/locations/1").accept(MediaType.APPLICATION_JSON)
			.exchange().expectStatus().isOk().expectBody()
			.consumeWith(document("temperature",
				responseFields(beneathPath("weather.temperature"), // <1>
					fieldWithPath("high").description("The forecast high in degrees celcius"), // <2>
					fieldWithPath("low").description("The forecast low in degrees celcius"))));
		// end::fields-subsection[]
	}

	public void bodySubsection() throws Exception {
		// tag::body-subsection[]
		this.webTestClient.get().uri("/locations/1").accept(MediaType.APPLICATION_JSON)
			.exchange().expectStatus().isOk().expectBody()
			.consumeWith(document("temperature",
				responseBody(beneathPath("weather.temperature")))); // <1>
		// end::body-subsection[]
	}

}
