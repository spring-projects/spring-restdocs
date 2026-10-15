/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.build.conventions;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * A {@link Plugin} that applies the Spring REST Docs project's Gradle build conventions.
 *
 * @author Andy Wilkinson
 */
public class ConventionsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		new JavaBasePluginConventions(project).apply();
		new MavenPublishPluginConventions(project).apply();
		new JavaTestFixturesPluginConventions(project).apply();
		new NoHttpConventions(project).apply();
	}

}
