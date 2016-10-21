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

	private final File root;

	SnippetsDirectoryResolver(File root) {
		this.root = root;
	}

	File getSnippetsDirectory(Map<String, Object> attributes) {
		if (new File(this.root, "pom.xml").exists()) {
			return getMavenSnippetsDirectory(attributes);
		}
		return getGradleSnippetsDirectory(attributes);
	}

	private File getMavenSnippetsDirectory(Map<String, Object> attributes) {
		Path rootPath = Paths.get(this.root.getAbsolutePath());
		Path docDirPath = Paths.get((String) attributes.get("docdir"));
		Path relativePath = docDirPath.relativize(rootPath);
		return new File(relativePath.toFile(), "target/generated-snippets");
	}

	private File getGradleSnippetsDirectory(Map<String, Object> attributes) {
		return new File((String) attributes.get("projectdir"),
				"build/generated-snippets");
	}

}
