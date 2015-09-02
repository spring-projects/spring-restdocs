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

package org.springframework.restdocs.templates;

import java.util.Map;

/**
 * A compiled {@code Template} that can be rendered to a {@link String}.
 *
 * @author Andy Wilkinson
 *
 */
public interface Template {

	/**
	 * Renders the template to a {@link String} using the given {@code context} for
	 * variable/property resolution.
	 *
	 * @param context The context to use
	 * @return The rendered template
	 */
	String render(Map<String, Object> context);

}
