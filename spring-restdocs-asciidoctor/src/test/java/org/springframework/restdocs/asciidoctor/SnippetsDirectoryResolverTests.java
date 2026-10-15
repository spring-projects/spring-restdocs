/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.asciidoctor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link SnippetsDirectoryResolver}.
 *
 * @author Andy Wilkinson
 */
public class SnippetsDirectoryResolverTests {

	@TempDir
	File temp;

	@Test
	public void mavenProjectsUseTargetGeneratedSnippets() throws IOException {
		new File(this.temp, "pom.xml").createNewFile();
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("docdir", new File(this.temp, "src/main/asciidoc").getAbsolutePath());
		File snippetsDirectory = getMavenSnippetsDirectory(attributes);
		assertThat(snippetsDirectory).isAbsolute();
		assertThat(snippetsDirectory).isEqualTo(new File(this.temp, "target/generated-snippets"));
	}

	@Test
	public void illegalStateExceptionWhenMavenPomCannotBeFound() {
		Map<String, Object> attributes = new HashMap<>();
		String docdir = new File(this.temp, "src/main/asciidoc").getAbsolutePath();
		attributes.put("docdir", docdir);
		assertThatIllegalStateException().isThrownBy(() -> getMavenSnippetsDirectory(attributes))
			.withMessage("pom.xml not found in '" + docdir + "' or above");
	}

	@Test
	public void illegalStateWhenDocdirAttributeIsNotSetInMavenProject() {
		Map<String, Object> attributes = new HashMap<>();
		assertThatIllegalStateException().isThrownBy(() -> getMavenSnippetsDirectory(attributes))
			.withMessage("docdir attribute not found");
	}

	@Test
	public void gradleProjectsUseBuildGeneratedSnippetsBeneathGradleProjectdir() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("gradle-projectdir", "project/dir");
		File snippetsDirectory = new SnippetsDirectoryResolver().getSnippetsDirectory(attributes);
		assertThat(snippetsDirectory).isAbsolute();
		assertThat(snippetsDirectory).isEqualTo(new File("project/dir/build/generated-snippets").getAbsoluteFile());
	}

	@Test
	public void gradleProjectsUseBuildGeneratedSnippetsBeneathGradleProjectdirWhenBothItAndProjectdirAreSet() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("gradle-projectdir", "project/dir");
		attributes.put("projectdir", "fallback/dir");
		File snippetsDirectory = new SnippetsDirectoryResolver().getSnippetsDirectory(attributes);
		assertThat(snippetsDirectory).isAbsolute();
		assertThat(snippetsDirectory).isEqualTo(new File("project/dir/build/generated-snippets").getAbsoluteFile());
	}

	@Test
	public void gradleProjectsUseBuildGeneratedSnippetsBeneathProjectdirWhenGradleProjectdirIsNotSet() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("projectdir", "project/dir");
		File snippetsDirectory = new SnippetsDirectoryResolver().getSnippetsDirectory(attributes);
		assertThat(snippetsDirectory).isAbsolute();
		assertThat(snippetsDirectory).isEqualTo(new File("project/dir/build/generated-snippets").getAbsoluteFile());
	}

	@Test
	public void illegalStateWhenGradleProjectdirAndProjectdirAttributesAreNotSetInGradleProject() {
		Map<String, Object> attributes = new HashMap<>();
		assertThatIllegalStateException()
			.isThrownBy(() -> new SnippetsDirectoryResolver().getSnippetsDirectory(attributes))
			.withMessage("projectdir attribute not found");
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
