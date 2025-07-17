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

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Base class for {@link Plugin} conventions.
 *
 * @param <T> the type of plugin to which the conventions apply
 * @author Andy Wilkinson
 */
abstract class Conventions<T extends Plugin<Project>> {

	private final Project project;

	private final Class<T> pluginType;

	Conventions(Project project, Class<T> pluginType) {
		this.project = project;
		this.pluginType = pluginType;
	}

	void apply() {
		this.project.getPlugins().withType(this.pluginType).all(this::apply);
	}

	abstract void apply(T plugin);

	protected Project getProject() {
		return this.project;
	}

}
