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

package org.springframework.restdocs.config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateFormats;

/**
 * A configurer that can be used to configure the generated documentation snippets.
 *
 * @param <PARENT> The type of the configurer's parent
 * @param <TYPE> The concrete type of the configurer to be returned from chained methods
 * @author Andy Wilkinson
 */
public abstract class SnippetConfigurer<PARENT, TYPE>
		extends AbstractNestedConfigurer<PARENT> {

	private List<Snippet> defaultSnippets = Arrays.asList(CliDocumentation.curlRequest(),
			CliDocumentation.httpieRequest(), HttpDocumentation.httpRequest(),
			HttpDocumentation.httpResponse());

	/**
	 * The default encoding for documentation snippets.
	 *
	 * @see #withEncoding(String)
	 */
	public static final String DEFAULT_SNIPPET_ENCODING = "UTF-8";

	/**
	 * The default format for documentation snippets.
	 *
	 * @see #withTemplateFormat(TemplateFormat)
	 */
	public static final TemplateFormat DEFAULT_TEMPLATE_FORMAT = TemplateFormats
			.asciidoctor();

	private String snippetEncoding = DEFAULT_SNIPPET_ENCODING;

	private TemplateFormat templateFormat = DEFAULT_TEMPLATE_FORMAT;

	/**
	 * Creates a new {@code SnippetConfigurer} with the given {@code parent}.
	 *
	 * @param parent the parent
	 */
	protected SnippetConfigurer(PARENT parent) {
		super(parent);
	}

	@Override
	public void apply(Map<String, Object> configuration,
			RestDocumentationContext context) {
		configuration.put(SnippetConfiguration.class.getName(),
				new SnippetConfiguration(this.snippetEncoding, this.templateFormat));
		configuration.put(RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_SNIPPETS,
				this.defaultSnippets);
	}

	/**
	 * Configures any documentation snippets to be written using the given
	 * {@code encoding}. The default is UTF-8.
	 *
	 * @param encoding the encoding
	 * @return {@code this}
	 */
	@SuppressWarnings("unchecked")
	public TYPE withEncoding(String encoding) {
		this.snippetEncoding = encoding;
		return (TYPE) this;
	}

	/**
	 * Configures the documentation snippets that will be produced by default.
	 *
	 * @param defaultSnippets the default snippets
	 * @return {@code this}
	 */
	@SuppressWarnings("unchecked")
	public TYPE withDefaults(Snippet... defaultSnippets) {
		this.defaultSnippets = Arrays.asList(defaultSnippets);
		return (TYPE) this;
	}

	/**
	 * Configures the format of the documentation snippet templates.
	 *
	 * @param format the snippet template format
	 * @return {@code this}
	 */
	@SuppressWarnings("unchecked")
	public TYPE withTemplateFormat(TemplateFormat format) {
		this.templateFormat = format;
		return (TYPE) this;
	}

}
