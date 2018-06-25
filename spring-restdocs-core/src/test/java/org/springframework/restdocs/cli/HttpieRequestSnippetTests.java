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
 * Tests for {@link HttpieRequestSnippet}.
 *
 * @author Andy Wilkinson
 * @author Yann Le Guern
 * @author Dmitriy Mayboroda
 * @author Jonathan Pearlin
 * @author Paul-Christian Volkmer
 * @author Raman Gupta
 * @author Tomasz Kopczynski
 */
@RunWith(Parameterized.class)
public class HttpieRequestSnippetTests extends AbstractSnippetTests {

	private CommandFormatter commandFormatter = CliDocumentation.singleLineFormat();

	public HttpieRequestSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void getRequest() throws IOException {
		this.snippets.expectHttpieRequest().withContents(
				codeBlock("bash").content("$ http GET 'http://localhost/foo'"));
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo").build());
	}

	@Test
	public void getRequestWithParameter() throws IOException {
		this.snippets.expectHttpieRequest().withContents(
				codeBlock("bash").content("$ http GET 'http://localhost/foo?a=alpha'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").param("a", "alpha").build());
	}

	@Test
	public void nonGetRequest() throws IOException {
		this.snippets.expectHttpieRequest().withContents(
				codeBlock("bash").content("$ http POST 'http://localhost/foo'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").method("POST").build());
	}

	@Test
	public void requestWithContent() throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ echo 'content' | http GET 'http://localhost/foo'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").content("content").build());
	}

	@Test
	public void getRequestWithQueryString() throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http GET 'http://localhost/foo?param=value'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo?param=value").build());
	}

	@Test
	public void getRequestWithTotallyOverlappingQueryStringAndParameters()
			throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http GET 'http://localhost/foo?param=value'"));
		new HttpieRequestSnippet(this.commandFormatter).document(
				this.operationBuilder.request("http://localhost/foo?param=value")
						.param("param", "value").build());
	}

	@Test
	public void getRequestWithPartiallyOverlappingQueryStringAndParameters()
			throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http GET 'http://localhost/foo?a=alpha&b=bravo'"));
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo?a=alpha")
						.param("a", "alpha").param("b", "bravo").build());
	}

	@Test
	public void getRequestWithDisjointQueryStringAndParameters() throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http GET 'http://localhost/foo?a=alpha&b=bravo'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo?a=alpha").param("b", "bravo").build());
	}

	@Test
	public void getRequestWithQueryStringWithNoValue() throws IOException {
		this.snippets.expectHttpieRequest().withContents(
				codeBlock("bash").content("$ http GET 'http://localhost/foo?param'"));
		new HttpieRequestSnippet(this.commandFormatter).document(
				this.operationBuilder.request("http://localhost/foo?param").build());
	}

	@Test
	public void postRequestWithQueryString() throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http POST 'http://localhost/foo?param=value'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo?param=value").method("POST").build());
	}

	@Test
	public void postRequestWithQueryStringWithNoValue() throws IOException {
		this.snippets.expectHttpieRequest().withContents(
				codeBlock("bash").content("$ http POST 'http://localhost/foo?param'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo?param").method("POST").build());
	}

	@Test
	public void postRequestWithOneParameter() throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http --form POST 'http://localhost/foo' 'k1=v1'"));
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo")
						.method("POST").param("k1", "v1").build());
	}

	@Test
	public void postRequestWithOneParameterWithNoValue() throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http --form POST 'http://localhost/foo' 'k1='"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").method("POST").param("k1").build());
	}

	@Test
	public void postRequestWithMultipleParameters() throws IOException {
		this.snippets.expectHttpieRequest().withContents(
				codeBlock("bash").content("$ http --form POST 'http://localhost/foo'"
						+ " 'k1=v1' 'k1=v1-bis' 'k2=v2'"));
		new HttpieRequestSnippet(this.commandFormatter).document(
				this.operationBuilder.request("http://localhost/foo").method("POST")
						.param("k1", "v1", "v1-bis").param("k2", "v2").build());
	}

	@Test
	public void postRequestWithUrlEncodedParameter() throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http --form POST 'http://localhost/foo' 'k1=a&b'"));
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo")
						.method("POST").param("k1", "a&b").build());
	}

	@Test
	public void postRequestWithDisjointQueryStringAndParameter() throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http --form POST 'http://localhost/foo?a=alpha' 'b=bravo'"));
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo?a=alpha")
						.method("POST").param("b", "bravo").build());
	}

	@Test
	public void postRequestWithTotallyOverlappingQueryStringAndParameters()
			throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http POST 'http://localhost/foo?a=alpha&b=bravo'"));
		new HttpieRequestSnippet(this.commandFormatter).document(
				this.operationBuilder.request("http://localhost/foo?a=alpha&b=bravo")
						.method("POST").param("a", "alpha").param("b", "bravo").build());
	}

	@Test
	public void postRequestWithPartiallyOverlappingQueryStringAndParameters()
			throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http --form POST 'http://localhost/foo?a=alpha' 'b=bravo'"));
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo?a=alpha")
						.method("POST").param("a", "alpha").param("b", "bravo").build());
	}

	@Test
	public void postRequestWithOverlappingParametersAndFormUrlEncodedBody()
			throws IOException {
		this.snippets.expectHttpieRequest()
				.withContents(codeBlock("bash").content(
						"$ echo 'a=alpha&b=bravo' | http POST 'http://localhost/foo' "
								+ "'Content-Type:application/x-www-form-urlencoded'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").method("POST").content("a=alpha&b=bravo")
				.header(HttpHeaders.CONTENT_TYPE,
						MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("a", "alpha").param("b", "bravo").build());
	}

	@Test
	public void putRequestWithOneParameter() throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http --form PUT 'http://localhost/foo' 'k1=v1'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").method("PUT").param("k1", "v1").build());
	}

	@Test
	public void putRequestWithMultipleParameters() throws IOException {
		this.snippets.expectHttpieRequest().withContents(
				codeBlock("bash").content("$ http --form PUT 'http://localhost/foo'"
						+ " 'k1=v1' 'k1=v1-bis' 'k2=v2'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").method("PUT").param("k1", "v1")
				.param("k1", "v1-bis").param("k2", "v2").build());
	}

	@Test
	public void putRequestWithUrlEncodedParameter() throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http --form PUT 'http://localhost/foo' 'k1=a&b'"));
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo")
						.method("PUT").param("k1", "a&b").build());
	}

	@Test
	public void requestWithHeaders() throws IOException {
		this.snippets.expectHttpieRequest().withContents(
				codeBlock("bash").content("$ http GET 'http://localhost/foo'"
						+ " 'Content-Type:application/json' 'a:alpha'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header("a", "alpha").build());
	}

	@Test
	public void requestWithHeadersMultiline() throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content(String.format("$ http GET 'http://localhost/foo' \\%n"
						+ "    'Content-Type:application/json' \\%n    'a:alpha'")));
		new HttpieRequestSnippet(CliDocumentation.multiLineFormat())
				.document(this.operationBuilder.request("http://localhost/foo")
						.header(HttpHeaders.CONTENT_TYPE,
								MediaType.APPLICATION_JSON_VALUE)
						.header("a", "alpha").build());
	}

	@Test
	public void requestWithCookies() throws IOException {
		this.snippets.expectHttpieRequest().withContents(
				codeBlock("bash").content("$ http GET 'http://localhost/foo'"
						+ " 'Cookie:name1=value1' 'Cookie:name2=value2'"));
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo")
						.cookie("name1", "value1").cookie("name2", "value2").build());
	}

	@Test
	public void multipartPostWithNoSubmittedFileName() throws IOException {
		String expectedContent = "$ http --form POST 'http://localhost/upload'"
				+ " 'metadata'@<(echo '{\"description\": \"foo\"}')";
		this.snippets.expectHttpieRequest()
				.withContents(codeBlock("bash").content(expectedContent));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("metadata", "{\"description\": \"foo\"}".getBytes()).build());
	}

	@Test
	public void multipartPostWithContentType() throws IOException {
		// httpie does not yet support manually set content type by part
		String expectedContent = "$ http --form POST 'http://localhost/upload'"
				+ " 'image'@'documents/images/example.png'";
		this.snippets.expectHttpieRequest()
				.withContents(codeBlock("bash").content(expectedContent));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", new byte[0])
				.header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
				.submittedFileName("documents/images/example.png").build());
	}

	@Test
	public void multipartPost() throws IOException {
		String expectedContent = "$ http --form POST 'http://localhost/upload'"
				+ " 'image'@'documents/images/example.png'";
		this.snippets.expectHttpieRequest()
				.withContents(codeBlock("bash").content(expectedContent));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", new byte[0])
				.submittedFileName("documents/images/example.png").build());
	}

	@Test
	public void multipartPostWithParameters() throws IOException {
		String expectedContent = "$ http --form POST 'http://localhost/upload'"
				+ " 'image'@'documents/images/example.png' 'a=apple' 'a=avocado'"
				+ " 'b=banana'";
		this.snippets.expectHttpieRequest()
				.withContents(codeBlock("bash").content(expectedContent));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", new byte[0])
				.submittedFileName("documents/images/example.png").and()
				.param("a", "apple", "avocado").param("b", "banana").build());
	}

	@Test
	public void basicAuthCredentialsAreSuppliedUsingAuthOption() throws IOException {
		this.snippets.expectHttpieRequest().withContents(codeBlock("bash")
				.content("$ http --auth 'user:secret' GET 'http://localhost/foo'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo")
				.header(HttpHeaders.AUTHORIZATION,
						"Basic " + Base64Utils.encodeToString("user:secret".getBytes()))
				.build());
	}

	@Test
	public void customAttributes() throws IOException {
		this.snippets.expectHttpieRequest()
				.withContents(containsString("httpie request title"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("httpie-request"))
				.willReturn(snippetResource("httpie-request-with-title"));
		new HttpieRequestSnippet(
				attributes(key("title")
						.value("httpie request title")),
				this.commandFormatter)
						.document(this.operationBuilder
								.attribute(TemplateEngine.class.getName(),
										new MustacheTemplateEngine(resolver))
								.request("http://localhost/foo").build());
	}

	@Test
	public void customHostHeaderIsIncluded() throws IOException {
		this.snippets.expectHttpieRequest()
				.withContents(codeBlock("bash").content(
						"$ http GET 'http://localhost/foo' 'Host:api.example.com'"
								+ " 'Content-Type:application/json' 'a:alpha'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo")
				.header(HttpHeaders.HOST, "api.example.com")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header("a", "alpha").build());
	}

	@Test
	public void postWithContentAndParameters() throws IOException {
		this.snippets.expectHttpieRequest().withContents(
				codeBlock("bash").content("$ echo 'Some content' | http POST "
						+ "'http://localhost/foo?a=alpha&b=bravo'"));
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").method("POST").param("a", "alpha")
				.param("b", "bravo").content("Some content").build());
	}

}
