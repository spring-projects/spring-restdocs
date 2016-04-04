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

package com.example.mockmvc;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

public class ExampleApplicationTestNgTests {

	public final ManualRestDocumentation restDocumentation = new ManualRestDocumentation(
			"target/generated-snippets");
	@SuppressWarnings("unused")
	// tag::setup[]
	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext context;

	@BeforeMethod
	public void setUp(Method method) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation))
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
