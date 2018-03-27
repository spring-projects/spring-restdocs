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

package org.springframework.restdocs.http;

import java.io.IOException;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link HttpResponseSnippet}.
 *
 * @author Andy Wilkinson
 * @author Jonathan Pearlin
 */
public class HttpResponseSnippetTests extends AbstractSnippetTests {

	public HttpResponseSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void basicResponse() throws IOException {
		this.snippets.expectHttpResponse().withContents(httpResponse(HttpStatus.OK));
		new HttpResponseSnippet().document(this.operationBuilder.build());
	}

	@Test
	public void nonOkResponse() throws IOException {
		this.snippets.expectHttpResponse()
				.withContents(httpResponse(HttpStatus.BAD_REQUEST));
		new HttpResponseSnippet().document(this.operationBuilder.response()
				.status(HttpStatus.BAD_REQUEST.value()).build());
	}

	@Test
	public void responseWithHeaders() throws IOException {
		this.snippets.expectHttpResponse().withContents(httpResponse(HttpStatus.OK)
				.header("Content-Type", "application/json").header("a", "alpha"));
		new HttpResponseSnippet().document(this.operationBuilder.response()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header("a", "alpha").build());
	}

	@Test
	public void responseWithContent() throws IOException {
		String content = "content";
		this.snippets.expectHttpResponse()
				.withContents(httpResponse(HttpStatus.OK).content(content)
						.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));
		new HttpResponseSnippet()
				.document(this.operationBuilder.response().content(content).build());
	}

	@Test
	public void responseWithCharset() throws IOException {
		String japaneseContent = "\u30b3\u30f3\u30c6\u30f3\u30c4";
		byte[] contentBytes = japaneseContent.getBytes("UTF-8");
		this.snippets.expectHttpResponse()
				.withContents(httpResponse(HttpStatus.OK)
						.header("Content-Type", "text/plain;charset=UTF-8")
						.content(japaneseContent)
						.header(HttpHeaders.CONTENT_LENGTH, contentBytes.length));
		new HttpResponseSnippet().document(this.operationBuilder.response()
				.header("Content-Type", "text/plain;charset=UTF-8").content(contentBytes)
				.build());
	}

	@Test
	public void responseWithCustomSnippetAttributes() throws IOException {
		this.snippets.expectHttpResponse()
				.withContents(containsString("Title for the response"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("http-response"))
				.willReturn(snippetResource("http-response-with-title"));
		new HttpResponseSnippet(attributes(key("title").value("Title for the response")))
				.document(this.operationBuilder.attribute(TemplateEngine.class.getName(),
						new MustacheTemplateEngine(resolver)).build());
	}

}
