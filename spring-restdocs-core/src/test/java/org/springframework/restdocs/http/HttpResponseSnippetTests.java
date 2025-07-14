/*
 * Copyright 2014-present the original author or authors.
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

package org.springframework.restdocs.http;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.restdocs.testfixtures.jupiter.AssertableSnippets;
import org.springframework.restdocs.testfixtures.jupiter.OperationBuilder;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link HttpResponseSnippet}.
 *
 * @author Andy Wilkinson
 * @author Jonathan Pearlin
 */
class HttpResponseSnippetTests {

	@RenderedSnippetTest
	void basicResponse(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpResponseSnippet().document(operationBuilder.build());
		assertThat(snippets.httpResponse()).isHttpResponse((response) -> response.ok());
	}

	@RenderedSnippetTest
	void nonOkResponse(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpResponseSnippet().document(operationBuilder.response().status(HttpStatus.BAD_REQUEST).build());
		assertThat(snippets.httpResponse()).isHttpResponse((response) -> response.badRequest());
	}

	@RenderedSnippetTest
	void responseWithHeaders(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpResponseSnippet().document(operationBuilder.response()
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.header("a", "alpha")
			.build());
		assertThat(snippets.httpResponse()).isHttpResponse(
				(response) -> response.ok().header("Content-Type", "application/json").header("a", "alpha"));
	}

	@RenderedSnippetTest
	void responseWithContent(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		String content = "content";
		new HttpResponseSnippet().document(operationBuilder.response().content(content).build());
		assertThat(snippets.httpResponse()).isHttpResponse((response) -> response.ok()
			.content(content)
			.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));
	}

	@RenderedSnippetTest
	void responseWithCharset(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		String japaneseContent = "\u30b3\u30f3\u30c6\u30f3\u30c4";
		byte[] contentBytes = japaneseContent.getBytes("UTF-8");
		new HttpResponseSnippet().document(operationBuilder.response()
			.header("Content-Type", "text/plain;charset=UTF-8")
			.content(contentBytes)
			.build());
		assertThat(snippets.httpResponse()).isHttpResponse((response) -> response.ok()
			.header("Content-Type", "text/plain;charset=UTF-8")
			.content(japaneseContent)
			.header(HttpHeaders.CONTENT_LENGTH, contentBytes.length));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "http-response", template = "http-response-with-title")
	void responseWithCustomSnippetAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpResponseSnippet(attributes(key("title").value("Title for the response")))
			.document(operationBuilder.build());
		assertThat(snippets.httpResponse()).contains("Title for the response");
	}

	@RenderedSnippetTest
	void responseWithCustomStatus(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpResponseSnippet().document(operationBuilder.response().status(HttpStatusCode.valueOf(215)).build());
		assertThat(snippets.httpResponse()).isHttpResponse(((response) -> response.status(215)));
	}

}
