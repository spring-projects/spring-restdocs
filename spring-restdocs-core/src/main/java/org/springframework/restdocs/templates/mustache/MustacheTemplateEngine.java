/*
 * Copyright 2014-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

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

	private final TemplateResourceResolver templateResourceResolver;

	private final Charset templateEncoding;

	private final Compiler compiler;

	private final Map<String, Object> context;

	/**
	 * Creates a new {@code MustacheTemplateEngine} that will use the given
	 * {@code templateResourceResolver} to resolve template paths. Templates will be read
	 * as UTF-8.
	 * @param templateResourceResolver the resolver to use
	 */
	public MustacheTemplateEngine(TemplateResourceResolver templateResourceResolver) {
		this(templateResourceResolver, Mustache.compiler().escapeHTML(false));
	}

	/**
	 * Creates a new {@code MustacheTemplateEngine} that will use the given
	 * {@code templateResourceResolver} to resolve template paths, reading them using the
	 * given {@code templateEncoding}.
	 * @param templateResourceResolver the resolver to use
	 * @param templateEncoding the charset to use when reading the templates
	 * @since 2.0.5
	 */
	public MustacheTemplateEngine(TemplateResourceResolver templateResourceResolver, Charset templateEncoding) {
		this(templateResourceResolver, templateEncoding, Mustache.compiler().escapeHTML(false));
	}

	/**
	 * Creates a new {@code MustacheTemplateEngine} that will use the given
	 * {@code templateResourceResolver} to resolve templates and the given
	 * {@code compiler} to compile them. Templates will be read as UTF-8.
	 * @param templateResourceResolver the resolver to use
	 * @param compiler the compiler to use
	 */
	public MustacheTemplateEngine(TemplateResourceResolver templateResourceResolver, Compiler compiler) {
		this(templateResourceResolver, compiler, Collections.<String, Object>emptyMap());
	}

	/**
	 * Creates a new {@code MustacheTemplateEngine} that will use the given
	 * {@code templateResourceResolver} to resolve templates and the given
	 * {@code compiler} to compile them. Templates will be read using the given
	 * {@code templateEncoding}.
	 * @param templateResourceResolver the resolver to use
	 * @param templateEncoding the charset to use when reading the templates
	 * @param compiler the compiler to use
	 * @since 2.0.5
	 */
	public MustacheTemplateEngine(TemplateResourceResolver templateResourceResolver, Charset templateEncoding,
			Compiler compiler) {
		this(templateResourceResolver, templateEncoding, compiler, Collections.<String, Object>emptyMap());
	}

	/**
	 * Creates a new {@code MustacheTemplateEngine} that will use the given
	 * {@code templateResourceResolver} to resolve templates. Templates will be read as
	 * UTF-8. Once read, the given {@code compiler} will be used to compile them. Compiled
	 * templates will be created with the given {@code context}.
	 * @param templateResourceResolver the resolver to use
	 * @param compiler the compiler to use
	 * @param context the context to pass to compiled templates
	 * @see MustacheTemplate#MustacheTemplate(org.springframework.restdocs.mustache.Template,
	 * Map)
	 */
	public MustacheTemplateEngine(TemplateResourceResolver templateResourceResolver, Compiler compiler,
			Map<String, Object> context) {
		this(templateResourceResolver, StandardCharsets.UTF_8, compiler, context);
	}

	/**
	 * Creates a new {@code MustacheTemplateEngine} that will use the given
	 * {@code templateResourceResolver} to resolve templates. Template will be read using
	 * the given {@code templateEncoding}. Once read, the given {@code compiler} will be
	 * used to compile them. Compiled templates will be created with the given
	 * {@code context}.
	 * @param templateResourceResolver the resolver to use
	 * @param templateEncoding the charset to use when reading the templates
	 * @param compiler the compiler to use
	 * @param context the context to pass to compiled templates
	 * @since 2.0.5
	 * @see MustacheTemplate#MustacheTemplate(org.springframework.restdocs.mustache.Template,
	 * Map)
	 */
	public MustacheTemplateEngine(TemplateResourceResolver templateResourceResolver, Charset templateEncoding,
			Compiler compiler, Map<String, Object> context) {
		this.templateResourceResolver = templateResourceResolver;
		this.templateEncoding = templateEncoding;
		this.compiler = compiler;
		this.context = context;
	}

	@Override
	public Template compileTemplate(String name) throws IOException {
		Resource templateResource = this.templateResourceResolver.resolveTemplateResource(name);
		return new MustacheTemplate(
				this.compiler.compile(new InputStreamReader(templateResource.getInputStream(), this.templateEncoding)),
				this.context);
	}

	/**
	 * Returns the {@link Compiler} used to compile Mustache templates.
	 * @return the compiler
	 */
	protected final Compiler getCompiler() {
		return this.compiler;
	}

	/**
	 * Returns the {@link TemplateResourceResolver} used to resolve the template resources
	 * prior to compilation.
	 * @return the resolver
	 */
	protected final TemplateResourceResolver getTemplateResourceResolver() {
		return this.templateResourceResolver;
	}

}
