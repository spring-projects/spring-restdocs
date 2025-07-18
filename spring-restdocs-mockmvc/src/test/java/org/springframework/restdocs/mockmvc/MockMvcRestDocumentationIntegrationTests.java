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

package org.springframework.restdocs.mockmvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationIntegrationTests.TestConfiguration;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.testfixtures.SnippetConditions;
import org.springframework.restdocs.testfixtures.SnippetConditions.CodeBlockCondition;
import org.springframework.restdocs.testfixtures.SnippetConditions.HttpRequestCondition;
import org.springframework.restdocs.testfixtures.SnippetConditions.HttpResponseCondition;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.maskLinks;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.replacePattern;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
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
@SpringJUnitConfig
@WebAppConfiguration
@ExtendWith(RestDocumentationExtension.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class MockMvcRestDocumentationIntegrationTests {

	private RestDocumentationContextProvider restDocumentation;

	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) {
		this.restDocumentation = restDocumentation;
		FileSystemUtils.deleteRecursively(new File("build/generated-snippets"));
	}

	@AfterEach
	void clearOutputDirSystemProperty() {
		System.clearProperty("org.springframework.restdocs.outputDir");
	}

	@Test
	void basicSnippetGeneration() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(new MockMvcRestDocumentationConfigurer(this.restDocumentation).snippets().withEncoding("UTF-8"))
			.build();
		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("basic"));
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/basic"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");
	}

	@Test
	void getRequestWithBody() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(new MockMvcRestDocumentationConfigurer(this.restDocumentation).snippets().withEncoding("UTF-8"))
			.build();
		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON).content("some body content"))
			.andExpect(status().isOk())
			.andDo(document("get-request-with-body"));
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/get-request-with-body"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");
	}

	@Test
	void markdownSnippetGeneration() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(new MockMvcRestDocumentationConfigurer(this.restDocumentation).snippets()
				.withEncoding("UTF-8")
				.withTemplateFormat(TemplateFormats.markdown()))
			.build();
		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("basic-markdown"));
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/basic-markdown"), "http-request.md",
				"http-response.md", "curl-request.md");
	}

	@Test
	void curlSnippetWithContent() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(post("/").accept(MediaType.APPLICATION_JSON).content("content"))
			.andExpect(status().isOk())
			.andDo(document("curl-snippet-with-content"));
		assertThat(new File("build/generated-snippets/curl-snippet-with-content/curl-request.adoc"))
			.has(content(codeBlock(TemplateFormats.asciidoctor(), "bash")
				.withContent(String.format("$ curl 'http://localhost:8080/' -i -X POST \\%n"
						+ "    -H 'Accept: application/json' \\%n" + "    -d 'content'"))));
	}

	@Test
	void curlSnippetWithCookies() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON).cookie(new Cookie("cookieName", "cookieVal")))
			.andExpect(status().isOk())
			.andDo(document("curl-snippet-with-cookies"));
		assertThat(new File("build/generated-snippets/curl-snippet-with-cookies/curl-request.adoc"))
			.has(content(codeBlock(TemplateFormats.asciidoctor(), "bash")
				.withContent(String.format("$ curl 'http://localhost:8080/' -i -X GET \\%n"
						+ "    -H 'Accept: application/json' \\%n" + "    --cookie 'cookieName=cookieVal'"))));
	}

	@Test
	void curlSnippetWithQueryStringOnPost() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(post("/?foo=bar").param("a", "alpha").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("curl-snippet-with-query-string"));
		assertThat(new File("build/generated-snippets/curl-snippet-with-query-string/curl-request.adoc"))
			.has(content(codeBlock(TemplateFormats.asciidoctor(), "bash")
				.withContent(String.format("$ curl " + "'http://localhost:8080/?foo=bar' -i -X POST \\%n"
						+ "    -H 'Accept: application/json' \\%n" + "    -d 'a=alpha'"))));
	}

	@Test
	void curlSnippetWithEmptyParameterQueryString() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(get("/").param("a", "").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("curl-snippet-with-empty-parameter-query-string"));
		assertThat(
				new File("build/generated-snippets/curl-snippet-with-empty-parameter-query-string/curl-request.adoc"))
			.has(content(codeBlock(TemplateFormats.asciidoctor(), "bash").withContent(String
				.format("$ curl 'http://localhost:8080/?a=' -i -X GET \\%n" + "    -H 'Accept: application/json'"))));
	}

	@Test
	void curlSnippetWithContentAndParametersOnPost() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(post("/").param("a", "alpha").accept(MediaType.APPLICATION_JSON).content("some content"))
			.andExpect(status().isOk())
			.andDo(document("curl-snippet-with-content-and-parameters"));
		assertThat(new File("build/generated-snippets/curl-snippet-with-content-and-parameters/curl-request.adoc"))
			.has(content(codeBlock(TemplateFormats.asciidoctor(), "bash")
				.withContent(String.format("$ curl 'http://localhost:8080/?a=alpha' -i -X POST \\%n"
						+ "    -H 'Accept: application/json' \\%n" + "    -d 'some content'"))));
	}

	@Test
	void httpieSnippetWithContent() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(post("/").accept(MediaType.APPLICATION_JSON).content("content"))
			.andExpect(status().isOk())
			.andDo(document("httpie-snippet-with-content"));
		assertThat(new File("build/generated-snippets/httpie-snippet-with-content/httpie-request.adoc")).has(
				content(codeBlock(TemplateFormats.asciidoctor(), "bash").withContent(String.format("$ echo 'content' | "
						+ "http POST 'http://localhost:8080/' \\%n" + "    'Accept:application/json'"))));
	}

	@Test
	void httpieSnippetWithCookies() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON).cookie(new Cookie("cookieName", "cookieVal")))
			.andExpect(status().isOk())
			.andDo(document("httpie-snippet-with-cookies"));
		assertThat(new File("build/generated-snippets/httpie-snippet-with-cookies/httpie-request.adoc"))
			.has(content(codeBlock(TemplateFormats.asciidoctor(), "bash")
				.withContent(String.format("$ http GET 'http://localhost:8080/' \\%n"
						+ "    'Accept:application/json' \\%n" + "    'Cookie:cookieName=cookieVal'"))));
	}

	@Test
	void httpieSnippetWithQueryStringOnPost() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(post("/?foo=bar").param("a", "alpha").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("httpie-snippet-with-query-string"));
		assertThat(new File("build/generated-snippets/httpie-snippet-with-query-string/httpie-request.adoc"))
			.has(content(codeBlock(TemplateFormats.asciidoctor(), "bash")
				.withContent(String.format("$ http --form POST 'http://localhost:8080/?foo=bar' \\%n"
						+ "    'Accept:application/json' \\%n    'a=alpha'"))));
	}

	@Test
	void httpieSnippetWithContentAndParametersOnPost() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(post("/").param("a", "alpha").content("some content").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("httpie-snippet-post-with-content-and-parameters"));
		assertThat(new File(
				"build/generated-snippets/httpie-snippet-post-with-content-and-parameters/httpie-request.adoc"))
			.has(content(codeBlock(TemplateFormats.asciidoctor(), "bash")
				.withContent(String.format("$ echo " + "'some content' | http POST "
						+ "'http://localhost:8080/?a=alpha' \\%n" + "    'Accept:application/json'"))));
	}

	@Test
	void linksSnippet() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("links", links(linkWithRel("rel").description("The description"))));

		assertExpectedSnippetFilesExist(new File("build/generated-snippets/links"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "links.adoc");
	}

	@Test
	void pathParametersSnippet() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(get("/{foo}", "").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("links", pathParameters(parameterWithName("foo").description("The description"))));
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/links"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "path-parameters.adoc");
	}

	@Test
	void queryParametersSnippet() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(get("/").param("foo", "bar").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("links", queryParameters(parameterWithName("foo").description("The description"))));
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/links"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "query-parameters.adoc");
	}

	@Test
	void requestFieldsSnippet() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(get("/").param("foo", "bar").content("{\"a\":\"alpha\"}").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("links", requestFields(fieldWithPath("a").description("The description"))));
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/links"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "request-fields.adoc");
	}

	@Test
	void requestPartsSnippet() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(multipart("/upload").file("foo", "bar".getBytes()))
			.andExpect(status().isOk())
			.andDo(document("request-parts", requestParts(partWithName("foo").description("The description"))));
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/request-parts"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "request-parts.adoc");
	}

	@Test
	void responseFieldsSnippet() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(get("/").param("foo", "bar").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("links", responseFields(fieldWithPath("a").description("The description"),
					subsectionWithPath("links").description("Links to other resources"))));
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/links"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc", "response-fields.adoc");
	}

	@Test
	void responseWithSetCookie() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(get("/set-cookie"))
			.andExpect(status().isOk())
			.andDo(document("set-cookie",
					responseHeaders(headerWithName(HttpHeaders.SET_COOKIE).description("set-cookie"))));
		assertThat(new File("build/generated-snippets/set-cookie/http-response.adoc"))
			.has(content(httpResponse(TemplateFormats.asciidoctor(), HttpStatus.OK).header(HttpHeaders.SET_COOKIE,
					"name=value; Domain=localhost; HttpOnly")));
	}

	@Test
	void parameterizedOutputDirectory() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("{method-name}"));
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/parameterized-output-directory"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc");
	}

	@Test
	void multiStep() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.alwaysDo(document("{method-name}-{step}"))
			.build();
		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/multi-step-1/"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");
		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/multi-step-2/"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/multi-step-3/"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");
	}

	@Test
	void alwaysDoWithAdditionalSnippets() throws Exception {
		RestDocumentationResultHandler documentation = document("{method-name}-{step}");
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.alwaysDo(documentation)
			.build();
		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(documentation.document(responseHeaders(headerWithName("a").description("one"))));
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/always-do-with-additional-snippets-1/"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc", "response-headers.adoc");
	}

	@Test
	void preprocessedRequest() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		Pattern pattern = Pattern.compile("(\"alpha\")");
		MvcResult result = mockMvc
			.perform(get("/").header("a", "alpha")
				.header("b", "bravo")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content("{\"a\":\"alpha\"}"))
			.andExpect(status().isOk())
			.andDo(document("original-request"))
			.andDo(document("preprocessed-request",
					preprocessRequest(prettyPrint(),
							modifyHeaders().remove("a").remove(HttpHeaders.HOST).remove(HttpHeaders.CONTENT_LENGTH),
							replacePattern(pattern, "\"<<beta>>\""))))
			.andReturn();
		HttpRequestCondition originalRequest = httpRequest(TemplateFormats.asciidoctor(), RequestMethod.GET, "/");
		Set<String> mvcResultHeaderNames = new HashSet<>();
		for (String headerName : IterableEnumeration.of(result.getRequest().getHeaderNames())) {
			originalRequest.header(headerName, result.getRequest().getHeader(headerName));
			mvcResultHeaderNames.add(headerName);
		}
		originalRequest.header("Host", "localhost:8080");
		if (!mvcResultHeaderNames.contains("Content-Length")) {
			originalRequest.header("Content-Length", "13");
		}
		assertThat(new File("build/generated-snippets/original-request/http-request.adoc"))
			.has(content(originalRequest.content("{\"a\":\"alpha\"}")));
		HttpRequestCondition preprocessedRequest = httpRequest(TemplateFormats.asciidoctor(), RequestMethod.GET, "/");
		List<String> removedHeaders = Arrays.asList("a", HttpHeaders.HOST, HttpHeaders.CONTENT_LENGTH);
		for (String headerName : IterableEnumeration.of(result.getRequest().getHeaderNames())) {
			if (!removedHeaders.contains(headerName)) {
				preprocessedRequest.header(headerName, result.getRequest().getHeader(headerName));
			}
		}
		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\"%n}");
		assertThat(new File("build/generated-snippets/preprocessed-request/http-request.adoc"))
			.has(content(preprocessedRequest.content(prettyPrinted)));
	}

	@Test
	void defaultPreprocessedRequest() throws Exception {
		Pattern pattern = Pattern.compile("(\"alpha\")");
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation).operationPreprocessors()
				.withRequestDefaults(prettyPrint(),
						modifyHeaders().remove("a").remove(HttpHeaders.HOST).remove(HttpHeaders.CONTENT_LENGTH),
						replacePattern(pattern, "\"<<beta>>\"")))
			.build();

		MvcResult result = mockMvc
			.perform(get("/").header("a", "alpha")
				.header("b", "bravo")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content("{\"a\":\"alpha\"}"))
			.andDo(document("default-preprocessed-request"))
			.andReturn();

		HttpRequestCondition preprocessedRequest = httpRequest(TemplateFormats.asciidoctor(), RequestMethod.GET, "/");
		List<String> removedHeaders = Arrays.asList("a", HttpHeaders.HOST, HttpHeaders.CONTENT_LENGTH);
		for (String headerName : IterableEnumeration.of(result.getRequest().getHeaderNames())) {
			if (!removedHeaders.contains(headerName)) {
				preprocessedRequest.header(headerName, result.getRequest().getHeader(headerName));
			}
		}
		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\"%n}");
		assertThat(new File("build/generated-snippets/default-preprocessed-request/http-request.adoc"))
			.has(content(preprocessedRequest.content(prettyPrinted)));
	}

	@Test
	void preprocessedResponse() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();

		Pattern pattern = Pattern.compile("(\"alpha\")");

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("original-response"))
			.andDo(document("preprocessed-response", preprocessResponse(prettyPrint(), maskLinks(),
					modifyHeaders().remove("a"), replacePattern(pattern, "\"<<beta>>\""))));

		String original = "{\"a\":\"alpha\",\"links\":[{\"rel\":\"rel\"," + "\"href\":\"href\"}]}";
		assertThat(new File("build/generated-snippets/original-response/http-response.adoc"))
			.has(content(httpResponse(TemplateFormats.asciidoctor(), HttpStatus.OK).header("a", "alpha")
				.header("Content-Type", "application/json;charset=UTF-8")
				.header(HttpHeaders.CONTENT_LENGTH, original.getBytes().length)
				.content(original)));
		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\",%n  \"links\" : "
				+ "[ {%n    \"rel\" : \"rel\",%n    \"href\" : \"...\"%n  } ]%n}");
		assertThat(new File("build/generated-snippets/preprocessed-response/http-response.adoc"))
			.has(content(httpResponse(TemplateFormats.asciidoctor(), HttpStatus.OK)
				.header("Content-Type", "application/json;charset=UTF-8")
				.header(HttpHeaders.CONTENT_LENGTH, prettyPrinted.getBytes().length)
				.content(prettyPrinted)));
	}

	@Test
	void defaultPreprocessedResponse() throws Exception {
		Pattern pattern = Pattern.compile("(\"alpha\")");
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation).operationPreprocessors()
				.withResponseDefaults(prettyPrint(), maskLinks(), modifyHeaders().remove("a"),
						replacePattern(pattern, "\"<<beta>>\"")))
			.build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("default-preprocessed-response"));

		String prettyPrinted = String.format("{%n  \"a\" : \"<<beta>>\",%n  \"links\" : "
				+ "[ {%n    \"rel\" : \"rel\",%n    \"href\" : \"...\"%n  } ]%n}");
		assertThat(new File("build/generated-snippets/default-preprocessed-response/http-response.adoc"))
			.has(content(httpResponse(TemplateFormats.asciidoctor(), HttpStatus.OK)
				.header("Content-Type", "application/json;charset=UTF-8")
				.header(HttpHeaders.CONTENT_LENGTH, prettyPrinted.getBytes().length)
				.content(prettyPrinted)));
	}

	@Test
	void customSnippetTemplate() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		ClassLoader classLoader = new URLClassLoader(
				new URL[] { new File("src/test/resources/custom-snippet-templates").toURI().toURL() },
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
		assertThat(new File("build/generated-snippets/custom-snippet-template/curl-request.adoc"))
			.hasContent("Custom curl request");
	}

	@Test
	void customContextPath() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();

		mockMvc.perform(get("/custom/").contextPath("/custom").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(document("custom-context-path"));
		assertThat(new File("build/generated-snippets/custom-context-path/curl-request.adoc"))
			.has(content(codeBlock(TemplateFormats.asciidoctor(), "bash").withContent(String.format(
					"$ curl 'http://localhost:8080/custom/' -i -X GET \\%n" + "    -H 'Accept: application/json'"))));
	}

	@Test
	void exceptionShouldBeThrownWhenCallDocumentMockMvcNotConfigured() {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
		assertThatThrownBy(() -> mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andDo(document("basic")))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("REST Docs configuration not found. Did you "
					+ "forget to apply a MockMvcRestDocumentationConfigurer when building the MockMvc instance?");

	}

	@Test
	void exceptionShouldBeThrownWhenCallDocumentSnippetsMockMvcNotConfigured() {
		RestDocumentationResultHandler documentation = document("{method-name}-{step}");
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
		assertThatThrownBy(() -> mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
			.andDo(documentation.document(responseHeaders(headerWithName("a").description("one")))))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("REST Docs configuration not found. Did you forget to apply a "
					+ "MockMvcRestDocumentationConfigurer when building the MockMvc instance?");
	}

	@Test
	void multiPart() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
			.apply(documentationConfiguration(this.restDocumentation))
			.build();
		mockMvc.perform(multipart("/upload").file("test", "content".getBytes()))
			.andExpect(status().isOk())
			.andDo(document("upload", requestParts(partWithName("test").description("Foo"))));
	}

	private void assertExpectedSnippetFilesExist(File directory, String... snippets) {
		for (String snippet : snippets) {
			assertThat(new File(directory, snippet)).isFile();
		}
	}

	private Condition<File> content(final Condition<String> delegate) {
		return new Condition<>() {

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

	private HttpRequestCondition httpRequest(TemplateFormat format, RequestMethod requestMethod, String uri) {
		return SnippetConditions.httpRequest(format, requestMethod, uri);
	}

	private HttpResponseCondition httpResponse(TemplateFormat format, HttpStatus status) {
		return SnippetConditions.httpResponse(format, status);
	}

	/**
	 * Test configuration that enables Spring MVC.
	 */
	@EnableWebMvc
	@Configuration(proxyBeanMethods = false)
	static final class TestConfiguration {

		@Bean
		TestController testController() {
			return new TestController();
		}

	}

	@RestController
	private static final class TestController {

		@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
		ResponseEntity<Map<String, Object>> foo() {
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
		String bar() {
			return "{\"companyName\": \"FooBar\",\"employee\": [{\"name\": \"Lorem\",\"age\": \"42\"},{\"name\": \"Ipsum\",\"age\": \"24\"}]}";
		}

		@RequestMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
		void upload() {

		}

		@RequestMapping("/set-cookie")
		void setCookie(HttpServletResponse response) {
			Cookie cookie = new Cookie("name", "value");
			cookie.setDomain("localhost");
			cookie.setHttpOnly(true);

			response.addCookie(cookie);
		}

	}

}
