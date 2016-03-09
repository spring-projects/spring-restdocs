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

package org.springframework.restdocs.cli;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.util.Base64Utils;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link CurlRequestSnippet}.
 *
 * @author Andy Wilkinson
 * @author Yann Le Guern
 * @author Dmitriy Mayboroda
 * @author Jonathan Pearlin
 * @author Paul-Christian Volkmer
 */
@RunWith(Parameterized.class)
public class CurlRequestSnippetTests extends AbstractSnippetTests {

	public CurlRequestSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void getRequest() throws IOException {
		this.snippet.expectCurlRequest("get-request").withContents(
				codeBlock("bash").content("$ curl 'http://localhost/foo' -i"));
		new CurlRequestSnippet().document(
				operationBuilder("get-request").request("http://localhost/foo").build());
	}

	@Test
	public void nonGetRequest() throws IOException {
		this.snippet.expectCurlRequest("non-get-request").withContents(
				codeBlock("bash").content("$ curl 'http://localhost/foo' -i -X POST"));
		new CurlRequestSnippet().document(operationBuilder("non-get-request")
				.request("http://localhost/foo").method("POST").build());
	}

	@Test
	public void requestWithContent() throws IOException {
		this.snippet.expectCurlRequest("request-with-content")
				.withContents(codeBlock("bash")
						.content("$ curl 'http://localhost/foo' -i -d 'content'"));
		new CurlRequestSnippet().document(operationBuilder("request-with-content")
				.request("http://localhost/foo").content("content").build());
	}

	@Test
	public void getRequestWithQueryString() throws IOException {
		this.snippet.expectCurlRequest("request-with-query-string")
				.withContents(codeBlock("bash")
						.content("$ curl 'http://localhost/foo?param=value' -i"));
		new CurlRequestSnippet().document(operationBuilder("request-with-query-string")
				.request("http://localhost/foo?param=value").build());
	}

	@Test
	public void getRequestWithQueryStringWithNoValue() throws IOException {
		this.snippet.expectCurlRequest("request-with-query-string-with-no-value")
				.withContents(codeBlock("bash")
						.content("$ curl 'http://localhost/foo?param' -i"));
		new CurlRequestSnippet()
				.document(operationBuilder("request-with-query-string-with-no-value")
						.request("http://localhost/foo?param").build());
	}

	@Test
	public void postRequestWithQueryString() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-query-string")
				.withContents(codeBlock("bash")
						.content("$ curl 'http://localhost/foo?param=value' -i -X POST"));
		new CurlRequestSnippet()
				.document(operationBuilder("post-request-with-query-string")
						.request("http://localhost/foo?param=value").method("POST")
						.build());
	}

	@Test
	public void postRequestWithQueryStringWithNoValue() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-query-string-with-no-value")
				.withContents(codeBlock("bash")
						.content("$ curl 'http://localhost/foo?param' -i -X POST"));
		new CurlRequestSnippet()
				.document(operationBuilder("post-request-with-query-string-with-no-value")
						.request("http://localhost/foo?param").method("POST").build());
	}

	@Test
	public void postRequestWithOneParameter() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-one-parameter")
				.withContents(codeBlock("bash")
						.content("$ curl 'http://localhost/foo' -i -X POST -d 'k1=v1'"));
		new CurlRequestSnippet()
				.document(operationBuilder("post-request-with-one-parameter")
						.request("http://localhost/foo").method("POST").param("k1", "v1")
						.build());
	}

	@Test
	public void postRequestWithOneParameterWithNoValue() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-one-parameter-with-no-value")
				.withContents(codeBlock("bash")
						.content("$ curl 'http://localhost/foo' -i -X POST -d 'k1='"));
		new CurlRequestSnippet().document(
				operationBuilder("post-request-with-one-parameter-with-no-value")
						.request("http://localhost/foo").method("POST").param("k1")
						.build());
	}

	@Test
	public void postRequestWithMultipleParameters() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-multiple-parameters")
				.withContents(codeBlock("bash")
						.content("$ curl 'http://localhost/foo' -i -X POST"
								+ " -d 'k1=v1&k1=v1-bis&k2=v2'"));
		new CurlRequestSnippet()
				.document(operationBuilder("post-request-with-multiple-parameters")
						.request("http://localhost/foo").method("POST")
						.param("k1", "v1", "v1-bis").param("k2", "v2").build());
	}

	@Test
	public void postRequestWithUrlEncodedParameter() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-url-encoded-parameter")
				.withContents(codeBlock("bash").content(
						"$ curl 'http://localhost/foo' -i -X POST -d 'k1=a%26b'"));
		new CurlRequestSnippet()
				.document(operationBuilder("post-request-with-url-encoded-parameter")
						.request("http://localhost/foo").method("POST").param("k1", "a&b")
						.build());
	}

	@Test
	public void postRequestWithQueryStringAndParameter() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-query-string-and-parameter")
				.withContents(codeBlock("bash").content(
						"$ curl 'http://localhost/foo?a=alpha' -i -X POST -d 'b=bravo'"));
		new CurlRequestSnippet()
				.document(operationBuilder("post-request-with-query-string-and-parameter")
						.request("http://localhost/foo?a=alpha").method("POST")
						.param("b", "bravo").build());
	}

	@Test
	public void postRequestWithOverlappingQueryStringAndParameters() throws IOException {
		this.snippet
				.expectCurlRequest(
						"post-request-with-overlapping-query-string-and-parameters")
				.withContents(codeBlock("bash").content(
						"$ curl 'http://localhost/foo?a=alpha' -i -X POST -d 'b=bravo'"));
		new CurlRequestSnippet().document(operationBuilder(
				"post-request-with-overlapping-query-string-and-parameters")
						.request("http://localhost/foo?a=alpha").method("POST")
						.param("a", "alpha").param("b", "bravo").build());
	}

	@Test
	public void putRequestWithOneParameter() throws IOException {
		this.snippet.expectCurlRequest("put-request-with-one-parameter")
				.withContents(codeBlock("bash")
						.content("$ curl 'http://localhost/foo' -i -X PUT -d 'k1=v1'"));
		new CurlRequestSnippet()
				.document(operationBuilder("put-request-with-one-parameter")
						.request("http://localhost/foo").method("PUT").param("k1", "v1")
						.build());
	}

	@Test
	public void putRequestWithMultipleParameters() throws IOException {
		this.snippet.expectCurlRequest("put-request-with-multiple-parameters")
				.withContents(codeBlock("bash")
						.content("$ curl 'http://localhost/foo' -i -X PUT"
								+ " -d 'k1=v1&k1=v1-bis&k2=v2'"));
		new CurlRequestSnippet()
				.document(operationBuilder("put-request-with-multiple-parameters")
						.request("http://localhost/foo").method("PUT").param("k1", "v1")
						.param("k1", "v1-bis").param("k2", "v2").build());
	}

	@Test
	public void putRequestWithUrlEncodedParameter() throws IOException {
		this.snippet.expectCurlRequest("put-request-with-url-encoded-parameter")
				.withContents(codeBlock("bash").content(
						"$ curl 'http://localhost/foo' -i -X PUT -d 'k1=a%26b'"));
		new CurlRequestSnippet()
				.document(operationBuilder("put-request-with-url-encoded-parameter")
						.request("http://localhost/foo").method("PUT").param("k1", "a&b")
						.build());
	}

	@Test
	public void requestWithHeaders() throws IOException {
		this.snippet.expectCurlRequest("request-with-headers")
				.withContents(codeBlock("bash").content("$ curl 'http://localhost/foo' -i"
						+ " -H 'Content-Type: application/json' -H 'a: alpha'"));
		new CurlRequestSnippet().document(
				operationBuilder("request-with-headers").request("http://localhost/foo")
						.header(HttpHeaders.CONTENT_TYPE,
								MediaType.APPLICATION_JSON_VALUE)
						.header("a", "alpha").build());
	}

	@Test
	public void multipartPostWithNoSubmittedFileName() throws IOException {
		String expectedContent = "$ curl 'http://localhost/upload' -i -X POST -H "
				+ "'Content-Type: multipart/form-data' -F "
				+ "'metadata={\"description\": \"foo\"}'";
		this.snippet.expectCurlRequest("multipart-post-no-original-filename")
				.withContents(codeBlock("bash").content(expectedContent));
		new CurlRequestSnippet()
				.document(operationBuilder("multipart-post-no-original-filename")
						.request("http://localhost/upload").method("POST")
						.header(HttpHeaders.CONTENT_TYPE,
								MediaType.MULTIPART_FORM_DATA_VALUE)
						.part("metadata", "{\"description\": \"foo\"}".getBytes())
						.build());
	}

	@Test
	public void multipartPostWithContentType() throws IOException {
		String expectedContent = "$ curl 'http://localhost/upload' -i -X POST -H "
				+ "'Content-Type: multipart/form-data' -F "
				+ "'image=@documents/images/example.png;type=image/png'";
		this.snippet.expectCurlRequest("multipart-post-with-content-type")
				.withContents(codeBlock("bash").content(expectedContent));
		new CurlRequestSnippet()
				.document(operationBuilder("multipart-post-with-content-type")
						.request("http://localhost/upload").method("POST")
						.header(HttpHeaders.CONTENT_TYPE,
								MediaType.MULTIPART_FORM_DATA_VALUE)
						.part("image", new byte[0])
						.header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
						.submittedFileName("documents/images/example.png").build());
	}

	@Test
	public void multipartPost() throws IOException {
		String expectedContent = "$ curl 'http://localhost/upload' -i -X POST -H "
				+ "'Content-Type: multipart/form-data' -F "
				+ "'image=@documents/images/example.png'";
		this.snippet.expectCurlRequest("multipart-post")
				.withContents(codeBlock("bash").content(expectedContent));
		new CurlRequestSnippet()
				.document(operationBuilder("multipart-post")
						.request("http://localhost/upload").method("POST")
						.header(HttpHeaders.CONTENT_TYPE,
								MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", new byte[0])
				.submittedFileName("documents/images/example.png").build());
	}

	@Test
	public void multipartPostWithParameters() throws IOException {
		String expectedContent = "$ curl 'http://localhost/upload' -i -X POST -H "
				+ "'Content-Type: multipart/form-data' -F "
				+ "'image=@documents/images/example.png' -F 'a=apple' -F 'a=avocado' "
				+ "-F 'b=banana'";
		this.snippet.expectCurlRequest("multipart-post-with-parameters")
				.withContents(codeBlock("bash").content(expectedContent));
		new CurlRequestSnippet()
				.document(operationBuilder("multipart-post-with-parameters")
						.request("http://localhost/upload").method("POST")
						.header(HttpHeaders.CONTENT_TYPE,
								MediaType.MULTIPART_FORM_DATA_VALUE)
						.part("image", new byte[0])
						.submittedFileName("documents/images/example.png").and()
						.param("a", "apple", "avocado").param("b", "banana").build());
	}

	@Test
	public void basicAuthCredentialsAreSuppliedUsingUserOption() throws IOException {
		this.snippet.expectCurlRequest("basic-auth").withContents(codeBlock("bash")
				.content("$ curl 'http://localhost/foo' -i -u 'user:secret'"));
		new CurlRequestSnippet()
				.document(operationBuilder("basic-auth").request("http://localhost/foo")
						.header(HttpHeaders.AUTHORIZATION,
								"Basic " + Base64Utils
										.encodeToString("user:secret".getBytes()))
				.build());
	}

	@Test
	public void customAttributes() throws IOException {
		this.snippet.expectCurlRequest("custom-attributes")
				.withContents(containsString("curl request title"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("curl-request"))
				.willReturn(snippetResource("curl-request-with-title"));
		new CurlRequestSnippet(
				attributes(
						key("title").value("curl request title")))
								.document(
										operationBuilder("custom-attributes")
												.attribute(TemplateEngine.class.getName(),
														new MustacheTemplateEngine(
																resolver))
												.request("http://localhost/foo").build());
	}

}
