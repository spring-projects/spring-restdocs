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

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.test.ExpectedSnippet;
import org.springframework.restdocs.test.OperationBuilder;
import org.springframework.util.Base64Utils;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.test.SnippetMatchers.codeBlock;

/**
 * Tests for {@link CurlRequestSnippet}.
 *
 * @author Andy Wilkinson
 * @author Yann Le Guern
 * @author Dmitriy Mayboroda
 * @author Jonathan Pearlin
 * @author Paul-Christian Volkmer
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
		new CurlRequestSnippet().document(new OperationBuilder("get-request",
				this.snippet.getOutputDirectory()).request("http://localhost/foo")
				.build());
	}

	@Test
	public void nonGetRequest() throws IOException {
		this.snippet.expectCurlRequest("non-get-request").withContents(
				codeBlock("bash").content("$ curl 'http://localhost/foo' -i -X POST"));
		new CurlRequestSnippet().document(new OperationBuilder("non-get-request",
				this.snippet.getOutputDirectory()).request("http://localhost/foo")
				.method("POST").build());
	}

	@Test
	public void requestWithContent() throws IOException {
		this.snippet.expectCurlRequest("request-with-content").withContents(
				codeBlock("bash")
						.content("$ curl 'http://localhost/foo' -i -d 'content'"));
		new CurlRequestSnippet().document(new OperationBuilder("request-with-content",
				this.snippet.getOutputDirectory()).request("http://localhost/foo")
				.content("content").build());
	}

	@Test
	public void getRequestWithQueryString() throws IOException {
		this.snippet.expectCurlRequest("request-with-query-string")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo?param=value' -i"));
		new CurlRequestSnippet().document(new OperationBuilder(
				"request-with-query-string", this.snippet.getOutputDirectory()).request(
				"http://localhost/foo?param=value").build());
	}

	@Test
	public void postRequestWithQueryString() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-query-string").withContents(
				codeBlock("bash").content(
						"$ curl 'http://localhost/foo?param=value' -i -X POST"));
		new CurlRequestSnippet().document(new OperationBuilder(
				"post-request-with-query-string", this.snippet.getOutputDirectory())
				.request("http://localhost/foo?param=value").method("POST").build());
	}

	@Test
	public void postRequestWithOneParameter() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-one-parameter").withContents(
				codeBlock("bash").content(
						"$ curl 'http://localhost/foo' -i -X POST -d 'k1=v1'"));
		new CurlRequestSnippet()
				.document(new OperationBuilder("post-request-with-one-parameter",
						this.snippet.getOutputDirectory())
						.request("http://localhost/foo").method("POST").param("k1", "v1")
						.build());
	}

	@Test
	public void postRequestWithMultipleParameters() throws IOException {
		this.snippet.expectCurlRequest("post-request-with-multiple-parameters")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo' -i -X POST"
										+ " -d 'k1=v1&k1=v1-bis&k2=v2'"));
		new CurlRequestSnippet().document(new OperationBuilder(
				"post-request-with-multiple-parameters", this.snippet
						.getOutputDirectory()).request("http://localhost/foo")
				.method("POST").param("k1", "v1", "v1-bis").param("k2", "v2").build());
	}

	@Test
	public void postRequestWithUrlEncodedParameter() throws IOException {
		this.snippet
				.expectCurlRequest("post-request-with-url-encoded-parameter")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo' -i -X POST -d 'k1=a%26b'"));
		new CurlRequestSnippet().document(new OperationBuilder(
				"post-request-with-url-encoded-parameter", this.snippet
						.getOutputDirectory()).request("http://localhost/foo")
				.method("POST").param("k1", "a&b").build());
	}

	@Test
	public void postRequestWithQueryStringAndParameter() throws IOException {
		this.snippet
				.expectCurlRequest("post-request-with-query-string-and-parameter")
				.withContents(
						codeBlock("bash")
								.content(
										"$ curl 'http://localhost/foo?a=alpha' -i -X POST -d 'b=bravo'"));
		new CurlRequestSnippet().document(new OperationBuilder(
				"post-request-with-query-string-and-parameter", this.snippet
						.getOutputDirectory()).request("http://localhost/foo?a=alpha")
				.method("POST").param("b", "bravo").build());
	}

	@Test
	public void postRequestWithOverlappingQueryStringAndParameters() throws IOException {
		this.snippet
				.expectCurlRequest(
						"post-request-with-overlapping-query-string-and-parameters")
				.withContents(
						codeBlock("bash")
								.content(
										"$ curl 'http://localhost/foo?a=alpha' -i -X POST -d 'b=bravo'"));
		new CurlRequestSnippet().document(new OperationBuilder(
				"post-request-with-overlapping-query-string-and-parameters", this.snippet
						.getOutputDirectory()).request("http://localhost/foo?a=alpha")
				.method("POST").param("a", "alpha").param("b", "bravo").build());
	}

	@Test
	public void putRequestWithOneParameter() throws IOException {
		this.snippet.expectCurlRequest("put-request-with-one-parameter").withContents(
				codeBlock("bash").content(
						"$ curl 'http://localhost/foo' -i -X PUT -d 'k1=v1'"));
		new CurlRequestSnippet().document(new OperationBuilder(
				"put-request-with-one-parameter", this.snippet.getOutputDirectory())
				.request("http://localhost/foo").method("PUT").param("k1", "v1").build());
	}

	@Test
	public void putRequestWithMultipleParameters() throws IOException {
		this.snippet.expectCurlRequest("put-request-with-multiple-parameters")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo' -i -X PUT"
										+ " -d 'k1=v1&k1=v1-bis&k2=v2'"));
		new CurlRequestSnippet()
				.document(new OperationBuilder("put-request-with-multiple-parameters",
						this.snippet.getOutputDirectory())
						.request("http://localhost/foo").method("PUT").param("k1", "v1")
						.param("k1", "v1-bis").param("k2", "v2").build());
	}

	@Test
	public void putRequestWithUrlEncodedParameter() throws IOException {
		this.snippet.expectCurlRequest("put-request-with-url-encoded-parameter")
				.withContents(
						codeBlock("bash").content(
								"$ curl 'http://localhost/foo' -i -X PUT -d 'k1=a%26b'"));
		new CurlRequestSnippet().document(new OperationBuilder(
				"put-request-with-url-encoded-parameter", this.snippet
						.getOutputDirectory()).request("http://localhost/foo")
				.method("PUT").param("k1", "a&b").build());
	}

	@Test
	public void requestWithHeaders() throws IOException {
		this.snippet.expectCurlRequest("request-with-headers").withContents(
				codeBlock("bash").content(
						"$ curl 'http://localhost/foo' -i"
								+ " -H 'Content-Type: application/json' -H 'a: alpha'"));
		new CurlRequestSnippet().document(new OperationBuilder("request-with-headers",
				this.snippet.getOutputDirectory()).request("http://localhost/foo")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header("a", "alpha").build());
	}

	@Test
	public void multipartPostWithNoSubmittedFileName() throws IOException {
		String expectedContent = "$ curl 'http://localhost/upload' -i -X POST -H "
				+ "'Content-Type: multipart/form-data' -F "
				+ "'metadata={\"description\": \"foo\"}'";
		this.snippet.expectCurlRequest("multipart-post-no-original-filename")
				.withContents(codeBlock("bash").content(expectedContent));
		new CurlRequestSnippet().document(new OperationBuilder(
				"multipart-post-no-original-filename", this.snippet.getOutputDirectory())
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("metadata", "{\"description\": \"foo\"}".getBytes()).build());
	}

	@Test
	public void multipartPostWithContentType() throws IOException {
		String expectedContent = "$ curl 'http://localhost/upload' -i -X POST -H "
				+ "'Content-Type: multipart/form-data' -F "
				+ "'image=@documents/images/example.png;type=image/png'";
		this.snippet.expectCurlRequest("multipart-post-with-content-type").withContents(
				codeBlock("bash").content(expectedContent));
		new CurlRequestSnippet().document(new OperationBuilder(
				"multipart-post-with-content-type", this.snippet.getOutputDirectory())
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", new byte[0])
				.header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
				.submittedFileName("documents/images/example.png").build());
	}

	@Test
	public void multipartPost() throws IOException {
		String expectedContent = "$ curl 'http://localhost/upload' -i -X POST -H "
				+ "'Content-Type: multipart/form-data' -F "
				+ "'image=@documents/images/example.png'";
		this.snippet.expectCurlRequest("multipart-post").withContents(
				codeBlock("bash").content(expectedContent));
		new CurlRequestSnippet().document(new OperationBuilder("multipart-post",
				this.snippet.getOutputDirectory()).request("http://localhost/upload")
				.method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", new byte[0])
				.submittedFileName("documents/images/example.png").build());
	}

	@Test
	public void multipartPostWithParameters() throws IOException {
		String expectedContent = "$ curl 'http://localhost/upload' -i -X POST -H "
				+ "'Content-Type: multipart/form-data' -F "
				+ "'image=@documents/images/example.png' -F 'a=apple' -F 'a=avocado' "
				+ "-F 'b=banana'";
		this.snippet.expectCurlRequest("multipart-post-with-parameters").withContents(
				codeBlock("bash").content(expectedContent));
		new CurlRequestSnippet().document(new OperationBuilder(
				"multipart-post-with-parameters", this.snippet.getOutputDirectory())
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", new byte[0])
				.submittedFileName("documents/images/example.png").and()
				.param("a", "apple", "avocado").param("b", "banana").build());
	}

	@Test
	public void customAttributes() throws IOException {
		this.snippet.expectCurlRequest("custom-attributes").withContents(
				containsString("curl request title"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("curl-request"))
				.willReturn(
						new FileSystemResource(
								"src/test/resources/custom-snippet-templates/curl-request-with-title.snippet"));
		new CurlRequestSnippet(attributes(key("title").value("curl request title")))
				.document(new OperationBuilder("custom-attributes", this.snippet
						.getOutputDirectory())
						.attribute(TemplateEngine.class.getName(),
								new MustacheTemplateEngine(resolver))
						.request("http://localhost/foo").build());
	}

	@Test
	public void basicAuthCredentialsAreSuppliedUsingUserOption() throws IOException {
		this.snippet.expectCurlRequest("basic-auth").withContents(
				codeBlock("bash").content(
						"$ curl 'http://localhost/foo' -i -u 'user:secret'"));
		new CurlRequestSnippet().document(new OperationBuilder("basic-auth", this.snippet
				.getOutputDirectory())
				.request("http://localhost/foo")
				.header(HttpHeaders.AUTHORIZATION,
						"Basic " + Base64Utils.encodeToString("user:secret".getBytes()))
				.build());
	}

}
