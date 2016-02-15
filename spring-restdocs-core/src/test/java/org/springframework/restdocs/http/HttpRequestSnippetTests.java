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

package org.springframework.restdocs.http;

import java.io.IOException;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link HttpRequestSnippet}.
 *
 * @author Andy Wilkinson
 * @author Jonathan Pearlin
 */
public class HttpRequestSnippetTests extends AbstractSnippetTests {

	private static final String BOUNDARY = "6o2knFse3p53ty9dmcQvWAIx1zInP11uCfbm";

	public HttpRequestSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void getRequest() throws IOException {
		this.snippet.expectHttpRequest("get-request")
				.withContents(httpRequest(RequestMethod.GET, "/foo").header("Alpha", "a")
						.header(HttpHeaders.HOST, "localhost"));

		new HttpRequestSnippet().document(operationBuilder("get-request")
				.request("http://localhost/foo").header("Alpha", "a").build());
	}

	@Test
	public void getRequestWithQueryString() throws IOException {
		this.snippet.expectHttpRequest("get-request-with-query-string")
				.withContents(httpRequest(RequestMethod.GET, "/foo?bar=baz")
						.header(HttpHeaders.HOST, "localhost"));

		new HttpRequestSnippet()
				.document(operationBuilder("get-request-with-query-string")
						.request("http://localhost/foo?bar=baz").build());
	}

	@Test
	public void getRequestWithQueryStringWithNoValue() throws IOException {
		this.snippet.expectHttpRequest("get-request-with-query-string-with-no-value")
				.withContents(httpRequest(RequestMethod.GET, "/foo?bar")
						.header(HttpHeaders.HOST, "localhost"));

		new HttpRequestSnippet()
				.document(operationBuilder("get-request-with-query-string-with-no-value")
						.request("http://localhost/foo?bar").build());
	}

	@Test
	public void postRequestWithContent() throws IOException {
		String content = "Hello, world";
		this.snippet.expectHttpRequest("post-request-with-content")
				.withContents(httpRequest(RequestMethod.POST, "/foo")
						.header(HttpHeaders.HOST, "localhost").content(content)
						.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));

		new HttpRequestSnippet().document(operationBuilder("post-request-with-content")
				.request("http://localhost/foo").method("POST").content(content).build());
	}

	@Test
	public void postRequestWithCharset() throws IOException {
		String japaneseContent = "\u30b3\u30f3\u30c6\u30f3\u30c4";
		byte[] contentBytes = japaneseContent.getBytes("UTF-8");
		this.snippet.expectHttpRequest("post-request-with-charset")
				.withContents(httpRequest(RequestMethod.POST, "/foo")
						.header("Content-Type", "text/plain;charset=UTF-8")
						.header(HttpHeaders.HOST, "localhost")
						.header(HttpHeaders.CONTENT_LENGTH, contentBytes.length)
						.content(japaneseContent));

		new HttpRequestSnippet().document(operationBuilder("post-request-with-charset")
				.request("http://localhost/foo").method("POST")
				.header("Content-Type", "text/plain;charset=UTF-8").content(contentBytes)
				.build());
	}

	@Test
	public void postRequestWithParameter() throws IOException {
		this.snippet.expectHttpRequest("post-request-with-parameter")
				.withContents(httpRequest(RequestMethod.POST, "/foo")
						.header(HttpHeaders.HOST, "localhost")
						.header("Content-Type", "application/x-www-form-urlencoded")
						.content("b%26r=baz&a=alpha"));

		new HttpRequestSnippet().document(operationBuilder("post-request-with-parameter")
				.request("http://localhost/foo").method("POST").param("b&r", "baz")
				.param("a", "alpha").build());
	}

	@Test
	public void postRequestWithParameterWithNoValue() throws IOException {
		this.snippet.expectHttpRequest("post-request-with-parameter")
				.withContents(httpRequest(RequestMethod.POST, "/foo")
						.header(HttpHeaders.HOST, "localhost")
						.header("Content-Type", "application/x-www-form-urlencoded")
						.content("bar="));

		new HttpRequestSnippet().document(operationBuilder("post-request-with-parameter")
				.request("http://localhost/foo").method("POST").param("bar").build());
	}

	@Test
	public void putRequestWithContent() throws IOException {
		String content = "Hello, world";
		this.snippet.expectHttpRequest("put-request-with-content")
				.withContents(httpRequest(RequestMethod.PUT, "/foo")
						.header(HttpHeaders.HOST, "localhost").content(content)
						.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));

		new HttpRequestSnippet().document(operationBuilder("put-request-with-content")
				.request("http://localhost/foo").method("PUT").content(content).build());
	}

	@Test
	public void putRequestWithParameter() throws IOException {
		this.snippet.expectHttpRequest("put-request-with-parameter")
				.withContents(httpRequest(RequestMethod.PUT, "/foo")
						.header(HttpHeaders.HOST, "localhost")
						.header("Content-Type", "application/x-www-form-urlencoded")
						.content("b%26r=baz&a=alpha"));

		new HttpRequestSnippet().document(operationBuilder("put-request-with-parameter")
				.request("http://localhost/foo").method("PUT").param("b&r", "baz")
				.param("a", "alpha").build());
	}

	@Test
	public void multipartPost() throws IOException {
		String expectedContent = createPart(String.format(
				"Content-Disposition: " + "form-data; " + "name=image%n%n<< data >>"));
		this.snippet
				.expectHttpRequest(
						"multipart-post")
				.withContents(httpRequest(RequestMethod.POST, "/upload")
						.header("Content-Type",
								"multipart/form-data; boundary=" + BOUNDARY)
						.header(HttpHeaders.HOST, "localhost").content(expectedContent));
		new HttpRequestSnippet()
				.document(operationBuilder("multipart-post")
						.request("http://localhost/upload").method("POST")
						.header(HttpHeaders.CONTENT_TYPE,
								MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", "<< data >>".getBytes()).build());
	}

	@Test
	public void multipartPostWithParameters() throws IOException {
		String param1Part = createPart(
				String.format("Content-Disposition: form-data; " + "name=a%n%napple"),
				false);
		String param2Part = createPart(
				String.format("Content-Disposition: form-data; " + "name=a%n%navocado"),
				false);
		String param3Part = createPart(
				String.format("Content-Disposition: form-data; " + "name=b%n%nbanana"),
				false);
		String filePart = createPart(String
				.format("Content-Disposition: form-data; " + "name=image%n%n<< data >>"));
		String expectedContent = param1Part + param2Part + param3Part + filePart;
		this.snippet
				.expectHttpRequest(
						"multipart-post-with-parameters")
				.withContents(httpRequest(RequestMethod.POST, "/upload")
						.header("Content-Type",
								"multipart/form-data; boundary=" + BOUNDARY)
						.header(HttpHeaders.HOST, "localhost").content(expectedContent));
		new HttpRequestSnippet()
				.document(operationBuilder("multipart-post-with-parameters")
						.request("http://localhost/upload").method("POST")
						.header(HttpHeaders.CONTENT_TYPE,
								MediaType.MULTIPART_FORM_DATA_VALUE)
						.param("a", "apple", "avocado").param("b", "banana")
						.part("image", "<< data >>".getBytes()).build());
	}

	@Test
	public void multipartPostWithParameterWithNoValue() throws IOException {
		String paramPart = createPart(
				String.format("Content-Disposition: form-data; " + "name=a%n"), false);
		String filePart = createPart(String
				.format("Content-Disposition: form-data; " + "name=image%n%n<< data >>"));
		String expectedContent = paramPart + filePart;
		this.snippet
				.expectHttpRequest(
						"multipart-post-with-parameter-with-no-value")
				.withContents(httpRequest(RequestMethod.POST, "/upload")
						.header("Content-Type",
								"multipart/form-data; boundary=" + BOUNDARY)
						.header(HttpHeaders.HOST, "localhost").content(expectedContent));
		new HttpRequestSnippet()
				.document(operationBuilder("multipart-post-with-parameter-with-no-value")
						.request("http://localhost/upload").method("POST")
						.header(HttpHeaders.CONTENT_TYPE,
								MediaType.MULTIPART_FORM_DATA_VALUE)
						.param("a").part("image", "<< data >>".getBytes()).build());
	}

	@Test
	public void multipartPostWithContentType() throws IOException {
		String expectedContent = createPart(
				String.format("Content-Disposition: form-data; name=image%nContent-Type: "
						+ "image/png%n%n<< data >>"));
		this.snippet
				.expectHttpRequest(
						"multipart-post-with-content-type")
				.withContents(httpRequest(RequestMethod.POST, "/upload")
						.header("Content-Type",
								"multipart/form-data; boundary=" + BOUNDARY)
						.header(HttpHeaders.HOST, "localhost").content(expectedContent));
		new HttpRequestSnippet()
				.document(operationBuilder("multipart-post-with-content-type")
						.request("http://localhost/upload").method("POST")
						.header(HttpHeaders.CONTENT_TYPE,
								MediaType.MULTIPART_FORM_DATA_VALUE)
						.part("image", "<< data >>".getBytes())
						.header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
						.build());
	}

	@Test
	public void getRequestWithCustomHost() throws IOException {
		this.snippet.expectHttpRequest("get-request-custom-host")
				.withContents(httpRequest(RequestMethod.GET, "/foo")
						.header(HttpHeaders.HOST, "api.example.com"));
		new HttpRequestSnippet().document(operationBuilder("get-request-custom-host")
				.request("http://localhost/foo")
				.header(HttpHeaders.HOST, "api.example.com").build());
	}

	@Test
	public void requestWithCustomSnippetAttributes() throws IOException {
		this.snippet.expectHttpRequest("request-with-snippet-attributes")
				.withContents(containsString("Title for the request"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("http-request"))
				.willReturn(snippetResource("http-request-with-title"));
		new HttpRequestSnippet(attributes(key("title").value("Title for the request")))
				.document(operationBuilder("request-with-snippet-attributes")
						.attribute(TemplateEngine.class.getName(),
								new MustacheTemplateEngine(resolver))
						.request("http://localhost/foo").build());
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
