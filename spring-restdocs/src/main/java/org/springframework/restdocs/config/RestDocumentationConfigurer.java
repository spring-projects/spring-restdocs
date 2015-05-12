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

	/**
	 * Creates a new {@link RestDocumentationConfigurer}.
	 * @see RestDocumentation#documentationConfiguration()
	 */
	public RestDocumentationConfigurer() {
		this.requestPostProcessor = new ConfigurerApplyingRequestPostProcessor(
				Arrays.<AbstractConfigurer> asList(this.uriConfigurer,
						this.snippetConfigurer, new StepCountConfigurer(),
						new ContentLengthHeaderConfigurer()));
	}

	public UriConfigurer uris() {
		return this.uriConfigurer;
	}

	public SnippetConfigurer snippets() {
		return this.snippetConfigurer;
	}

	@Override
	public RequestPostProcessor beforeMockMvcCreated(
			ConfigurableMockMvcBuilder<?> builder, WebApplicationContext context) {
		return this.requestPostProcessor;
	}

	private static class StepCountConfigurer extends AbstractConfigurer {

		@Override
		void apply(MockHttpServletRequest request) {
			RestDocumentationContext currentContext = RestDocumentationContext
					.currentContext();
			if (currentContext != null) {
				currentContext.getAndIncrementStepCount();
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

	private static class ConfigurerApplyingRequestPostProcessor implements
			RequestPostProcessor {

		private final List<AbstractConfigurer> configurers;

		private ConfigurerApplyingRequestPostProcessor(
				List<AbstractConfigurer> configurers) {
			this.configurers = configurers;
		}

		@Override
		public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
			for (AbstractConfigurer configurer : this.configurers) {
				configurer.apply(request);
			}
			return request;
		}

	}
}
