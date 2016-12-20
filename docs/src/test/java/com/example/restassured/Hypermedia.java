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
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

public class Hypermedia {

	private RequestSpecification spec;

	public void defaultExtractor() throws Exception {
		// tag::links[]
		RestAssured.given(this.spec)
			.accept("application/json")
			.filter(document("index", links( // <1>
					linkWithRel("alpha").description("Link to the alpha resource"), // <2>
					linkWithRel("bravo").description("Link to the bravo resource")))) // <3>
			.get("/").then().assertThat().statusCode(is(200));
		// end::links[]
	}

	public void explicitExtractor() throws Exception {
		RestAssured.given(this.spec)
		.accept("application/json")
		// tag::explicit-extractor[]
		.filter(document("index", links(halLinks(), // <1>
				linkWithRel("alpha").description("Link to the alpha resource"),
				linkWithRel("bravo").description("Link to the bravo resource"))))
		// end::explicit-extractor[]
		.get("/").then().assertThat().statusCode(is(200));
	}

}
