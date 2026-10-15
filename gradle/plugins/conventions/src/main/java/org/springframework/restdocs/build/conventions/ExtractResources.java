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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/**
 * {@link Task} to extract resources from the classpath and write them to disk.
 *
 * @author Andy Wilkinson
 */
public abstract class ExtractResources extends DefaultTask {

	@Input
	public abstract Property<String> getPackageName();

	@Input
	public abstract ListProperty<String> getResourceNames();

	@OutputDirectory
	public abstract DirectoryProperty getDestinationDirectory();

	@Input
	public abstract MapProperty<String, String> getProperties();

	@TaskAction
	void extractResources() throws IOException {
		for (String resourceName : getResourceNames().get()) {
			try (InputStream resourceStream = getClass().getClassLoader()
				.getResourceAsStream(getPackageName().getOrElse("").replace(".", "/") + "/" + resourceName)) {
				String resource = new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
				for (Map.Entry<String, String> property : getProperties().get().entrySet()) {
					resource = resource.replace("${%s}".formatted(property.getKey()), property.getValue());
				}
				Files.writeString(getDestinationDirectory().file(resourceName).get().getAsFile().toPath(), resource);
			}
		}
	}

}
