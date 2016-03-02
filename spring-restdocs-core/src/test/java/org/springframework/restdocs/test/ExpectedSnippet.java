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

package org.springframework.restdocs.test;

import java.io.File;
import java.io.IOException;

import org.hamcrest.Matcher;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.test.SnippetMatchers.SnippetMatcher;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * The {@code ExpectedSnippet} rule is used to verify that a {@link TemplatedSnippet} has
 * generated the expected snippet.
 *
 * @author Andy Wilkinson
 * @author Andreas Evers
 */
public class ExpectedSnippet implements TestRule {

	private final TemplateFormat templateFormat;

	private final SnippetMatcher snippet;

	private String expectedName;

	private String expectedType;

	private File outputDirectory;

	public ExpectedSnippet(TemplateFormat templateFormat) {
		this.templateFormat = templateFormat;
		this.snippet = SnippetMatchers.snippet(templateFormat);
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		this.outputDirectory = new File(
				"build/" + description.getTestClass().getSimpleName());
		return new ExpectedSnippetStatement(base);
	}

	private void verifySnippet() throws IOException {
		if (this.outputDirectory != null && this.expectedName != null) {
			File snippetDir = new File(this.outputDirectory, this.expectedName);
			File snippetFile = new File(snippetDir,
					this.expectedType + "." + this.templateFormat.getFileExtension());
			assertThat(snippetFile, is(this.snippet));
		}
	}

	public ExpectedSnippet expectCurlRequest(String name) {
		expect(name, "curl-request");
		return this;
	}

	public ExpectedSnippet expectHttpieRequest(String name) {
		expect(name, "httpie-request");
		return this;
	}

	public ExpectedSnippet expectRequestFields(String name) {
		expect(name, "request-fields");
		return this;
	}

	public ExpectedSnippet expectResponseFields(String name) {
		expect(name, "response-fields");
		return this;
	}

	public ExpectedSnippet expectRequestHeaders(String name) {
		expect(name, "request-headers");
		return this;
	}

	public ExpectedSnippet expectResponseHeaders(String name) {
		expect(name, "response-headers");
		return this;
	}

	public ExpectedSnippet expectLinks(String name) {
		expect(name, "links");
		return this;
	}

	public ExpectedSnippet expectHttpRequest(String name) {
		expect(name, "http-request");
		return this;
	}

	public ExpectedSnippet expectHttpResponse(String name) {
		expect(name, "http-response");
		return this;
	}

	public ExpectedSnippet expectRequestParameters(String name) {
		expect(name, "request-parameters");
		return this;
	}

	public ExpectedSnippet expectPathParameters(String name) {
		expect(name, "path-parameters");
		return this;
	}

	private ExpectedSnippet expect(String name, String type) {
		this.expectedName = name;
		this.expectedType = type;
		return this;
	}

	public void withContents(Matcher<String> matcher) {
		this.snippet.withContents(matcher);
	}

	public File getOutputDirectory() {
		return this.outputDirectory;
	}

	private final class ExpectedSnippetStatement extends Statement {

		private final Statement delegate;

		private ExpectedSnippetStatement(Statement delegate) {
			this.delegate = delegate;
		}

		@Override
		public void evaluate() throws Throwable {
			this.delegate.evaluate();
			verifySnippet();
		}

	}

}
