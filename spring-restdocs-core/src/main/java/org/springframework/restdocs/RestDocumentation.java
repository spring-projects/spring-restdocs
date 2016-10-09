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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A JUnit {@link TestRule} used to bootstrap the generation of REST documentation from
 * JUnit tests.
 *
 * @author Andy Wilkinson
 * @deprecated Since 1.1 in favor of {@link JUnitRestDocumentation}
 */
@Deprecated
public class RestDocumentation implements TestRule, RestDocumentationContextProvider {

	private final JUnitRestDocumentation delegate;

	/**
	 * Creates a new {@code RestDocumentation} instance that will generate snippets to the
	 * to &lt;gradle/maven build path&gt;/generated-snippet if no runtime property ({@code snippetOutputDirectory}) is set.
	 */
	public RestDocumentation() {
		this.delegate = new JUnitRestDocumentation();
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return this.delegate.apply(base, description);
	}

	@Override
	public RestDocumentationContext beforeOperation() {
		return this.delegate.beforeOperation();
	}

}
