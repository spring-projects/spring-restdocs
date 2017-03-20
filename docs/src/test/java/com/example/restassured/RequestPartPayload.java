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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

public class RequestPartPayload {

	private RequestSpecification spec;

	public void fields() throws Exception {
		// tag::fields[]
		Map<String, String> metadata = new HashMap<>();
		metadata.put("version", "1.0");
		RestAssured.given(this.spec).accept("application/json")
			.filter(document("image-upload", requestPartFields("metadata", // <1>
					fieldWithPath("version").description("The version of the image")))) // <2>
			.when().multiPart("image", new File("image.png"), "image/png")
					.multiPart("metadata", metadata).post("images")
			.then().assertThat().statusCode(is(200));
		// end::fields[]
	}

	public void body() throws Exception {
		// tag::body[]
		Map<String, String> metadata = new HashMap<>();
		metadata.put("version", "1.0");
		RestAssured.given(this.spec).accept("application/json")
			.filter(document("image-upload", requestPartBody("metadata"))) // <1>
			.when().multiPart("image", new File("image.png"), "image/png")
					.multiPart("metadata", metadata).post("images")
			.then().assertThat().statusCode(is(200));
		// end::body[]
	}

}
