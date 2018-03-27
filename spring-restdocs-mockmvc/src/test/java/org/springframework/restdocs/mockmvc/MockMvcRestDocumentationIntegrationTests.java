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

package org.springframework.restdocs.mockmvc;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationIntegrationTests.TestConfiguration;
import org.springframework.restdocs.test.SnippetMatchers.HttpRequestMatcher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.IterableEnumeration.iterable;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.maskLinks;
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
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.templates.TemplateFormats.asciidoctor;
import static org.springframework.restdocs.templates.TemplateFormats.markdown;
import static org.springframework.restdocs.test.SnippetMatchers.codeBlock;
import static org.springframework.restdocs.test.SnippetMatchers.httpRequest;
import static org.springframework.restdocs.test.SnippetMatchers.httpResponse;
import static org.springframework.restdocs.test.SnippetMatchers.snippet;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for using Spring REST Docs with Spring Test's MockMvc.
 *
 * @author Andy Wilkinson
 * @author Dewet Diener
 * @author Tomasz Kopczynski
 * @author Filip Hrisafov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = TestConfiguration.class)
public class MockMvcRestDocumentationIntegrationTests {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@Autowired
	private WebApplicationContext context;

	@Before
	public void deleteSnippets() {
		FileSystemUtils.deleteRecursively(new File("build/generated-snippets"));
	}

	@After
	public void clearOutputDirSystemProperty() {
		System.clearProperty("org.springframework.restdocs.outputDir");
	}

	@Test
	public void basicSnippetGeneration() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(new MockMvcRestDocumentationConfigurer(this.restDocumentation)
						.snippets().withEncoding("UTF-8"))
				.build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(document("basic"));
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/basic"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc");
	}

	@Test
	public void markdownSnippetGeneration() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(new MockMvcRestDocumentationConfigurer(this.restDocumentation)
						.snippets().withEncoding("UTF-8").withTemplateFormat(markdown()))
				.build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(document("basic-markdown"));
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/basic-markdown"), "http-request.md",
				"http-response.md", "curl-request.md");
	}

	@Test
	public void curlSnippetWithContent() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		mockMvc.perform(post("/").accept(MediaType.APPLICATION_JSON).content("content"))
				.andExpect(status().isOk()).andDo(document("curl-snippet-with-content"));
		assertThat(new File(
				"build/generated-snippets/curl-snippet-with-content/curl-request.adoc"),
				is(snippet(asciidoctor())
						.withContents(codeBlock(asciidoctor(), "bash").content(String
								.format("$ curl 'http://localhost:8080/' -i -X POST \\%n"
										+ "    -H 'Accept: application/json' \\%n"
										+ "    -d 'content'")))));
	}

	@Test
	public void curlSnippetWithCookies() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)
				.cookie(new Cookie("cookieName", "cookieVal"))).andExpect(status().isOk())
				.andDo(document("curl-snippet-with-cookies"));
		assertThat(new File(
				"build/generated-snippets/curl-snippet-with-cookies/curl-request.adoc"),
				is(snippet(asciidoctor())
						.withContents(codeBlock(asciidoctor(), "bash").content(String
								.format("$ curl 'http://localhost:8080/' -i -X GET \\%n"
										+ "    -H 'Accept: application/json' \\%n"
										+ "    --cookie 'cookieName=cookieVal'")))));
	}

	@Test
	public void curlSnippetWithQueryStringOnPost() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();
		mockMvc.perform(post("/?foo=bar").param("foo", "bar").param("a", "alpha")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andDo(document("curl-snippet-with-query-string"));
		assertThat(new File(
				"build/generated-snippets/curl-snippet-with-query-string/curl-request.adoc"),
				is(snippet(asciidoctor()).withContents(
						codeBlock(asciidoctor(), "bash").content(String.format("$ curl "
								+ "'http://localhost:8080/?foo=bar' -i -X POST \\%n"
								+ "    -H 'Accept: application/json' \\%n"
								+ "    -d 'a=alpha'")))));
	}

	@Test
	public void curlSnippetWithContentAndParametersOnPost() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();
		mockMvc.perform(post("/").param("a", "alpha").accept(MediaType.APPLICATION_JSON)
				.content("some content")).andExpect(status().isOk())
				.andDo(document("curl-snippet-with-content-and-parameters"));
		assertThat(new File(
				"build/generated-snippets/curl-snippet-with-content-and-parameters/curl-request.adoc"),
				is(snippet(asciidoctor()).withContents(
						codeBlock(asciidoctor(), "bash").content(String.format(
								"$ curl 'http://localhost:8080/?a=alpha' -i -X POST \\%n"
										+ "    -H 'Accept: application/json' \\%n"
										+ "    -d 'some content'")))));
	}

	@Test
	public void httpieSnippetWithContent() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		mockMvc.perform(post("/").accept(MediaType.APPLICATION_JSON).content("content"))
				.andExpect(status().isOk())
				.andDo(document("httpie-snippet-with-content"));
		assertThat(new File(
				"build/generated-snippets/httpie-snippet-with-content/httpie-request.adoc"),
				is(snippet(asciidoctor()).withContents(codeBlock(asciidoctor(), "bash")
						.content(String.format("$ echo 'content' | "
								+ "http POST 'http://localhost:8080/' \\%n"
								+ "    'Accept:application/json'")))));
	}

	@Test
	public void httpieSnippetWithCookies() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)
				.cookie(new Cookie("cookieName", "cookieVal"))).andExpect(status().isOk())
				.andDo(document("httpie-snippet-with-cookies"));
		assertThat(new File(
				"build/generated-snippets/httpie-snippet-with-cookies/httpie-request.adoc"),
				is(snippet(asciidoctor()).withContents(codeBlock(asciidoctor(), "bash")
						.content(String.format("$ http GET 'http://localhost:8080/' \\%n"
								+ "    'Accept:application/json' \\%n"
								+ "    'Cookie:cookieName=cookieVal'")))));
	}

	@Test
	public void httpieSnippetWithQueryStringOnPost() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();
		mockMvc.perform(post("/?foo=bar").param("foo", "bar").param("a", "alpha")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andDo(document("httpie-snippet-with-query-string"));
		assertThat(new File(
				"build/generated-snippets/httpie-snippet-with-query-string/httpie-request.adoc"),
				is(snippet(asciidoctor()).withContents(
						codeBlock(asciidoctor(), "bash").content(String.format("$ http "
								+ "--form POST 'http://localhost:8080/?foo=bar' \\%n"
								+ "    'Accept:application/json' \\%n    'a=alpha'")))));
	}

	@Test
	public void httpieSnippetWithContentAndParametersOnPost() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();
		mockMvc.perform(post("/").param("a", "alpha").content("some content")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andDo(document("httpie-snippet-post-with-content-and-parameters"));
		assertThat(new File(
				"build/generated-snippets/httpie-snippet-post-with-content-and-parameters/httpie-request.adoc"),
				is(snippet(asciidoctor()).withContents(codeBlock(asciidoctor(), "bash")
						.content(String.format("$ echo " + "'some content' | http POST "
								+ "'http://localhost:8080/?a=alpha' \\%n"
								+ "    'Accept:application/json'")))));
	}

	@Test
	public void linksSnippet() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(document("links",
						links(linkWithRel("rel").description("The description"))));

		assertExpectedSnippetFilesExist(new File("build/generated-snippets/links"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc",
				"links.adoc");
	}

	@Test
	public void pathParametersSnippet() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		mockMvc.perform(get("{foo}", "/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(document("links", pathParameters(
						parameterWithName("foo").description("The description"))));

		assertExpectedSnippetFilesExist(new File("build/generated-snippets/links"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc",
				"path-parameters.adoc");
	}

	@Test
	public void requestParametersSnippet() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		mockMvc.perform(get("/").param("foo", "bar").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(document("links", requestParameters(
						parameterWithName("foo").description("The description"))));

		assertExpectedSnippetFilesExist(new File("build/generated-snippets/links"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc",
				"request-parameters.adoc");
	}

	@Test
	public void requestFieldsSnippet() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		mockMvc.perform(get("/").param("foo", "bar").content("{\"a\":\"alpha\"}")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andDo(document("links", requestFields(
						fieldWithPath("a").description("The description"))));

		assertExpectedSnippetFilesExist(new File("build/generated-snippets/links"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc",
				"request-fields.adoc");
	}

	@Test
	public void requestPartsSnippet() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		mockMvc.perform(multipart("/upload").file("foo", "bar".getBytes()))
				.andExpect(status().isOk()).andDo(document("request-parts", requestParts(
						partWithName("foo").description("The description"))));

		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/request-parts"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "request-parts.adoc");
	}

	@Test
	public void responseFieldsSnippet() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		mockMvc.perform(get("/").param("foo", "bar").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("links",
						responseFields(fieldWithPath("a").description("The description"),
								subsectionWithPath("links")
										.description("Links to other resources"))));

		assertExpectedSnippetFilesExist(new File("build/generated-snippets/links"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc",
				"response-fields.adoc");
	}

	@Test
	public void responseWithSetCookie() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		mockMvc.perform(get("/set-cookie")).andExpect(status().isOk())
				.andDo(document("set-cookie",
						responseHeaders(headerWithName(HttpHeaders.SET_COOKIE)
								.description("set-cookie"))));

		assertThat(new File("build/generated-snippets/set-cookie/http-response.adoc"),
				is(snippet(asciidoctor())
						.withContents(httpResponse(asciidoctor(), HttpStatus.OK).header(
								HttpHeaders.SET_COOKIE,
								"name=value; Domain=localhost; HttpOnly"))));
	}

	@Test
	public void parameterizedOutputDirectory() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(document("{method-name}"));
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/parameterized-output-directory"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc");
	}

	@Test
	public void multiStep() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation))
				.alwaysDo(document("{method-name}-{step}")).build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/multi-step-1/"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/multi-step-2/"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/multi-step-3/"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");
	}

	@Test
	public void alwaysDoWithAdditionalSnippets() throws Exception {
		RestDocumentationResultHandler documentation = document("{method-name}-{step}");
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation))
				.alwaysDo(documentation).build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(documentation.document(
						responseHeaders(headerWithName("a").description("one"))));

		assertExpectedSnippetFilesExist(
				new File(
						"build/generated-snippets/always-do-with-additional-snippets-1/"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc",
				"response-headers.adoc");
	}

	@Test
	public void preprocessedRequest() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		Pattern pattern = Pattern.compile("(\"alpha\")");

		MvcResult result = mockMvc
				.perform(get("/").header("a", "alpha").header("b", "bravo")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content("{\"a\":\"alpha\"}"))
				.andExpect(status().isOk()).andDo(document("original-request"))
				.andDo(document("preprocessed-request",
						preprocessRequest(prettyPrint(),
								removeHeaders("a", HttpHeaders.HOST,
										HttpHeaders.CONTENT_LENGTH),
								replacePattern(pattern, "\"<<beta>>\""))))
				.andReturn();

		HttpRequestMatcher originalRequest = httpRequest(asciidoctor(), RequestMethod.GET,
				"/");
		for (String headerName : iterable(result.getRequest().getHeaderNames())) {
			originalRequest.header(headerName, result.getRequest().getHeader(headerName));
		}
		assertThat(
				new File("build/generated-snippets/original-request/http-request.adoc"),
				is(snippet(asciidoctor()).withContents(originalRequest
						.header("Host", "localhost:8080").header("Content-Length", "13")
						.content("{\"a\":\"alpha\"}"))));
		HttpRequestMatcher preprocessedRequest = httpRequest(asciidoctor(),
				RequestMethod.GET, "/");
		List<String> removedHeaders = Arrays.asList("a", HttpHeaders.HOST,
				HttpHeaders.CONTENT_LENGTH);
		for (String headerName : iterable(result.getRequest().getHeaderNames())) {
			if (!removedHeaders.contains(headerName)) {
				preprocessedRequest.header(headerName,
						result.getRequest().getHeader(headerName));
			}
		}
		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\"%n}");
		assertThat(new File(
				"build/generated-snippets/preprocessed-request/http-request.adoc"),
				is(snippet(asciidoctor())
						.withContents(preprocessedRequest.content(prettyPrinted))));
	}

	@Test
	public void defaultPreprocessedRequest() throws Exception {
		Pattern pattern = Pattern.compile("(\"alpha\")");
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)
						.operationPreprocessors().withRequestDefaults(prettyPrint(),
								removeHeaders("a", HttpHeaders.HOST,
										HttpHeaders.CONTENT_LENGTH),
								replacePattern(pattern, "\"<<beta>>\"")))
				.build();

		MvcResult result = mockMvc
				.perform(get("/").header("a", "alpha").header("b", "bravo")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON).content("{\"a\":\"alpha\"}"))
				.andDo(document("default-preprocessed-request")).andReturn();

		HttpRequestMatcher preprocessedRequest = httpRequest(asciidoctor(),
				RequestMethod.GET, "/");
		List<String> removedHeaders = Arrays.asList("a", HttpHeaders.HOST,
				HttpHeaders.CONTENT_LENGTH);
		for (String headerName : iterable(result.getRequest().getHeaderNames())) {
			if (!removedHeaders.contains(headerName)) {
				preprocessedRequest.header(headerName,
						result.getRequest().getHeader(headerName));
			}
		}
		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\"%n}");
		assertThat(new File(
				"build/generated-snippets/default-preprocessed-request/http-request.adoc"),
				is(snippet(asciidoctor())
						.withContents(preprocessedRequest.content(prettyPrinted))));
	}

	@Test
	public void preprocessedResponse() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		Pattern pattern = Pattern.compile("(\"alpha\")");

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(document("original-response"))
				.andDo(document("preprocessed-response",
						preprocessResponse(prettyPrint(), maskLinks(), removeHeaders("a"),
								replacePattern(pattern, "\"<<beta>>\""))));

		String original = "{\"a\":\"alpha\",\"links\":[{\"rel\":\"rel\","
				+ "\"href\":\"href\"}]}";
		assertThat(
				new File("build/generated-snippets/original-response/http-response.adoc"),
				is(snippet(asciidoctor()).withContents(
						httpResponse(asciidoctor(), HttpStatus.OK).header("a", "alpha")
								.header("Content-Type", "application/json;charset=UTF-8")
								.header(HttpHeaders.CONTENT_LENGTH,
										original.getBytes().length)
								.content(original))));
		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\",%n  \"links\" : "
				+ "[ {%n    \"rel\" : \"rel\",%n    \"href\" : \"...\"%n  } ]%n}");
		assertThat(new File(
				"build/generated-snippets/preprocessed-response/http-response.adoc"),
				is(snippet(asciidoctor())
						.withContents(httpResponse(asciidoctor(), HttpStatus.OK)
								.header("Content-Type", "application/json;charset=UTF-8")
								.header(HttpHeaders.CONTENT_LENGTH,
										prettyPrinted.getBytes().length)
								.content(prettyPrinted))));
	}

	@Test
	public void defaultPreprocessedResponse() throws Exception {
		Pattern pattern = Pattern.compile("(\"alpha\")");
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)
						.operationPreprocessors().withResponseDefaults(prettyPrint(),
								maskLinks(), removeHeaders("a"),
								replacePattern(pattern, "\"<<beta>>\"")))
				.build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("default-preprocessed-response"));

		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\",%n  \"links\" : "
				+ "[ {%n    \"rel\" : \"rel\",%n    \"href\" : \"...\"%n  } ]%n}");
		assertThat(new File(
				"build/generated-snippets/default-preprocessed-response/http-response.adoc"),
				is(snippet(asciidoctor())
						.withContents(httpResponse(asciidoctor(), HttpStatus.OK)
								.header("Content-Type", "application/json;charset=UTF-8")
								.header(HttpHeaders.CONTENT_LENGTH,
										prettyPrinted.getBytes().length)
								.content(prettyPrinted))));
	}

	@Test
	public void customSnippetTemplate() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		ClassLoader classLoader = new URLClassLoader(new URL[] {
				new File("src/test/resources/custom-snippet-templates").toURI().toURL() },
				getClass().getClassLoader());
		ClassLoader previous = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try {
			mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andDo(document("custom-snippet-template"));
		}
		finally {
			Thread.currentThread().setContextClassLoader(previous);
		}
		assertThat(new File(
				"build/generated-snippets/custom-snippet-template/curl-request.adoc"),
				is(snippet(asciidoctor()).withContents(equalTo("Custom curl request"))));

		mockMvc.perform(get("/")).andDo(document("index", curlRequest(
				attributes(key("title").value("Access the index using curl")))));
	}

	@Test
	public void customContextPath() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();

		mockMvc.perform(
				get("/custom/").contextPath("/custom").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(document("custom-context-path"));
		assertThat(
				new File(
						"build/generated-snippets/custom-context-path/curl-request.adoc"),
				is(snippet(asciidoctor()).withContents(
						codeBlock(asciidoctor(), "bash").content(String.format(
								"$ curl 'http://localhost:8080/custom/' -i -X GET \\%n"
										+ "    -H 'Accept: application/json'")))));
	}

	@Test
	public void multiPart() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).build();
		mockMvc.perform(multipart("/upload").file("test", "content".getBytes()))
				.andExpect(status().isOk()).andDo(document("upload",
						requestParts(partWithName("test").description("Foo"))));
	}

	private void assertExpectedSnippetFilesExist(File directory, String... snippets) {
		for (String snippet : snippets) {
			assertTrue(new File(directory, snippet).isFile());
		}
	}

	/**
	 * Test configuration that enables Spring MVC.
	 */
	@Configuration
	@EnableWebMvc
	static class TestConfiguration {

		@Bean
		public TestController testController() {
			return new TestController();
		}

	}

	@RestController
	private static class TestController {

		@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
		public ResponseEntity<Map<String, Object>> foo() {
			Map<String, Object> response = new HashMap<>();
			response.put("a", "alpha");
			Map<String, String> link = new HashMap<>();
			link.put("rel", "rel");
			link.put("href", "href");
			response.put("links", Arrays.asList(link));
			HttpHeaders headers = new HttpHeaders();
			headers.add("a", "alpha");
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}

		@RequestMapping(value = "/company/5", produces = MediaType.APPLICATION_JSON_VALUE)
		public String bar() {
			return "{\"companyName\": \"FooBar\",\"employee\": [{\"name\": \"Lorem\",\"age\": \"42\"},{\"name\": \"Ipsum\",\"age\": \"24\"}]}";
		}

		@RequestMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
		public void upload() {

		}

		@RequestMapping("/set-cookie")
		public void setCookie(HttpServletResponse response) {
			Cookie cookie = new Cookie("name", "value");
			cookie.setDomain("localhost");
			cookie.setHttpOnly(true);

			response.addCookie(cookie);
		}

	}

}
