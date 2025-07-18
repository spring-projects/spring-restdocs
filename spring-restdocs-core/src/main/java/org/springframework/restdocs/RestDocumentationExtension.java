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

package org.springframework.restdocs;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * A JUnit Jupiter {@link Extension} used to automatically manage the
 * {@link RestDocumentationContext}.
 *
 * @author Andy Wilkinson
 */
public class RestDocumentationExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

	private final @Nullable String outputDirectory;

	/**
	 * Creates a new {@code RestDocumentationExtension} that will use the default output
	 * directory.
	 */
	public RestDocumentationExtension() {
		this(null);
	}

	/**
	 * Creates a new {@code RestDocumentationExtension} that will use the given
	 * {@code outputDirectory}.
	 * @param outputDirectory snippet output directory
	 * @since 2.0.4
	 */
	public RestDocumentationExtension(@Nullable String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		this.getDelegate(context).beforeTest(context.getRequiredTestClass(), context.getRequiredTestMethod().getName());
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		this.getDelegate(context).afterTest();
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
		if (isTestMethodContext(extensionContext)) {
			return RestDocumentationContextProvider.class.isAssignableFrom(parameterContext.getParameter().getType());
		}
		return false;
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext context) {
		return (RestDocumentationContextProvider) () -> getDelegate(context).beforeOperation();
	}

	private boolean isTestMethodContext(ExtensionContext context) {
		return context.getTestClass().isPresent() && context.getTestMethod().isPresent();
	}

	private ManualRestDocumentation getDelegate(ExtensionContext context) {
		Namespace namespace = Namespace.create(getClass(), context.getUniqueId());
		return context.getStore(namespace)
			.getOrComputeIfAbsent(ManualRestDocumentation.class, this::createManualRestDocumentation,
					ManualRestDocumentation.class);
	}

	private ManualRestDocumentation createManualRestDocumentation(Class<ManualRestDocumentation> key) {
		if (this.outputDirectory != null) {
			return new ManualRestDocumentation(this.outputDirectory);
		}
		else {
			return new ManualRestDocumentation();
		}
	}

}
