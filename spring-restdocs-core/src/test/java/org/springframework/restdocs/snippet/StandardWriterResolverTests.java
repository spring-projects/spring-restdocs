/*
 * Copyright 2014-2017 the original author or authors.
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

package org.springframework.restdocs.snippet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.templates.TemplateFormats.asciidoctor;

/**
 * Tests for {@link StandardWriterResolver}.
 *
 * @author Andy Wilkinson
 */
public class StandardWriterResolverTests {

	@Rule
	public final TemporaryFolder temp = new TemporaryFolder();

	private final PlaceholderResolverFactory placeholderResolverFactory = mock(
			PlaceholderResolverFactory.class);

	private final StandardWriterResolver resolver = new StandardWriterResolver(
			this.placeholderResolverFactory, "UTF-8", asciidoctor());

	@Test
	public void absoluteInput() {
		String absolutePath = new File("foo").getAbsolutePath();
		assertThat(
				this.resolver.resolveFile(absolutePath, "bar.txt",
						createContext(absolutePath)),
				is(new File(absolutePath, "bar.txt")));
	}

	@Test
	public void configuredOutputAndRelativeInput() {
		File outputDir = new File("foo").getAbsoluteFile();
		assertThat(
				this.resolver.resolveFile("bar", "baz.txt",
						createContext(outputDir.getAbsolutePath())),
				is(new File(outputDir, "bar/baz.txt")));
	}

	@Test
	public void configuredOutputAndAbsoluteInput() {
		File outputDir = new File("foo").getAbsoluteFile();
		String absolutePath = new File("bar").getAbsolutePath();
		assertThat(
				this.resolver.resolveFile(absolutePath, "baz.txt",
						createContext(outputDir.getAbsolutePath())),
				is(new File(absolutePath, "baz.txt")));
	}

	@Test
	public void placeholdersAreResolvedInOperationName() throws IOException {
		File outputDirectory = this.temp.newFolder();
		RestDocumentationContext context = createContext(
				outputDirectory.getAbsolutePath());
		PlaceholderResolver resolver = mock(PlaceholderResolver.class);
		given(resolver.resolvePlaceholder("a")).willReturn("alpha");
		given(this.placeholderResolverFactory.create(context)).willReturn(resolver);
		Writer writer = this.resolver.resolve("{a}", "bravo", context);
		assertSnippetLocation(writer, new File(outputDirectory, "alpha/bravo.adoc"));
	}

	@Test
	public void placeholdersAreResolvedInSnippetName() throws IOException {
		File outputDirectory = this.temp.newFolder();
		RestDocumentationContext context = createContext(
				outputDirectory.getAbsolutePath());
		PlaceholderResolver resolver = mock(PlaceholderResolver.class);
		given(resolver.resolvePlaceholder("b")).willReturn("bravo");
		given(this.placeholderResolverFactory.create(context)).willReturn(resolver);
		Writer writer = this.resolver.resolve("alpha", "{b}", context);
		assertSnippetLocation(writer, new File(outputDirectory, "alpha/bravo.adoc"));
	}

	private RestDocumentationContext createContext(String outputDir) {
		ManualRestDocumentation manualRestDocumentation = new ManualRestDocumentation(
				outputDir);
		manualRestDocumentation.beforeTest(getClass(), null);
		RestDocumentationContext context = manualRestDocumentation.beforeOperation();
		return context;
	}

	private void assertSnippetLocation(Writer writer, File expectedLocation)
			throws IOException {
		writer.write("test");
		writer.flush();
		assertThat(expectedLocation.exists(), is(true));
		assertThat(FileCopyUtils.copyToString(new FileReader(expectedLocation)),
				is(equalTo("test")));
	}

}
