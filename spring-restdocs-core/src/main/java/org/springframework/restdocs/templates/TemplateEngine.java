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

import java.io.IOException;

import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

/**
 * A {@code TemplateEngine} is used to render documentation snippets.
 *
 * @author Andy Wilkinson
 * @see MustacheTemplateEngine
 */
public interface TemplateEngine {

	/**
	 * Compiles the template at the given {@code path}. Typically, a
	 * {@link TemplateResourceResolver} will be used to resolve the path into a resource
	 * that can be read and compiled.
	 *
	 * @param path the path of the template
	 * @return the compiled {@code Template}
	 * @throws IOException if compilation fails
	 */
	Template compileTemplate(String path) throws IOException;

}
