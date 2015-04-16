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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.springframework.restdocs.curl.CurlDocumentation.documentCurlRequest;
import static org.springframework.restdocs.curl.CurlDocumentation.documentCurlRequestAndResponse;
import static org.springframework.restdocs.curl.CurlDocumentation.documentCurlResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.StubMvcResult;

/**
 * Tests for {@link CurlDocumentation}
 * 
 * @author Andy Wilkinson
 * @author Yann Le Guern
 * @author Dmitriy Mayboroda
 */
public class CurlDocumentationTests {

	private final File outputDir = new File("build/curl-documentation-tests");

	@Before
	public void setup() {
		System.setProperty("org.springframework.restdocs.outputDir",
				this.outputDir.getAbsolutePath());
	}

	@After
	public void cleanup() {
		System.clearProperty("org.springframework.restdocs.outputDir");
	}

	@Test
	public void getRequest() throws IOException {
		documentCurlRequest("get-request").handle(
				new StubMvcResult(new MockHttpServletRequest("GET", "/foo"), null));
		assertThat(requestSnippetLines("get-request"),
				hasItem("$ curl http://localhost/foo -i"));
	}

	@Test
	public void nonGetRequest() throws IOException {
		documentCurlRequest("non-get-request").handle(
				new StubMvcResult(new MockHttpServletRequest("POST", "/foo"), null));
		assertThat(requestSnippetLines("non-get-request"),
				hasItem("$ curl http://localhost/foo -i -X POST"));
	}

	@Test
	public void requestWithoutResponseHeaderInclusion() throws IOException {
		documentCurlRequest("request-without-response-header-inclusion")
				.includeResponseHeaders(false)
				.handle(new StubMvcResult(new MockHttpServletRequest("GET", "/foo"), null));
		assertThat(requestSnippetLines("request-without-response-header-inclusion"),
				hasItem("$ curl http://localhost/foo"));
	}

	@Test
	public void requestWithContent() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setContent("content".getBytes());
		documentCurlRequest("request-with-content").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("request-with-content"),
				hasItem("$ curl http://localhost/foo -i -d 'content'"));
	}

	@Test
	public void requestWitUriQueryString() throws IOException {
		documentCurlRequest("request-with-uri-query-string").handle(
				new StubMvcResult(new MockHttpServletRequest("GET", "/foo?param=value"),
						null));
		assertThat(requestSnippetLines("request-with-uri-query-string"),
				hasItem("$ curl http://localhost/foo?param=value -i"));
	}

	@Test
	public void requestWithQueryString() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setQueryString("param=value");
		documentCurlRequest("request-with-query-string").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("request-with-query-string"),
				hasItem("$ curl http://localhost/foo?param=value -i"));
	}

	@Test
	public void requestWithOneParameter() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.addParameter("k1", "v1");
		documentCurlRequest("request-with-one-parameter").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("request-with-one-parameter"),
				hasItem("$ curl http://localhost/foo?k1=v1 -i"));
	}

	@Test
	public void requestWithMultipleParameters() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.addParameter("k1", "v1");
		request.addParameter("k2", "v2");
		request.addParameter("k1", "v1-bis");
		documentCurlRequest("request-with-multiple-parameters").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("request-with-multiple-parameters"),
				hasItem("$ curl http://localhost/foo?k1=v1\\&k1=v1-bis\\&k2=v2 -i"));
	}

	@Test
	public void requestWithUrlEncodedParameter() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.addParameter("k1", "foo bar&");
		documentCurlRequest("request-with-url-encoded-parameter").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("request-with-url-encoded-parameter"),
				hasItem("$ curl http://localhost/foo?k1=foo+bar%26 -i"));
	}

	@Test
	public void postRequestWithOneParameter() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/foo");
		request.addParameter("k1", "v1");
		documentCurlRequest("post-request-with-one-parameter").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("post-request-with-one-parameter"),
				hasItem("$ curl http://localhost/foo -i -X POST -d 'k1=v1'"));
	}

	@Test
	public void postRequestWithMultipleParameters() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/foo");
		request.addParameter("k1", "v1");
		request.addParameter("k2", "v2");
		request.addParameter("k1", "v1-bis");
		documentCurlRequest("post-request-with-multiple-parameters").handle(
				new StubMvcResult(request, null));
		assertThat(
				requestSnippetLines("post-request-with-multiple-parameters"),
				hasItem("$ curl http://localhost/foo -i -X POST -d 'k1=v1\\&k1=v1-bis\\&k2=v2'"));
	}

	@Test
	public void postRequestWithUrlEncodedParameter() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/foo");
		request.addParameter("k1", "a&b");
		documentCurlRequest("post-request-with-url-encoded-parameter").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("post-request-with-url-encoded-parameter"),
				hasItem("$ curl http://localhost/foo -i -X POST -d 'k1=a%26b'"));
	}

	@Test
	public void requestWithHeaders() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setContentType(MediaType.APPLICATION_JSON_VALUE);
		request.addHeader("a", "alpha");
		documentCurlRequest("request-with-headers").handle(
				new StubMvcResult(request, null));
		assertThat(
				requestSnippetLines("request-with-headers"),
				hasItem("$ curl http://localhost/foo -i -H \"Content-Type: application/json\" -H \"a: alpha\""));
	}

	@Test
	public void basicResponse() throws IOException {
		documentCurlResponse("basic-response").handle(
				new StubMvcResult(null, new MockHttpServletResponse()));
		assertThat(responseSnippetLines("basic-response"), hasItem("HTTP/1.1 200 OK"));
	}

	@Test
	public void nonOkResponse() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		documentCurlResponse("non-ok-response").handle(new StubMvcResult(null, response));
		assertThat(responseSnippetLines("non-ok-response"),
				hasItem("HTTP/1.1 400 Bad Request"));
	}

	@Test
	public void responseWithHeaders() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setHeader("a", "alpha");
		documentCurlResponse("non-ok-response").handle(new StubMvcResult(null, response));
		assertThat(responseSnippetLines("non-ok-response"),
				hasItems("HTTP/1.1 200 OK", "Content-Type: application/json", "a: alpha"));
	}

	@Test
	public void responseWithHeaderInclusionDisabled() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setHeader("a", "alpha");
		response.getWriter().append("content");
		documentCurlResponse("response-with-header-inclusion-disabled")
				.includeResponseHeaders(false).handle(new StubMvcResult(null, response));
		List<String> responseSnippetLines = responseSnippetLines("response-with-header-inclusion-disabled");
		assertThat(
				responseSnippetLines,
				not(hasItems("HTTP/1.1 200 OK", "Content-Type: application/json",
						"a: alpha")));
		assertThat(responseSnippetLines, hasItem("content"));
	}

	@Test
	public void responseWithContent() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.getWriter().append("content");
		documentCurlResponse("response-with-content").handle(
				new StubMvcResult(null, response));
		assertThat(responseSnippetLines("response-with-content"),
				hasItems("HTTP/1.1 200 OK", "content"));
	}

	@Test
	public void requestAndResponse() throws IOException {
		documentCurlRequestAndResponse("request-and-response").handle(
				new StubMvcResult(new MockHttpServletRequest("GET", "/foo"),
						new MockHttpServletResponse()));
		assertThat(requestResponseSnippetLines("request-and-response"),
				hasItems("$ curl http://localhost/foo -i", "HTTP/1.1 200 OK"));
	}

	@Test
	public void httpWithNonStandardPort() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerPort(8080);
		documentCurlRequest("http-with-non-standard-port").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("http-with-non-standard-port"),
				hasItem("$ curl http://localhost:8080/foo -i"));
	}

	@Test
	public void httpsWithStandardPort() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerPort(443);
		request.setScheme("https");
		documentCurlRequest("https-with-standard-port").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("https-with-standard-port"),
				hasItem("$ curl https://localhost/foo -i"));
	}

	@Test
	public void httpsWithNonStandardPort() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerPort(8443);
		request.setScheme("https");
		documentCurlRequest("https-with-non-standard-port").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("https-with-non-standard-port"),
				hasItem("$ curl https://localhost:8443/foo -i"));
	}

	@Test
	public void requestWithCustomHost() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerName("api.example.com");
		documentCurlRequest("request-with-custom-host").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("request-with-custom-host"),
				hasItem("$ curl http://api.example.com/foo -i"));
	}

	@Test
	public void requestWithContextPath() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerName("api.example.com");
		request.setContextPath("v3");
		documentCurlRequest("request-with-custom-context").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("request-with-custom-context"),
				hasItem("$ curl http://api.example.com/v3/foo -i"));
	}

	private List<String> requestSnippetLines(String snippetName) throws IOException {
		return snippetLines(snippetName, "request");
	}

	private List<String> responseSnippetLines(String snippetName) throws IOException {
		return snippetLines(snippetName, "response");
	}

	private List<String> requestResponseSnippetLines(String snippetName)
			throws IOException {
		return snippetLines(snippetName, "request-response");
	}

	private List<String> snippetLines(String snippetName, String snippetType)
			throws IOException {
		File snippetDir = new File(this.outputDir, snippetName);
		File snippetFile = new File(snippetDir, snippetType + ".asciidoc");
		String line = null;
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(snippetFile));
		try {
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}
		finally {
			reader.close();
		}
		return lines;
	}
}
