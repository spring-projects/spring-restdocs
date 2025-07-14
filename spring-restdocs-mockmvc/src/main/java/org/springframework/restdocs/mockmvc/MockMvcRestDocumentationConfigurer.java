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

package org.springframework.restdocs.mockmvc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.config.RestDocumentationConfigurer;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.WebApplicationContext;

/**
 * A MockMvc-specific {@link RestDocumentationConfigurer}.
 *
 * @author Andy Wilkinson
 * @author Filip Hrisafov
 * @since 1.1.0
 */
public final class MockMvcRestDocumentationConfigurer extends
		RestDocumentationConfigurer<MockMvcSnippetConfigurer, MockMvcOperationPreprocessorsConfigurer, MockMvcRestDocumentationConfigurer>
		implements MockMvcConfigurer {

	private final MockMvcSnippetConfigurer snippetConfigurer = new MockMvcSnippetConfigurer(this);

	private final UriConfigurer uriConfigurer = new UriConfigurer(this);

	private final MockMvcOperationPreprocessorsConfigurer operationPreprocessorsConfigurer = new MockMvcOperationPreprocessorsConfigurer(
			this);

	private final RestDocumentationContextProvider contextManager;

	MockMvcRestDocumentationConfigurer(RestDocumentationContextProvider contextManager) {
		this.contextManager = contextManager;
	}

	/**
	 * Returns a {@link UriConfigurer} that can be used to configure the request URIs that
	 * will be documented.
	 * @return the URI configurer
	 */
	public UriConfigurer uris() {
		return this.uriConfigurer;
	}

	@Override
	public RequestPostProcessor beforeMockMvcCreated(ConfigurableMockMvcBuilder<?> builder,
			WebApplicationContext context) {
		return new ConfigurerApplyingRequestPostProcessor(this.contextManager);
	}

	@Override
	public void afterConfigurerAdded(ConfigurableMockMvcBuilder<?> builder) {
		// Nothing to do
	}

	@Override
	public MockMvcSnippetConfigurer snippets() {
		return this.snippetConfigurer;
	}

	@Override
	public MockMvcOperationPreprocessorsConfigurer operationPreprocessors() {
		return this.operationPreprocessorsConfigurer;
	}

	private final class ConfigurerApplyingRequestPostProcessor implements RequestPostProcessor {

		private static final Function<MockHttpServletRequest, String> urlTemplateExtractor;

		static {
			Function<MockHttpServletRequest, String> fromRequestAttribute = (
					request) -> (String) request.getAttribute(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE);
			Function<MockHttpServletRequest, String> extractor;
			try {
				Method accessorMethod = MockHttpServletRequest.class.getMethod("getUriTemplate");
				extractor = (request) -> {
					String urlTemplate = fromRequestAttribute.apply(request);
					return (urlTemplate != null) ? urlTemplate
							: (String) ReflectionUtils.invokeMethod(accessorMethod, request);
				};
			}
			catch (Exception ex) {
				extractor = fromRequestAttribute;
			}
			urlTemplateExtractor = extractor;
		}

		private final RestDocumentationContextProvider contextManager;

		private ConfigurerApplyingRequestPostProcessor(RestDocumentationContextProvider contextManager) {
			this.contextManager = contextManager;
		}

		@Override
		public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
			RestDocumentationContext context = this.contextManager.beforeOperation();
			Map<String, Object> configuration = new HashMap<>();
			configuration.put(MockHttpServletRequest.class.getName(), request);
			configuration.put(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
					urlTemplateExtractor.apply(request));
			configuration.put(RestDocumentationContext.class.getName(), context);
			request.setAttribute(RestDocumentationResultHandler.ATTRIBUTE_NAME_CONFIGURATION, configuration);
			MockMvcRestDocumentationConfigurer.this.apply(configuration, context);
			MockMvcRestDocumentationConfigurer.this.uriConfigurer.apply(configuration, context);
			return request;
		}

	}

}
