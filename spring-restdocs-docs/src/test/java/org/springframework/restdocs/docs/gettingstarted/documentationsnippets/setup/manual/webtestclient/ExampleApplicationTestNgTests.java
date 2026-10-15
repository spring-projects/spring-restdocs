/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.gettingstarted.documentationsnippets.setup.manual.webtestclient;

import java.lang.reflect.Method;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

public class ExampleApplicationTestNgTests {

	public final ManualRestDocumentation restDocumentation = new ManualRestDocumentation();

	@SuppressWarnings("unused")
	private WebTestClient webTestClient;

	@Autowired
	private ApplicationContext context;

	@BeforeMethod
	public void setUp(Method method) {
		this.webTestClient = WebTestClient.bindToApplicationContext(this.context)
			.configureClient()
			.filter(documentationConfiguration(this.restDocumentation)) // <1>
			.build();
		this.restDocumentation.beforeTest(getClass(), method.getName());
	}

	@AfterMethod
	public void tearDown() {
		this.restDocumentation.afterTest();
	}

}
