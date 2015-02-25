package org.springframework.restdocs;

import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationIntegrationTests.TestConfiguration;
import org.springframework.restdocs.config.RestDocumentationConfigurer;
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
				"request.asciidoc", "response.asciidoc", "request-response.asciidoc");
	}

	@Test
	public void parameterizedOutputDirectory() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(new RestDocumentationConfigurer()).build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(document("{method-name}"));
		assertExpectedSnippetFilesExist(new File(
				"build/generated-snippets/parameterized-output-directory"),
				"request.asciidoc", "response.asciidoc", "request-response.asciidoc");
	}

	@Test
	public void multiStep() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(new RestDocumentationConfigurer())
				.alwaysDo(document("{method-name}-{step}")).build();

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andExpect(
				status().isOk());
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/multi-step-1/"), "request.asciidoc",
				"response.asciidoc", "request-response.asciidoc");

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andExpect(
				status().isOk());
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/multi-step-2/"), "request.asciidoc",
				"response.asciidoc", "request-response.asciidoc");

		mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andExpect(
				status().isOk());
		assertExpectedSnippetFilesExist(
				new File("build/generated-snippets/multi-step-3/"), "request.asciidoc",
				"response.asciidoc", "request-response.asciidoc");

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
		public Map<String, String> foo() {
			Map<String, String> response = new HashMap<String, String>();
			response.put("a", "alpha");
			return response;
		}

	}

}
