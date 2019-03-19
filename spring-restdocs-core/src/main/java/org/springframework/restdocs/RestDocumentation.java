/*
 * Copyright 2014-2015 the original author or authors.
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

package org.springframework.restdocs;

import java.io.File;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A JUnit {@link TestRule} used to bootstrap the generation of REST documentation from
 * JUnit tests.
 *
 * @author Andy Wilkinson
 *
 */
public class RestDocumentation implements TestRule {

	private final String outputDirectory;

	private RestDocumentationContext context;

	/**
	 * Creates a new {@code RestDocumentation} instance that will generate snippets to the
	 * given {@code outputDirectory}.
	 *
	 * @param outputDirectory the output directory
	 */
	public RestDocumentation(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				Class<?> testClass = description.getTestClass();
				String methodName = description.getMethodName();
				RestDocumentation.this.context = new RestDocumentationContext(testClass,
						methodName, new File(RestDocumentation.this.outputDirectory));
				try {
					base.evaluate();
				}
				finally {
					RestDocumentation.this.context = null;
				}
			}

		};

	}

	/**
	 * Notification that a RESTful operation that should be documented is about to be
	 * performed. Returns a {@link RestDocumentationContext} for the operation.
	 *
	 * @return the context for the operation
	 */
	public RestDocumentationContext beforeOperation() {
		this.context.getAndIncrementStepCount();
		return this.context;
	}

}
