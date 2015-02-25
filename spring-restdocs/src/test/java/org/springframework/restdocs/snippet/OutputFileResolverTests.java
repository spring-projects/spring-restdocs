/*
 * Copyright 2014-2015 the original author or authors.
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

package org.springframework.restdocs.snippet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Method;

import org.junit.Test;
import org.springframework.restdocs.config.RestDocumentationTestExecutionListener;
import org.springframework.test.context.TestContext;

/**
 * Tests for {@link OutputFileResolver}.
 * 
 * @author Andy Wilkinson
 */
public class OutputFileResolverTests {

	private final OutputFileResolver resolver = new OutputFileResolver();

	@Test
	public void noConfiguredOutputDirectoryAndRelativeInput() {
		assertThat(this.resolver.resolve("foo", "bar.txt"), is(nullValue()));
	}

	@Test
	public void absoluteInput() {
		String absolutePath = new File("foo").getAbsolutePath();
		assertThat(this.resolver.resolve(absolutePath, "bar.txt"), is(new File(
				absolutePath, "bar.txt")));
	}

	@Test
	public void configuredOutputAndRelativeInput() {
		String outputDir = new File("foo").getAbsolutePath();
		System.setProperty("org.springframework.restdocs.outputDir", outputDir);
		try {
			assertThat(this.resolver.resolve("bar", "baz.txt"), is(new File(outputDir,
					"bar/baz.txt")));
		}
		finally {
			System.clearProperty("org.springframework.restdocs.outputDir");
		}
	}

	@Test
	public void configuredOutputAndAbsoluteInput() {
		String outputDir = new File("foo").getAbsolutePath();
		String absolutePath = new File("bar").getAbsolutePath();
		System.setProperty("org.springframework.restdocs.outputDir", outputDir);
		try {
			assertThat(this.resolver.resolve(absolutePath, "baz.txt"), is(new File(
					absolutePath, "baz.txt")));
		}
		finally {
			System.clearProperty("org.springframework.restdocs.outputDir");
		}
	}

	@Test(expected = IllegalStateException.class)
	public void placeholderWithoutAReplacement() {
		this.resolver.resolve("{method-name}", "foo.txt");
	}

	@Test
	public void dashSeparatedMethodName() throws Exception {
		RestDocumentationTestExecutionListener listener = new RestDocumentationTestExecutionListener();
		TestContext testContext = mock(TestContext.class);
		Method method = getClass().getMethod("dashSeparatedMethodName");
		when(testContext.getTestMethod()).thenReturn(method);
		listener.beforeTestMethod(testContext);
		try {
			assertThat(this.resolver.resolve(new File("{method-name}").getAbsolutePath(),
					"foo.txt"),
					is(new File(new File("dash-separated-method-name").getAbsolutePath(),
							"foo.txt")));
		}
		finally {
			listener.afterTestMethod(testContext);
		}
	}

	@Test
	public void underscoreSeparatedMethodName() throws Exception {
		RestDocumentationTestExecutionListener listener = new RestDocumentationTestExecutionListener();
		TestContext testContext = mock(TestContext.class);
		Method method = getClass().getMethod("underscoreSeparatedMethodName");
		when(testContext.getTestMethod()).thenReturn(method);
		listener.beforeTestMethod(testContext);
		try {
			assertThat(
					this.resolver.resolve(new File("{method_name}").getAbsolutePath(),
							"foo.txt"),
					is(new File(new File("underscore_separated_method_name")
							.getAbsolutePath(), "foo.txt")));
		}
		finally {
			listener.afterTestMethod(testContext);
		}
	}

	@Test
	public void camelCaseMethodName() throws Exception {
		RestDocumentationTestExecutionListener listener = new RestDocumentationTestExecutionListener();
		TestContext testContext = mock(TestContext.class);
		Method method = getClass().getMethod("camelCaseMethodName");
		when(testContext.getTestMethod()).thenReturn(method);
		listener.beforeTestMethod(testContext);
		try {
			assertThat(this.resolver.resolve(new File("{methodName}").getAbsolutePath(),
					"foo.txt"),
					is(new File(new File("camelCaseMethodName").getAbsolutePath(),
							"foo.txt")));
		}
		finally {
			listener.afterTestMethod(testContext);
		}
	}

	@Test
	public void stepCount() throws Exception {
		RestDocumentationTestExecutionListener listener = new RestDocumentationTestExecutionListener();
		TestContext testContext = mock(TestContext.class);
		Method method = getClass().getMethod("stepCount");
		when(testContext.getTestMethod()).thenReturn(method);
		listener.beforeTestMethod(testContext);
		try {
			assertThat(this.resolver.resolve(new File("{step}").getAbsolutePath(),
					"foo.txt"), is(new File(new File("0").getAbsolutePath(), "foo.txt")));
		}
		finally {
			listener.afterTestMethod(testContext);
		}
	}
}
