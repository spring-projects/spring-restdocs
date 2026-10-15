/*
 * Copyright 2014-present the original author or authors.
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
	 * template resources formatted with the given {@code templateFormat}.
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
		throw new IllegalStateException("Template named '" + name + "' could not be resolved");
	}

	private Resource getFormatSpecificCustomTemplate(String name) {
		return new ClassPathResource(String.format("org/springframework/restdocs/templates/%s/%s.snippet",
				this.templateFormat.getId(), name));
	}

	private Resource getCustomTemplate(String name) {
		return new ClassPathResource(String.format("org/springframework/restdocs/templates/%s.snippet", name));
	}

	private Resource getDefaultTemplate(String name) {
		return new ClassPathResource(String.format("org/springframework/restdocs/templates/%s/default-%s.snippet",
				this.templateFormat.getId(), name));
	}

}
