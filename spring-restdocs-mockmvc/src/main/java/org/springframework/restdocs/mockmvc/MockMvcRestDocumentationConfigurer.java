/*
 * Copyright 2012-2016 the original author or authors.
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

package org.springframework.restdocs.mockmvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.config.AbstractConfigurer;
import org.springframework.restdocs.config.RestDocumentationConfigurer;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

/**
 * A MockMvc-specific {@link RestDocumentationConfigurer}.
 *
 * @author Andy Wilkinson
 */
public class MockMvcRestDocumentationConfigurer
		extends
		RestDocumentationConfigurer<MockMvcSnippetConfigurer, MockMvcRestDocumentationConfigurer>
		implements MockMvcConfigurer {

	private final MockMvcSnippetConfigurer snippetConfigurer = new MockMvcSnippetConfigurer(
			this);

	private final UriConfigurer uriConfigurer = new UriConfigurer(this);

	private final RestDocumentation restDocumentation;

	MockMvcRestDocumentationConfigurer(RestDocumentation restDocumentation) {
		super();
		this.restDocumentation = restDocumentation;
	}

	/**
	 * Returns a {@link UriConfigurer} that can be used to configure the request URIs that
	 * will be documented.
	 *
	 * @return the URI configurer
	 */
	public UriConfigurer uris() {
		return this.uriConfigurer;
	}

	@Override
	public RequestPostProcessor beforeMockMvcCreated(
			ConfigurableMockMvcBuilder<?> builder, WebApplicationContext context) {
		return new ConfigurerApplyingRequestPostProcessor(this.restDocumentation,
				Arrays.asList(snippets(), getTemplateEngineConfigurer(),
						getWriterResolverConfigurer(), this.uriConfigurer));
	}

	@Override
	public void afterConfigurerAdded(ConfigurableMockMvcBuilder<?> builder) {
		// Nothing to do
	}

	@Override
	public MockMvcSnippetConfigurer snippets() {
		return this.snippetConfigurer;
	}

	private static final class ConfigurerApplyingRequestPostProcessor implements
			RequestPostProcessor {

		private final RestDocumentation restDocumentation;

		private final List<AbstractConfigurer> configurers;

		private ConfigurerApplyingRequestPostProcessor(
				RestDocumentation restDocumentation, List<AbstractConfigurer> configurers) {
			this.restDocumentation = restDocumentation;
			this.configurers = configurers;
		}

		@Override
		public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
			RestDocumentationContext context = this.restDocumentation.beforeOperation();
			request.setAttribute(RestDocumentationContext.class.getName(), context);
			Map<String, Object> configuration = new HashMap<>();
			configuration.put(MockHttpServletRequest.class.getName(), request);
			String urlTemplateAttribute = "org.springframework.restdocs.urlTemplate";
			configuration.put(urlTemplateAttribute,
					request.getAttribute(urlTemplateAttribute));
			request.setAttribute("org.springframework.restdocs.configuration",
					configuration);
			for (AbstractConfigurer configurer : this.configurers) {
				configurer.apply(configuration, context);
			}
			return request;
		}
	}

}
