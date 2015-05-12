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

import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

/**
 * Base class for {@link NestedConfigurer} implementations.
 * 
 * @author Andy Wilkinson
 * @param <PARENT> The type of the configurer's parent
 */
abstract class AbstractNestedConfigurer<PARENT extends MockMvcConfigurer> extends
		AbstractConfigurer implements NestedConfigurer<PARENT>, MockMvcConfigurer {

	private final PARENT parent;

	protected AbstractNestedConfigurer(PARENT parent) {
		this.parent = parent;
	}

	@Override
	public PARENT and() {
		return this.parent;
	}

	@Override
	public void afterConfigurerAdded(ConfigurableMockMvcBuilder<?> builder) {
		this.parent.afterConfigurerAdded(builder);
	}

	@Override
	public RequestPostProcessor beforeMockMvcCreated(
			ConfigurableMockMvcBuilder<?> builder, WebApplicationContext context) {
		return this.parent.beforeMockMvcCreated(builder, context);
	}

}
