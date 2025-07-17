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

import org.gradle.api.Project;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.plugins.JavaPlatformPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.publish.tasks.GenerateModuleMetadata;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.JavadocMemberLevel;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

/**
 * Conventions for when the {@link MavenPublishPlugin} is applied.
 *
 * @author Andy Wilkinson
 */
class MavenPublishPluginConventions extends Conventions<MavenPublishPlugin> {

	MavenPublishPluginConventions(Project project) {
		super(project, MavenPublishPlugin.class);
	}

	@Override
	void apply(MavenPublishPlugin plugin) {
		getProject().getTasks().withType(GenerateModuleMetadata.class).configureEach((task) -> task.setEnabled(false));
		PublishingExtension publishing = getProject().getExtensions().getByType(PublishingExtension.class);
		configureDeploymentRepository(publishing);
		publishing.publications((publications) -> publications.create("maven", MavenPublication.class,
				this::configureMavenPublication));
	}

	private void configureMavenPublication(MavenPublication maven) {
		configureContents(maven);
		configurePom(maven);
	}

	private void configureContents(MavenPublication maven) {
		getProject().getPlugins().withType(JavaPlugin.class).configureEach((javaPlugin) -> {
			SoftwareComponent java = getProject().getComponents().getByName("java");
			maven.from(java);
			maven.versionMapping((versionMapping) -> {
				versionMapping.usage("java-api", (strategy) -> strategy.fromResolutionResult());
				versionMapping.usage("java-runtime", (strategy) -> strategy.fromResolutionResult());
			});
			getProject().getExtensions().configure(JavaPluginExtension.class, (extension) -> {
				extension.withSourcesJar();
				extension.withJavadocJar();
			});
			getProject().getTasks().withType(Javadoc.class).configureEach((javadoc) -> {
				javadoc.setDescription("Generates project-level javadoc for use in -javadoc jar");
				StandardJavadocDocletOptions options = (StandardJavadocDocletOptions) javadoc.getOptions();
				options.setMemberLevel(JavadocMemberLevel.PROTECTED);
				options.setAuthor(true);
				options.header("Spring REST Docs " + getProject().getVersion());
				options.docTitle(options.getHeader() + " API");
				options.addStringOption("-quiet");
				options.encoding("UTF-8");
				options.source("17");
				options.links(
						"https://docs.spring.io/spring-framework/docs/"
								+ getProject().property("springFrameworkVersion") + "/javadoc-api/",
						"https://docs.jboss.org/hibernate/validator/9.0/api/",
						"https://jakarta.ee/specifications/bean-validation/3.1/apidocs/");
			});
		});
		getProject().getPlugins()
			.withType(JavaPlatformPlugin.class)
			.configureEach((javaPlatformPlugin) -> maven.from(getProject().getComponents().getByName("javaPlatform")));
	}

	private void configureDeploymentRepository(PublishingExtension publishing) {
		Object deploymentRepository = getProject().findProperty("deploymentRepository");
		if (deploymentRepository != null) {
			publishing.getRepositories().maven((repository) -> {
				repository.setName("deployment");
				repository.setUrl(deploymentRepository);
			});
		}
	}

	private void configurePom(MavenPublication maven) {
		maven.pom((pom) -> {
			pom.getName().set(getProject().provider(getProject()::getDescription));
			pom.getDescription().set(getProject().provider(getProject()::getDescription));
			pom.getUrl().set("https://github.com/spring-projects/spring-restdocs");
			pom.organization((organization) -> {
				organization.getName().set("Spring IO");
				organization.getUrl().set("https://projects.spring.io/spring-restdocs");
			});
			pom.licenses((licenses) -> licenses.license((licence) -> {
				licence.getName().set("The Apache Software License, Version 2.0");
				licence.getUrl().set("https://www.apache.org/licenses/LICENSE-2.0.txt");
				licence.getDistribution().set("repo");
			}));
			pom.scm((scm) -> {
				scm.getUrl().set("https://github.com/spring-projects/spring-restdocs");
				scm.getConnection().set("scm:git:git://github.com/spring-projects/spring-restdocs");
				scm.getDeveloperConnection().set("scm:git:git://github.com/spring-projects/spring-restdocs");
			});
			pom.developers((developers) -> developers.developer((developer) -> {
				developer.getId().set("wilkinsona");
				developer.getName().set("Andy Wilkinson");
				developer.getEmail().set("andy.wilkinson@broadcom.com");
			}));
			pom.issueManagement((issueManagement) -> {
				issueManagement.getSystem().set("GitHub");
				issueManagement.getUrl().set("https://github.com/spring-projects/spring-restdocs/issues");
			});
		});
	}

}
