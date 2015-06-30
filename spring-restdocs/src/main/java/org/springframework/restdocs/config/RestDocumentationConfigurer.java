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

package org.springframework.restdocs.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.snippet.RestDocumentationContextPlaceholderResolver;
import org.springframework.restdocs.snippet.StandardWriterResolver;
import org.springframework.restdocs.snippet.WriterResolver;
import org.springframework.restdocs.templates.StandardTemplateResourceResolver;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.test.web.servlet.setup.MockMvcConfigurerAdapter;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

/**
 * A {@link MockMvcConfigurer} that can be used to configure the documentation
 * 
 * @author Andy Wilkinson
 * @author Dmitriy Mayboroda
 * @see ConfigurableMockMvcBuilder#apply(MockMvcConfigurer)
 * @see RestDocumentation#documentationConfiguration()
 */
public class RestDocumentationConfigurer extends MockMvcConfigurerAdapter {

	private final UriConfigurer uriConfigurer = new UriConfigurer(this);

	private final SnippetConfigurer snippetConfigurer = new SnippetConfigurer(this);

	private final RequestPostProcessor requestPostProcessor;

	private TemplateEngineConfigurer templateEngineConfigurer = new TemplateEngineConfigurer();

	private WriterResolverConfigurer writerResolverConfigurer = new WriterResolverConfigurer();

	/**
	 * Creates a new {@link RestDocumentationConfigurer}.
	 * @see RestDocumentation#documentationConfiguration()
	 */
	public RestDocumentationConfigurer() {
		this.requestPostProcessor = new ConfigurerApplyingRequestPostProcessor(
				Arrays.<AbstractConfigurer> asList(this.uriConfigurer,
						this.writerResolverConfigurer, this.snippetConfigurer,
						new StepCountConfigurer(), new ContentLengthHeaderConfigurer(),
						this.templateEngineConfigurer));
	}

	public UriConfigurer uris() {
		return this.uriConfigurer;
	}

	public SnippetConfigurer snippets() {
		return this.snippetConfigurer;
	}

	public RestDocumentationConfigurer templateEngine(TemplateEngine templateEngine) {
		this.templateEngineConfigurer.setTemplateEngine(templateEngine);
		return this;
	}

	public RestDocumentationConfigurer writerResolver(WriterResolver writerResolver) {
		this.writerResolverConfigurer.setWriterResolver(writerResolver);
		return this;
	}

	@Override
	public RequestPostProcessor beforeMockMvcCreated(
			ConfigurableMockMvcBuilder<?> builder, WebApplicationContext context) {
		return this.requestPostProcessor;
	}

	private static class StepCountConfigurer extends AbstractConfigurer {

		@Override
		void apply(MockHttpServletRequest request) {
			RestDocumentationContext context = (RestDocumentationContext) request
					.getAttribute(RestDocumentationContext.class.getName());
			if (context != null) {
				context.getAndIncrementStepCount();
			}
		}

	}

	private static class ContentLengthHeaderConfigurer extends AbstractConfigurer {

		@Override
		void apply(MockHttpServletRequest request) {
			long contentLength = request.getContentLengthLong();
			if (contentLength > 0
					&& !StringUtils.hasText(request.getHeader("Content-Length"))) {
				request.addHeader("Content-Length", request.getContentLengthLong());
			}
		}

	}

	private static class TemplateEngineConfigurer extends AbstractConfigurer {

		private TemplateEngine templateEngine = new MustacheTemplateEngine(
				new StandardTemplateResourceResolver());

		@Override
		void apply(MockHttpServletRequest request) {
			request.setAttribute(TemplateEngine.class.getName(), this.templateEngine);
		}

		void setTemplateEngine(TemplateEngine templateEngine) {
			this.templateEngine = templateEngine;
		}

	}

	private static class WriterResolverConfigurer extends AbstractConfigurer {

		private WriterResolver writerResolver;

		@Override
		void apply(MockHttpServletRequest request) {
			WriterResolver resolverToUse = this.writerResolver;
			if (resolverToUse == null) {
				resolverToUse = new StandardWriterResolver(
						new RestDocumentationContextPlaceholderResolver(
								(RestDocumentationContext) request
										.getAttribute(RestDocumentationContext.class
												.getName())));
			}
			request.setAttribute(WriterResolver.class.getName(), resolverToUse);
		}

		void setWriterResolver(WriterResolver writerResolver) {
			this.writerResolver = writerResolver;
		}

	}

	private static class ConfigurerApplyingRequestPostProcessor implements
			RequestPostProcessor {

		private final List<AbstractConfigurer> configurers;

		private ConfigurerApplyingRequestPostProcessor(
				List<AbstractConfigurer> configurers) {
			this.configurers = configurers;
		}

		@Override
		public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
			request.setAttribute(RestDocumentationContext.class.getName(),
					RestDocumentationContextHolder.getCurrentContext());
			for (AbstractConfigurer configurer : this.configurers) {
				configurer.apply(request);
			}
			return request;
		}

	}
}
