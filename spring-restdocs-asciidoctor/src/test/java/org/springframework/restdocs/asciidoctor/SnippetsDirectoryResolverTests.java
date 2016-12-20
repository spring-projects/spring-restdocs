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

package org.springframework.restdocs.asciidoctor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link SnippetsDirectoryResolver}.
 *
 * @author Andy Wilkinson
 */
public class SnippetsDirectoryResolverTests {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void mavenProjectsUseTargetGeneratedSnippetsRelativeToDocdir()
			throws IOException {
		this.temporaryFolder.newFile("pom.xml");
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("docdir",
				new File(this.temporaryFolder.getRoot(), "src/main/asciidoc")
						.getAbsolutePath());
		File snippetsDirectory = getMavenSnippetsDirectory(attributes);
		assertThat(snippetsDirectory.isAbsolute(), is(false));
		assertThat(snippetsDirectory,
				equalTo(new File("../../../target/generated-snippets")));
	}

	@Test
	public void illegalStateExceptionWhenMavenPomCannotBeFound() throws IOException {
		Map<String, Object> attributes = new HashMap<>();
		String docdir = new File(this.temporaryFolder.getRoot(), "src/main/asciidoc")
				.getAbsolutePath();
		attributes.put("docdir", docdir);
		this.thrown.expect(IllegalStateException.class);
		this.thrown
				.expectMessage(equalTo("pom.xml not found in '" + docdir + "' or above"));
		getMavenSnippetsDirectory(attributes);
	}

	@Test
	public void illegalStateWhenDocdirAttributeIsNotSetInMavenProject()
			throws IOException {
		Map<String, Object> attributes = new HashMap<>();
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage(equalTo("docdir attribute not found"));
		getMavenSnippetsDirectory(attributes);
	}

	@Test
	public void gradleProjectsUseBuildGeneratedSnippetsBeneathProjectDir()
			throws IOException {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("projectdir", "project/dir");
		File snippetsDirectory = new SnippetsDirectoryResolver()
				.getSnippetsDirectory(attributes);
		assertThat(snippetsDirectory,
				equalTo(new File("project/dir/build/generated-snippets")));
	}

	@Test
	public void illegalStateWhenProjectdirAttributeIsNotSetInGradleProject()
			throws IOException {
		Map<String, Object> attributes = new HashMap<>();
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage(equalTo("projectdir attribute not found"));
		new SnippetsDirectoryResolver().getSnippetsDirectory(attributes);
	}

	private File getMavenSnippetsDirectory(Map<String, Object> attributes) {
		System.setProperty("maven.home", "/maven/home");
		try {
			return new SnippetsDirectoryResolver().getSnippetsDirectory(attributes);
		}
		finally {
			System.clearProperty("maven.home");
		}
	}

}
