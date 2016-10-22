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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Resolves the directory from which snippets can be read for inclusion in an Asciidoctor
 * document. The resolved directory is relative to the {@code docdir} of the Asciidoctor
 * document that it being rendered.
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
		return new File(docdir.relativize(findPom(docdir).getParent()).toFile(),
				"target/generated-snippets");
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
		return new File(getRequiredAttribute(attributes, "projectdir"),
				"build/generated-snippets");
	}

	private String getRequiredAttribute(Map<String, Object> attributes, String name) {
		String attribute = (String) attributes.get(name);
		if (attribute == null || attribute.length() == 0) {
			throw new IllegalStateException(name + " attribute not found");
		}
		return attribute;
	}

}
