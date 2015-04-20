/*
 * Copyright 2014-2015 the original author or authors.
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

package org.springframework.restdocs.build

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.GradleBuild

public class SampleBuildConfigurer {

	private final String name

	private String workingDir

	SampleBuildConfigurer(String name) {
		this.name = name
	}

	void workingDir(String workingDir) {
		this.workingDir = workingDir
	}

	Task createTask(Project project, Object... dependencies) {
		Task mavenBuild = mavenBuild(project, dependencies)
		Task gradleBuild = gradleBuild(project, dependencies)
		Task sampleBuild = project.tasks.create name
		sampleBuild.description = "Builds the ${name} sample"
		sampleBuild.group = "Build"
		sampleBuild.dependsOn mavenBuild, gradleBuild
		return sampleBuild
	}

	private Task mavenBuild(Project project, Object... dependencies) {
		Task mavenBuild = project.tasks.create("${name}Maven", Exec)
		mavenBuild.description = "Builds the ${name} sample with Maven"
		mavenBuild.group = "Build"
		mavenBuild.workingDir = this.workingDir
		String suffix = File.separatorChar == '/' ? '' : '.bat'
		mavenBuild.commandLine = [System.env.MAVEN_HOME ?
				"${System.env.MAVEN_HOME}/bin/mvn${suffix}" : "mvn${suffix}",
						'clean', 'package']
		mavenBuild.dependsOn dependencies
		return mavenBuild
	}

	private Task gradleBuild(Project project, Object... dependencies) {
		Task gradleBuild = project.tasks.create("${name}Gradle", GradleBuild)
		gradleBuild.description = "Builds the ${name} sample with Gradle"
		gradleBuild.group = "Build"
		gradleBuild.dir = this.workingDir
		gradleBuild.tasks = ['clean', 'build']
		gradleBuild.dependsOn dependencies
		return gradleBuild
	}

}