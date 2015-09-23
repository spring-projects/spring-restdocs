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

import org.springframework.core.io.Resource;

/**
 * A {@code TemplateResourceResolver} is responsible for resolving a name for a template
 * into a {@link Resource} from which the template can be read.
 *
 * @author Andy Wilkinson
 */
public interface TemplateResourceResolver {

	/**
	 * Resolves a {@link Resource} for the template with the given {@code name}.
	 *
	 * @param name the name of the template
	 * @return the {@code Resource} from which the template can be read
	 */
	Resource resolveTemplateResource(String name);

}
