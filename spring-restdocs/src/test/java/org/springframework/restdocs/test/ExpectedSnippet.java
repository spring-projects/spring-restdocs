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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.hamcrest.Matcher;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.restdocs.snippet.SnippetWritingResultHandler;
import org.springframework.restdocs.test.SnippetMatchers.SnippetMatcher;

/**
 * The {@code ExpectedSnippet} rule is used to verify that a
 * {@link SnippetWritingResultHandler} has generated the expected snippet.
 * 
 * @author Andy Wilkinson
 */
public class ExpectedSnippet implements TestRule {

	private String expectedName;

	private String expectedType;

	private SnippetMatcher snippet = SnippetMatchers.snippet();

	private File outputDir;

	@Override
	public Statement apply(final Statement base, Description description) {
		this.outputDir = new File("build/" + description.getTestClass().getSimpleName());
		return new OutputDirectoryStatement(new ExpectedSnippetStatement(base),
				this.outputDir);
	}

	private static final class OutputDirectoryStatement extends Statement {

		private final Statement delegate;

		private final File outputDir;

		public OutputDirectoryStatement(Statement delegate, File outputDir) {
			this.delegate = delegate;
			this.outputDir = outputDir;
		}

		@Override
		public void evaluate() throws Throwable {
			System.setProperty("org.springframework.restdocs.outputDir",
					this.outputDir.getAbsolutePath());
			try {
				this.delegate.evaluate();
			}
			finally {
				System.clearProperty("org.springframework.restdocs.outputDir");
			}
		}
	}

	private final class ExpectedSnippetStatement extends Statement {

		private final Statement delegate;

		public ExpectedSnippetStatement(Statement delegate) {
			this.delegate = delegate;
		}

		@Override
		public void evaluate() throws Throwable {
			this.delegate.evaluate();
			verifySnippet();
		}

	}

	private void verifySnippet() throws IOException {
		if (this.outputDir != null && this.expectedName != null) {
			File snippetDir = new File(this.outputDir, this.expectedName);
			File snippetFile = new File(snippetDir, this.expectedType + ".adoc");
			assertThat(snippetFile, is(this.snippet));
		}
	}

	public ExpectedSnippet expectCurlRequest(String name) {
		expect(name, "curl-request");
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

	public ExpectedSnippet expectQueryParameters(String name) {
		expect(name, "query-parameters");
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

}
