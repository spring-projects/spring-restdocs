/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.build.conventions;

import io.spring.nohttp.gradle.NoHttpCheckstylePlugin;
import io.spring.nohttp.gradle.NoHttpExtension;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.plugins.quality.Checkstyle;

class NoHttpConventions {

	private final Project project;

	NoHttpConventions(Project project) {
		this.project = project;
	}

	void apply() {
		if (this.project.equals(this.project.getRootProject())) {
			return;
		}
		this.project.getPluginManager().apply(NoHttpCheckstylePlugin.class);
		configureNoHttpExtension(this.project, this.project.getExtensions().getByType(NoHttpExtension.class));
		this.project.getTasks()
			.named(NoHttpCheckstylePlugin.CHECKSTYLE_NOHTTP_TASK_NAME, Checkstyle.class)
			.configure((task) -> task.getConfigDirectory().set(this.project.getRootProject().file("config/nohttp")));
	}

	private void configureNoHttpExtension(Project project, NoHttpExtension extension) {
		ConfigurableFileTree source = extension.getSource();
		source.exclude("bin/**");
		source.exclude("build/**");
		source.exclude("out/**");
		source.exclude("target/**");
		source.exclude(".settings/**");
		source.exclude(".classpath");
		source.exclude(".project");
		source.exclude(".gradle");
	}

}
