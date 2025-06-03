/*
 * Copyright 2014-2025 the original author or authors.
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

package org.springframework.restdocs.payload;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.testfixtures.jupiter.AssertableSnippets;
import org.springframework.restdocs.testfixtures.jupiter.OperationBuilder;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link ResponseBodySnippet}.
 *
 * @author Andy Wilkinson
 */
class ResponseBodySnippetTests {

	@RenderedSnippetTest
	void responseWithBody(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseBodySnippet().document(operationBuilder.response().content("some content").build());
		assertThat(snippets.responseBody())
			.isCodeBlock((codeBlock) -> codeBlock.withOptions("nowrap").content("some content"));
	}

	@RenderedSnippetTest
	void responseWithNoBody(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseBodySnippet().document(operationBuilder.response().build());
		assertThat(snippets.responseBody()).isCodeBlock((codeBlock) -> codeBlock.withOptions("nowrap").content(""));
	}

	@RenderedSnippetTest
	void responseWithJsonMediaType(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseBodySnippet().document(
				operationBuilder.response().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build());
		assertThat(snippets.responseBody())
			.isCodeBlock((codeBlock) -> codeBlock.withLanguageAndOptions("json", "nowrap").content(""));
	}

	@RenderedSnippetTest
	void responseWithJsonSubtypeMediaType(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseBodySnippet().document(operationBuilder.response()
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
			.build());
		assertThat(snippets.responseBody())
			.isCodeBlock((codeBlock) -> codeBlock.withLanguageAndOptions("json", "nowrap").content(""));
	}

	@RenderedSnippetTest
	void responseWithXmlMediaType(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseBodySnippet().document(
				operationBuilder.response().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE).build());
		assertThat(snippets.responseBody())
			.isCodeBlock((codeBlock) -> codeBlock.withLanguageAndOptions("xml", "nowrap").content(""));
	}

	@RenderedSnippetTest
	void responseWithXmlSubtypeMediaType(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new ResponseBodySnippet().document(operationBuilder.response()
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_ATOM_XML_VALUE)
			.build());
		assertThat(snippets.responseBody())
			.isCodeBlock((codeBlock) -> codeBlock.withLanguageAndOptions("xml", "nowrap").content(""));
	}

	@RenderedSnippetTest
	void subsectionOfResponseBody(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		responseBody(beneathPath("a.b"))
			.document(operationBuilder.response().content("{\"a\":{\"b\":{\"c\":5}}}").build());
		assertThat(snippets.responseBody("beneath-a.b"))
			.isCodeBlock((codeBlock) -> codeBlock.withOptions("nowrap").content("{\"c\":5}"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "response-body", template = "response-body-with-language")
	void customSnippetAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new ResponseBodySnippet(attributes(key("language").value("json")))
			.document(operationBuilder.response().content("{\"a\":\"alpha\"}").build());
		assertThat(snippets.responseBody()).isCodeBlock(
				(codeBlock) -> codeBlock.withLanguageAndOptions("json", "nowrap").content("{\"a\":\"alpha\"}"));
	}

}
