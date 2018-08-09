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

package org.springframework.restdocs.request;

import java.io.IOException;

import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.AbstractSnippetTests;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link RequestUriSnippet}.
 *
 * @author Ryan O'Meara
 */
public class RequestUriSnippetTests extends AbstractSnippetTests {

	private static final String BOUNDARY = "6o2knFse3p53ty9dmcQvWAIx1zInP11uCfbm";

	public RequestUriSnippetTests(String name, TemplateFormat templateFormat) {
		super(name, templateFormat);
	}

	@Test
	public void getRequest() throws IOException {
		this.snippets.expectRequestUri().withContents(
				requestUri(RequestMethod.GET, "/{a}/{b}").header("Accept", "a"));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/foo/bar").header("Accept", "a").build());
	}

	@Test
	public void getRequestWithParameters() throws IOException {
		this.snippets.expectRequestUri()
				.withContents(requestUri(RequestMethod.GET, "/{a}/{b}"));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/foo").header("Alpha", "a").param("b", "bravo")
				.build());
	}
	
	@Test
	public void getRequestWithQueryString() throws IOException {
		this.snippets.expectRequestUri()
				.withContents(requestUri(RequestMethod.GET, "/{a}/{b}"));

		new RequestUriSnippet()
				.document(this.operationBuilder
						.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
								"/{a}/{b}")
						.request("http://localhost/foo?bar=baz").build());
	}

	@Test
	public void getRequestWithQueryStringWithNoValue() throws IOException {
		this.snippets.expectRequestUri()
				.withContents(requestUri(RequestMethod.GET, "/{a}/{b}"));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/foo?bar").build());
	}

	@Test
	public void getWithTotallyOverlappingQueryStringAndParameters() throws IOException {
		this.snippets.expectRequestUri()
				.withContents(requestUri(RequestMethod.GET, "/{a}/{b}"));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/foo?a=alpha&b=bravo").param("a", "alpha")
				.param("b", "bravo").build());
	}

	@Test
	public void postRequestWithContent() throws IOException {
		String content = "Hello, world";
		this.snippets.expectRequestUri()
				.withContents(requestUri(RequestMethod.POST, "/{a}/{b}"));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/foo").method("POST").content(content).build());
	}

	@Test
	public void postRequestWithContentAndParameters() throws IOException {
		String content = "Hello, world";
		this.snippets.expectRequestUri()
				.withContents(requestUri(RequestMethod.POST, "/{a}/{b}"));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/foo").method("POST").param("a", "alpha")
				.content(content).build());
	}

	@Test
	public void postRequestWithContentAndOverlappingQueryStringAndParameters()
			throws IOException {
		String content = "Hello, world";
		this.snippets.expectRequestUri()
				.withContents(requestUri(RequestMethod.POST, "/{a}/{b}"));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/foo?b=bravo&a=alpha").method("POST")
				.param("a", "alpha").param("b", "bravo").content(content).build());
	}

	@Test
	public void postRequestWithCharset() throws IOException {
		String japaneseContent = "\u30b3\u30f3\u30c6\u30f3\u30c4";
		byte[] contentBytes = japaneseContent.getBytes("UTF-8");
		this.snippets.expectRequestUri()
				.withContents(requestUri(RequestMethod.POST, "/{a}/{b}")
						.header("Content-Type", "text/plain;charset=UTF-8"));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/foo").method("POST")
				.header("Content-Type", "text/plain;charset=UTF-8").content(contentBytes)
				.build());
	}

	@Test
	public void postRequestWithParameter() throws IOException {
		this.snippets.expectRequestUri()
				.withContents(requestUri(RequestMethod.POST, "/{a}/{b}")
						.header("Content-Type", "application/x-www-form-urlencoded"));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/foo").method("POST").param("b&r", "baz")
				.param("a", "alpha").build());
	}

	@Test
	public void postRequestWithParameterWithNoValue() throws IOException {
		this.snippets.expectRequestUri()
				.withContents(requestUri(RequestMethod.POST, "/{a}/{b}")
						.header("Content-Type", "application/x-www-form-urlencoded"));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/foo").method("POST").param("bar").build());
	}

	@Test
	public void putRequestWithContent() throws IOException {
		String content = "Hello, world";
		this.snippets.expectRequestUri()
				.withContents(requestUri(RequestMethod.PUT, "/{a}/{b}"));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/foo").method("PUT").content(content).build());
	}

	@Test
	public void putRequestWithParameter() throws IOException {
		this.snippets.expectRequestUri()
				.withContents(requestUri(RequestMethod.PUT, "/{a}/{b}")
						.header("Content-Type", "application/x-www-form-urlencoded"));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/foo").method("PUT").param("b&r", "baz")
				.param("a", "alpha").build());
	}

	@Test
	public void multipartPost() throws IOException {
		this.snippets.expectRequestUri().withContents(
				requestUri(RequestMethod.POST, "/{a}/{b}").header("Content-Type",
						"multipart/form-data; boundary=" + BOUNDARY));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", "<< data >>".getBytes()).build());
	}

	@Test
	public void multipartPostWithFilename() throws IOException {
		this.snippets.expectRequestUri().withContents(
				requestUri(RequestMethod.POST, "/{a}/{b}").header("Content-Type",
						"multipart/form-data; boundary=" + BOUNDARY));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", "<< data >>".getBytes()).submittedFileName("image.png")
				.build());
	}

	@Test
	public void multipartPostWithParameters() throws IOException {
		this.snippets.expectRequestUri().withContents(
				requestUri(RequestMethod.POST, "/{a}/{b}").header("Content-Type",
						"multipart/form-data; boundary=" + BOUNDARY));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.param("a", "apple", "avocado").param("b", "banana")
				.part("image", "<< data >>".getBytes()).build());
	}

	@Test
	public void multipartPostWithParameterWithNoValue() throws IOException {
		this.snippets.expectRequestUri().withContents(
				requestUri(RequestMethod.POST, "/{a}/{b}").header("Content-Type",
						"multipart/form-data; boundary=" + BOUNDARY));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.param("a").part("image", "<< data >>".getBytes()).build());
	}

	@Test
	public void multipartPostWithContentType() throws IOException {
		this.snippets.expectRequestUri().withContents(
				requestUri(RequestMethod.POST, "/{a}/{b}").header("Content-Type",
						"multipart/form-data; boundary=" + BOUNDARY));

		new RequestUriSnippet().document(this.operationBuilder
				.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
						"/{a}/{b}")
				.request("http://localhost/upload").method("POST")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
				.part("image", "<< data >>".getBytes())
				.header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE).build());
	}

	@Test
	public void requestWithCustomSnippetAttributes() throws IOException {
		this.snippets.expectRequestUri()
				.withContents(containsString("Title for the request"));
		TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
		given(resolver.resolveTemplateResource("request-uri"))
				.willReturn(snippetResource("request-uri-with-title"));

		new RequestUriSnippet(attributes(key("title").value("Title for the request")))
				.document(this.operationBuilder
						.attribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
								"/{a}/{b}")
						.attribute(TemplateEngine.class.getName(),
								new MustacheTemplateEngine(resolver))
						.request("http://localhost/foo").build());
	}

}
