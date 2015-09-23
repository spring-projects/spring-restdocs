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

package org.springframework.restdocs.snippet;

import java.io.File;

import org.junit.Test;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link StandardWriterResolver}.
 *
 * @author Andy Wilkinson
 */
public class StandardWriterResolverTests {

	private final PlaceholderResolver placeholderResolver = mock(PlaceholderResolver.class);

	private final StandardWriterResolver resolver = new StandardWriterResolver(
			this.placeholderResolver);

	@Test
	public void noConfiguredOutputDirectoryAndRelativeInput() {
		assertThat(this.resolver.resolveFile("foo", "bar.txt",
				new RestDocumentationContext(null, null, null)), is(nullValue()));
	}

	@Test
	public void absoluteInput() {
		String absolutePath = new File("foo").getAbsolutePath();
		assertThat(this.resolver.resolveFile(absolutePath, "bar.txt",
				new RestDocumentationContext(null, null, null)), is(new File(
				absolutePath, "bar.txt")));
	}

	@Test
	public void configuredOutputAndRelativeInput() {
		File outputDir = new File("foo").getAbsoluteFile();
		assertThat(this.resolver.resolveFile("bar", "baz.txt",
				new RestDocumentationContext(null, null, outputDir)), is(new File(
				outputDir, "bar/baz.txt")));
	}

	@Test
	public void configuredOutputAndAbsoluteInput() {
		File outputDir = new File("foo").getAbsoluteFile();
		String absolutePath = new File("bar").getAbsolutePath();
		assertThat(this.resolver.resolveFile(absolutePath, "baz.txt",
				new RestDocumentationContext(null, null, outputDir)), is(new File(
				absolutePath, "baz.txt")));
	}

}
