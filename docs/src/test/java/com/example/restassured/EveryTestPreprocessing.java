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

import org.junit.Before;
import org.junit.Rule;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

public class EveryTestPreprocessing {

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation(
			"target/generated-snippets");

	// tag::setup[]
	private RequestSpecification spec;

	private RestDocumentationFilter document;

	@Before
	public void setup() {
		this.document = document("{method-name}",
				preprocessRequest(removeHeaders("Foo")),
				preprocessResponse(prettyPrint())); // <1>
		this.spec = new RequestSpecBuilder()
				.addFilter(documentationConfiguration(this.restDocumentation))
				.addFilter(this.document)// <2>
				.build();
	}

	// end::setup[]

	public void use() throws Exception {
		// tag::use[]
		this.document.snippets( // <1>
				links(linkWithRel("self").description("Canonical self link")));
		RestAssured.given(this.spec) // <2>
			.when().get("/")
			.then().assertThat().statusCode(is(200));
		// end::use[]
	}

}
