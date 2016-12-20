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

package org.springframework.restdocs.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.runners.model.Statement;

import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.test.SnippetMatchers.SnippetMatcher;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * The {@code ExpectedSnippets} rule is used to verify that a test of a
 * {@link TemplatedSnippet} has generated the expected snippets.
 *
 * @author Andy Wilkinson
 * @author Andreas Evers
 */
public class ExpectedSnippets extends OperationTestRule {

	private final TemplateFormat templateFormat;

	private String operationName;

	private File outputDirectory;

	private List<ExpectedSnippet> expectations = new ArrayList<>();

	public ExpectedSnippets(TemplateFormat templateFormat) {
		this.templateFormat = templateFormat;
	}

	@Override
	public Statement apply(final Statement base, File outputDirectory,
			String operationName) {
		this.outputDirectory = outputDirectory;
		this.operationName = operationName;
		return new ExpectedSnippetsStatement(base);
	}

	private void verifySnippets() throws IOException {
		if (this.outputDirectory != null && this.operationName != null) {
			File snippetDir = new File(this.outputDirectory, this.operationName);
			for (ExpectedSnippet expectation : this.expectations) {
				expectation.verify(snippetDir);
			}
		}
	}

	public ExpectedSnippet expectCurlRequest() {
		return expect("curl-request");
	}

	public ExpectedSnippet expectHttpieRequest() {
		return expect("httpie-request");
	}

	public ExpectedSnippet expectRequestFields() {
		return expect("request-fields");
	}

	public ExpectedSnippet expectRequestPartFields(String partName) {
		return expect("request-part-" + partName + "-fields");
	}

	public ExpectedSnippet expectResponseFields() {
		return expect("response-fields");
	}

	public ExpectedSnippet expectRequestHeaders() {
		return expect("request-headers");
	}

	public ExpectedSnippet expectResponseHeaders() {
		return expect("response-headers");
	}

	public ExpectedSnippet expectLinks() {
		return expect("links");
	}

	public ExpectedSnippet expectHttpRequest() {
		return expect("http-request");
	}

	public ExpectedSnippet expectHttpResponse() {
		return expect("http-response");
	}

	public ExpectedSnippet expectRequestParameters() {
		return expect("request-parameters");
	}

	public ExpectedSnippet expectPathParameters() {
		return expect("path-parameters");
	}

	public ExpectedSnippet expectRequestParts() {
		return expect("request-parts");
	}

	public ExpectedSnippet expect(String type) {
		ExpectedSnippet expectedSnippet = new ExpectedSnippet(
				SnippetMatchers.snippet(this.templateFormat), type);
		this.expectations.add(expectedSnippet);
		return expectedSnippet;
	}

	public File getOutputDirectory() {
		return this.outputDirectory;
	}

	public String getOperationName() {
		return this.operationName;
	}

	/**
	 * Expecations for a particular snippet.
	 */
	public final class ExpectedSnippet {

		private final SnippetMatcher snippetMatcher;

		private final String snippetName;

		private ExpectedSnippet(SnippetMatcher snippetMatcher, String snippetName) {
			this.snippetMatcher = snippetMatcher;
			this.snippetName = snippetName;
		}

		private void verify(File snippetDir) {
			File snippetFile = new File(snippetDir, this.snippetName + "."
					+ ExpectedSnippets.this.templateFormat.getFileExtension());
			assertThat(snippetFile, is(this.snippetMatcher));
		}

		public void withContents(Matcher<String> matcher) {
			this.snippetMatcher.withContents(matcher);
		}

	}

	private final class ExpectedSnippetsStatement extends Statement {

		private final Statement delegate;

		private ExpectedSnippetsStatement(Statement delegate) {
			this.delegate = delegate;
		}

		@Override
		public void evaluate() throws Throwable {
			this.delegate.evaluate();
			verifySnippets();
		}

	}

}
