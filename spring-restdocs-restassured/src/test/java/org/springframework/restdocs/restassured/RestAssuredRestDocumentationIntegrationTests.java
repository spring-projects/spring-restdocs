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

package org.springframework.restdocs.restassured;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentationIntegrationTests.TestApplication;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.maskLinks;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.replacePattern;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.restassured.operation.preprocess.RestAssuredPreprocessors.modifyUris;
import static org.springframework.restdocs.templates.TemplateFormats.asciidoctor;
import static org.springframework.restdocs.test.SnippetMatchers.codeBlock;
import static org.springframework.restdocs.test.SnippetMatchers.httpRequest;
import static org.springframework.restdocs.test.SnippetMatchers.httpResponse;
import static org.springframework.restdocs.test.SnippetMatchers.snippet;

/**
 * Integration tests for using Spring REST Docs with REST Assured.
 *
 * @author Andy Wilkinson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
public class RestAssuredRestDocumentationIntegrationTests {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation(
			"build/generated-snippets");

	@Value("${local.server.port}")
	private int port;

	@Test
	public void defaultSnippetGeneration() {
		given().port(this.port).filter(documentationConfiguration(this.restDocumentation))
				.filter(document("default")).get("/").then().statusCode(200);
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/default"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc");
	}

	@Test
	public void curlSnippetWithContent() throws Exception {
		String contentType = "text/plain; charset=UTF-8";
		given().port(this.port).filter(documentationConfiguration(this.restDocumentation))
				.filter(document("curl-snippet-with-content")).accept("application/json")
				.content("content").contentType(contentType).post("/").then()
				.statusCode(200);

		assertThat(
				new File(
						"build/generated-snippets/curl-snippet-with-content/curl-request.adoc"),
				is(snippet(asciidoctor()).withContents(codeBlock(asciidoctor(), "bash")
						.content("$ curl 'http://localhost:" + this.port + "/' -i "
								+ "-X POST -H 'Accept: application/json' "
								+ "-H 'Content-Type: " + contentType + "' "
								+ "-d 'content'"))));
	}

	@Test
	public void curlSnippetWithQueryStringOnPost() throws Exception {
		given().port(this.port).filter(documentationConfiguration(this.restDocumentation))
				.filter(document("curl-snippet-with-query-string"))
				.accept("application/json").param("foo", "bar").param("a", "alpha")
				.post("/?foo=bar").then().statusCode(200);
		String contentType = "application/x-www-form-urlencoded; charset=ISO-8859-1";
		assertThat(
				new File(
						"build/generated-snippets/curl-snippet-with-query-string/curl-request.adoc"),
				is(snippet(asciidoctor()).withContents(codeBlock(asciidoctor(), "bash")
						.content("$ curl " + "'http://localhost:" + this.port
								+ "/?foo=bar' -i -X POST "
								+ "-H 'Accept: application/json' " + "-H 'Content-Type: "
								+ contentType + "' " + "-d 'a=alpha'"))));
	}

	@Test
	public void linksSnippet() throws Exception {
		given().port(this.port).filter(documentationConfiguration(this.restDocumentation))
				.filter(document("links",
						links(linkWithRel("rel").description("The description"))))
				.accept("application/json").get("/").then().statusCode(200);
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/links"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc",
				"links.adoc");
	}

	@Test
	public void pathParametersSnippet() throws Exception {
		given().port(this.port).filter(documentationConfiguration(this.restDocumentation))
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
		given().port(this.port).filter(documentationConfiguration(this.restDocumentation))
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
		given().port(this.port).filter(documentationConfiguration(this.restDocumentation))
				.filter(document("request-fields",
						requestFields(fieldWithPath("a").description("The description"))))
				.accept("application/json").content("{\"a\":\"alpha\"}").post("/").then()
				.statusCode(200);
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/request-fields"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "request-fields.adoc");
	}

	@Test
	public void responseFieldsSnippet() throws Exception {
		given().port(this.port).filter(documentationConfiguration(this.restDocumentation))
				.filter(document("response-fields",
						responseFields(fieldWithPath("a").description("The description"),
								fieldWithPath("links")
										.description("Links to other resources"))))
				.accept("application/json").get("/").then().statusCode(200);

		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/response-fields"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "response-fields.adoc");
	}

	@Test
	public void parameterizedOutputDirectory() throws Exception {
		given().port(this.port).filter(documentationConfiguration(this.restDocumentation))
				.filter(document("{method-name}")).get("/").then().statusCode(200);
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/parameterized-output-directory"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc");
	}

	@Test
	public void multiStep() throws Exception {
		RequestSpecification spec = new RequestSpecBuilder().setPort(this.port)
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
	public void preprocessedRequest() throws Exception {
		Pattern pattern = Pattern.compile("(\"alpha\")");
		given().port(this.port).filter(documentationConfiguration(this.restDocumentation))
				.header("a", "alpha").header("b", "bravo").contentType("application/json")
				.accept("application/json").content("{\"a\":\"alpha\"}")
				.filter(document("original-request"))
				.filter(document("preprocessed-request",
						preprocessRequest(prettyPrint(),
								removeHeaders("a", HttpHeaders.HOST,
										HttpHeaders.CONTENT_LENGTH),
								replacePattern(pattern, "\"<<beta>>\""))))
				.get("/").then().statusCode(200);
		assertThat(
				new File("build/generated-snippets/original-request/http-request.adoc"),
				is(snippet(asciidoctor())
						.withContents(httpRequest(asciidoctor(), RequestMethod.GET, "/")
								.header("a", "alpha").header("b", "bravo")
								.header("Accept", MediaType.APPLICATION_JSON_VALUE)
								.header("Content-Type", "application/json; charset=UTF-8")
								.header("Host", "localhost")
								.header("Content-Length", "13")
								.content("{\"a\":\"alpha\"}"))));
		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\"%n}");
		assertThat(
				new File(
						"build/generated-snippets/preprocessed-request/http-request.adoc"),
				is(snippet(asciidoctor())
						.withContents(httpRequest(asciidoctor(), RequestMethod.GET, "/")
								.header("b", "bravo")
								.header("Accept", MediaType.APPLICATION_JSON_VALUE)
								.header("Content-Type", "application/json; charset=UTF-8")
								.content(prettyPrinted))));
	}

	@Test
	public void preprocessedResponse() throws Exception {
		Pattern pattern = Pattern.compile("(\"alpha\")");
		given().port(this.port).filter(documentationConfiguration(this.restDocumentation))
				.filter(document("original-response"))
				.filter(document("preprocessed-response", preprocessResponse(
						prettyPrint(), maskLinks(),
						removeHeaders("a", "Transfer-Encoding", "Date", "Server"),
						replacePattern(pattern, "\"<<beta>>\""), modifyUris()
								.scheme("https").host("api.example.com").removePort())))
				.get("/").then().statusCode(200);
		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\",%n  \"links\" : "
				+ "[ {%n    \"rel\" : \"rel\",%n    \"href\" : \"...\"%n  } ]%n}");
		assertThat(
				new File(
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
	public void customSnippetTemplate() throws Exception {
		ClassLoader classLoader = new URLClassLoader(new URL[] {
				new File("src/test/resources/custom-snippet-templates").toURI().toURL() },
				getClass().getClassLoader());
		ClassLoader previous = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try {
			given().port(this.port).accept("application/json")
					.filter(documentationConfiguration(this.restDocumentation))
					.filter(document("custom-snippet-template")).get("/").then()
					.statusCode(200);
		}
		finally {
			Thread.currentThread().setContextClassLoader(previous);
		}
		assertThat(
				new File(
						"build/generated-snippets/custom-snippet-template/curl-request.adoc"),
				is(snippet(asciidoctor()).withContents(equalTo("Custom curl request"))));
	}

	private void assertExpectedSnippetFilesExist(File directory, String... snippets) {
		for (String snippet : snippets) {
			File snippetFile = new File(directory, snippet);
			assertTrue("Snippet " + snippetFile + " not found", snippetFile.isFile());
		}
	}

	/**
	 * Minimal test application called by the tests.
	 */
	@Configuration
	@EnableAutoConfiguration
	@RestController
	static class TestApplication {

		@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<Map<String, Object>> foo() {
			Map<String, Object> response = new HashMap<>();
			response.put("a", "alpha");
			Map<String, String> link = new HashMap<>();
			link.put("rel", "rel");
			link.put("href", "href");
			response.put("links", Arrays.asList(link));
			HttpHeaders headers = new HttpHeaders();
			headers.add("a", "alpha");
			headers.add("Foo", "http://localhost:12345/foo/bar");
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}

		@RequestMapping(value = "/company/5", produces = MediaType.APPLICATION_JSON_VALUE)
		public String bar() {
			return "{\"companyName\": \"FooBar\",\"employee\": [{\"name\": \"Lorem\",\"age\": \"42\"},{\"name\": \"Ipsum\",\"age\": \"24\"}]}";
		}

	}

}
