/*
 * Copyright 2014-present the original author or authors.
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
