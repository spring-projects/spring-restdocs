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

import java.util.Map;

import org.springframework.restdocs.RestDocumentationContext;

/**
 * Abstract configurer that declares methods that are internal to the documentation
 * configuration implementation.
 *
 * @author Andy Wilkinson
 */
public abstract class AbstractConfigurer {

	/**
	 * Applies the configurer to the given {@code configuration}.
	 *
	 * @param configuration the configuration to be configured
	 * @param context the current documentation context
	 */
	public abstract void apply(Map<String, Object> configuration,
			RestDocumentationContext context);

}
