/*
 * Copyright 2014-present the original author or authors.
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
