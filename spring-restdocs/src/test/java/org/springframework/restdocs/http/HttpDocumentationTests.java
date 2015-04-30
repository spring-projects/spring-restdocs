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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.restdocs.http.HttpDocumentation.documentHttpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.documentHttpResponse;
import static org.springframework.restdocs.test.SnippetMatchers.httpRequest;
import static org.springframework.restdocs.test.SnippetMatchers.httpResponse;
import static org.springframework.restdocs.test.StubMvcResult.result;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.test.ExpectedSnippet;

/**
 * Tests for {@link HttpDocumentation}
 *
 * @author Andy Wilkinson
 * @author Jonathan Pearlin
 */
public class HttpDocumentationTests {

	@Rule
	public final ExpectedSnippet snippet = new ExpectedSnippet();

	@Test
	public void getRequest() throws IOException {
		this.snippet.expectHttpRequest("get-request").withContents(
				httpRequest(GET, "/foo").header("Alpha", "a"));

		documentHttpRequest("get-request").handle(
				result(get("/foo").header("Alpha", "a")));
	}

	@Test
	public void getRequestWithQueryString() throws IOException {
		this.snippet.expectHttpRequest("get-request-with-query-string").withContents(
				httpRequest(GET, "/foo?bar=baz"));

		documentHttpRequest("get-request-with-query-string").handle(
				result(get("/foo?bar=baz")));
	}

	@Test
	public void getRequestWithParameter() throws IOException {
		this.snippet.expectHttpRequest("get-request-with-parameter").withContents(
				httpRequest(GET, "/foo?b%26r=baz"));

		documentHttpRequest("get-request-with-parameter").handle(
				result(get("/foo").param("b&r", "baz")));
	}

	@Test
	public void postRequestWithContent() throws IOException {
		this.snippet.expectHttpRequest("post-request-with-content").withContents(
				httpRequest(POST, "/foo") //
						.content("Hello, world"));

		documentHttpRequest("post-request-with-content").handle(
				result(post("/foo").content("Hello, world")));
	}

	@Test
	public void postRequestWithParameter() throws IOException {
		this.snippet.expectHttpRequest("post-request-with-parameter").withContents(
				httpRequest(POST, "/foo") //
						.header("Content-Type", "application/x-www-form-urlencoded") //
						.content("b%26r=baz&a=alpha"));

		documentHttpRequest("post-request-with-parameter").handle(
				result(post("/foo").param("b&r", "baz").param("a", "alpha")));
	}

	@Test
	public void putRequestWithContent() throws IOException {
		this.snippet.expectHttpRequest("put-request-with-content").withContents(
				httpRequest(PUT, "/foo") //
						.content("Hello, world"));

		documentHttpRequest("put-request-with-content").handle(
				result(put("/foo").content("Hello, world")));
	}

	@Test
	public void putRequestWithParameter() throws IOException {
		this.snippet.expectHttpRequest("put-request-with-parameter").withContents(
				httpRequest(PUT, "/foo") //
						.header("Content-Type", "application/x-www-form-urlencoded") //
						.content("b%26r=baz&a=alpha"));

		documentHttpRequest("put-request-with-parameter").handle(
				result(put("/foo").param("b&r", "baz").param("a", "alpha")));
	}

	@Test
	public void basicResponse() throws IOException {
		this.snippet.expectHttpResponse("basic-response").withContents(httpResponse(OK));
		documentHttpResponse("basic-response").handle(result());
	}

	@Test
	public void nonOkResponse() throws IOException {
		this.snippet.expectHttpResponse("non-ok-response").withContents(
				httpResponse(BAD_REQUEST));

		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(BAD_REQUEST.value());
		documentHttpResponse("non-ok-response").handle(result(response));
	}

	@Test
	public void responseWithHeaders() throws IOException {
		this.snippet.expectHttpResponse("response-with-headers").withContents(
				httpResponse(OK) //
						.header("Content-Type", "application/json") //
						.header("a", "alpha"));

		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setHeader("a", "alpha");
		documentHttpResponse("response-with-headers").handle(result(response));
	}

	@Test
	public void responseWithContent() throws IOException {
		this.snippet.expectHttpResponse("response-with-content").withContents(
				httpResponse(OK).content("content"));
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.getWriter().append("content");
		documentHttpResponse("response-with-content").handle(result(response));
	}

}
