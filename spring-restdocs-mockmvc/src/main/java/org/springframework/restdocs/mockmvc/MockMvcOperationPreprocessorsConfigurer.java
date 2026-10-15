/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.mockmvc;

import org.springframework.restdocs.config.OperationPreprocessorsConfigurer;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

/**
 * A configurer that can be used to configure the operation preprocessors.
 *
 * @author Filip Hrisafov
 * @since 2.0.0
 */
public final class MockMvcOperationPreprocessorsConfigurer extends
		OperationPreprocessorsConfigurer<MockMvcRestDocumentationConfigurer, MockMvcOperationPreprocessorsConfigurer>
		implements MockMvcConfigurer {

	MockMvcOperationPreprocessorsConfigurer(MockMvcRestDocumentationConfigurer parent) {
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
