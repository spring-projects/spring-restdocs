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
package org.springframework.restdocs.templates;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Standard implementation of {@link TemplateResourceResolver}.
 * <p>
 * Templates are resolved by first looking for a resource on the classpath named
 * {@code org/springframework/restdocs/templates/&#123;name&#125;.snippet}. If no such
 * resource exists {@code default-} is prepended to the name and the classpath is checked
 * again. The built-in snippet templates are all named {@code default- name}, thereby
 * allowing them to be overridden.
 * 
 * @author Andy Wilkinson
 */
public class StandardTemplateResourceResolver implements TemplateResourceResolver {

	@Override
	public Resource resolveTemplateResource(String name) {
		ClassPathResource classPathResource = new ClassPathResource(
				"org/springframework/restdocs/templates/" + name + ".snippet");
		if (!classPathResource.exists()) {
			classPathResource = new ClassPathResource(
					"org/springframework/restdocs/templates/default-" + name + ".snippet");
			if (!classPathResource.exists()) {
				throw new IllegalStateException("Template named '" + name
						+ "' could not be resolved");
			}
		}
		return classPathResource;
	}

}
