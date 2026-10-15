/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.asciidoctor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

/**
 * Resolves the directory from which snippets can be read for inclusion in an Asciidoctor
 * document. The resolved directory is absolute.
 *
 * @author Andy Wilkinson
 */
class SnippetsDirectoryResolver {

	File getSnippetsDirectory(Map<String, Object> attributes) {
		if (System.getProperty("maven.home") != null) {
			return getMavenSnippetsDirectory(attributes);
		}
		return getGradleSnippetsDirectory(attributes);
	}

	private File getMavenSnippetsDirectory(Map<String, Object> attributes) {
		Path docdir = Paths.get(getRequiredAttribute(attributes, "docdir"));
		Path pom = findPom(docdir);
		Path parent = pom.getParent();
		if (parent == null) {
			throw new IllegalStateException("Pom '" + pom + "' has no parent directory");
		}
		return new File(parent.toFile(), "target/generated-snippets").getAbsoluteFile();
	}

	private Path findPom(Path docdir) {
		Path path = docdir;
		while (path != null) {
			Path pom = path.resolve("pom.xml");
			if (Files.isRegularFile(pom)) {
				return pom;
			}
			path = path.getParent();
		}
		throw new IllegalStateException("pom.xml not found in '" + docdir + "' or above");
	}

	private File getGradleSnippetsDirectory(Map<String, Object> attributes) {
		return new File(getRequiredAttribute(attributes, "gradle-projectdir",
				() -> getRequiredAttribute(attributes, "projectdir")), "build/generated-snippets")
			.getAbsoluteFile();
	}

	private String getRequiredAttribute(Map<String, Object> attributes, String name) {
		return getRequiredAttribute(attributes, name, null);
	}

	private String getRequiredAttribute(Map<String, Object> attributes, String name,
			@Nullable Supplier<String> fallback) {
		String attribute = (String) attributes.get(name);
		if (attribute == null || attribute.length() == 0) {
			if (fallback != null) {
				return fallback.get();
			}
			throw new IllegalStateException(name + " attribute not found");
		}
		return attribute;
	}

}
