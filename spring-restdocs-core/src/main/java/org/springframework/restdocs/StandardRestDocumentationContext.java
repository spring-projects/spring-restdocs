/*
 * Copyright 2014-2017 the original author or authors.
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

package org.springframework.restdocs;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Standard implementation of {@link RestDocumentationContext}.
 *
 * @author Andy Wilkinson
 */
final class StandardRestDocumentationContext implements RestDocumentationContext {

	private final AtomicInteger stepCount = new AtomicInteger(0);

	private final Class<?> testClass;

	private final String testMethodName;

	private final File outputDirectory;

	StandardRestDocumentationContext(Class<?> testClass, String testMethodName,
			File outputDirectory) {
		this.testClass = testClass;
		this.testMethodName = testMethodName;
		this.outputDirectory = outputDirectory;
	}

	@Override
	public Class<?> getTestClass() {
		return this.testClass;
	}

	@Override
	public String getTestMethodName() {
		return this.testMethodName;
	}

	int getAndIncrementStepCount() {
		return this.stepCount.getAndIncrement();
	}

	@Override
	public int getStepCount() {
		return this.stepCount.get();
	}

	@Override
	public File getOutputDirectory() {
		return this.outputDirectory;
	}

}
