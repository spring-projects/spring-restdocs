/*
 * Copyright 2025 the original author or authors.
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

package org.springframework.restdocs.build.antora;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gradle.node.NodeExtension;
import com.github.gradle.node.npm.task.NpmInstallTask;
import io.spring.gradle.antora.GenerateAntoraYmlPlugin;
import io.spring.gradle.antora.GenerateAntoraYmlTask;
import org.antora.gradle.AntoraTask;
import org.gradle.StartParameter;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

/**
 * {@link Plugin} that configures Antora.
 *
 * @author Andy Wilkinson
 * @author Phillip Webb
 */
public class AntoraPlugin implements Plugin<Project> {

	/**
	 * Default Antora source directory.
	 */
	public static final String ANTORA_SOURCE_DIR = "src/docs/antora";

	private static final List<String> NAV_FILES = List.of("nav.adoc", "local-nav.adoc");

	@Override
	public void apply(Project target) {
		target.getPlugins().apply(org.antora.gradle.AntoraPlugin.class);
		target.getPlugins().apply(GenerateAntoraYmlPlugin.class);
		TaskContainer tasks = target.getTasks();
		Provider<Directory> nodeProjectDir = target.getLayout().getBuildDirectory().dir(".gradle/nodeproject");
		TaskProvider<GenerateAntoraPlaybook> generateAntoraPlaybook = tasks.register("generateAntoraPlaybook",
				GenerateAntoraPlaybook.class, (task) -> task.getOutputFile()
					.set(nodeProjectDir.map((directory) -> directory.file("antora-playbook.yml"))));
		TaskProvider<Copy> copyAntoraPackageJson = tasks.register("copyAntoraPackageJson", Copy.class, (task) -> {
			task.from(target.getRootProject().file("antora"),
					(spec) -> spec.include("package.json", "package-lock.json", "patches/**"))
				.into(nodeProjectDir);
		});
		TaskProvider<NpmInstallTask> npmInstall = tasks.register("antoraNpmInstall", NpmInstallTask.class, (task) -> {
			task.dependsOn(copyAntoraPackageJson);
			Map<String, String> environment = new HashMap<>();
			environment.put("npm_config_omit", "optional");
			environment.put("npm_config_update_notifier", "false");
			task.getEnvironment().set(environment);
			task.getNpmCommand().set(List.of("ci", "--silent", "--no-progress"));
		});
		tasks.withType(GenerateAntoraYmlTask.class, (task) -> {
			task.getOutputs().doNotCacheIf("getAsciidocAttributes() changes output", (t) -> true);
			task.setProperty("componentName", "restdocs");
			task.setProperty("outputFile",
					target.getLayout().getBuildDirectory().file("generated/docs/antora-yml/antora.yml"));
			task.setProperty("yml", getDefaultYml(target));
			task.getAsciidocAttributes().putAll(getAsciidocAttributes(target));

		});
		tasks.withType(AntoraTask.class, (antoraTask) -> {
			antoraTask.setGroup("Documentation");
			antoraTask.dependsOn(npmInstall, generateAntoraPlaybook);
			antoraTask.setPlaybook("antora-playbook.yml");
			antoraTask.setUiBundleUrl(getUiBundleUrl(target));
			antoraTask.getArgs().set(target.provider(() -> getAntoraNpxArgs(target, antoraTask)));
			target.getPlugins()
				.withType(JavaBasePlugin.class,
						(javaBasePlugin) -> target.getTasks()
							.getByName(JavaBasePlugin.CHECK_TASK_NAME)
							.dependsOn(antoraTask));
		});
		target.getExtensions().configure(NodeExtension.class, (nodeExtension) -> {
			nodeExtension.getWorkDir().set(target.getLayout().getBuildDirectory().dir(".gradle/nodejs"));
			nodeExtension.getNpmWorkDir().set(target.getLayout().getBuildDirectory().dir(".gradle/npm"));
			nodeExtension.getNodeProjectDir().set(nodeProjectDir);
		});

	}

	private Map<String, ?> getDefaultYml(Project project) {
		String navFile = null;
		for (String candidate : NAV_FILES) {
			if (project.file(ANTORA_SOURCE_DIR + "/" + candidate).exists()) {
				if (navFile != null) {
					throw new IllegalStateException("Multiple nav files found");
				}
				navFile = candidate;
			}
		}
		Map<String, Object> defaultYml = new LinkedHashMap<>();
		defaultYml.put("title", "Spring REST Docs");
		if (navFile != null) {
			defaultYml.put("nav", List.of(navFile));
		}
		return defaultYml;
	}

	private Provider<Map<String, String>> getAsciidocAttributes(Project project) {
		return project.provider(() -> {
			String version = project.getVersion().toString();
			return Map.of( //
					"branch-or-tag", version.toString().endsWith("SNAPSHOT") ? "main" : "v$%s".formatted(version), //
					"github", "https://github.com/spring-projects/spring-restdocs", //
					"include-java", "ROOT:example$java/org/springframework/restdocs/docs", //
					"project-version", version.toString(), //
					"samples", "https://github.com/spring-projects/spring-restdocs-samples/tree/main", //
					"source", "https://github.com/spring-projects/spring-restdocs/tree/{branch-or-tag}", //
					"spring-boot-docs", "https://docs.spring.io/spring-boot/reference", //
					"spring-framework-api", "https://docs.spring.io/spring-framework/docs/7.0.0/javadoc-api", //
					"spring-framework-docs", "https://docs.spring.io/spring-framework/reference");
		});
	}

	private String getUiBundleUrl(Project project) {
		try {
			File packageJson = project.getRootProject().file("antora/package.json");
			ObjectMapper objectMapper = new ObjectMapper();
			Map<?, ?> json = objectMapper.readerFor(Map.class).readValue(packageJson);
			Map<?, ?> config = (json != null) ? (Map<?, ?>) json.get("config") : null;
			String url = (config != null) ? (String) config.get("ui-bundle-url") : null;
			if (url == null || url.length() == 0) {
				throw new IllegalStateException("package.json has no ui-bundle-url config");
			}
			return url;
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	private List<String> getAntoraNpxArgs(Project project, AntoraTask antoraTask) {
		logWarningIfNodeModulesInUserHome(project);
		StartParameter startParameter = project.getGradle().getStartParameter();
		boolean showStacktrace = startParameter.getShowStacktrace().name().startsWith("ALWAYS");
		boolean debugLogging = project.getGradle().getStartParameter().getLogLevel() == LogLevel.DEBUG;
		String playbookPath = antoraTask.getPlaybook();
		List<String> arguments = new ArrayList<>();
		arguments.addAll(List.of("--package", "@antora/cli"));
		arguments.add("antora");
		arguments.addAll((!showStacktrace) ? Collections.emptyList() : List.of("--stacktrace"));
		arguments.addAll((!debugLogging) ? List.of("--quiet") : List.of("--log-level", "all"));
		arguments.addAll(List.of("--ui-bundle-url", antoraTask.getUiBundleUrl()));
		arguments.add(playbookPath);
		return arguments;
	}

	private void logWarningIfNodeModulesInUserHome(Project project) {
		if (new File(System.getProperty("user.home"), "node_modules").exists()) {
			project.getLogger()
				.warn("Detected the existence of $HOME/node_modules. This directory is "
						+ "not compatible with this plugin. Please remove it.");
		}
	}

}
