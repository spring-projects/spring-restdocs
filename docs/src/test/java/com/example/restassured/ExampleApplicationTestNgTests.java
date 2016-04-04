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

import java.lang.reflect.Method;

import org.springframework.restdocs.ManualRestDocumentation;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;

import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

public class ExampleApplicationTestNgTests {

	private final ManualRestDocumentation restDocumentation = new ManualRestDocumentation(
			"build/generated-snippets");

	@SuppressWarnings("unused")
	// tag::setup[]
	private RequestSpecification spec;

	@BeforeMethod
	public void setUp(Method method) {
		this.spec = new RequestSpecBuilder().addFilter(
				documentationConfiguration(this.restDocumentation))
				.build();
		this.restDocumentation.beforeTest(getClass(), method.getName());
	}

	// end::setup[]

	// tag::teardown[]
	@AfterMethod
	public void tearDown() {
		this.restDocumentation.afterTest();
	}
	// end::teardown[]
}
