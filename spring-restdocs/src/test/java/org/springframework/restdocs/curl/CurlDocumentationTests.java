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
import static org.junit.Assert.assertThat;
import static org.springframework.restdocs.curl.CurlDocumentation.documentCurlRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
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
	public void requestWithContextPathWithSlash() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerName("api.example.com");
		request.setContextPath("/v3");
		documentCurlRequest("request-with-custom-context-with-slash").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("request-with-custom-context-with-slash"),
				hasItem("$ curl http://api.example.com/v3/foo -i"));
	}

	@Test
	public void requestWithContextPathWithoutSlash() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.setServerName("api.example.com");
		request.setContextPath("v3");
		documentCurlRequest("request-with-custom-context-without-slash").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("request-with-custom-context-without-slash"),
				hasItem("$ curl http://api.example.com/v3/foo -i"));
	}

	private List<String> requestSnippetLines(String snippetName) throws IOException {
		return snippetLines(snippetName, "curl-request");
	}

	private List<String> snippetLines(String snippetName, String snippetType)
			throws IOException {
		File snippetDir = new File(this.outputDir, snippetName);
		File snippetFile = new File(snippetDir, snippetType + ".adoc");
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
