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

package org.springframework.restdocs.build.conventions;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.plugins.JavaTestFixturesPlugin;

/**
 * Conventions for when the {@link JavaTestFixturesPlugin} is applied.
 *
 * @author Andy Wilkinson
 */
class JavaTestFixturesPluginConventions extends Conventions<JavaTestFixturesPlugin> {

	JavaTestFixturesPluginConventions(Project project) {
		super(project, JavaTestFixturesPlugin.class);
	}

	@Override
	void apply(JavaTestFixturesPlugin plugin) {
		ConfigurationContainer configurations = getProject().getConfigurations();
		AdhocComponentWithVariants javaComponent = (AdhocComponentWithVariants) getProject().getComponents()
			.getByName("java");
		javaComponent.withVariantsFromConfiguration(configurations.getByName("testFixturesApiElements"),
				(variant) -> variant.skip());
		javaComponent.withVariantsFromConfiguration(configurations.getByName("testFixturesRuntimeElements"),
				(variant) -> variant.skip());
	}

}
