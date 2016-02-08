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

package org.springframework.restdocs;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code RestDocumentationContext} encapsulates the context in which the documentation of
 * a RESTful API is being performed.
 *
 * @author Andy Wilkinson
 */
public final class RestDocumentationContext {

	private final AtomicInteger stepCount = new AtomicInteger(0);

	private final Class<?> testClass;

	private final String testMethodName;

	private final File outputDirectory;

	/**
	 * Creates a new {@code RestDocumentationContext} for a test on the given
	 * {@code testClass} with given {@code testMethodName} that will generate
	 * documentation to the given {@code outputDirectory}.
	 *
	 * @param testClass the class whose test is being executed
	 * @param testMethodName the name of the test method that is being executed
	 * @param outputDirectory the directory to which documentation should be written.
	 * @deprecated Since 1.1 in favor of {@link ManualRestDocumentation}.
	 */
	@Deprecated
	public RestDocumentationContext(Class<?> testClass, String testMethodName,
			File outputDirectory) {
		this.testClass = testClass;
		this.testMethodName = testMethodName;
		this.outputDirectory = outputDirectory;
	}

	/**
	 * Returns the class whose tests are currently executing.
	 *
	 * @return The test class
	 */
	public Class<?> getTestClass() {
		return this.testClass;
	}

	/**
	 * Returns the name of the test method that is currently executing.
	 *
	 * @return The name of the test method
	 */
	public String getTestMethodName() {
		return this.testMethodName;
	}

	/**
	 * Returns the current step count and then increments it.
	 *
	 * @return The step count prior to it being incremented
	 */
	int getAndIncrementStepCount() {
		return this.stepCount.getAndIncrement();
	}

	/**
	 * Returns the current step count.
	 *
	 * @return The current step count
	 */
	public int getStepCount() {
		return this.stepCount.get();
	}

	/**
	 * Returns the output directory to which generated snippets should be written.
	 *
	 * @return the output directory
	 */
	public File getOutputDirectory() {
		return this.outputDirectory;
	}

}
