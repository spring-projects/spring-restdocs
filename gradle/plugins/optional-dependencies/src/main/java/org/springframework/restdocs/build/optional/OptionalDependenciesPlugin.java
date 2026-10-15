/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.build.optional;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Usage;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.plugins.ide.eclipse.EclipsePlugin;
import org.gradle.plugins.ide.eclipse.model.EclipseModel;

/**
 * A {@code Plugin} that adds support for Maven-style optional dependencies. Creates a new
 * {@code optional} configuration. The {@code optional} configuration is part of the
 * project's compile and runtime classpath's but does not affect the classpath of
 * dependent projects.
 *
 * @author Andy Wilkinson
 */
public class OptionalDependenciesPlugin implements Plugin<Project> {

	/**
	 * Name of the {@code optional} configuration.
	 */
	public static final String OPTIONAL_CONFIGURATION_NAME = "optional";

	@Override
	public void apply(Project project) {
		Configuration optional = project.getConfigurations().create(OPTIONAL_CONFIGURATION_NAME);
		project.getConfigurations().all((configuration) -> {
			if (configuration.getName().startsWith("testRuntimeClasspath_")
					|| configuration.getName().startsWith("testCompileClasspath_")) {
				configuration.extendsFrom(optional);
			}
		});
		optional.attributes((attributes) -> attributes.attribute(Usage.USAGE_ATTRIBUTE,
				project.getObjects().named(Usage.class, Usage.JAVA_RUNTIME)));
		project.getPlugins().withType(JavaPlugin.class, (javaPlugin) -> {
			SourceSetContainer sourceSets = project.getExtensions()
				.getByType(JavaPluginExtension.class)
				.getSourceSets();
			sourceSets.all((sourceSet) -> {
				sourceSet.setCompileClasspath(sourceSet.getCompileClasspath().plus(optional));
				sourceSet.setRuntimeClasspath(sourceSet.getRuntimeClasspath().plus(optional));
			});
			project.getTasks()
				.withType(Javadoc.class)
				.all((javadoc) -> javadoc.setClasspath(javadoc.getClasspath().plus(optional)));
		});
		project.getPlugins()
			.withType(EclipsePlugin.class,
					(eclipsePlugin) -> project.getExtensions()
						.getByType(EclipseModel.class)
						.classpath((classpath) -> classpath.getPlusConfigurations().add(optional)));
	}

}
