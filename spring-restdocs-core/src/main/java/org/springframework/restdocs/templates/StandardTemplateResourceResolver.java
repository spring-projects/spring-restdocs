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

/**
 * Standard implementation of {@link TemplateResourceResolver}.
 * <p>
 * Templates are resolved by looking for resources on the classpath. The following
 * locations are checked in order:
 * <ol>
 * <li>
 * <code>org/springframework/restdocs/templates/${templateFormatId}/${name}.snippet</code>
 * </li>
 * <li><code>org/springframework/restdocs/templates/${name}.snippet</code></li>
 * <li>
 * <code>org/springframework/restdocs/templates/${templateFormatId}/default-${name}.snippet</code>
 * </li>
 * </ol>
 *
 * @author Andy Wilkinson
 * @see TemplateFormat#getId()
 */
public class StandardTemplateResourceResolver implements TemplateResourceResolver {

	private final TemplateFormat templateFormat;

	/**
	 * Creates a new {@code StandardTemplateResourceResolver} that will produce default
	 * template resources formatted with Asciidoctor.
	 *
	 * @deprecated since 1.1.0 in favour of
	 * {@link #StandardTemplateResourceResolver(TemplateFormat)}
	 */
	@Deprecated
	public StandardTemplateResourceResolver() {
		this(TemplateFormats.asciidoctor());
	}

	/**
	 * Creates a new {@code StandardTemplateResourceResolver} that will produce default
	 * template resources formatted with the given {@code templateFormat}.
	 *
	 * @param templateFormat the format for the default snippet templates
	 */
	public StandardTemplateResourceResolver(TemplateFormat templateFormat) {
		this.templateFormat = templateFormat;
	}

	@Override
	public Resource resolveTemplateResource(String name) {
		Resource formatSpecificCustomTemplate = getFormatSpecificCustomTemplate(name);
		if (formatSpecificCustomTemplate.exists()) {
			return formatSpecificCustomTemplate;
		}
		Resource customTemplate = getCustomTemplate(name);
		if (customTemplate.exists()) {
			return customTemplate;
		}
		Resource defaultTemplate = getDefaultTemplate(name);
		if (defaultTemplate.exists()) {
			return defaultTemplate;
		}
		throw new IllegalStateException(
				"Template named '" + name + "' could not be resolved");
	}

	private Resource getFormatSpecificCustomTemplate(String name) {
		return new ClassPathResource(
				String.format("org/springframework/restdocs/templates/%s/%s.snippet",
						this.templateFormat.getId(), name));
	}

	private Resource getCustomTemplate(String name) {
		return new ClassPathResource(
				String.format("org/springframework/restdocs/templates/%s.snippet", name));
	}

	private Resource getDefaultTemplate(String name) {
		return new ClassPathResource(String.format(
				"org/springframework/restdocs/templates/%s/default-%s.snippet",
				this.templateFormat.getId(), name));
	}

}
