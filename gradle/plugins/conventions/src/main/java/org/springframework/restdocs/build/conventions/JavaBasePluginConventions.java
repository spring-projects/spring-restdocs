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

import java.util.List;
import java.util.Map;

import io.spring.javaformat.gradle.SpringJavaFormatPlugin;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.quality.CheckstyleExtension;
import org.gradle.api.plugins.quality.CheckstylePlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;

/**
 * Conventions for the {@link JavaBasePlugin}.
 *
 * @author Andy Wilkinson
 */
class JavaBasePluginConventions extends Conventions<JavaBasePlugin> {

	JavaBasePluginConventions(Project project) {
		super(project, JavaBasePlugin.class);
	}

	@Override
	void apply(JavaBasePlugin plugin) {
		configureCheckstyle();
		configureJavaFormat();
		configureSourceAndTargetCompatibility();
		configureJavaCompileTasks();
		configureTestTasks();
		configureDependencyManagement();
	}

	private void configureDependencyManagement() {
		Configuration internal = getProject().getConfigurations().create("internal", (configuration) -> {
			configuration.setCanBeConsumed(false);
			configuration.setCanBeResolved(false);
			DependencyHandler dependencies = getProject().getDependencies();
			configuration.getDependencies()
				.add(dependencies.platform(dependencies.project(Map.of("path", ":spring-restdocs-platform"))));
		});
		getProject().getExtensions().configure(JavaPluginExtension.class, (extension) -> {
			SourceSetContainer sourceSets = extension.getSourceSets();
			sourceSets.all((sourceSet) -> configureDependencyManagement(sourceSet, internal));
		});
	}

	private void configureDependencyManagement(SourceSet sourceSet, Configuration internal) {
		getProject().getConfigurations()
			.getByName(sourceSet.getCompileClasspathConfigurationName())
			.extendsFrom(internal);
		getProject().getConfigurations()
			.getByName(sourceSet.getRuntimeClasspathConfigurationName())
			.extendsFrom(internal);
	}

	private void configureSourceAndTargetCompatibility() {
		getProject().getExtensions().configure(JavaPluginExtension.class, (extension) -> {
			extension.setSourceCompatibility(JavaVersion.VERSION_17);
			extension.setTargetCompatibility(JavaVersion.VERSION_17);
		});
	}

	private void configureJavaCompileTasks() {
		getProject().getTasks().withType(JavaCompile.class).configureEach((javaCompile) -> {
			CompileOptions options = javaCompile.getOptions();
			options.setCompilerArgs(List.of("-Werror", "-Xlint:unchecked", "-Xlint:deprecation", "-Xlint:rawtypes",
					"-Xlint:varargs", "-Xlint:options"));
			options.setEncoding("UTF-8");
		});
	}

	private void configureTestTasks() {
		getProject().getTasks().withType(Test.class).configureEach((test) -> {
			test.setMaxHeapSize("1024M");
			test.useJUnitPlatform();
		});
	}

	private void configureCheckstyle() {
		getProject().getPlugins().apply(CheckstylePlugin.class);
		String checkstyleVersion = "10.12.4";
		getProject().getExtensions().configure(CheckstyleExtension.class, (checkstyle) -> {
			Project rootProject = getProject().getRootProject();
			checkstyle.setConfigFile(rootProject.file("config/checkstyle/checkstyle.xml"));
			checkstyle.setConfigProperties(Map.of("checkstyle.config.dir", rootProject.file("config/checkstyle")));
			checkstyle.setToolVersion(checkstyleVersion);
		});
		getProject().getDependencies()
			.add("checkstyle", "io.spring.javaformat:spring-javaformat-checkstyle:"
					+ SpringJavaFormatPlugin.class.getPackage().getImplementationVersion());
		getProject().getDependencies().add("checkstyle", "com.puppycrawl.tools:checkstyle:" + checkstyleVersion);
	}

	private void configureJavaFormat() {
		getProject().getPlugins().apply(SpringJavaFormatPlugin.class);
	}

}
