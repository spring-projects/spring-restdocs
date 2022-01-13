/*
 * Copyright 2014-2022 the original author or authors.
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

package org.springframework.restdocs.templates;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.Test;

import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link TemplateResourceResolver}.
 *
 * @author Andy Wilkinson
 */
public class StandardTemplateResourceResolverTests {

	private final TemplateResourceResolver resolver = new StandardTemplateResourceResolver(
			TemplateFormats.asciidoctor());

	private final TestClassLoader classLoader = new TestClassLoader();

	@Test
	public void formatSpecificCustomSnippetHasHighestPrecedence() throws Exception {
		this.classLoader.addResource("org/springframework/restdocs/templates/asciidoctor/test.snippet",
				getClass().getResource("test-format-specific-custom.snippet"));
		this.classLoader.addResource("org/springframework/restdocs/templates/test.snippet",
				getClass().getResource("test-custom.snippet"));
		this.classLoader.addResource("org/springframework/restdocs/templates/asciidoctor/default-test.snippet",
				getClass().getResource("test-default.snippet"));
		Resource snippet = doWithThreadContextClassLoader(this.classLoader, new Callable<Resource>() {

			@Override
			public Resource call() {
				return StandardTemplateResourceResolverTests.this.resolver.resolveTemplateResource("test");
			}

		});

		assertThat(snippet.getURL()).isEqualTo(getClass().getResource("test-format-specific-custom.snippet"));
	}

	@Test
	public void generalCustomSnippetIsUsedInAbsenceOfFormatSpecificCustomSnippet() throws Exception {
		this.classLoader.addResource("org/springframework/restdocs/templates/test.snippet",
				getClass().getResource("test-custom.snippet"));
		this.classLoader.addResource("org/springframework/restdocs/templates/asciidoctor/default-test.snippet",
				getClass().getResource("test-default.snippet"));
		Resource snippet = doWithThreadContextClassLoader(this.classLoader, new Callable<Resource>() {

			@Override
			public Resource call() {
				return StandardTemplateResourceResolverTests.this.resolver.resolveTemplateResource("test");
			}

		});

		assertThat(snippet.getURL()).isEqualTo(getClass().getResource("test-custom.snippet"));
	}

	@Test
	public void defaultSnippetIsUsedInAbsenceOfCustomSnippets() throws Exception {
		this.classLoader.addResource("org/springframework/restdocs/templates/asciidoctor/default-test.snippet",
				getClass().getResource("test-default.snippet"));
		Resource snippet = doWithThreadContextClassLoader(this.classLoader, new Callable<Resource>() {

			@Override
			public Resource call() {
				return StandardTemplateResourceResolverTests.this.resolver.resolveTemplateResource("test");
			}

		});

		assertThat(snippet.getURL()).isEqualTo(getClass().getResource("test-default.snippet"));
	}

	@Test
	public void failsIfCustomAndDefaultSnippetsDoNotExist() throws Exception {
		assertThatIllegalStateException()
				.isThrownBy(() -> doWithThreadContextClassLoader(this.classLoader,
						() -> StandardTemplateResourceResolverTests.this.resolver.resolveTemplateResource("test")))
				.withMessage("Template named 'test' could not be resolved");
	}

	private <T> T doWithThreadContextClassLoader(ClassLoader classLoader, Callable<T> action) throws Exception {
		ClassLoader previous = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		try {
			return action.call();
		}
		finally {
			Thread.currentThread().setContextClassLoader(previous);
		}
	}

	private static class TestClassLoader extends ClassLoader {

		private Map<String, URL> resources = new HashMap<>();

		private void addResource(String name, URL url) {
			this.resources.put(name, url);
		}

		@Override
		public URL getResource(String name) {
			return this.resources.get(name);
		}

	}

}
