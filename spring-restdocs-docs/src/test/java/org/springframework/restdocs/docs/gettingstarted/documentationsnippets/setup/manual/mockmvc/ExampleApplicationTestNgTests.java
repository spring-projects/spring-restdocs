/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.gettingstarted.documentationsnippets.setup.manual.mockmvc;

import java.lang.reflect.Method;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

public class ExampleApplicationTestNgTests {

	public final ManualRestDocumentation restDocumentation = new ManualRestDocumentation();

	@SuppressWarnings("unused")
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

	@AfterMethod
	public void tearDown() {
		this.restDocumentation.afterTest();
	}

}
