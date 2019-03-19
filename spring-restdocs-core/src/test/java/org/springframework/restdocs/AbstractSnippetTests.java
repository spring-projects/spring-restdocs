/*
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.test.GeneratedSnippets;
import org.springframework.restdocs.test.OperationBuilder;
import org.springframework.restdocs.test.SnippetConditions;
import org.springframework.restdocs.test.SnippetConditions.CodeBlockCondition;
import org.springframework.restdocs.test.SnippetConditions.HttpRequestCondition;
import org.springframework.restdocs.test.SnippetConditions.HttpResponseCondition;
import org.springframework.restdocs.test.SnippetConditions.TableCondition;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Abstract base class for testing snippet generation.
 *
 * @author Andy Wilkinson
 */
@RunWith(Parameterized.class)
public abstract class AbstractSnippetTests {

	protected final TemplateFormat templateFormat;

	@Rule
	public GeneratedSnippets generatedSnippets;

	@Rule
	public OperationBuilder operationBuilder;

	@Parameters(name = "{0}")
	public static List<Object[]> parameters() {
		return Arrays.asList(
				new Object[] { "Asciidoctor", TemplateFormats.asciidoctor() },
				new Object[] { "Markdown", TemplateFormats.markdown() });
	}

	public AbstractSnippetTests(String name, TemplateFormat templateFormat) {
		this.generatedSnippets = new GeneratedSnippets(templateFormat);
		this.templateFormat = templateFormat;
		this.operationBuilder = new OperationBuilder(this.templateFormat);
	}

	public CodeBlockCondition<?> codeBlock(String language) {
		return this.codeBlock(language, null);
	}

	public CodeBlockCondition<?> codeBlock(String language, String options) {
		return SnippetConditions.codeBlock(this.templateFormat, language, options);
	}

	public TableCondition<?> tableWithHeader(String... headers) {
		return SnippetConditions.tableWithHeader(this.templateFormat, headers);
	}

	public TableCondition<?> tableWithTitleAndHeader(String title, String... headers) {
		return SnippetConditions.tableWithTitleAndHeader(this.templateFormat, title,
				headers);
	}

	public HttpRequestCondition httpRequest(RequestMethod method, String uri) {
		return SnippetConditions.httpRequest(this.templateFormat, method, uri);
	}

	public HttpResponseCondition httpResponse(HttpStatus responseStatus) {
		return SnippetConditions.httpResponse(this.templateFormat, responseStatus);
	}

	protected FileSystemResource snippetResource(String name) {
		return new FileSystemResource("src/test/resources/custom-snippet-templates/"
				+ this.templateFormat.getId() + "/" + name + ".snippet");
	}

}
