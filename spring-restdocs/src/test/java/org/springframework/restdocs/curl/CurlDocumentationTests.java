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

import static org.springframework.restdocs.curl.CurlDocumentation.documentCurlRequest;
import static org.springframework.restdocs.test.SnippetMatchers.codeBlock;
import static org.springframework.restdocs.test.StubMvcResult.result;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.test.ExpectedSnippet;

/**
 * Tests for {@link CurlDocumentation}
 *
 * @author Andy Wilkinson
 * @author Yann Le Guern
 * @author Dmitriy Mayboroda
 * @author Jonathan Pearlin
 */
public class CurlDocumentationTests {

	@Rule
	public ExpectedSnippet snippet = new ExpectedSnippet();

	@Test
	public void getRequest() throws IOException {
		this.snippet.expectCurlRequest("get-request").withContents(
				codeBlock("bash").content("$ curl 'http://localhost/foo' -i"));
		documentCurlRequest("get-request").handle(result(get("/foo")));
	}

	@Test
	public void nonGetRequest() throws IOException {
		this.snippet.expectCurlRequest("non-get-request").withContents(
				codeBlock("bash").content("$ curl 'http://localhost/foo' -i -X POST"));
		documentCurlRequest("non-get-request").handle(result(post("/foo")));
	}

	@Test
	public void requestWithContent() throws IOException {
		this.snippet.expectCurlRequest("request-with-content").withContents(
				codeBlock("bash")
						.content("$ curl 'http://localhost/foo' -i -d 'content'"));
		documentCurlRequest("request-with-content").handle(
				result(get("/foo").content("content")));
	}

	@Test
	public void requestWithQueryString() throws IOException {
		this.snippet.expectCurlRequest("request-with-query-string")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo?param=value' -i"));
		documentCurlRequest("request-with-query-string").handle(
				result(get("/foo?param=value")));
	}

	@Test
	public void requestWithOneParameter() throws IOException {
		this.snippet.expectCurlRequest("request-with-one-parameter").withContents(
				codeBlock("bash").content("$ curl 'http://localhost/foo?k1=v1' -i"));
		documentCurlRequest("request-with-one-parameter").handle(
				result(get("/foo").param("k1", "v1")));
	}

	@Test
	public void requestWithMultipleParameters() throws IOException {
		this.snippet.expectCurlRequest("request-with-multiple-parameters").withContents(
				codeBlock("bash").content(
						"$ curl 'http://localhost/foo?k1=v1&k1=v1-bis&k2=v2' -i"));
		documentCurlRequest("request-with-multiple-parameters").handle(
				result(get("/foo").param("k1", "v1").param("k2", "v2")
						.param("k1", "v1-bis")));
	}

	@Test
	public void requestWithUrlEncodedParameter() throws IOException {
		this.snippet.expectCurlRequest("request-with-url-encoded-parameter")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo?k1=foo+bar%26' -i"));
		documentCurlRequest("request-with-url-encoded-parameter").handle(
				result(get("/foo").param("k1", "foo bar&")));
	}

	@Test
	public void postRequestWithOneParameter() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-one-parameter").withContents(
				codeBlock("bash").content(
						"$ curl 'http://localhost/foo' -i -X POST -d 'k1=v1'"));
		documentCurlRequest("post-request-with-one-parameter").handle(
				result(post("/foo").param("k1", "v1")));
	}

	@Test
	public void postRequestWithMultipleParameters() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-multiple-parameters")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo' -i -X POST"
										+ " -d 'k1=v1&k1=v1-bis&k2=v2'"));
		documentCurlRequest("post-request-with-multiple-parameters").handle(
				result(post("/foo").param("k1", "v1", "v1-bis").param("k2", "v2")));
	}

	@Test
	public void postRequestWithUrlEncodedParameter() throws IOException {
		this.snippet
				.expectCurlRequest("post-request-with-url-encoded-parameter")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo' -i -X POST -d 'k1=a%26b'"));
		documentCurlRequest("post-request-with-url-encoded-parameter").handle(
				result(post("/foo").param("k1", "a&b")));
	}

	@Test
	public void putRequestWithOneParameter() throws IOException {
		this.snippet.expectCurlRequest("put-request-with-one-parameter").withContents(
				codeBlock("bash").content(
						"$ curl 'http://localhost/foo' -i -X PUT -d 'k1=v1'"));
		documentCurlRequest("put-request-with-one-parameter").handle(
				result(put("/foo").param("k1", "v1")));
	}

	@Test
	public void putRequestWithMultipleParameters() throws IOException {
		this.snippet.expectCurlRequest("put-request-with-multiple-parameters")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo' -i -X PUT"
										+ " -d 'k1=v1&k1=v1-bis&k2=v2'"));
		documentCurlRequest("put-request-with-multiple-parameters").handle(
				result(put("/foo").param("k1", "v1", "v1-bis").param("k2", "v2")));
	}

	@Test
	public void putRequestWithUrlEncodedParameter() throws IOException {
		this.snippet.expectCurlRequest("put-request-with-url-encoded-parameter")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo' -i -X PUT -d 'k1=a%26b'"));
		documentCurlRequest("put-request-with-url-encoded-parameter").handle(
				result(put("/foo").param("k1", "a&b")));
	}

	@Test
	public void requestWithHeaders() throws IOException {
		this.snippet.expectCurlRequest("request-with-headers").withContents(
				codeBlock("bash").content(
						"$ curl 'http://localhost/foo' -i"
								+ " -H 'Content-Type: application/json' -H 'a: alpha'"));
		documentCurlRequest("request-with-headers").handle(
				result(get("/foo").contentType(MediaType.APPLICATION_JSON).header("a",
						"alpha")));
	}

	@Test
	public void httpWithNonStandardPort() throws IOException {
		this.snippet.expectCurlRequest("http-with-non-standard-port").withContents(
				codeBlock("bash").content("$ curl 'http://localhost:8080/foo' -i"));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerPort(8080);
		documentCurlRequest("http-with-non-standard-port").handle(result(request));
	}

	@Test
	public void httpsWithStandardPort() throws IOException {
		this.snippet.expectCurlRequest("https-with-standard-port").withContents(
				codeBlock("bash").content("$ curl 'https://localhost/foo' -i"));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerPort(443);
		request.setScheme("https");
		documentCurlRequest("https-with-standard-port").handle(result(request));
	}

	@Test
	public void httpsWithNonStandardPort() throws IOException {
		this.snippet.expectCurlRequest("https-with-non-standard-port").withContents(
				codeBlock("bash").content("$ curl 'https://localhost:8443/foo' -i"));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerPort(8443);
		request.setScheme("https");
		documentCurlRequest("https-with-non-standard-port").handle(result(request));
	}

	@Test
	public void requestWithCustomHost() throws IOException {
		this.snippet.expectCurlRequest("request-with-custom-host").withContents(
				codeBlock("bash").content("$ curl 'http://api.example.com/foo' -i"));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerName("api.example.com");
		documentCurlRequest("request-with-custom-host").handle(result(request));
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
		documentCurlRequest("request-with-custom-context-with-slash").handle(
				result(request));
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
		documentCurlRequest("request-with-custom-context-without-slash").handle(
				result(request));
	}

}
