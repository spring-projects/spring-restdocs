/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.gettingstarted.documentationsnippets.setup.manual.resttestclient;

import java.lang.reflect.Method;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.documentationConfiguration;

public class ExampleApplicationTestNgTests {

	public final ManualRestDocumentation restDocumentation = new ManualRestDocumentation();

	@SuppressWarnings("unused")
	private RestTestClient restTestClient;

	@Autowired
	private WebApplicationContext context;

	@BeforeMethod
	public void setUp(Method method) {
		this.restTestClient = RestTestClient.bindToApplicationContext(this.context)
			.requestInterceptor(documentationConfiguration(this.restDocumentation)) // <1>
			.build();
		this.restDocumentation.beforeTest(getClass(), method.getName());
	}

	@AfterMethod
	public void tearDown() {
		this.restDocumentation.afterTest();
	}

}
