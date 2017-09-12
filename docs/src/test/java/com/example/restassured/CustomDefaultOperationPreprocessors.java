/*
 * Copyright 2014-2018 the original author or authors.
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

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import org.junit.Before;
import org.junit.Rule;

import org.springframework.restdocs.JUnitRestDocumentation;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

public class CustomDefaultOperationPreprocessors {

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@SuppressWarnings("unused")
	private RequestSpecification spec;

	@Before
	public void setup() {
		// tag::custom-default-preprocessors[]
		this.spec = new RequestSpecBuilder()
				.addFilter(documentationConfiguration(this.restDocumentation).operationPreprocessors()
						.withDefaultRequestPreprocessors(removeHeaders("Foo"))
						.withDefaultResponsePreprocessors(prettyPrint()))
				.build();
		// end::custom-default-preprocessors[]
	}
}
