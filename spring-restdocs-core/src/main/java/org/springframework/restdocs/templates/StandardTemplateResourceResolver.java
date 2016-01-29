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

package org.springframework.restdocs.templates;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.restdocs.snippet.SnippetFormat;
import org.springframework.restdocs.snippet.SnippetFormats;

/**
 * Standard implementation of {@link TemplateResourceResolver}.
 * <p>
 * Templates are resolved by looking for a resource on the classpath named
 * {@code org/springframework/restdocs/templates/&#123;name&#125;.snippet}. If no such
 * resource exists an attempt is made to return a default resource that is appropriate for
 * the configured snippet format.
 *
 * @author Andy Wilkinson
 */
public class StandardTemplateResourceResolver implements TemplateResourceResolver {

	private final SnippetFormat snippetFormat;

	/**
	 * Creates a new {@code StandardTemplateResourceResolver} that will produce default
	 * template resources formatted with Asciidoctor.
	 *
	 * @deprecated since 1.1.0 in favour of
	 * {@link #StandardTemplateResourceResolver(SnippetFormat)}
	 */
	@Deprecated
	public StandardTemplateResourceResolver() {
		this(SnippetFormats.asciidoctor());
	}

	/**
	 * Creates a new {@code StandardTemplateResourceResolver} that will produce default
	 * template resources formatted with the given {@code snippetFormat}.
	 *
	 * @param snippetFormat the format for the default snippet templates
	 */
	public StandardTemplateResourceResolver(SnippetFormat snippetFormat) {
		this.snippetFormat = snippetFormat;
	}

	@Override
	public Resource resolveTemplateResource(String name) {
		ClassPathResource classPathResource = new ClassPathResource(
				"org/springframework/restdocs/templates/" + name + ".snippet");
		if (!classPathResource.exists()) {
			classPathResource = new ClassPathResource(
					"org/springframework/restdocs/templates/"
							+ this.snippetFormat.getFileExtension() + "/" + name
							+ ".snippet");
			if (!classPathResource.exists()) {
				throw new IllegalStateException("Template named '" + name
						+ "' could not be resolved");
			}
		}
		return classPathResource;
	}

}
