/*
 * Copyright 2012-2016 the original author or authors.
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

/**
 * {@code ManualRestDocumentation} is used to manually manage the
 * {@link RestDocumentationContext}. Primarly intended for use with TestNG, but suitable
 * for use in any environment where manual management of the context is required.
 * <p>
 * Users of JUnit should use {@link JUnitRestDocumentation} and take advantage of its
 * Rule-based support for automatic management of the context.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public final class ManualRestDocumentation implements RestDocumentationContextProvider {

	private final File outputDirectory;

	private RestDocumentationContext context;

	/**
	 * Creates a new {@code ManualRestDocumentation} instance that will generate snippets
	 * to the given {@code outputDirectory}.
	 *
	 * @param outputDirectory the output directory
	 */
	public ManualRestDocumentation(String outputDirectory) {
		this.outputDirectory = new File(outputDirectory);
	}

	/**
	 * Notification that a test is about to begin. Creates a
	 * {@link RestDocumentationContext} for the test on the given {@code testClass} with
	 * the given {@code testMethodName}. Must be followed by a call to
	 * {@link #afterTest()} once the test has completed.
	 *
	 * @param testClass the test class
	 * @param testMethodName the name of the test method
	 * @throws IllegalStateException if a context has already be created
	 */
	@SuppressWarnings("deprecation")
	public void beforeTest(Class<?> testClass, String testMethodName) {
		if (this.context != null) {
			throw new IllegalStateException(
					"Context already exists. Did you forget to call afterTest()?");
		}
		this.context = new RestDocumentationContext(testClass, testMethodName,
				this.outputDirectory);
	}

	/**
	 * Notification that a test has completed. Clears the {@link RestDocumentationContext}
	 * that was previously established by a call to {@link #beforeTest(Class, String)}.
	 */
	public void afterTest() {
		this.context = null;
	}

	@Override
	public RestDocumentationContext beforeOperation() {
		this.context.getAndIncrementStepCount();
		return this.context;
	}

}
