/*
 * Copyright 2012-2016 the original author or authors.
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

package org.springframework.restdocs;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.test.ExpectedSnippet;
import org.springframework.restdocs.test.OperationBuilder;
import org.springframework.restdocs.test.SnippetMatchers;
import org.springframework.restdocs.test.SnippetMatchers.CodeBlockMatcher;
import org.springframework.restdocs.test.SnippetMatchers.HttpRequestMatcher;
import org.springframework.restdocs.test.SnippetMatchers.HttpResponseMatcher;
import org.springframework.restdocs.test.SnippetMatchers.TableMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.restdocs.templates.TemplateFormats.asciidoctor;
import static org.springframework.restdocs.templates.TemplateFormats.markdown;

/**
 * Abstract base class for testing snippet generation.
 *
 * @author Andy Wilkinson
 */
@RunWith(Parameterized.class)
public abstract class AbstractSnippetTests {

	protected final TemplateFormat templateFormat;

	@Rule
	public ExpectedSnippet snippet;

	@Parameters(name = "{0}")
	public static List<Object[]> parameters() {
		return Arrays.asList(new Object[] { "Asciidoctor", asciidoctor() },
				new Object[] { "Markdown", markdown() });
	}

	public AbstractSnippetTests(String name, TemplateFormat templateFormat) {
		this.snippet = new ExpectedSnippet(templateFormat);
		this.templateFormat = templateFormat;
	}

	public CodeBlockMatcher<?> codeBlock(String language) {
		return SnippetMatchers.codeBlock(this.templateFormat, language);
	}

	public TableMatcher<?> tableWithHeader(String... headers) {
		return SnippetMatchers.tableWithHeader(this.templateFormat, headers);
	}

	public TableMatcher<?> tableWithTitleAndHeader(String title, String... headers) {
		return SnippetMatchers.tableWithTitleAndHeader(this.templateFormat, title,
				headers);
	}

	public HttpRequestMatcher httpRequest(RequestMethod method, String uri) {
		return SnippetMatchers.httpRequest(this.templateFormat, method, uri);
	}

	public HttpResponseMatcher httpResponse(HttpStatus responseStatus) {
		return SnippetMatchers.httpResponse(this.templateFormat, responseStatus);
	}

	public OperationBuilder operationBuilder(String name) {
		return new OperationBuilder(name, this.snippet.getOutputDirectory(),
				this.templateFormat);
	}

	protected FileSystemResource snippetResource(String name) {
		return new FileSystemResource("src/test/resources/custom-snippet-templates/"
				+ this.templateFormat.getId() + "/" + name + ".snippet");
	}

}
