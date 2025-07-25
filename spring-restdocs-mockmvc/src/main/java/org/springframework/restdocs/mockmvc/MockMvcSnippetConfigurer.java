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

import org.springframework.restdocs.config.SnippetConfigurer;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

/**
 * A configurer that can be used to configure the generated documentation snippets.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public final class MockMvcSnippetConfigurer extends
		SnippetConfigurer<MockMvcRestDocumentationConfigurer, MockMvcSnippetConfigurer> implements MockMvcConfigurer {

	MockMvcSnippetConfigurer(MockMvcRestDocumentationConfigurer parent) {
		super(parent);
	}

	@Override
	public void afterConfigurerAdded(ConfigurableMockMvcBuilder<?> builder) {
		and().afterConfigurerAdded(builder);
	}

	@Override
	public RequestPostProcessor beforeMockMvcCreated(ConfigurableMockMvcBuilder<?> builder,
			WebApplicationContext context) {
		return and().beforeMockMvcCreated(builder, context);
	}

}
