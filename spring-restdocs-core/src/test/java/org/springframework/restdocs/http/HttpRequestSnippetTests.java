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
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.GET, "/foo").header("Alpha", "a")
						.header(HttpHeaders.HOST, "localhost"));

		new HttpRequestSnippet().document(this.operationBuilder
				.request("http://localhost/foo").header("Alpha", "a").build());
	}

	@Test
	public void getRequestWithParameters() throws IOException {
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.GET, "/foo?b=bravo")
						.header("Alpha", "a").header(HttpHeaders.HOST, "localhost"));

		new HttpRequestSnippet()
				.document(this.operationBuilder.request("http://localhost/foo")
						.header("Alpha", "a").param("b", "bravo").build());
	}

	@Test
	public void getRequestWithPort() throws IOException {
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.GET, "/foo").header("Alpha", "a")
						.header(HttpHeaders.HOST, "localhost:8080"));

		new HttpRequestSnippet().document(this.operationBuilder
				.request("http://localhost:8080/foo").header("Alpha", "a").build());
	}

	@Test
	public void getRequestWithCookies() throws IOException {
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.GET, "/foo")
						.header(HttpHeaders.HOST, "localhost")
						.header(HttpHeaders.COOKIE, "name1=value1")
						.header(HttpHeaders.COOKIE, "name2=value2"));

		new HttpRequestSnippet()
				.document(this.operationBuilder.request("http://localhost/foo")
						.cookie("name1", "value1").cookie("name2", "value2").build());
	}

	@Test
	public void getRequestWithQueryString() throws IOException {
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.GET, "/foo?bar=baz")
						.header(HttpHeaders.HOST, "localhost"));

		new HttpRequestSnippet().document(
				this.operationBuilder.request("http://localhost/foo?bar=baz").build());
	}

	@Test
	public void getRequestWithQueryStringWithNoValue() throws IOException {
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.GET, "/foo?bar")
						.header(HttpHeaders.HOST, "localhost"));

		new HttpRequestSnippet().document(
				this.operationBuilder.request("http://localhost/foo?bar").build());
	}

	@Test
	public void getWithPartiallyOverlappingQueryStringAndParameters() throws IOException {
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.GET, "/foo?a=alpha&b=bravo")
						.header(HttpHeaders.HOST, "localhost"));

		new HttpRequestSnippet()
				.document(this.operationBuilder.request("http://localhost/foo?a=alpha")
						.param("a", "alpha").param("b", "bravo").build());
	}

	@Test
	public void getWithTotallyOverlappingQueryStringAndParameters() throws IOException {
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.GET, "/foo?a=alpha&b=bravo")
						.header(HttpHeaders.HOST, "localhost"));

		new HttpRequestSnippet().document(
				this.operationBuilder.request("http://localhost/foo?a=alpha&b=bravo")
						.param("a", "alpha").param("b", "bravo").build());
	}

	@Test
	public void postRequestWithContent() throws IOException {
		String content = "Hello, world";
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/foo")
						.header(HttpHeaders.HOST, "localhost").content(content)
						.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));

		new HttpRequestSnippet().document(this.operationBuilder
				.request("http://localhost/foo").method("POST").content(content).build());
	}

	@Test
	public void postRequestWithContentAndParameters() throws IOException {
		String content = "Hello, world";
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/foo?a=alpha")
						.header(HttpHeaders.HOST, "localhost").content(content)
						.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));

		new HttpRequestSnippet()
				.document(this.operationBuilder.request("http://localhost/foo")
						.method("POST").param("a", "alpha").content(content).build());
	}

	@Test
	public void postRequestWithContentAndDisjointQueryStringAndParameters()
			throws IOException {
		String content = "Hello, world";
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/foo?b=bravo&a=alpha")
						.header(HttpHeaders.HOST, "localhost").content(content)
						.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));

		new HttpRequestSnippet()
				.document(this.operationBuilder.request("http://localhost/foo?b=bravo")
						.method("POST").param("a", "alpha").content(content).build());
	}

	@Test
	public void postRequestWithContentAndPartiallyOverlappingQueryStringAndParameters()
			throws IOException {
		String content = "Hello, world";
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/foo?b=bravo&a=alpha")
						.header(HttpHeaders.HOST, "localhost").content(content)
						.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));

		new HttpRequestSnippet().document(this.operationBuilder
				.request("http://localhost/foo?b=bravo").method("POST")
				.param("a", "alpha").param("b", "bravo").content(content).build());
	}

	@Test
	public void postRequestWithContentAndTotallyOverlappingQueryStringAndParameters()
			throws IOException {
		String content = "Hello, world";
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/foo?b=bravo&a=alpha")
						.header(HttpHeaders.HOST, "localhost").content(content)
						.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));

		new HttpRequestSnippet().document(this.operationBuilder
				.request("http://localhost/foo?b=bravo&a=alpha").method("POST")
				.param("a", "alpha").param("b", "bravo").content(content).build());
	}

	@Test
	public void postRequestWithOverlappingParametersAndFormUrlEncodedBody()
			throws IOException {
		String content = "a=alpha&b=bravo";
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/foo")
						.header(HttpHeaders.CONTENT_TYPE,
								MediaType.APPLICATION_FORM_URLENCODED_VALUE)
						.header(HttpHeaders.HOST, "localhost").content(content)
						.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));
		new HttpRequestSnippet().document(this.operationBuilder
				.request("http://localhost/foo").method("POST").content("a=alpha&b=bravo")
				.header(HttpHeaders.CONTENT_TYPE,
						MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("a", "alpha").param("b", "bravo").build());
	}

	@Test
	public void postRequestWithCharset() throws IOException {
		String japaneseContent = "\u30b3\u30f3\u30c6\u30f3\u30c4";
		byte[] contentBytes = japaneseContent.getBytes("UTF-8");
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/foo")
						.header("Content-Type", "text/plain;charset=UTF-8")
						.header(HttpHeaders.HOST, "localhost")
						.header(HttpHeaders.CONTENT_LENGTH, contentBytes.length)
						.content(japaneseContent));

		new HttpRequestSnippet()
				.document(this.operationBuilder.request("http://localhost/foo")
						.method("POST").header("Content-Type", "text/plain;charset=UTF-8")
						.content(contentBytes).build());
	}

	@Test
	public void postRequestWithParameter() throws IOException {
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/foo")
						.header(HttpHeaders.HOST, "localhost")
						.header("Content-Type", "application/x-www-form-urlencoded")
						.content("b%26r=baz&a=alpha"));

		new HttpRequestSnippet()
				.document(this.operationBuilder.request("http://localhost/foo")
						.method("POST").param("b&r", "baz").param("a", "alpha").build());
	}

	@Test
	public void postRequestWithParameterWithNoValue() throws IOException {
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/foo")
						.header(HttpHeaders.HOST, "localhost")
						.header("Content-Type", "application/x-www-form-urlencoded")
						.content("bar="));

		new HttpRequestSnippet().document(this.operationBuilder
				.request("http://localhost/foo").method("POST").param("bar").build());
	}

	@Test
	public void putRequestWithContent() throws IOException {
		String content = "Hello, world";
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.PUT, "/foo")
						.header(HttpHeaders.HOST, "localhost").content(content)
						.header(HttpHeaders.CONTENT_LENGTH, content.getBytes().length));

		new HttpRequestSnippet().document(this.operationBuilder
				.request("http://localhost/foo").method("PUT").content(content).build());
	}

	@Test
	public void putRequestWithParameter() throws IOException {
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.PUT, "/foo")
						.header(HttpHeaders.HOST, "localhost")
						.header("Content-Type", "application/x-www-form-urlencoded")
						.content("b%26r=baz&a=alpha"));

		new HttpRequestSnippet()
				.document(this.operationBuilder.request("http://localhost/foo")
						.method("PUT").param("b&r", "baz").param("a", "alpha").build());
	}

	@Test
	public void multipartPost() throws IOException {
		String expectedContent = createPart(String.format(
				"Content-Disposition: " + "form-data; " + "name=image%n%n<< data >>"));
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/upload")
						.header("Content-Type",
								"multipart/form-data; boundary=" + BOUNDARY)
						.header(HttpHeaders.HOST, "localhost").content(expectedContent));
		new HttpRequestSnippet().document(this.operationBuilder
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", "<< data >>".getBytes()).build());
	}

	@Test
	public void multipartPostWithFilename() throws IOException {
		String expectedContent = createPart(String.format("Content-Disposition: "
				+ "form-data; " + "name=image; filename=image.png%n%n<< data >>"));
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/upload")
						.header("Content-Type",
								"multipart/form-data; boundary=" + BOUNDARY)
						.header(HttpHeaders.HOST, "localhost").content(expectedContent));
		new HttpRequestSnippet().document(this.operationBuilder
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", "<< data >>".getBytes()).submittedFileName("image.png")
				.build());
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
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/upload")
						.header("Content-Type",
								"multipart/form-data; boundary=" + BOUNDARY)
						.header(HttpHeaders.HOST, "localhost").content(expectedContent));
		new HttpRequestSnippet().document(this.operationBuilder
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
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
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/upload")
						.header("Content-Type",
								"multipart/form-data; boundary=" + BOUNDARY)
						.header(HttpHeaders.HOST, "localhost").content(expectedContent));
		new HttpRequestSnippet().document(this.operationBuilder
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.param("a").part("image", "<< data >>".getBytes()).build());
	}

	@Test
	public void multipartPostWithContentType() throws IOException {
		String expectedContent = createPart(
				String.format("Content-Disposition: form-data; name=image%nContent-Type: "
						+ "image/png%n%n<< data >>"));
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.POST, "/upload")
						.header("Content-Type",
								"multipart/form-data; boundary=" + BOUNDARY)
						.header(HttpHeaders.HOST, "localhost").content(expectedContent));
		new HttpRequestSnippet().document(this.operationBuilder
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", "<< data >>".getBytes())
				.header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE).build());
	}

	@Test
	public void getRequestWithCustomHost() throws IOException {
		this.snippets.expectHttpRequest()
				.withContents(httpRequest(RequestMethod.GET, "/foo")
						.header(HttpHeaders.HOST, "api.example.com"));
		new HttpRequestSnippet()
				.document(this.operationBuilder.request("http://localhost/foo")
						.header(HttpHeaders.HOST, "api.example.com").build());
	}

	@Test
	public void requestWithCustomSnippetAttributes() throws IOException {
		this.snippets.expectHttpRequest()
				.withContents(containsString("Title for the request"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("http-request"))
				.willReturn(snippetResource("http-request-with-title"));
		new HttpRequestSnippet(attributes(key("title").value("Title for the request")))
				.document(this.operationBuilder
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
