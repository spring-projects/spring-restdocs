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
import org.springframework.http.MediaType;
import org.springframework.restdocs.testfixtures.jupiter.AssertableSnippets;
import org.springframework.restdocs.testfixtures.jupiter.OperationBuilder;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest;
import org.springframework.restdocs.testfixtures.jupiter.SnippetTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link HttpRequestSnippet}.
 *
 * @author Andy Wilkinson
 * @author Jonathan Pearlin
 */
class HttpRequestSnippetTests {

	private static final String BOUNDARY = "6o2knFse3p53ty9dmcQvWAIx1zInP11uCfbm";

	@RenderedSnippetTest
	void getRequest(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpRequestSnippet()
			.document(operationBuilder.request("http://localhost/foo").header("Alpha", "a").build());
		assertThat(snippets.httpRequest())
			.isHttpRequest((request) -> request.get("/foo").header("Alpha", "a").header(HttpHeaders.HOST, "localhost"));
	}

	@RenderedSnippetTest
	void getRequestWithQueryParameters(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpRequestSnippet()
			.document(operationBuilder.request("http://localhost/foo?b=bravo").header("Alpha", "a").build());
		assertThat(snippets.httpRequest()).isHttpRequest(
				(request) -> request.get("/foo?b=bravo").header("Alpha", "a").header(HttpHeaders.HOST, "localhost"));
	}

	@RenderedSnippetTest
	void getRequestWithPort(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpRequestSnippet()
			.document(operationBuilder.request("http://localhost:8080/foo").header("Alpha", "a").build());
		assertThat(snippets.httpRequest()).isHttpRequest(
				(request) -> request.get("/foo").header("Alpha", "a").header(HttpHeaders.HOST, "localhost:8080"));
	}

	@RenderedSnippetTest
	void getRequestWithCookies(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpRequestSnippet().document(operationBuilder.request("http://localhost/foo")
			.cookie("name1", "value1")
			.cookie("name2", "value2")
			.build());
		assertThat(snippets.httpRequest()).isHttpRequest((request) -> request.get("/foo")
			.header(HttpHeaders.HOST, "localhost")
			.header(HttpHeaders.COOKIE, "name1=value1; name2=value2"));
	}

	@RenderedSnippetTest
	void getRequestWithQueryString(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpRequestSnippet().document(operationBuilder.request("http://localhost/foo?bar=baz").build());
		assertThat(snippets.httpRequest())
			.isHttpRequest((request) -> request.get("/foo?bar=baz").header(HttpHeaders.HOST, "localhost"));
	}

	@RenderedSnippetTest
	void getRequestWithQueryStringWithNoValue(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpRequestSnippet().document(operationBuilder.request("http://localhost/foo?bar").build());
		assertThat(snippets.httpRequest())
			.isHttpRequest((request) -> request.get("/foo?bar").header(HttpHeaders.HOST, "localhost"));
	}

	@RenderedSnippetTest
	void postRequestWithContent(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		String content = "Hello, world";
		new HttpRequestSnippet()
			.document(operationBuilder.request("http://localhost/foo").method("POST").content(content).build());
		assertThat(snippets.httpRequest()).isHttpRequest((request) -> request.post("/foo")
			.header(HttpHeaders.HOST, "localhost")
			.content(content)
			.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));
	}

	@RenderedSnippetTest
	void postRequestWithContentAndQueryParameters(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		String content = "Hello, world";
		new HttpRequestSnippet()
			.document(operationBuilder.request("http://localhost/foo?a=alpha").method("POST").content(content).build());
		assertThat(snippets.httpRequest()).isHttpRequest((request) -> request.post("/foo?a=alpha")
			.header(HttpHeaders.HOST, "localhost")
			.content(content)
			.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));

	}

	@RenderedSnippetTest
	void postRequestWithCharset(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		String japaneseContent = "\u30b3\u30f3\u30c6\u30f3\u30c4";
		byte[] contentBytes = japaneseContent.getBytes("UTF-8");
		new HttpRequestSnippet().document(operationBuilder.request("http://localhost/foo")
			.method("POST")
			.header("Content-Type", "text/plain;charset=UTF-8")
			.content(contentBytes)
			.build());
		assertThat(snippets.httpRequest()).isHttpRequest((request) -> request.post("/foo")
			.header("Content-Type", "text/plain;charset=UTF-8")
			.header(HttpHeaders.HOST, "localhost")
			.header(HttpHeaders.CONTENT_LENGTH, contentBytes.length)
			.content(japaneseContent));
	}

	@RenderedSnippetTest
	void putRequestWithContent(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		String content = "Hello, world";
		new HttpRequestSnippet()
			.document(operationBuilder.request("http://localhost/foo").method("PUT").content(content).build());
		assertThat(snippets.httpRequest()).isHttpRequest((request) -> request.put("/foo")
			.header(HttpHeaders.HOST, "localhost")
			.content(content)
			.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));
	}

	@RenderedSnippetTest
	void multipartPost(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpRequestSnippet().document(operationBuilder.request("http://localhost/upload")
			.method("POST")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
			.part("image", "<< data >>".getBytes())
			.build());
		String expectedContent = createPart(
				String.format("Content-Disposition: " + "form-data; " + "name=image%n%n<< data >>"));
		assertThat(snippets.httpRequest()).isHttpRequest((request) -> request.post("/upload")
			.header("Content-Type", "multipart/form-data; boundary=" + BOUNDARY)
			.header(HttpHeaders.HOST, "localhost")
			.content(expectedContent));
	}

	@RenderedSnippetTest
	void multipartPut(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpRequestSnippet().document(operationBuilder.request("http://localhost/upload")
			.method("PUT")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
			.part("image", "<< data >>".getBytes())
			.build());
		String expectedContent = createPart(
				String.format("Content-Disposition: " + "form-data; " + "name=image%n%n<< data >>"));
		assertThat(snippets.httpRequest()).isHttpRequest((request) -> request.put("/upload")
			.header("Content-Type", "multipart/form-data; boundary=" + BOUNDARY)
			.header(HttpHeaders.HOST, "localhost")
			.content(expectedContent));
	}

	@RenderedSnippetTest
	void multipartPatch(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpRequestSnippet().document(operationBuilder.request("http://localhost/upload")
			.method("PATCH")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
			.part("image", "<< data >>".getBytes())
			.build());
		String expectedContent = createPart(
				String.format("Content-Disposition: " + "form-data; " + "name=image%n%n<< data >>"));
		assertThat(snippets.httpRequest()).isHttpRequest((request) -> request.patch("/upload")
			.header("Content-Type", "multipart/form-data; boundary=" + BOUNDARY)
			.header(HttpHeaders.HOST, "localhost")
			.content(expectedContent));
	}

	@RenderedSnippetTest
	void multipartPostWithFilename(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpRequestSnippet().document(operationBuilder.request("http://localhost/upload")
			.method("POST")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
			.part("image", "<< data >>".getBytes())
			.submittedFileName("image.png")
			.build());
		String expectedContent = createPart(String
			.format("Content-Disposition: " + "form-data; " + "name=image; filename=image.png%n%n<< data >>"));
		assertThat(snippets.httpRequest()).isHttpRequest((request) -> request.post("/upload")
			.header("Content-Type", "multipart/form-data; boundary=" + BOUNDARY)
			.header(HttpHeaders.HOST, "localhost")
			.content(expectedContent));
	}

	@RenderedSnippetTest
	void multipartPostWithContentType(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpRequestSnippet().document(operationBuilder.request("http://localhost/upload")
			.method("POST")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
			.part("image", "<< data >>".getBytes())
			.header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
			.build());
		String expectedContent = createPart(String
			.format("Content-Disposition: form-data; name=image%nContent-Type: " + "image/png%n%n<< data >>"));
		assertThat(snippets.httpRequest()).isHttpRequest((request) -> request.post("/upload")
			.header("Content-Type", "multipart/form-data; boundary=" + BOUNDARY)
			.header(HttpHeaders.HOST, "localhost")
			.content(expectedContent));
	}

	@RenderedSnippetTest
	void getRequestWithCustomHost(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpRequestSnippet().document(
				operationBuilder.request("http://localhost/foo").header(HttpHeaders.HOST, "api.example.com").build());
		assertThat(snippets.httpRequest())
			.isHttpRequest((request) -> request.get("/foo").header(HttpHeaders.HOST, "api.example.com"));
	}

	@RenderedSnippetTest
	@SnippetTemplate(snippet = "http-request", template = "http-request-with-title")
	void requestWithCustomSnippetAttributes(OperationBuilder operationBuilder, AssertableSnippets snippets)
			throws IOException {
		new HttpRequestSnippet(attributes(key("title").value("Title for the request")))
			.document(operationBuilder.request("http://localhost/foo").build());
		assertThat(snippets.httpRequest()).contains("Title for the request");
	}

	@RenderedSnippetTest
	void deleteWithQueryString(OperationBuilder operationBuilder, AssertableSnippets snippets) throws IOException {
		new HttpRequestSnippet()
			.document(operationBuilder.request("http://localhost/foo?a=alpha&b=bravo").method("DELETE").build());
		assertThat(snippets.httpRequest())
			.isHttpRequest((request) -> request.delete("/foo?a=alpha&b=bravo").header("Host", "localhost"));
	}

	@RenderedSnippetTest
	void postRequestWithNoContentTypeAndNoBodyDoesNotIncludeContentTypeHeader(OperationBuilder operationBuilder,
			AssertableSnippets snippets) throws IOException {
		new HttpRequestSnippet().document(operationBuilder.request("http://localhost/foo").method("POST").build());
		assertThat(snippets.httpRequest())
			.isHttpRequest((request) -> request.post("/foo").header(HttpHeaders.HOST, "localhost"));
	}

	private String createPart(String content) {
		return this.createPart(content, true);
	}

	private String createPart(String content, boolean last) {
		StringBuilder part = new StringBuilder();
		part.append(String.format("--%s%n%s%n", BOUNDARY, content));
		if (last) {
			part.append(String.format("--%s--", BOUNDARY));
		}
		return part.toString();
	}

}
