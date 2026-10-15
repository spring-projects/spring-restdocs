/*
 * Copyright 2014-present the original author or authors.
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
