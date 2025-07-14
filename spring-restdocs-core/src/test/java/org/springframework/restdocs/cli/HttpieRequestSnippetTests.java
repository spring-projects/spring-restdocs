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

package org.springframework.restdocs.cli;

import java.io.IOException;
import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.testfixtures.jupiter.AssertableSnippets;
import org.springframework.restdocs.testfixtures.jupiter.OperationBuilder;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link HttpieRequestSnippet}.
 *
 * @author Andy Wilkinson
 * @author Yann Le Guern
 * @author Dmitriy Mayboroda
 * @author Jonathan Pearlin
 * @author Paul-Christian Volkmer
 * @author Raman Gupta
 * @author Tomasz Kopczynski
 */
class HttpieRequestSnippetTests {

	private CommandFormatter commandFormatter = CliDocumentation.singleLineFormat();

	@RenderedSnippetTest
	void getRequest(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
			.document(operationBuilder.request("http://localhost/foo").build());
		assertThat(snippets.httpieRequest())
			.isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash").content("$ http GET 'http://localhost/foo'"));
	}

	@RenderedSnippetTest
	void nonGetRequest(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
			.document(operationBuilder.request("http://localhost/foo").method("POST").build());
		assertThat(snippets.httpieRequest())
			.isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash").content("$ http POST 'http://localhost/foo'"));
	}

	@RenderedSnippetTest
	void requestWithContent(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
			.document(operationBuilder.request("http://localhost/foo").content("content").build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content("$ echo 'content' | http GET 'http://localhost/foo'"));
	}

	@RenderedSnippetTest
	void getRequestWithQueryString(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
			.document(operationBuilder.request("http://localhost/foo?param=value").build());
		assertThat(snippets.httpieRequest()).isCodeBlock(
				(codeBlock) -> codeBlock.withLanguage("bash").content("$ http GET 'http://localhost/foo?param=value'"));
	}

	@RenderedSnippetTest
	void getRequestWithQueryStringWithNoValue(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
			.document(operationBuilder.request("http://localhost/foo?param").build());
		assertThat(snippets.httpieRequest()).isCodeBlock(
				(codeBlock) -> codeBlock.withLanguage("bash").content("$ http GET 'http://localhost/foo?param'"));
	}

	@RenderedSnippetTest
	void postRequestWithQueryString(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
			.document(operationBuilder.request("http://localhost/foo?param=value").method("POST").build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content("$ http POST 'http://localhost/foo?param=value'"));
	}

	@RenderedSnippetTest
	void postRequestWithQueryStringWithNoValue(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
			.document(operationBuilder.request("http://localhost/foo?param").method("POST").build());
		assertThat(snippets.httpieRequest()).isCodeBlock(
				(codeBlock) -> codeBlock.withLanguage("bash").content("$ http POST 'http://localhost/foo?param'"));
	}

	@RenderedSnippetTest
	void postRequestWithOneParameter(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/foo")
			.method("POST")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.content("k1=v1")
			.build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content("$ http --form POST 'http://localhost/foo' 'k1=v1'"));
	}

	@RenderedSnippetTest
	void postRequestWithOneParameterWithNoValue(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/foo")
			.method("POST")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.content("k1")
			.build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content("$ http --form POST 'http://localhost/foo' 'k1='"));
	}

	@RenderedSnippetTest
	void postRequestWithMultipleParameters(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/foo")
			.method("POST")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.content("k1=v1&k1=v1-bis&k2=v2")
			.build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content("$ http --form POST 'http://localhost/foo' 'k1=v1' 'k1=v1-bis' 'k2=v2'"));
	}

	@RenderedSnippetTest
	void postRequestWithUrlEncodedParameter(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/foo")
			.method("POST")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.content("k1=a%26b")
			.build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content("$ http --form POST 'http://localhost/foo' 'k1=a&b'"));
	}

	@RenderedSnippetTest
	void putRequestWithOneParameter(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/foo")
			.method("PUT")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.content("k1=v1")
			.build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content("$ http --form PUT 'http://localhost/foo' 'k1=v1'"));
	}

	@RenderedSnippetTest
	void putRequestWithMultipleParameters(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/foo")
			.method("PUT")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.content("k1=v1&k1=v1-bis&k2=v2")
			.build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content("$ http --form PUT 'http://localhost/foo'" + " 'k1=v1' 'k1=v1-bis' 'k2=v2'"));
	}

	@RenderedSnippetTest
	void putRequestWithUrlEncodedParameter(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/foo")
			.method("PUT")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.content("k1=a%26b")
			.build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content("$ http --form PUT 'http://localhost/foo' 'k1=a&b'"));
	}

	@RenderedSnippetTest
	void requestWithHeaders(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/foo")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.header("a", "alpha")
			.build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content(("$ http GET 'http://localhost/foo'" + " 'Content-Type:application/json' 'a:alpha'")));
	}

	@RenderedSnippetTest
	void requestWithHeadersMultiline(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpieRequestSnippet(CliDocumentation.multiLineFormat())
			.document(operationBuilder.request("http://localhost/foo")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header("a", "alpha")
				.build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content(String.format("$ http GET 'http://localhost/foo' \\%n"
					+ "    'Content-Type:application/json' \\%n    'a:alpha'")));
	}

	@RenderedSnippetTest
	void requestWithCookies(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/foo")
			.cookie("name1", "value1")
			.cookie("name2", "value2")
			.build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content(("$ http GET 'http://localhost/foo'" + " 'Cookie:name1=value1' 'Cookie:name2=value2'")));
	}

	@RenderedSnippetTest
	void multipartPostWithNoSubmittedFileName(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/upload")
			.method("POST")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
			.part("metadata", "{\"description\": \"foo\"}".getBytes())
			.build());
		String expectedContent = "$ http --multipart POST 'http://localhost/upload'"
				+ " 'metadata'='{\"description\": \"foo\"}'";
		assertThat(snippets.httpieRequest())
			.isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash").content(expectedContent));
	}

	@RenderedSnippetTest
	void multipartPostWithContentType(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/upload")
			.method("POST")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
			.part("image", new byte[0])
			.header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
			.submittedFileName("documents/images/example.png")
			.build());
		// httpie does not yet support manually set content type by part
		String expectedContent = "$ http --multipart POST 'http://localhost/upload'"
				+ " 'image'@'documents/images/example.png'";
		assertThat(snippets.httpieRequest())
			.isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash").content(expectedContent));
	}

	@RenderedSnippetTest
	void multipartPost(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/upload")
			.method("POST")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
			.part("image", new byte[0])
			.submittedFileName("documents/images/example.png")
			.build());
		String expectedContent = "$ http --multipart POST 'http://localhost/upload'"
				+ " 'image'@'documents/images/example.png'";
		assertThat(snippets.httpieRequest())
			.isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash").content(expectedContent));
	}

	@RenderedSnippetTest
	void basicAuthCredentialsAreSuppliedUsingAuthOption(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/foo")
			.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString("user:secret".getBytes()))
			.build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content("$ http --auth 'user:secret' GET 'http://localhost/foo'"));
	}

	@RenderedSnippetTest
	void customAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(operationBuilder.request("http://localhost/foo")
			.header(HttpHeaders.HOST, "api.example.com")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.header("a", "alpha")
			.build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content("$ http GET 'http://localhost/foo' 'Host:api.example.com'"
					+ " 'Content-Type:application/json' 'a:alpha'"));
	}

	@RenderedSnippetTest
	void deleteWithQueryString(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
			.document(operationBuilder.request("http://localhost/foo?a=alpha&b=bravo").method("DELETE").build());
		assertThat(snippets.httpieRequest()).isCodeBlock((codeBlock) -> codeBlock.withLanguage("bash")
			.content("$ http DELETE 'http://localhost/foo?a=alpha&b=bravo'"));
	}

}
