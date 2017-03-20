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

package com.example.restassured;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Rule;

import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

public class EveryTestPreprocessing {

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	// tag::setup[]
	private RequestSpecification spec;

	private RestDocumentationFilter documentationFilter;

	@Before
	public void setup() {
		this.documentationFilter = document("{method-name}",
				preprocessRequest(removeHeaders("Foo")),
				preprocessResponse(prettyPrint())); // <1>
		this.spec = new RequestSpecBuilder()
				.addFilter(documentationConfiguration(this.restDocumentation))
				.addFilter(this.documentationFilter)// <2>
				.build();
	}

	// end::setup[]

	public void use() throws Exception {
		// tag::use[]
		RestAssured.given(this.spec) // <1>
			.filter(this.documentationFilter.document( // <2>
					links(linkWithRel("self").description("Canonical self link"))))
			.when().get("/")
			.then().assertThat().statusCode(is(200));
		// end::use[]
	}

}
