/*
 * Copyright 2014-present the original author or authors.
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

package org.springframework.restdocs.resttestclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.Cookie;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.testfixtures.SnippetConditions;
import org.springframework.restdocs.testfixtures.SnippetConditions.CodeBlockCondition;
import org.springframework.restdocs.testfixtures.SnippetConditions.HttpResponseCondition;
import org.springframework.restdocs.testfixtures.SnippetConditions.TableCondition;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.document;
import static org.springframework.restdocs.resttestclient.RestTestClientRestDocumentation.documentationConfiguration;

/**
 * Integration tests for using Spring REST Docs with Spring Framework's
 * {@link RestTestClient}.
 *
 * @author Andy Wilkinson
 */
@ExtendWith(RestDocumentationExtension.class)
class RestTestClientRestDocumentationIntegrationTests {

	private RestTestClient restTestClient;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) {
		RouterFunction<ServerResponse> route = RouterFunctions
			.route(RequestPredicates.GET("/"),
					(request) -> ServerResponse.status(HttpStatus.OK).body(new Person("Jane", "Doe")))
			.andRoute(RequestPredicates.GET("/{foo}/{bar}"),
					(request) -> ServerResponse.status(HttpStatus.OK).body(new Person("Jane", "Doe")))
			.andRoute(RequestPredicates.POST("/upload"), (request) -> ServerResponse.status(HttpStatus.OK).build())
			.andRoute(RequestPredicates.GET("/set-cookie"), (request) -> {
				Cookie cookie = new Cookie("name", "value");
				cookie.setDomain("localhost");
				cookie.setHttpOnly(true);
				return ServerResponse.ok().cookie(cookie).build();
			});
		this.restTestClient = RestTestClient.bindToRouterFunction(route)
			.baseUrl("https://api.example.com")
			.requestInterceptor(documentationConfiguration(restDocumentation))
			.build();
	}

	@Test
	void defaultSnippetGeneration() {
		File outputDir = new File("build/generated-snippets/default-snippets");
		FileSystemUtils.deleteRecursively(outputDir);
		this.restTestClient.get()
			.uri("/")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("default-snippets"));
		assertExpectedSnippetFilesExist(outputDir, "http-request.adoc", "http-response.adoc", "curl-request.adoc",
				"httpie-request.adoc", "request-body.adoc", "response-body.adoc");
	}

	@Test
	void pathParametersSnippet() {
		this.restTestClient.get()
			.uri("/{foo}/{bar}", "1", "2")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(
					document("path-parameters", pathParameters(parameterWithName("foo").description("Foo description"),
							parameterWithName("bar").description("Bar description"))));
		assertThat(new File("build/generated-snippets/path-parameters/path-parameters.adoc")).has(content(
				tableWithTitleAndHeader(TemplateFormats.asciidoctor(), "+/{foo}/{bar}+", "Parameter", "Description")
					.row("`foo`", "Foo description")
					.row("`bar`", "Bar description")));
	}

	@Test
	void queryParametersSnippet() {
		this.restTestClient.get()
			.uri("/?a=alpha&b=bravo")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("query-parameters",
					queryParameters(parameterWithName("a").description("Alpha description"),
							parameterWithName("b").description("Bravo description"))));
		assertThat(new File("build/generated-snippets/query-parameters/query-parameters.adoc"))
			.has(content(tableWithHeader(TemplateFormats.asciidoctor(), "Parameter", "Description")
				.row("`a`", "Alpha description")
				.row("`b`", "Bravo description")));
	}

	@Test
	void multipart() {
		MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
		multipartData.add("a", "alpha");
		multipartData.add("b", "bravo");
		Consumer<EntityExchangeResult<byte[]>> documentation = document("multipart",
				requestParts(partWithName("a").description("Part a"), partWithName("b").description("Part b")));
		this.restTestClient.post()
			.uri("/upload")
			.body(multipartData)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(documentation);
		assertThat(new File("build/generated-snippets/multipart/request-parts.adoc"))
			.has(content(tableWithHeader(TemplateFormats.asciidoctor(), "Part", "Description").row("`a`", "Part a")
				.row("`b`", "Part b")));
	}

	@Test
	void responseWithSetCookie() {
		this.restTestClient.get()
			.uri("/set-cookie")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("set-cookie"));
		assertThat(new File("build/generated-snippets/set-cookie/http-response.adoc")).content()
			.has(httpResponse(TemplateFormats.asciidoctor(), HttpStatus.OK).header(HttpHeaders.SET_COOKIE,
					"name=value; Domain=localhost; HttpOnly"));
	}

	@Test
	void curlSnippetWithCookies() {
		this.restTestClient.get()
			.uri("/")
			.cookie("cookieName", "cookieVal")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("curl-snippet-with-cookies"));
		assertThat(new File("build/generated-snippets/curl-snippet-with-cookies/curl-request.adoc"))
			.has(content(codeBlock(TemplateFormats.asciidoctor(), "bash")
				.withContent(String.format("$ curl 'https://api.example.com/' -i -X GET \\%n"
						+ "    -H 'Accept: application/json' \\%n" + "    --cookie 'cookieName=cookieVal'"))));
	}

	@Test
	void curlSnippetWithEmptyParameterQueryString() {
		this.restTestClient.get()
			.uri("/?a=")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("curl-snippet-with-empty-parameter-query-string"));
		assertThat(
				new File("build/generated-snippets/curl-snippet-with-empty-parameter-query-string/curl-request.adoc"))
			.has(content(codeBlock(TemplateFormats.asciidoctor(), "bash").withContent(String
				.format("$ curl 'https://api.example.com/?a=' -i -X GET \\%n" + "    -H 'Accept: application/json'"))));
	}

	@Test
	void httpieSnippetWithCookies() {
		this.restTestClient.get()
			.uri("/")
			.cookie("cookieName", "cookieVal")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.consumeWith(document("httpie-snippet-with-cookies"));
		assertThat(new File("build/generated-snippets/httpie-snippet-with-cookies/httpie-request.adoc"))
			.has(content(codeBlock(TemplateFormats.asciidoctor(), "bash")
				.withContent(String.format("$ http GET 'https://api.example.com/' \\%n"
						+ "    'Accept:application/json' \\%n" + "    'Cookie:cookieName=cookieVal'"))));
	}

	@Test
	void illegalStateExceptionShouldBeThrownWhenCallDocumentRestTestClientNotConfigured() {
		assertThatThrownBy(() -> this.restTestClient.mutate()
			.requestInterceptors(List::clear)
			.build()
			.get()
			.uri("/")
			.exchange()
			.expectBody()
			.consumeWith(document("default-snippets"))).isInstanceOf(IllegalStateException.class)
			.hasMessage("REST Docs configuration not found. Did you forget to register a "
					+ "RestTestClientRestDocumentationConfigurer as an interceptor?");
	}

	private void assertExpectedSnippetFilesExist(File directory, String... snippets) {
		File[] files = directory.listFiles();
		assertThat(files).isNotNull();
		Set<File> actual = new HashSet<>(Arrays.asList(files));
		Set<File> expected = Stream.of(snippets)
			.map((snippet) -> new File(directory, snippet))
			.collect(Collectors.toSet());
		assertThat(actual).isEqualTo(expected);
	}

	private Condition<File> content(final Condition<String> delegate) {
		return new Condition<>(delegate.description()) {

			@Override
			public boolean matches(File value) {
				try {
					return delegate.matches(FileCopyUtils
						.copyToString(new InputStreamReader(new FileInputStream(value), StandardCharsets.UTF_8)));
				}
				catch (IOException ex) {
					fail("Failed to read '" + value + "'", ex);
					return false;
				}
			}

		};
	}

	private CodeBlockCondition<?> codeBlock(TemplateFormat format, String language) {
		return SnippetConditions.codeBlock(format, language);
	}

	private HttpResponseCondition httpResponse(TemplateFormat format, HttpStatus status) {
		return SnippetConditions.httpResponse(format, status);
	}

	private TableCondition<?> tableWithHeader(TemplateFormat format, String... headers) {
		return SnippetConditions.tableWithHeader(format, headers);
	}

	private TableCondition<?> tableWithTitleAndHeader(TemplateFormat format, String title, String... headers) {
		return SnippetConditions.tableWithTitleAndHeader(format, title, headers);
	}

	/**
	 * A person.
	 */
	public static class Person {

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
