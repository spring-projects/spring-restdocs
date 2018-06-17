/*
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.build.matrix

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.testing.Test

public class MatrixTestExtension {

	private List<Entry> entries = []

	MatrixTestExtension(Project project) {
		project.afterEvaluate {
			configureTestTasks(project)
		}
	}

	void methodMissing(String name, args) {
		Entry entry = new Entry();
		Closure closure = args[0]
		closure.delegate = entry
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()
		entries << entry
	}

	void configureTestTasks(Project project) {
		if (!entries.empty) {
			cartesianProduct(entries.collect { entry ->
				entry.versions.collect { ['group': entry.group, 'version': it] }
			}).forEach { configureTestTask(project, it) }
		}
	}

	void configureTestTask(Project project, List<Map<String, String>> versionSelectors) {
		String identifier = "";
		versionSelectors.forEach {
			identifier += "_${it.group}_${it.version}"
		}
		String description = "Runs the unit tests using "
		description += versionSelectors.collect { "${it.group} ${it.version}" }.join(", ")
		Test matrixTest = project.tasks.create("matrixTest" + identifier, Test) { test ->
			test.setDescription(description);
			test.setGroup(JavaBasePlugin.VERIFICATION_GROUP);
			def testSourceSet = project.sourceSets.test
			def configuration = project.configurations.create(testSourceSet.runtimeClasspathConfigurationName + identifier) {
				extendsFrom(project.configurations.getByName(testSourceSet.runtimeClasspathConfigurationName))
				resolutionStrategy.eachDependency { dependency ->
					versionSelectors
							.findAll{ it.group == dependency.requested.group }
							.each { dependency.useVersion it.version }
				}
			}
			classpath = project.files(testSourceSet.output, project.sourceSets.main.output, configuration)
		}
		project.tasks.getByName('check').dependsOn(matrixTest)
	}

	List<List<Map<String, String>>> cartesianProduct(List<List<Map<String, String>>> lists) {
		if (lists.size() == 1) {
			return lists
		}
		return cartesianProduct(lists, 0)
	}

	List<List<Map<String, String>>> cartesianProduct(List<List<Map<String, String>>> lists, int index) {
		List<List<Map<String, String>>> result = [];
		if (index == lists.size()) {
			result.add([]);
		} else {
			lists.get(index).each { list ->
				cartesianProduct(lists, index + 1).each { product ->
					product.add(list)
					result.add(product)
				}
			}
		}
		return result;
	}

	class Entry {

		String group

		List<String> versions

	}

}