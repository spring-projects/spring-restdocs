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

package org.springframework.restdocs;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.restdocs.RestDocumentation.modifyResponseTo;
import static org.springframework.restdocs.response.ResponsePostProcessors.maskLinks;
import static org.springframework.restdocs.response.ResponsePostProcessors.prettyPrintContent;
import static org.springframework.restdocs.response.ResponsePostProcessors.removeHeaders;
import static org.springframework.restdocs.response.ResponsePostProcessors.replacePattern;
import static org.springframework.restdocs.test.SnippetMatchers.httpResponse;
import static org.springframework.restdocs.test.SnippetMatchers.snippet;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.RestDocumentationIntegrationTests.TestConfiguration;
import org.springframework.restdocs.config.RestDocumentationConfigurer;
import org.springframework.restdocs.hypermedia.Link;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Integration tests for Spring REST Docs
 * 
 * @author Andy Wilkinson
 * @author Dewet Diener
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = TestConfiguration.class)
public class RestDocumentationIntegrationTests {

	@Autowired
	private WebApplicationContext context;

	@Before
	public void setOutputDirSystemProperty() {
		System.setProperty("org.springframework.restdocs.outputDir",
				"build/generated-snippets");
	}

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
				.apply(new RestDocumentationConfigurer()).build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(document("basic"));
		assertExpectedSnippetFilesExist(new File("build/generated-snippets/basic"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc");
	}

	@Test
	public void parameterizedOutputDirectory() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(new RestDocumentationConfigurer()).build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(document("{method-name}"));
		assertExpectedSnippetFilesExist(new File(
				"build/generated-snippets/parameterized-output-directory"),
				"http-request.adoc", "http-response.adoc", "curl-request.adoc");
	}

	@Test
	public void multiStep() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(new RestDocumentationConfigurer())
				.alwaysDo(document("{method-name}-{step}")).build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andExpect(
				status().isOk());
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/multi-step-1/"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andExpect(
				status().isOk());
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/multi-step-2/"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andExpect(
				status().isOk());
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/multi-step-3/"), "http-request.adoc",
				"http-response.adoc", "curl-request.adoc");
	}

	@Test
	public void postProcessedResponse() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(new RestDocumentationConfigurer()).build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(document("original"));

		assertThat(
				new File("build/generated-snippets/original/http-response.adoc"),
				is(snippet().withContents(
						httpResponse(HttpStatus.OK)
								.header("a", "alpha")
								.header("Content-Type", "application/json")
								.content(
										"{\"a\":\"alpha\",\"links\":[{\"rel\":\"rel\","
												+ "\"href\":\"href\"}]}"))));

		Pattern pattern = Pattern.compile("(\"alpha\")");
		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(modifyResponseTo(prettyPrintContent(), removeHeaders("a"),
						replacePattern(pattern, "\"<<beta>>\""), maskLinks())
						.andDocument("post-processed"));

		assertThat(
				new File("build/generated-snippets/post-processed/http-response.adoc"),
				is(snippet().withContents(
						httpResponse(HttpStatus.OK).header("Content-Type",
								"application/json").content(
								String.format("{%n  \"a\" : \"<<beta>>\",%n  \"links\" :"
										+ " [ {%n    \"rel\" : \"rel\",%n    \"href\" :"
										+ " \"...\"%n  } ]%n}")))));
	}

	private void assertExpectedSnippetFilesExist(File directory, String... snippets) {
		for (String snippet : snippets) {
			assertTrue(new File(directory, snippet).isFile());
		}
	}

	@Configuration
	@EnableWebMvc
	static class TestConfiguration extends WebMvcConfigurerAdapter {

		@Bean
		public TestController testController() {
			return new TestController();
		}

	}

	@RestController
	static class TestController {

		@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<Map<String, Object>> foo() {
			Map<String, Object> response = new HashMap<>();
			response.put("a", "alpha");
			response.put("links", Arrays.asList(new Link("rel", "href")));
			HttpHeaders headers = new HttpHeaders();
			headers.add("a", "alpha");
			return new ResponseEntity<Map<String, Object>>(response, headers,
					HttpStatus.OK);
		}
	}

}
