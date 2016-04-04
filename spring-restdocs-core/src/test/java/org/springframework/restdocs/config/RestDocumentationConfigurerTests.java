/*
 * Copyright 2012-2016 the original author or authors.
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

package org.springframework.restdocs.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Test;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.cli.CurlRequestSnippet;
import org.springframework.restdocs.cli.HttpieRequestSnippet;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.http.HttpRequestSnippet;
import org.springframework.restdocs.http.HttpResponseSnippet;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.StandardWriterResolver;
import org.springframework.restdocs.snippet.WriterResolver;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link RestDocumentationConfigurer}.
 *
 * @author Andy Wilkinson
 */
public class RestDocumentationConfigurerTests {

	private final TestRestDocumentationConfigurer configurer = new TestRestDocumentationConfigurer();

	@SuppressWarnings("unchecked")
	@Test
	public void defaultConfiguration() {
		Map<String, Object> configuration = new HashMap<>();
		this.configurer.apply(configuration, createContext());
		assertThat(configuration, hasEntry(equalTo(TemplateEngine.class.getName()),
				instanceOf(MustacheTemplateEngine.class)));
		assertThat(configuration, hasEntry(equalTo(WriterResolver.class.getName()),
				instanceOf(StandardWriterResolver.class)));
		assertThat(configuration,
				hasEntry(
						equalTo(RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_SNIPPETS),
						instanceOf(List.class)));
		List<Snippet> defaultSnippets = (List<Snippet>) configuration
				.get(RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_SNIPPETS);
		assertThat(defaultSnippets,
				contains(instanceOf(CurlRequestSnippet.class),
						instanceOf(HttpieRequestSnippet.class),
						instanceOf(HttpRequestSnippet.class),
						instanceOf(HttpResponseSnippet.class)));
		assertThat(configuration, hasEntry(equalTo(SnippetConfiguration.class.getName()),
				instanceOf(SnippetConfiguration.class)));
		SnippetConfiguration snippetConfiguration = (SnippetConfiguration) configuration
				.get(SnippetConfiguration.class.getName());
		assertThat(snippetConfiguration.getEncoding(), is(equalTo("UTF-8")));
		assertThat(snippetConfiguration.getTemplateFormat(),
				is(equalTo(TemplateFormats.asciidoctor())));
	}

	@Test
	public void customTemplateEngine() {
		Map<String, Object> configuration = new HashMap<>();
		TemplateEngine templateEngine = mock(TemplateEngine.class);
		this.configurer.templateEngine(templateEngine).apply(configuration,
				createContext());
		assertThat(configuration, Matchers.<String, Object>hasEntry(
				TemplateEngine.class.getName(), templateEngine));
	}

	@Test
	public void customWriterResolver() {
		Map<String, Object> configuration = new HashMap<>();
		WriterResolver writerResolver = mock(WriterResolver.class);
		this.configurer.writerResolver(writerResolver).apply(configuration,
				createContext());
		assertThat(configuration, Matchers.<String, Object>hasEntry(
				WriterResolver.class.getName(), writerResolver));
	}

	@Test
	public void customDefaultSnippets() {
		Map<String, Object> configuration = new HashMap<>();
		this.configurer.snippets().withDefaults(CliDocumentation.curlRequest())
				.apply(configuration, createContext());
		assertThat(configuration,
				hasEntry(
						equalTo(RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_SNIPPETS),
						instanceOf(List.class)));
		@SuppressWarnings("unchecked")
		List<Snippet> defaultSnippets = (List<Snippet>) configuration
				.get(RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_SNIPPETS);
		assertThat(defaultSnippets, contains(instanceOf(CurlRequestSnippet.class)));
	}

	@Test
	public void customSnippetEncoding() {
		Map<String, Object> configuration = new HashMap<>();
		this.configurer.snippets().withEncoding("ISO 8859-1").apply(configuration,
				createContext());
		assertThat(configuration, hasEntry(equalTo(SnippetConfiguration.class.getName()),
				instanceOf(SnippetConfiguration.class)));
		SnippetConfiguration snippetConfiguration = (SnippetConfiguration) configuration
				.get(SnippetConfiguration.class.getName());
		assertThat(snippetConfiguration.getEncoding(), is(equalTo("ISO 8859-1")));
	}

	@Test
	public void customTemplateFormat() {
		Map<String, Object> configuration = new HashMap<>();
		this.configurer.snippets().withTemplateFormat(TemplateFormats.markdown())
				.apply(configuration, createContext());
		assertThat(configuration, hasEntry(equalTo(SnippetConfiguration.class.getName()),
				instanceOf(SnippetConfiguration.class)));
		SnippetConfiguration snippetConfiguration = (SnippetConfiguration) configuration
				.get(SnippetConfiguration.class.getName());
		assertThat(snippetConfiguration.getTemplateFormat(),
				is(equalTo(TemplateFormats.markdown())));
	}

	private RestDocumentationContext createContext() {
		ManualRestDocumentation manualRestDocumentation = new ManualRestDocumentation(
				"build");
		manualRestDocumentation.beforeTest(null, null);
		RestDocumentationContext context = manualRestDocumentation.beforeOperation();
		return context;
	}

	private static final class TestRestDocumentationConfigurer extends
			RestDocumentationConfigurer<TestSnippetConfigurer, TestRestDocumentationConfigurer> {

		private final TestSnippetConfigurer snippetConfigurer = new TestSnippetConfigurer(
				this);

		@Override
		public TestSnippetConfigurer snippets() {
			return this.snippetConfigurer;
		}

	}

	private static final class TestSnippetConfigurer extends
			SnippetConfigurer<TestRestDocumentationConfigurer, TestSnippetConfigurer> {

		private TestSnippetConfigurer(TestRestDocumentationConfigurer parent) {
			super(parent);
		}

	}

}
