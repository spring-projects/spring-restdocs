/*
 * Copyright 2014-present the original author or authors.
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
