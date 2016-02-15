/*
 * Copyright 2014-2016 the original author or authors.
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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A JUnit {@link TestRule} used to automatically manage the
 * {@link RestDocumentationContext}.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public class JUnitRestDocumentation
		implements RestDocumentationContextProvider, TestRule {

	private final ManualRestDocumentation delegate;

	/**
	 * Creates a new {@code JUnitRestDocumentation} instance that will generate snippets
	 * to the given {@code outputDirectory}.
	 *
	 * @param outputDirectory the output directory
	 */
	public JUnitRestDocumentation(String outputDirectory) {
		this.delegate = new ManualRestDocumentation(outputDirectory);
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				Class<?> testClass = description.getTestClass();
				String methodName = description.getMethodName();
				JUnitRestDocumentation.this.delegate.beforeTest(testClass, methodName);
				try {
					base.evaluate();
				}
				finally {
					JUnitRestDocumentation.this.delegate.afterTest();
				}
			}

		};

	}

	@Override
	public RestDocumentationContext beforeOperation() {
		return this.delegate.beforeOperation();
	}

}
