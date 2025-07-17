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

package org.springframework.restdocs.build.toolchain;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JavaToolchainService;

public class ToolchainPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		String toolchainVersion = (String) project.findProperty("toolchainVersion");
		if (toolchainVersion != null) {
			JavaToolchainService toolchainService = project.getExtensions().getByType(JavaToolchainService.class);
			project.getTasks()
				.withType(Test.class)
				.configureEach((test) -> test.getJavaLauncher()
					.set(toolchainService.launcherFor(
							(spec) -> spec.getLanguageVersion().set(JavaLanguageVersion.of(toolchainVersion)))));
		}
	}

}
