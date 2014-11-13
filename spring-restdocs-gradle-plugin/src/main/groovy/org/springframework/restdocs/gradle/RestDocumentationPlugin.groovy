/*
 * Copyright 2014 the original author or authors.
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

package org.springframework.restdocs.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.ide.eclipse.GenerateEclipseClasspath


class RestDocumentationPlugin implements Plugin<Project> {

	void apply(Project project) {
		project.sourceSets {
			documentation {
				java { srcDir 'src/documentation/java' }

				compileClasspath = project.files(project.sourceSets.main.output,
						project.configurations.documentationCompile)
				runtimeClasspath = project.files(project.sourceSets.main.output,
						project.sourceSets.documentation.output,
						project.configurations.documentationRuntime)
			}
		}

		project.configurations.documentationCompile.extendsFrom project.configurations.compile

		project.configurations.documentationRuntime.extendsFrom(
				project.configurations.runtime, project.configurations.documentationCompile)

		project.tasks.withType(GenerateEclipseClasspath) {
			it.classpath.sourceSets += project.sourceSets.documentation
			it.classpath.plusConfigurations += [
				project.configurations.documentationRuntime
			]
		}

		def restDocumentationSnippets = project.tasks.create('restDocumentationSnippets', RestDocumentationSnippets)


		project.apply plugin: 'org.asciidoctor.gradle.asciidoctor'

		project.asciidoctor {
			dependsOn restDocumentationSnippets
			group ''
			sourceDir = project.file 'src/documentation/asciidoc'
			options = [
				attributes: [
					generated: new File("$project.buildDir/generated-documentation").toURI().toURL(),
					'allow-uri-read': true
				]
			]
			inputs.files restDocumentationSnippets.outputs.files
		}

		project.task('restDocumentation') {
			dependsOn ':asciidoctor'
			description 'Generates RESTful service documentation using AsciiDoctor'
			group 'Documentation'
		}
	}
}