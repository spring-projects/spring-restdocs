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

package org.springframework.restdocs.curl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.RestDocumentationRequestBuilders.fileUpload;
import static org.springframework.restdocs.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.test.SnippetMatchers.codeBlock;
import static org.springframework.restdocs.test.StubMvcResult.result;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.ExpectedSnippet;

/**
 * Tests for {@link CurlRequestSnippet}
 *
 * @author Andy Wilkinson
 * @author Yann Le Guern
 * @author Dmitriy Mayboroda
 * @author Jonathan Pearlin
 */
public class CurlRequestSnippetTests {

	@Rule
	public ExpectedSnippet snippet = new ExpectedSnippet();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void getRequest() throws IOException {
		this.snippet.expectCurlRequest("get-request").withContents(
				codeBlock("bash").content("$ curl 'http://localhost/foo' -i"));
		new CurlRequestSnippet()
				.document("get-request", result(get("/foo")));
	}

	@Test
	public void nonGetRequest() throws IOException {
		this.snippet.expectCurlRequest("non-get-request").withContents(
				codeBlock("bash").content("$ curl 'http://localhost/foo' -i -X POST"));
		new CurlRequestSnippet().document("non-get-request",
				result(post("/foo")));
	}

	@Test
	public void requestWithContent() throws IOException {
		this.snippet.expectCurlRequest("request-with-content").withContents(
				codeBlock("bash")
						.content("$ curl 'http://localhost/foo' -i -d 'content'"));
		new CurlRequestSnippet().document("request-with-content",
				result(get("/foo").content("content")));
	}

	@Test
	public void requestWithQueryString() throws IOException {
		this.snippet.expectCurlRequest("request-with-query-string")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo?param=value' -i"));
		new CurlRequestSnippet().document("request-with-query-string",
				result(get("/foo?param=value")));
	}

	@Test
	public void requestWithOneParameter() throws IOException {
		this.snippet.expectCurlRequest("request-with-one-parameter").withContents(
				codeBlock("bash").content("$ curl 'http://localhost/foo?k1=v1' -i"));
		new CurlRequestSnippet().document("request-with-one-parameter",
				result(get("/foo").param("k1", "v1")));
	}

	@Test
	public void requestWithMultipleParameters() throws IOException {
		this.snippet.expectCurlRequest("request-with-multiple-parameters").withContents(
				codeBlock("bash").content(
						"$ curl 'http://localhost/foo?k1=v1&k1=v1-bis&k2=v2' -i"));
		new CurlRequestSnippet().document(
				"request-with-multiple-parameters", result(get("/foo").param("k1", "v1")
						.param("k2", "v2").param("k1", "v1-bis")));
	}

	@Test
	public void requestWithUrlEncodedParameter() throws IOException {
		this.snippet.expectCurlRequest("request-with-url-encoded-parameter")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo?k1=foo+bar%26' -i"));
		new CurlRequestSnippet().document(
				"request-with-url-encoded-parameter",
				result(get("/foo").param("k1", "foo bar&")));
	}

	@Test
	public void postRequestWithOneParameter() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-one-parameter").withContents(
				codeBlock("bash").content(
						"$ curl 'http://localhost/foo' -i -X POST -d 'k1=v1'"));
		new CurlRequestSnippet().document("post-request-with-one-parameter",
				result(post("/foo").param("k1", "v1")));
	}

	@Test
	public void postRequestWithMultipleParameters() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-multiple-parameters")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo' -i -X POST"
										+ " -d 'k1=v1&k1=v1-bis&k2=v2'"));
		new CurlRequestSnippet().document(
				"post-request-with-multiple-parameters",
				result(post("/foo").param("k1", "v1", "v1-bis").param("k2", "v2")));
	}

	@Test
	public void postRequestWithUrlEncodedParameter() throws IOException {
		this.snippet
				.expectCurlRequest("post-request-with-url-encoded-parameter")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo' -i -X POST -d 'k1=a%26b'"));
		new CurlRequestSnippet().document(
				"post-request-with-url-encoded-parameter",
				result(post("/foo").param("k1", "a&b")));
	}

	@Test
	public void putRequestWithOneParameter() throws IOException {
		this.snippet.expectCurlRequest("put-request-with-one-parameter").withContents(
				codeBlock("bash").content(
						"$ curl 'http://localhost/foo' -i -X PUT -d 'k1=v1'"));
		new CurlRequestSnippet().document("put-request-with-one-parameter",
				result(put("/foo").param("k1", "v1")));
	}

	@Test
	public void putRequestWithMultipleParameters() throws IOException {
		this.snippet.expectCurlRequest("put-request-with-multiple-parameters")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo' -i -X PUT"
										+ " -d 'k1=v1&k1=v1-bis&k2=v2'"));
		new CurlRequestSnippet().document(
				"put-request-with-multiple-parameters",
				result(put("/foo").param("k1", "v1", "v1-bis").param("k2", "v2")));
	}

	@Test
	public void putRequestWithUrlEncodedParameter() throws IOException {
		this.snippet.expectCurlRequest("put-request-with-url-encoded-parameter")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo' -i -X PUT -d 'k1=a%26b'"));
		new CurlRequestSnippet().document(
				"put-request-with-url-encoded-parameter",
				result(put("/foo").param("k1", "a&b")));
	}

	@Test
	public void requestWithHeaders() throws IOException {
		this.snippet.expectCurlRequest("request-with-headers").withContents(
				codeBlock("bash").content(
						"$ curl 'http://localhost/foo' -i"
								+ " -H 'Content-Type: application/json' -H 'a: alpha'"));
		new CurlRequestSnippet().document(
				"request-with-headers",
				result(get("/foo").contentType(MediaType.APPLICATION_JSON).header("a",
						"alpha")));
	}

	@Test
	public void httpWithNonStandardPort() throws IOException {
		this.snippet.expectCurlRequest("http-with-non-standard-port").withContents(
				codeBlock("bash").content("$ curl 'http://localhost:8080/foo' -i"));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerPort(8080);
		new CurlRequestSnippet().document("http-with-non-standard-port",
				result(request));
	}

	@Test
	public void httpsWithStandardPort() throws IOException {
		this.snippet.expectCurlRequest("https-with-standard-port").withContents(
				codeBlock("bash").content("$ curl 'https://localhost/foo' -i"));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerPort(443);
		request.setScheme("https");
		new CurlRequestSnippet().document("https-with-standard-port",
				result(request));
	}

	@Test
	public void httpsWithNonStandardPort() throws IOException {
		this.snippet.expectCurlRequest("https-with-non-standard-port").withContents(
				codeBlock("bash").content("$ curl 'https://localhost:8443/foo' -i"));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerPort(8443);
		request.setScheme("https");
		new CurlRequestSnippet().document("https-with-non-standard-port",
				result(request));
	}

	@Test
	public void requestWithCustomHost() throws IOException {
		this.snippet.expectCurlRequest("request-with-custom-host").withContents(
				codeBlock("bash").content("$ curl 'http://api.example.com/foo' -i"));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerName("api.example.com");
		new CurlRequestSnippet().document("request-with-custom-host",
				result(request));
	}

	@Test
	public void requestWithContextPathWithSlash() throws IOException {
		this.snippet.expectCurlRequest("request-with-custom-context-with-slash")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://api.example.com/v3/foo' -i"));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerName("api.example.com");
		request.setContextPath("/v3");
		new CurlRequestSnippet().document(
				"request-with-custom-context-with-slash", result(request));
	}

	@Test
	public void requestWithContextPathWithoutSlash() throws IOException {
		this.snippet.expectCurlRequest("request-with-custom-context-without-slash")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://api.example.com/v3/foo' -i"));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerName("api.example.com");
		request.setContextPath("v3");
		new CurlRequestSnippet().document(
				"request-with-custom-context-without-slash", result(request));
	}

	@Test
	public void multipartPostWithNoOriginalFilename() throws IOException {
		String expectedContent = "$ curl 'http://localhost/upload' -i -X POST -H "
				+ "'Content-Type: multipart/form-data' -F "
				+ "'metadata={\"description\": \"foo\"}'";
		this.snippet.expectCurlRequest("multipart-post-no-original-filename")
				.withContents(codeBlock("bash").content(expectedContent));
		MockMultipartFile multipartFile = new MockMultipartFile("metadata",
				"{\"description\": \"foo\"}".getBytes());
		new CurlRequestSnippet().document(
				"multipart-post-no-original-filename",
				result(fileUpload("/upload").file(multipartFile)));
	}

	@Test
	public void multipartPostWithContentType() throws IOException {
		String expectedContent = "$ curl 'http://localhost/upload' -i -X POST -H "
				+ "'Content-Type: multipart/form-data' -F "
				+ "'image=@documents/images/example.png;type=image/png'";
		this.snippet.expectCurlRequest("multipart-post-with-content-type").withContents(
				codeBlock("bash").content(expectedContent));
		MockMultipartFile multipartFile = new MockMultipartFile("image",
				"documents/images/example.png", MediaType.IMAGE_PNG_VALUE,
				"bytes".getBytes());
		new CurlRequestSnippet().document(
				"multipart-post-with-content-type",
				result(fileUpload("/upload").file(multipartFile)));
	}

	@Test
	public void multipartPost() throws IOException {
		String expectedContent = "$ curl 'http://localhost/upload' -i -X POST -H "
				+ "'Content-Type: multipart/form-data' -F "
				+ "'image=@documents/images/example.png'";
		this.snippet.expectCurlRequest("multipart-post").withContents(
				codeBlock("bash").content(expectedContent));
		MockMultipartFile multipartFile = new MockMultipartFile("image",
				"documents/images/example.png", null, "bytes".getBytes());
		new CurlRequestSnippet().document("multipart-post",
				result(fileUpload("/upload").file(multipartFile)));
	}

	@Test
	public void multipartPostWithParameters() throws IOException {
		String expectedContent = "$ curl 'http://localhost/upload' -i -X POST -H "
				+ "'Content-Type: multipart/form-data' -F "
				+ "'image=@documents/images/example.png' -F 'a=apple' -F 'a=avocado' "
				+ "-F 'b=banana'";
		this.snippet.expectCurlRequest("multipart-post").withContents(
				codeBlock("bash").content(expectedContent));
		MockMultipartFile multipartFile = new MockMultipartFile("image",
				"documents/images/example.png", null, "bytes".getBytes());
		new CurlRequestSnippet().document(
				"multipart-post",
				result(fileUpload("/upload").file(multipartFile)
						.param("a", "apple", "avocado").param("b", "banana")));
	}

	@Test
	public void customAttributes() throws IOException {
		this.snippet.expectCurlRequest("custom-attributes").withContents(
				containsString("curl request title"));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		when(resolver.resolveTemplateResource("curl-request"))
				.thenReturn(
						new FileSystemResource(
								"src/test/resources/custom-snippet-templates/curl-request-with-title.snippet"));
		request.setAttribute(TemplateEngine.class.getName(), new MustacheTemplateEngine(
				resolver));
		new CurlRequestSnippet(attributes(key("title").value(
				"curl request title"))).document("custom-attributes", result(request));
	}

}
