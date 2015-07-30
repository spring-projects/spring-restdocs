/*
 * Copyright 2014-2015 the original author or authors.
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

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.RestDocumentationRequestBuilders.fileUpload;
import static org.springframework.restdocs.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.test.SnippetMatchers.httpRequest;
import static org.springframework.restdocs.test.StubMvcResult.result;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.ExpectedSnippet;

/**
 * Tests for {@link HttpRequestSnippet}
 * 
 * @author Andy Wilkinson
 * @author Jonathan Pearlin
 *
 */
public class HttpRequestDocumentationHandlerTests {

	private static final String BOUNDARY = "6o2knFse3p53ty9dmcQvWAIx1zInP11uCfbm";

	@Rule
	public final ExpectedSnippet snippet = new ExpectedSnippet();

	@Test
	public void getRequest() throws IOException {
		this.snippet.expectHttpRequest("get-request").withContents(
				httpRequest(GET, "/foo").header(HttpHeaders.HOST, "localhost").header(
						"Alpha", "a"));

		new HttpRequestSnippet().document("get-request", result(get("/foo")
				.header("Alpha", "a")));
	}

	@Test
	public void getRequestWithQueryString() throws IOException {
		this.snippet.expectHttpRequest("get-request-with-query-string").withContents(
				httpRequest(GET, "/foo?bar=baz").header(HttpHeaders.HOST, "localhost"));

		new HttpRequestSnippet().document("get-request-with-query-string",
				result(get("/foo?bar=baz")));
	}

	@Test
	public void getRequestWithParameter() throws IOException {
		this.snippet.expectHttpRequest("get-request-with-parameter").withContents(
				httpRequest(GET, "/foo?b%26r=baz").header(HttpHeaders.HOST, "localhost"));

		new HttpRequestSnippet().document("get-request-with-parameter",
				result(get("/foo").param("b&r", "baz")));
	}

	@Test
	public void postRequestWithContent() throws IOException {
		this.snippet.expectHttpRequest("post-request-with-content").withContents(
				httpRequest(POST, "/foo").header(HttpHeaders.HOST, "localhost").content(
						"Hello, world"));

		new HttpRequestSnippet().document("post-request-with-content",
				result(post("/foo").content("Hello, world")));
	}

	@Test
	public void postRequestWithParameter() throws IOException {
		this.snippet.expectHttpRequest("post-request-with-parameter").withContents(
				httpRequest(POST, "/foo").header(HttpHeaders.HOST, "localhost")
						.header("Content-Type", "application/x-www-form-urlencoded")
						.content("b%26r=baz&a=alpha"));

		new HttpRequestSnippet().document("post-request-with-parameter",
				result(post("/foo").param("b&r", "baz").param("a", "alpha")));
	}

	@Test
	public void putRequestWithContent() throws IOException {
		this.snippet.expectHttpRequest("put-request-with-content").withContents(
				httpRequest(PUT, "/foo").header(HttpHeaders.HOST, "localhost").content(
						"Hello, world"));

		new HttpRequestSnippet().document("put-request-with-content",
				result(put("/foo").content("Hello, world")));
	}

	@Test
	public void putRequestWithParameter() throws IOException {
		this.snippet.expectHttpRequest("put-request-with-parameter").withContents(
				httpRequest(PUT, "/foo").header(HttpHeaders.HOST, "localhost")
						.header("Content-Type", "application/x-www-form-urlencoded")
						.content("b%26r=baz&a=alpha"));

		new HttpRequestSnippet().document("put-request-with-parameter",
				result(put("/foo").param("b&r", "baz").param("a", "alpha")));
	}

	@Test
	public void multipartPost() throws IOException {
		String expectedContent = createPart(String.format("Content-Disposition: "
				+ "form-data; " + "name=image%n%n<< data >>"));
		this.snippet.expectHttpRequest("multipart-post").withContents(
				httpRequest(POST, "/upload")
						.header(HttpHeaders.HOST, "localhost")
						.header("Content-Type",
								"multipart/form-data; boundary=" + BOUNDARY)
						.content(expectedContent));
		MockMultipartFile multipartFile = new MockMultipartFile("image",
				"documents/images/example.png", null, "<< data >>".getBytes());
		new HttpRequestSnippet().document("multipart-post",
				result(fileUpload("/upload").file(multipartFile)));
	}

	@Test
	public void multipartPostWithParameters() throws IOException {
		String param1Part = createPart(String.format("Content-Disposition: form-data; "
				+ "name=a%n%napple"), false);
		String param2Part = createPart(String.format("Content-Disposition: form-data; "
				+ "name=a%n%navocado"), false);
		String param3Part = createPart(String.format("Content-Disposition: form-data; "
				+ "name=b%n%nbanana"), false);
		String filePart = createPart(String.format("Content-Disposition: form-data; "
				+ "name=image%n%n<< data >>"));
		String expectedContent = param1Part + param2Part + param3Part + filePart;
		this.snippet.expectHttpRequest("multipart-post").withContents(
				httpRequest(POST, "/upload")
						.header(HttpHeaders.HOST, "localhost")
						.header("Content-Type",
								"multipart/form-data; boundary=" + BOUNDARY)
						.content(expectedContent));
		MockMultipartFile multipartFile = new MockMultipartFile("image",
				"documents/images/example.png", null, "<< data >>".getBytes());
		new HttpRequestSnippet().document(
				"multipart-post",
				result(fileUpload("/upload").file(multipartFile)
						.param("a", "apple", "avocado").param("b", "banana")));
	}

	@Test
	public void multipartPostWithContentType() throws IOException {
		String expectedContent = createPart(String
				.format("Content-Disposition: form-data; name=image%nContent-Type: "
						+ "image/png%n%n<< data >>"));
		this.snippet.expectHttpRequest("multipart-post-with-content-type").withContents(
				httpRequest(POST, "/upload")
						.header(HttpHeaders.HOST, "localhost")
						.header("Content-Type",
								"multipart/form-data; boundary=" + BOUNDARY)
						.content(expectedContent));
		MockMultipartFile multipartFile = new MockMultipartFile("image",
				"documents/images/example.png", MediaType.IMAGE_PNG_VALUE,
				"<< data >>".getBytes());
		new HttpRequestSnippet().document(
				"multipart-post-with-content-type",
				result(fileUpload("/upload").file(multipartFile)));
	}

	@Test
	public void getRequestWithCustomServerName() throws IOException {
		this.snippet.expectHttpRequest("get-request-custom-server-name").withContents(
				httpRequest(GET, "/foo").header(HttpHeaders.HOST, "api.example.com"));

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerName("api.example.com");

		new HttpRequestSnippet().document("get-request-custom-server-name",
				result(request));
	}

	@Test
	public void getRequestWithCustomHost() throws IOException {
		this.snippet.expectHttpRequest("get-request-custom-host").withContents(
				httpRequest(GET, "/foo").header(HttpHeaders.HOST, "api.example.com"));

		new HttpRequestSnippet().document("get-request-custom-host",
				result(get("/foo").header(HttpHeaders.HOST, "api.example.com")));
	}

	@Test
	public void requestWithCustomSnippetAttributes() throws IOException {
		this.snippet.expectHttpRequest("request-with-snippet-attributes").withContents(
				containsString("Title for the request"));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("http-request"))
				.thenReturn(
						new FileSystemResource(
								"src/test/resources/custom-snippet-templates/http-request-with-title.snippet"));
		request.setAttribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(
				resolver));
		new HttpRequestSnippet(attributes(key("title").value(
				"Title for the request"))).document("request-with-snippet-attributes",
				result(request));
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
