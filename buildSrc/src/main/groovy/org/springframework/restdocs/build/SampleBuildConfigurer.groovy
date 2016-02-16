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

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.GradleBuild

public class SampleBuildConfigurer {

	private final String name

	private String workingDir

	private boolean build = true

	SampleBuildConfigurer(String name) {
		this.name = name
	}

	void workingDir(String workingDir) {
		this.workingDir = workingDir
	}

	void build(boolean build) {
		this.build = build
	}

	Task createTask(Project project, Object... dependencies) {
		File sampleDir = new File(this.workingDir).absoluteFile

		Task sampleBuild = project.tasks.create name
		sampleBuild.description = "Builds the ${name} sample"
		sampleBuild.group = "Build"

		if (new File(sampleDir, 'build.gradle').isFile()) {
			if (build) {
				Task gradleBuild = createGradleBuild(project, dependencies)
				Task verifyIncludesTask = createVerifyIncludes(project, new File(sampleDir, 'build/asciidoc'))
				verifyIncludesTask.dependsOn gradleBuild
				sampleBuild.dependsOn verifyIncludesTask
			}
			sampleBuild.doFirst {
				replaceVersion(new File(this.workingDir, 'build.gradle'),
						"springRestdocsVersion = '.*'",
						"springRestdocsVersion = '${project.version}'")
			}
		}
		else if (new File(sampleDir, 'pom.xml').isFile()) {
			if (build) {
				Task mavenBuild = createMavenBuild(project, sampleDir, dependencies)
				Task verifyIncludesTask = createVerifyIncludes(project, new File(sampleDir, 'target/generated-docs'))
				verifyIncludesTask.dependsOn(mavenBuild)
				sampleBuild.dependsOn verifyIncludesTask
			}
			sampleBuild.doFirst {
				replaceVersion(new File(this.workingDir, 'pom.xml'),
					'<spring-restdocs.version>.*</spring-restdocs.version>',
					"<spring-restdocs.version>${project.version}</spring-restdocs.version>")
			}
		}
		else {
			throw new IllegalStateException("No pom.xml or build.gradle was found in $sampleDir")
		}
		return sampleBuild
	}

	private Task createMavenBuild(Project project, File sampleDir, Object... dependencies) {
		Task mavenBuild = project.tasks.create("${name}Maven", Exec)
		mavenBuild.description = "Builds the ${name} sample with Maven"
		mavenBuild.group = "Build"
		mavenBuild.workingDir = this.workingDir
		mavenBuild.commandLine = [isWindows() ? "${sampleDir.absolutePath}/mvnw.cmd" : './mvnw', 'clean', 'package']
		mavenBuild.dependsOn dependencies
		return mavenBuild
	}

	private boolean isWindows() {
		return File.separatorChar == '\\'
	}

	private Task createGradleBuild(Project project, Object... dependencies) {
		Task gradleBuild = project.tasks.create("${name}Gradle", GradleBuild)
		gradleBuild.description = "Builds the ${name} sample with Gradle"
		gradleBuild.group = "Build"
		gradleBuild.dir = this.workingDir
		gradleBuild.tasks = ['clean', 'build']
		gradleBuild.dependsOn dependencies
		return gradleBuild
	}

	private void replaceVersion(File target, String pattern, String replacement) {
		def lines = target.readLines()
		target.withWriter { writer ->
			lines.each { line ->
				writer.println(line.replaceAll(pattern, replacement))
			}
		}
	}

	private Task createVerifyIncludes(Project project, File buildDir) {
		Task verifyIncludesTask = project.tasks.create("${name}VerifyIncludes")
		verifyIncludesTask.description = "Verifies the includes in the ${name} sample"
		verifyIncludesTask << {
			Map unprocessedIncludes = [:]
			buildDir.eachFileRecurse { file ->
				if (file.name.endsWith('.html')) {
					file.eachLine { line ->
						if (line.contains(new File(this.workingDir).absolutePath)) {
							unprocessedIncludes.get(file, []).add(line)
						}
					}
				}
			}
			if (unprocessedIncludes) {
				StringWriter message = new StringWriter()
				PrintWriter writer = new PrintWriter(message)
				writer.println 'Found unprocessed includes:'
				unprocessedIncludes.each { file, lines ->
					writer.println "    ${file}:"
					lines.each { line -> writer.println "        ${line}" }
				}
				throw new GradleException(message.toString())
			}
		}
		return verifyIncludesTask
	}
}