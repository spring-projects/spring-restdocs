/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.templates;

import org.springframework.core.io.Resource;

/**
 * A {@code TemplateResourceResolver} is responsible for resolving a name for a template
 * into a {@link Resource} from which the template can be read.
 *
 * @author Andy Wilkinson
 */
public interface TemplateResourceResolver {

	/**
	 * Resolves a {@link Resource} for the template with the given {@code name}.
	 * @param name the name of the template
	 * @return the {@code Resource} from which the template can be read
	 */
	Resource resolveTemplateResource(String name);

}
