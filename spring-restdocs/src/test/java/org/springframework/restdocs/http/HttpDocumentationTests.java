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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.springframework.restdocs.http.HttpDocumentation.documentHttpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.documentHttpResponse;

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
 * Tests for {@link HttpDocumentation}
 * 
 * @author Andy Wilkinson
 */
public class HttpDocumentationTests {

	private final File outputDir = new File("build/http-documentation-tests");

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
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.addHeader("Alpha", "a");
		documentHttpRequest("get-request").handle(new StubMvcResult(request, null));
		assertThat(requestSnippetLines("get-request"),
				hasItems("GET /foo HTTP/1.1", "Alpha: a"));
	}

	@Test
	public void getRequestWithQueryString() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo?bar=baz");
		documentHttpRequest("get-request-with-query-string").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("get-request-with-query-string"),
				hasItems("GET /foo?bar=baz HTTP/1.1"));
	}

	@Test
	public void getRequestWithParameter() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
		request.addParameter("b&r", "baz");
		documentHttpRequest("get-request-with-parameter").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("get-request-with-parameter"),
				hasItems("GET /foo?b%26r=baz HTTP/1.1"));
	}

	@Test
	public void postRequestWithContent() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/foo");
		byte[] content = "Hello, world".getBytes();
		request.setContent(content);
		documentHttpRequest("post-request-with-content").handle(
				new StubMvcResult(request, null));
		assertThat(requestSnippetLines("post-request-with-content"),
				hasItems("POST /foo HTTP/1.1", "Hello, world"));
	}

	@Test
	public void postRequestWithParameter() throws IOException {
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/foo");
		request.addParameter("b&r", "baz");
		request.addParameter("a", "alpha");
		documentHttpRequest("post-request-with-parameter").handle(
				new StubMvcResult(request, null));
		assertThat(
				requestSnippetLines("post-request-with-parameter"),
				hasItems("POST /foo HTTP/1.1",
						"Content-Type: application/x-www-form-urlencoded",
						"b%26r=baz&a=alpha"));
	}

	@Test
	public void basicResponse() throws IOException {
		documentHttpResponse("basic-response").handle(
				new StubMvcResult(null, new MockHttpServletResponse()));
		assertThat(responseSnippetLines("basic-response"), hasItem("HTTP/1.1 200 OK"));
	}

	@Test
	public void nonOkResponse() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		documentHttpResponse("non-ok-response").handle(new StubMvcResult(null, response));
		assertThat(responseSnippetLines("non-ok-response"),
				hasItem("HTTP/1.1 400 Bad Request"));
	}

	@Test
	public void responseWithHeaders() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setHeader("a", "alpha");
		documentHttpResponse("non-ok-response").handle(new StubMvcResult(null, response));
		assertThat(responseSnippetLines("non-ok-response"),
				hasItems("HTTP/1.1 200 OK", "Content-Type: application/json", "a: alpha"));
	}

	@Test
	public void responseWithContent() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.getWriter().append("content");
		documentHttpResponse("response-with-content").handle(
				new StubMvcResult(null, response));
		assertThat(responseSnippetLines("response-with-content"),
				hasItems("HTTP/1.1 200 OK", "content"));
	}

	private List<String> requestSnippetLines(String snippetName) throws IOException {
		return snippetLines(snippetName, "http-request");
	}

	private List<String> responseSnippetLines(String snippetName) throws IOException {
		return snippetLines(snippetName, "http-response");
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
