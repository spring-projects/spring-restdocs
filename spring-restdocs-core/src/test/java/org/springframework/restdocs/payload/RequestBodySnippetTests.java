/*
 * Copyright 2014-2018 the original author or authors.
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

package org.springframework.restdocs.payload;

import java.io.IOException;

import org.junit.Test;

import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link RequestBodySnippet}.
 *
 * @author Andy Wilkinson
 */
public class RequestBodySnippetTests extends AbstractSnippetTests {

	public RequestBodySnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void requestWithBody() throws IOException {
		this.snippets.expect("request-body")
				.withContents(codeBlock(null, "nowrap").content("some content"));

		requestBody().document(this.operationBuilder.request("http://localhost")
				.content("some content").build());
	}

	@Test
	public void requestWithNoBody() throws IOException {
		this.snippets.expect("request-body")
				.withContents(codeBlock(null, "nowrap").content(""));
		requestBody().document(this.operationBuilder.request("http://localhost").build());
	}

	@Test
	public void subsectionOfRequestBody() throws IOException {
		this.snippets.expect("request-body-beneath-a.b")
				.withContents(codeBlock(null, "nowrap").content("{\"c\":5}"));

		requestBody(beneathPath("a.b"))
				.document(this.operationBuilder.request("http://localhost")
						.content("{\"a\":{\"b\":{\"c\":5}}}").build());
	}

	@Test
	public void customSnippetAttributes() throws IOException {
		this.snippets.expect("request-body")
				.withContents(codeBlock("json", "nowrap").content("{\"a\":\"alpha\"}"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-body"))
				.willReturn(snippetResource("request-body-with-language"));
		requestBody(attributes(key("language").value("json")))
				.document(this.operationBuilder
						.attribute(TemplateEngine.class.getName(),
								new MustacheTemplateEngine(resolver))
						.request("http://localhost").content("{\"a\":\"alpha\"}")
						.build());
	}

}
