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

package org.springframework.restdocs.restassured3;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.web.bind.annotation.RequestMethod;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.maskLinks;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.replacePattern;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.templates.TemplateFormats.asciidoctor;
import static org.springframework.restdocs.test.SnippetMatchers.codeBlock;
import static org.springframework.restdocs.test.SnippetMatchers.httpRequest;
import static org.springframework.restdocs.test.SnippetMatchers.httpResponse;
import static org.springframework.restdocs.test.SnippetMatchers.snippet;

/**
 * Integration tests for using Spring REST Docs with REST Assured.
 *
 * @author Andy Wilkinson
 * @author Tomasz Kopczynski
 * @author Filip Hrisafov
 */
public class RestAssuredRestDocumentationIntegrationTests {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@ClassRule
	public static TomcatServer tomcat = new TomcatServer();

	@Test
	public void defaultSnippetGeneration() {
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.filter(document("default")).get("/").then().statusCode(200);
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/default"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc");
	}

	@Test
	public void curlSnippetWithContent() throws Exception {
		String contentType = "text/plain; charset=UTF-8";
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.filter(document("curl-snippet-with-content")).accept("application/json")
				.body("content").contentType(contentType).post("/").then()
				.statusCode(200);

		assertThat(new File(
				"build/generated-snippets/curl-snippet-with-content/curl-request.adoc"),
				is(snippet(asciidoctor()).withContents(codeBlock(asciidoctor(), "bash")
						.content(String.format("$ curl 'http://localhost:"
								+ tomcat.getPort() + "/' -i -X POST \\%n"
								+ "    -H 'Accept: application/json' \\%n"
								+ "    -H 'Content-Type: " + contentType + "' \\%n"
								+ "    -d 'content'")))));
	}

	@Test
	public void curlSnippetWithCookies() throws Exception {
		String contentType = "text/plain; charset=UTF-8";
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.filter(document("curl-snippet-with-cookies")).accept("application/json")
				.contentType(contentType).cookie("cookieName", "cookieVal").get("/")
				.then().statusCode(200);
		assertThat(new File(
				"build/generated-snippets/curl-snippet-with-cookies/curl-request.adoc"),
				is(snippet(asciidoctor()).withContents(codeBlock(asciidoctor(), "bash")
						.content(String.format("$ curl 'http://localhost:"
								+ tomcat.getPort() + "/' -i -X GET \\%n"
								+ "    -H 'Accept: application/json' \\%n"
								+ "    -H 'Content-Type: " + contentType + "' \\%n"
								+ "    --cookie 'cookieName=cookieVal'")))));
	}

	@Test
	public void curlSnippetWithQueryStringOnPost() throws Exception {
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.filter(document("curl-snippet-with-query-string"))
				.accept("application/json").param("foo", "bar").param("a", "alpha")
				.post("/?foo=bar").then().statusCode(200);
		String contentType = "application/x-www-form-urlencoded; charset=ISO-8859-1";
		assertThat(new File(
				"build/generated-snippets/curl-snippet-with-query-string/curl-request.adoc"),
				is(snippet(asciidoctor()).withContents(codeBlock(asciidoctor(), "bash")
						.content(String.format("$ curl " + "'http://localhost:"
								+ tomcat.getPort() + "/?foo=bar' -i -X POST \\%n"
								+ "    -H 'Accept: application/json' \\%n"
								+ "    -H 'Content-Type: " + contentType + "' \\%n"
								+ "    -d 'a=alpha'")))));
	}

	@Test
	public void linksSnippet() throws Exception {
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.filter(document("links",
						links(linkWithRel("rel").description("The description"))))
				.accept("application/json").get("/").then().statusCode(200);
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/links"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc",
				"links.adoc");
	}

	@Test
	public void pathParametersSnippet() throws Exception {
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.filter(document("path-parameters",
						pathParameters(
								parameterWithName("foo").description("The description"))))
				.accept("application/json").get("/{foo}", "").then().statusCode(200);
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/path-parameters"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "path-parameters.adoc");
	}

	@Test
	public void requestParametersSnippet() throws Exception {
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.filter(document("request-parameters",
						requestParameters(
								parameterWithName("foo").description("The description"))))
				.accept("application/json").param("foo", "bar").get("/").then()
				.statusCode(200);
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/request-parameters"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc",
				"request-parameters.adoc");
	}

	@Test
	public void requestFieldsSnippet() throws Exception {
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.filter(document("request-fields",
						requestFields(fieldWithPath("a").description("The description"))))
				.accept("application/json").body("{\"a\":\"alpha\"}").post("/").then()
				.statusCode(200);
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/request-fields"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "request-fields.adoc");
	}

	@Test
	public void requestPartsSnippet() throws Exception {
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.filter(document("request-parts",
						requestParts(partWithName("a").description("The description"))))
				.multiPart("a", "foo").post("/upload").then().statusCode(200);
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/request-parts"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "request-parts.adoc");
	}

	@Test
	public void responseFieldsSnippet() throws Exception {
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.filter(document("response-fields",
						responseFields(fieldWithPath("a").description("The description"),
								subsectionWithPath("links")
										.description("Links to other resources"))))
				.accept("application/json").get("/").then().statusCode(200);

		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/response-fields"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "response-fields.adoc");
	}

	@Test
	public void parameterizedOutputDirectory() throws Exception {
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.filter(document("{method-name}")).get("/").then().statusCode(200);
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/parameterized-output-directory"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc");
	}

	@Test
	public void multiStep() throws Exception {
		RequestSpecification spec = new RequestSpecBuilder().setPort(tomcat.getPort())
				.addFilter(documentationConfiguration(this.restDocumentation))
				.addFilter(document("{method-name}-{step}")).build();
		given(spec).get("/").then().statusCode(200);
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/multi-step-1/"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");
		given(spec).get("/").then().statusCode(200);
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/multi-step-2/"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");
		given(spec).get("/").then().statusCode(200);
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/multi-step-3/"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");
	}

	@Test
	public void additionalSnippets() throws Exception {
		RestDocumentationFilter documentation = document("{method-name}-{step}");
		RequestSpecification spec = new RequestSpecBuilder().setPort(tomcat.getPort())
				.addFilter(documentationConfiguration(this.restDocumentation))
				.addFilter(documentation).build();
		given(spec)
				.filter(documentation
						.document(responseHeaders(headerWithName("a").description("one"),
								headerWithName("Foo").description("two"))))
				.get("/").then().statusCode(200);
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/additional-snippets-1/"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc",
				"response-headers.adoc");
	}

	@Test
	public void responseWithCookie() {
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.filter(document("set-cookie",
						preprocessResponse(removeHeaders(HttpHeaders.DATE,
								HttpHeaders.CONTENT_TYPE))))
				.get("/set-cookie").then().statusCode(200);
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/set-cookie"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc");

		assertThat(new File("build/generated-snippets/set-cookie/http-response.adoc"),
				is(snippet(asciidoctor())
						.withContents(httpResponse(asciidoctor(), HttpStatus.OK).header(
								HttpHeaders.SET_COOKIE,
								"name=value; Domain=localhost; HttpOnly"))));
	}

	@Test
	public void preprocessedRequest() throws Exception {
		Pattern pattern = Pattern.compile("(\"alpha\")");
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.header("a", "alpha").header("b", "bravo").contentType("application/json")
				.accept("application/json").body("{\"a\":\"alpha\"}")
				.filter(document("original-request"))
				.filter(document("preprocessed-request",
						preprocessRequest(prettyPrint(),
								replacePattern(pattern, "\"<<beta>>\""),
								modifyUris().removePort(),
								removeHeaders("a", HttpHeaders.CONTENT_LENGTH))))
				.get("/").then().statusCode(200);
		assertThat(
				new File("build/generated-snippets/original-request/http-request.adoc"),
				is(snippet(asciidoctor())
						.withContents(httpRequest(asciidoctor(), RequestMethod.GET, "/")
								.header("a", "alpha").header("b", "bravo")
								.header("Accept", MediaType.APPLICATION_JSON_VALUE)
								.header("Content-Type", "application/json; charset=UTF-8")
								.header("Host", "localhost:" + tomcat.getPort())
								.header("Content-Length", "13")
								.content("{\"a\":\"alpha\"}"))));
		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\"%n}");
		assertThat(new File(
				"build/generated-snippets/preprocessed-request/http-request.adoc"),
				is(snippet(asciidoctor())
						.withContents(httpRequest(asciidoctor(), RequestMethod.GET, "/")
								.header("b", "bravo")
								.header("Accept", MediaType.APPLICATION_JSON_VALUE)
								.header("Content-Type", "application/json; charset=UTF-8")
								.header("Host", "localhost").content(prettyPrinted))));
	}

	@Test
	public void defaultPreprocessedRequest() throws Exception {
		Pattern pattern = Pattern.compile("(\"alpha\")");
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation)
						.operationPreprocessors().withRequestDefaults(prettyPrint(),
								replacePattern(pattern, "\"<<beta>>\""),
								modifyUris().removePort(),
								removeHeaders("a", HttpHeaders.CONTENT_LENGTH)))
				.header("a", "alpha").header("b", "bravo").contentType("application/json")
				.accept("application/json").body("{\"a\":\"alpha\"}")
				.filter(document("default-preprocessed-request")).get("/").then()
				.statusCode(200);
		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\"%n}");
		assertThat(new File(
				"build/generated-snippets/default-preprocessed-request/http-request.adoc"),
				is(snippet(asciidoctor())
						.withContents(httpRequest(asciidoctor(), RequestMethod.GET, "/")
								.header("b", "bravo")
								.header("Accept", MediaType.APPLICATION_JSON_VALUE)
								.header("Content-Type", "application/json; charset=UTF-8")
								.header("Host", "localhost").content(prettyPrinted))));
	}

	@Test
	public void preprocessedResponse() throws Exception {
		Pattern pattern = Pattern.compile("(\"alpha\")");
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation))
				.filter(document("original-response"))
				.filter(document("preprocessed-response", preprocessResponse(
						prettyPrint(), maskLinks(),
						removeHeaders("a", "Transfer-Encoding", "Date", "Server"),
						replacePattern(pattern, "\"<<beta>>\""), modifyUris()
								.scheme("https").host("api.example.com").removePort())))
				.get("/").then().statusCode(200);
		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\",%n  \"links\" : "
				+ "[ {%n    \"rel\" : \"rel\",%n    \"href\" : \"...\"%n  } ]%n}");
		assertThat(new File(
				"build/generated-snippets/preprocessed-response/http-response.adoc"),
				is(snippet(asciidoctor())
						.withContents(httpResponse(asciidoctor(), HttpStatus.OK)
								.header("Foo", "https://api.example.com/foo/bar")
								.header("Content-Type", "application/json;charset=UTF-8")
								.header(HttpHeaders.CONTENT_LENGTH,
										prettyPrinted.getBytes().length)
								.content(prettyPrinted))));
	}

	@Test
	public void defaultPreprocessedResponse() throws Exception {
		Pattern pattern = Pattern.compile("(\"alpha\")");
		given().port(tomcat.getPort())
				.filter(documentationConfiguration(this.restDocumentation)
						.operationPreprocessors().withResponseDefaults(prettyPrint(),
								maskLinks(),
								removeHeaders("a", "Transfer-Encoding", "Date", "Server"),
								replacePattern(pattern, "\"<<beta>>\""),
								modifyUris().scheme("https").host("api.example.com")
										.removePort()))
				.filter(document("default-preprocessed-response")).get("/").then()
				.statusCode(200);
		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\",%n  \"links\" : "
				+ "[ {%n    \"rel\" : \"rel\",%n    \"href\" : \"...\"%n  } ]%n}");
		assertThat(new File(
				"build/generated-snippets/default-preprocessed-response/http-response.adoc"),
				is(snippet(asciidoctor())
						.withContents(httpResponse(asciidoctor(), HttpStatus.OK)
								.header("Foo", "https://api.example.com/foo/bar")
								.header("Content-Type", "application/json;charset=UTF-8")
								.header(HttpHeaders.CONTENT_LENGTH,
										prettyPrinted.getBytes().length)
								.content(prettyPrinted))));
	}

	@Test
	public void customSnippetTemplate() throws Exception {
		ClassLoader classLoader = new URLClassLoader(new URL[] {
				new File("src/test/resources/custom-snippet-templates").toURI().toURL() },
				getClass().getClassLoader());
		ClassLoader previous = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try {
			given().port(tomcat.getPort()).accept("application/json")
					.filter(documentationConfiguration(this.restDocumentation))
					.filter(document("custom-snippet-template")).get("/").then()
					.statusCode(200);
		}
		finally {
			Thread.currentThread().setContextClassLoader(previous);
		}
		assertThat(new File(
				"build/generated-snippets/custom-snippet-template/curl-request.adoc"),
				is(snippet(asciidoctor()).withContents(equalTo("Custom curl request"))));
	}

	private void assertExpectedSnippetFilesExist(File directory, String... snippets) {
		for (String snippet : snippets) {
			File snippetFile = new File(directory, snippet);
			assertTrue("Snippet " + snippetFile + " not found", snippetFile.isFile());
		}
	}

}
