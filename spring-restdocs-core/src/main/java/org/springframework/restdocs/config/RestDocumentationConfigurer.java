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
import org.springframework.restdocs.snippet.RestDocumentationContextPlaceholderResolver;
import org.springframework.restdocs.snippet.StandardWriterResolver;
import org.springframework.restdocs.snippet.WriterResolver;
import org.springframework.restdocs.templates.StandardTemplateResourceResolver;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;

/**
 * Abstract base class for the configuration of Spring REST Docs.
 *
 * @param <S> The concrete type of the {@link SnippetConfigurer}.
 * @param <T> The concrete type of this configurer, to be returned from methods that
 * support chaining
 * @author Andy Wilkinson
 */
public abstract class RestDocumentationConfigurer<S extends AbstractConfigurer, T> {

	private final WriterResolverConfigurer writerResolverConfigurer = new WriterResolverConfigurer();

	private final TemplateEngineConfigurer templateEngineConfigurer = new TemplateEngineConfigurer();

	/**
	 * Returns a {@link SnippetConfigurer} that can be used to configure the snippets that
	 * will be generated.
	 *
	 * @return the snippet configurer
	 */
	public abstract S snippets();

	/**
	 * Configures the {@link TemplateEngine} that will be used for snippet rendering.
	 *
	 * @param templateEngine the template engine to use
	 * @return {@code this}
	 */
	@SuppressWarnings("unchecked")
	public final T templateEngine(TemplateEngine templateEngine) {
		this.templateEngineConfigurer.setTemplateEngine(templateEngine);
		return (T) this;
	}

	/**
	 * Configures the {@link WriterResolver} that will be used to resolve a writer for a
	 * snippet.
	 *
	 * @param writerResolver The writer resolver to use
	 * @return {@code this}
	 */
	@SuppressWarnings("unchecked")
	public final T writerResolver(WriterResolver writerResolver) {
		this.writerResolverConfigurer.setWriterResolver(writerResolver);
		return (T) this;
	}

	/**
	 * Applies this configurer to the given {@code configuration} within the given
	 * {@code context}.
	 *
	 * @param configuration the configuration
	 * @param context the current context
	 */
	protected final void apply(Map<String, Object> configuration,
			RestDocumentationContext context) {
		List<AbstractConfigurer> configurers = Arrays.asList(snippets(),
				this.templateEngineConfigurer, this.writerResolverConfigurer);
		for (AbstractConfigurer configurer : configurers) {
			configurer.apply(configuration, context);
		}
	}

	private static final class TemplateEngineConfigurer extends AbstractConfigurer {

		private TemplateEngine templateEngine;

		@Override
		public void apply(Map<String, Object> configuration,
				RestDocumentationContext context) {
			TemplateEngine engineToUse = this.templateEngine;
			if (engineToUse == null) {
				SnippetConfiguration snippetConfiguration = (SnippetConfiguration) configuration
						.get(SnippetConfiguration.class.getName());
				engineToUse = new MustacheTemplateEngine(
						new StandardTemplateResourceResolver(
								snippetConfiguration.getTemplateFormat()));
			}
			configuration.put(TemplateEngine.class.getName(), engineToUse);
		}

		private void setTemplateEngine(TemplateEngine templateEngine) {
			this.templateEngine = templateEngine;
		}

	}

	private static final class WriterResolverConfigurer extends AbstractConfigurer {

		private WriterResolver writerResolver;

		@Override
		public void apply(Map<String, Object> configuration,
				RestDocumentationContext context) {
			WriterResolver resolverToUse = this.writerResolver;
			if (resolverToUse == null) {
				SnippetConfiguration snippetConfiguration = (SnippetConfiguration) configuration
						.get(SnippetConfiguration.class.getName());
				resolverToUse = new StandardWriterResolver(
						new RestDocumentationContextPlaceholderResolver(context),
						snippetConfiguration.getEncoding(),
						snippetConfiguration.getTemplateFormat());
			}
			configuration.put(WriterResolver.class.getName(), resolverToUse);
		}

		private void setWriterResolver(WriterResolver writerResolver) {
			this.writerResolver = writerResolver;
		}

	}

}
