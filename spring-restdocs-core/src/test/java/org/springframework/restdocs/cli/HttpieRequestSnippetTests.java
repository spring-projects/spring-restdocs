/*
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.util.Base64Utils;

import static org.assertj.core.api.Assertions.assertThat;

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
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo").build());
		assertThat(this.generatedSnippets.httpieRequest())
				.is(codeBlock("bash").withContent("$ http GET 'http://localhost/foo'"));
	}

	@Test
	public void getRequestWithParameter() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").param("a", "alpha").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http GET 'http://localhost/foo?a=alpha'"));
	}

	@Test
	public void nonGetRequest() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").method("POST").build());
		assertThat(this.generatedSnippets.httpieRequest())
				.is(codeBlock("bash").withContent("$ http POST 'http://localhost/foo'"));
	}

	@Test
	public void requestWithContent() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").content("content").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ echo 'content' | http GET 'http://localhost/foo'"));
	}

	@Test
	public void getRequestWithQueryString() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo?param=value").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http GET 'http://localhost/foo?param=value'"));
	}

	@Test
	public void getRequestWithTotallyOverlappingQueryStringAndParameters()
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(
				this.operationBuilder.request("http://localhost/foo?param=value")
						.param("param", "value").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http GET 'http://localhost/foo?param=value'"));
	}

	@Test
	public void getRequestWithPartiallyOverlappingQueryStringAndParameters()
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo?a=alpha")
						.param("a", "alpha").param("b", "bravo").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http GET 'http://localhost/foo?a=alpha&b=bravo'"));
	}

	@Test
	public void getRequestWithDisjointQueryStringAndParameters() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo?a=alpha").param("b", "bravo").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http GET 'http://localhost/foo?a=alpha&b=bravo'"));
	}

	@Test
	public void getRequestWithQueryStringWithNoValue() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(
				this.operationBuilder.request("http://localhost/foo?param").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(
				codeBlock("bash").withContent("$ http GET 'http://localhost/foo?param'"));
	}

	@Test
	public void postRequestWithQueryString() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo?param=value").method("POST").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http POST 'http://localhost/foo?param=value'"));
	}

	@Test
	public void postRequestWithQueryStringWithNoValue() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo?param").method("POST").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http POST 'http://localhost/foo?param'"));
	}

	@Test
	public void postRequestWithOneParameter() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo")
						.method("POST").param("k1", "v1").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http --form POST 'http://localhost/foo' 'k1=v1'"));
	}

	@Test
	public void postRequestWithOneParameterWithNoValue() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").method("POST").param("k1").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http --form POST 'http://localhost/foo' 'k1='"));
	}

	@Test
	public void postRequestWithMultipleParameters() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(
				this.operationBuilder.request("http://localhost/foo").method("POST")
						.param("k1", "v1", "v1-bis").param("k2", "v2").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(
				codeBlock("bash").withContent("$ http --form POST 'http://localhost/foo'"
						+ " 'k1=v1' 'k1=v1-bis' 'k2=v2'"));
	}

	@Test
	public void postRequestWithUrlEncodedParameter() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo")
						.method("POST").param("k1", "a&b").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http --form POST 'http://localhost/foo' 'k1=a&b'"));
	}

	@Test
	public void postRequestWithDisjointQueryStringAndParameter() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo?a=alpha")
						.method("POST").param("b", "bravo").build());
		assertThat(this.generatedSnippets.httpieRequest())
				.is(codeBlock("bash").withContent(
						"$ http --form POST 'http://localhost/foo?a=alpha' 'b=bravo'"));
	}

	@Test
	public void postRequestWithTotallyOverlappingQueryStringAndParameters()
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(
				this.operationBuilder.request("http://localhost/foo?a=alpha&b=bravo")
						.method("POST").param("a", "alpha").param("b", "bravo").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http POST 'http://localhost/foo?a=alpha&b=bravo'"));
	}

	@Test
	public void postRequestWithPartiallyOverlappingQueryStringAndParameters()
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo?a=alpha")
						.method("POST").param("a", "alpha").param("b", "bravo").build());
		assertThat(this.generatedSnippets.httpieRequest())
				.is(codeBlock("bash").withContent(
						"$ http --form POST 'http://localhost/foo?a=alpha' 'b=bravo'"));
	}

	@Test
	public void postRequestWithOverlappingParametersAndFormUrlEncodedBody()
			throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").method("POST").content("a=alpha&b=bravo")
				.header(HttpHeaders.CONTENT_TYPE,
						MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("a", "alpha").param("b", "bravo").build());
		assertThat(this.generatedSnippets.httpieRequest())
				.is(codeBlock("bash").withContent(
						"$ echo 'a=alpha&b=bravo' | http POST 'http://localhost/foo' "
								+ "'Content-Type:application/x-www-form-urlencoded'"));
	}

	@Test
	public void putRequestWithOneParameter() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").method("PUT").param("k1", "v1").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http --form PUT 'http://localhost/foo' 'k1=v1'"));
	}

	@Test
	public void putRequestWithMultipleParameters() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").method("PUT").param("k1", "v1")
				.param("k1", "v1-bis").param("k2", "v2").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(
				codeBlock("bash").withContent("$ http --form PUT 'http://localhost/foo'"
						+ " 'k1=v1' 'k1=v1-bis' 'k2=v2'"));
	}

	@Test
	public void putRequestWithUrlEncodedParameter() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo")
						.method("PUT").param("k1", "a&b").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http --form PUT 'http://localhost/foo' 'k1=a&b'"));
	}

	@Test
	public void requestWithHeaders() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header("a", "alpha").build());
		assertThat(this.generatedSnippets.httpieRequest())
				.is(codeBlock("bash").withContent("$ http GET 'http://localhost/foo'"
						+ " 'Content-Type:application/json' 'a:alpha'"));
	}

	@Test
	public void requestWithHeadersMultiline() throws IOException {
		new HttpieRequestSnippet(CliDocumentation.multiLineFormat())
				.document(this.operationBuilder.request("http://localhost/foo")
						.header(HttpHeaders.CONTENT_TYPE,
								MediaType.APPLICATION_JSON_VALUE)
						.header("a", "alpha").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent(String.format("$ http GET 'http://localhost/foo' \\%n"
						+ "    'Content-Type:application/json' \\%n    'a:alpha'")));
	}

	@Test
	public void requestWithCookies() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter)
				.document(this.operationBuilder.request("http://localhost/foo")
						.cookie("name1", "value1").cookie("name2", "value2").build());
		assertThat(this.generatedSnippets.httpieRequest())
				.is(codeBlock("bash").withContent("$ http GET 'http://localhost/foo'"
						+ " 'Cookie:name1=value1' 'Cookie:name2=value2'"));
	}

	@Test
	public void multipartPostWithNoSubmittedFileName() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("metadata", "{\"description\": \"foo\"}".getBytes()).build());
		String expectedContent = "$ http --form POST 'http://localhost/upload'"
				+ " 'metadata'@<(echo '{\"description\": \"foo\"}')";
		assertThat(this.generatedSnippets.httpieRequest())
				.is(codeBlock("bash").withContent(expectedContent));
	}

	@Test
	public void multipartPostWithContentType() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", new byte[0])
				.header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
				.submittedFileName("documents/images/example.png").build());
		// httpie does not yet support manually set content type by part
		String expectedContent = "$ http --form POST 'http://localhost/upload'"
				+ " 'image'@'documents/images/example.png'";
		assertThat(this.generatedSnippets.httpieRequest())
				.is(codeBlock("bash").withContent(expectedContent));
	}

	@Test
	public void multipartPost() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", new byte[0])
				.submittedFileName("documents/images/example.png").build());
		String expectedContent = "$ http --form POST 'http://localhost/upload'"
				+ " 'image'@'documents/images/example.png'";
		assertThat(this.generatedSnippets.httpieRequest())
				.is(codeBlock("bash").withContent(expectedContent));
	}

	@Test
	public void multipartPostWithParameters() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", new byte[0])
				.submittedFileName("documents/images/example.png").and()
				.param("a", "apple", "avocado").param("b", "banana").build());
		String expectedContent = "$ http --form POST 'http://localhost/upload'"
				+ " 'image'@'documents/images/example.png' 'a=apple' 'a=avocado'"
				+ " 'b=banana'";
		assertThat(this.generatedSnippets.httpieRequest())
				.is(codeBlock("bash").withContent(expectedContent));
	}

	@Test
	public void basicAuthCredentialsAreSuppliedUsingAuthOption() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo")
				.header(HttpHeaders.AUTHORIZATION,
						"Basic " + Base64Utils.encodeToString("user:secret".getBytes()))
				.build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http --auth 'user:secret' GET 'http://localhost/foo'"));
	}

	@Test
	public void customAttributes() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo")
				.header(HttpHeaders.HOST, "api.example.com")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.header("a", "alpha").build());
		assertThat(this.generatedSnippets.httpieRequest()).is(codeBlock("bash")
				.withContent("$ http GET 'http://localhost/foo' 'Host:api.example.com'"
						+ " 'Content-Type:application/json' 'a:alpha'"));
	}

	@Test
	public void postWithContentAndParameters() throws IOException {
		new HttpieRequestSnippet(this.commandFormatter).document(this.operationBuilder
				.request("http://localhost/foo").method("POST").param("a", "alpha")
				.param("b", "bravo").content("Some content").build());
		assertThat(this.generatedSnippets.httpieRequest())
				.is(codeBlock("bash").withContent("$ echo 'Some content' | http POST "
						+ "'http://localhost/foo?a=alpha&b=bravo'"));
	}

}
