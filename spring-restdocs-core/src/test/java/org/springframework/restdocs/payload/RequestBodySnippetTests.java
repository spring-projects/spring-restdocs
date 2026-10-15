/*
 * Copyright 2014-present the original author or authors.
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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link RequestBodySnippet}.
 *
 * @author Andy Wilkinson
 */
class RequestBodySnippetTests {

	@RenderedSnippetTest
	void requestWithBody(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		requestBody().document(operationBuilder.request("http://localhost").content("some content").build());
		assertThat(snippets.requestBody())
			.isCodeBlock((codeBlock) -> codeBlock.withOptions("nowrap").content("some content"));
	}

	@RenderedSnippetTest
	void requestWithNoBody(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		requestBody().document(operationBuilder.request("http://localhost").build());
		assertThat(snippets.requestBody()).isCodeBlock((codeBlock) -> codeBlock.withOptions("nowrap").content(""));
	}

	@RenderedSnippetTest
	void requestWithJsonMediaType(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		requestBody().document(operationBuilder.request("http://localhost")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build());
		assertThat(snippets.requestBody())
			.isCodeBlock((codeBlock) -> codeBlock.withLanguageAndOptions("json", "nowrap").content(""));
	}

	@RenderedSnippetTest
	void requestWithJsonSubtypeMediaType(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		requestBody().document(operationBuilder.request("http://localhost")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
			.build());
		assertThat(snippets.requestBody())
			.isCodeBlock((codeBlock) -> codeBlock.withLanguageAndOptions("json", "nowrap").content(""));
	}

	@RenderedSnippetTest
	void requestWithXmlMediaType(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		requestBody().document(operationBuilder.request("http://localhost")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
			.build());
		assertThat(snippets.requestBody())
			.isCodeBlock((codeBlock) -> codeBlock.withLanguageAndOptions("xml", "nowrap").content(""));
	}

	@RenderedSnippetTest
	void requestWithXmlSubtypeMediaType(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		requestBody().document(operationBuilder.request("http://localhost")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_ATOM_XML_VALUE)
			.build());
		assertThat(snippets.requestBody())
			.isCodeBlock((codeBlock) -> codeBlock.withLanguageAndOptions("xml", "nowrap").content(""));
	}

	@RenderedSnippetTest
	void subsectionOfRequestBody(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		requestBody(beneathPath("a.b"))
			.document(operationBuilder.request("http://localhost").content("{\"a\":{\"b\":{\"c\":5}}}").build());
		assertThat(snippets.requestBody("beneath-a.b"))
			.isCodeBlock((codeBlock) -> codeBlock.withOptions("nowrap").content("{\"c\":5}"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "request-body", template = "request-body-with-language")
	void customSnippetAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		requestBody(attributes(key("language").value("json")))
			.document(operationBuilder.request("http://localhost").content("{\"a\":\"alpha\"}").build());
		assertThat(snippets.requestBody()).isCodeBlock(
				(codeBlock) -> codeBlock.withLanguageAndOptions("json", "nowrap").content("{\"a\":\"alpha\"}"));
	}

}
