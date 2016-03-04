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

package org.springframework.restdocs.templates.mustache;

import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.core.io.Resource;
import org.springframework.restdocs.mustache.Mustache;
import org.springframework.restdocs.mustache.Mustache.Compiler;
import org.springframework.restdocs.templates.Template;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateResourceResolver;

/**
 * A <a href="https://mustache.github.io">Mustache</a>-based {@link TemplateEngine}
 * implemented using <a href="https://github.com/samskivert/jmustache">JMustache</a>.
 * <p>
 * Note that JMustache has been repackaged and embedded to prevent classpath conflicts.
 *
 * @author Andy Wilkinson
 */
public class MustacheTemplateEngine implements TemplateEngine {

	private final Compiler compiler;

	private final TemplateResourceResolver templateResourceResolver;

	/**
	 * Creates a new {@code MustacheTemplateEngine} that will use the given
	 * {@code templateResourceResolver} to resolve template paths.
	 *
	 * @param templateResourceResolver the resolver to use
	 */
	public MustacheTemplateEngine(TemplateResourceResolver templateResourceResolver) {
		this(templateResourceResolver, Mustache.compiler().escapeHTML(false));
	}

	/**
	 * Creates a new {@code MustacheTemplateEngine} that will use the given
	 * {@code templateResourceResolver} to resolve templates and the given
	 * {@code compiler} to compile them.
	 *
	 * @param templateResourceResolver the resolver to use
	 * @param compiler the compiler to use
	 */
	public MustacheTemplateEngine(TemplateResourceResolver templateResourceResolver,
			Compiler compiler) {
		this.templateResourceResolver = templateResourceResolver;
		this.compiler = compiler;
	}

	@Override
	public Template compileTemplate(String name) throws IOException {
		Resource templateResource = this.templateResourceResolver
				.resolveTemplateResource(name);
		return new MustacheTemplate(this.compiler
				.compile(new InputStreamReader(templateResource.getInputStream())));
	}

	/**
	 * Returns the {@link Compiler} used to compile Mustache templates.
	 *
	 * @return the compiler
	 */
	protected final Compiler getCompiler() {
		return this.compiler;
	}

	/**
	 * Returns the {@link TemplateResourceResolver} used to resolve the template resources
	 * prior to compilation.
	 *
	 * @return the resolver
	 */
	protected final TemplateResourceResolver getTemplateResourceResolver() {
		return this.templateResourceResolver;
	}

}
