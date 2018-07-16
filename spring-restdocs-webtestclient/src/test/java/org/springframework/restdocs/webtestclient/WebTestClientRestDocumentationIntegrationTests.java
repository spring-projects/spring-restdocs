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

package org.springframework.restdocs.webtestclient;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.templates.TemplateFormats.asciidoctor;
import static org.springframework.restdocs.test.SnippetMatchers.codeBlock;
import static org.springframework.restdocs.test.SnippetMatchers.httpResponse;
import static org.springframework.restdocs.test.SnippetMatchers.snippet;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithHeader;
import static org.springframework.restdocs.test.SnippetMatchers.tableWithTitleAndHeader;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * Integration tests for using Spring REST Docs with Spring Framework's WebTestClient.
 *
 * @author Andy Wilkinson
 */
public class WebTestClientRestDocumentationIntegrationTests {

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	private WebTestClient webTestClient;

	@Before
	public void setUp() {
		RouterFunction<ServerResponse> route = RouterFunctions
				.route(RequestPredicates.GET("/"),
						(request) -> ServerResponse.status(HttpStatus.OK)
								.body(fromObject(new Person("Jane", "Doe"))))
				.andRoute(RequestPredicates.GET("/{foo}/{bar}"),
						(request) -> ServerResponse.status(HttpStatus.OK)
								.body(fromObject(new Person("Jane", "Doe"))))
				.andRoute(RequestPredicates.POST("/upload"), (request) -> {
					return request.body(BodyExtractors.toMultipartData()).map((parts) -> {
						return ServerResponse.status(HttpStatus.OK).build().block();
					});
				}).andRoute(RequestPredicates.GET("/set-cookie"),
						(request) -> ServerResponse.ok()
								.cookie(ResponseCookie.from("name", "value")
										.domain("localhost").httpOnly(true).build())
								.build());
		this.webTestClient = WebTestClient.bindToRouterFunction(route).configureClient()
				.baseUrl("https://api.example.com")
				.filter(documentationConfiguration(this.restDocumentation)).build();
	}

	@Test
	public void defaultSnippetGeneration() {
		File outputDir = new File("build/generated-snippets/default-snippets");
		FileSystemUtils.deleteRecursively(outputDir);
		this.webTestClient.get().uri("/").exchange().expectStatus().isOk().expectBody()
				.consumeWith(document("default-snippets"));
		assertExpectedSnippetFilesExist(outputDir, "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "httpie-request.adoc",
				"request-body.adoc", "response-body.adoc");
	}

	@Test
	public void pathParametersSnippet() {
		this.webTestClient.get().uri("/{foo}/{bar}", "1", "2").exchange().expectStatus()
				.isOk().expectBody()
				.consumeWith(document("path-parameters", pathParameters(
						parameterWithName("foo").description("Foo description"),
						parameterWithName("bar").description("Bar description"))));
		assertThat(
				new File("build/generated-snippets/path-parameters/path-parameters.adoc"),
				is(snippet(asciidoctor()).withContents(
						tableWithTitleAndHeader(TemplateFormats.asciidoctor(),
								"+/{foo}/{bar}+", "Parameter", "Description")
										.row("`foo`", "Foo description")
										.row("`bar`", "Bar description"))));
	}

	@Test
	public void requestParametersSnippet() {
		this.webTestClient.get().uri("/?a=alpha&b=bravo").exchange().expectStatus().isOk()
				.expectBody()
				.consumeWith(document("request-parameters", requestParameters(
						parameterWithName("a").description("Alpha description"),
						parameterWithName("b").description("Bravo description"))));
		assertThat(new File(
				"build/generated-snippets/request-parameters/request-parameters.adoc"),
				is(snippet(asciidoctor()).withContents(
						tableWithHeader(TemplateFormats.asciidoctor(), "Parameter",
								"Description").row("`a`", "Alpha description").row("`b`",
										"Bravo description"))));
	}

	@Test
	public void multipart() throws Exception {
		MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
		multipartData.add("a", "alpha");
		multipartData.add("b", "bravo");
		Consumer<EntityExchangeResult<byte[]>> documentation = document("multipart",
				requestParts(partWithName("a").description("Part a"),
						partWithName("b").description("Part b")));
		this.webTestClient.post().uri("/upload")
				.body(BodyInserters.fromMultipartData(multipartData)).exchange()
				.expectStatus().isOk().expectBody().consumeWith(documentation);
		assertThat(new File("build/generated-snippets/multipart/request-parts.adoc"),
				is(snippet(asciidoctor())
						.withContents(tableWithHeader(TemplateFormats.asciidoctor(),
								"Part", "Description").row("`a`", "Part a").row("`b`",
										"Part b"))));
	}

	@Test
	public void responseWithSetCookie() throws Exception {
		this.webTestClient.get().uri("/set-cookie").exchange().expectStatus().isOk()
				.expectBody().consumeWith(document("set-cookie"));
		assertThat(new File("build/generated-snippets/set-cookie/http-response.adoc"),
				is(snippet(asciidoctor())
						.withContents(httpResponse(asciidoctor(), HttpStatus.OK).header(
								HttpHeaders.SET_COOKIE,
								"name=value; Domain=localhost; HttpOnly"))));
	}

	@Test
	public void curlSnippetWithCookies() throws Exception {
		this.webTestClient.get().uri("/").cookie("cookieName", "cookieVal")
				.accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectBody().consumeWith(document("curl-snippet-with-cookies"));
		assertThat(new File(
				"build/generated-snippets/curl-snippet-with-cookies/curl-request.adoc"),
				is(snippet(asciidoctor())
						.withContents(codeBlock(asciidoctor(), "bash").content(String
								.format("$ curl 'https://api.example.com/' -i -X GET \\%n"
										+ "    -H 'Accept: application/json' \\%n"
										+ "    --cookie 'cookieName=cookieVal'")))));
	}

	@Test
	public void httpieSnippetWithCookies() throws Exception {
		this.webTestClient.get().uri("/").cookie("cookieName", "cookieVal")
				.accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectBody().consumeWith(document("httpie-snippet-with-cookies"));
		assertThat(new File(
				"build/generated-snippets/httpie-snippet-with-cookies/httpie-request.adoc"),
				is(snippet(asciidoctor())
						.withContents(codeBlock(asciidoctor(), "bash").content(
								String.format("$ http GET 'https://api.example.com/' \\%n"
										+ "    'Accept:application/json' \\%n"
										+ "    'Cookie:cookieName=cookieVal'")))));
	}

	private void assertExpectedSnippetFilesExist(File directory, String... snippets) {
		Set<File> actual = new HashSet<>(Arrays.asList(directory.listFiles()));
		Set<File> expected = Stream.of(snippets)
				.map((snippet) -> new File(directory, snippet))
				.collect(Collectors.toSet());
		assertThat(actual, equalTo(expected));
	}

	/**
	 * A person.
	 */
	static class Person {

		private final String firstName;

		private final String lastName;

		Person(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public String getFirstName() {
			return this.firstName;
		}

		public String getLastName() {
			return this.lastName;
		}

	}

}
