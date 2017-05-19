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

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestExtensionContext;

/**
 * A JUnit Jupiter {@link Extension} used to automatically manage the
 * {@link RestDocumentationContext}.
 *
 * @author Andy Wilkinson
 */
public class RestDocumentationExtension implements Extension, BeforeEachCallback,
		AfterEachCallback, RestDocumentationContextProvider, ParameterResolver {

	private final ManualRestDocumentation delegate = new ManualRestDocumentation();

	@Override
	public void beforeEach(TestExtensionContext context) throws Exception {
		Class<?> testClass = context.getTestClass().orElseThrow(
				() -> new IllegalStateException("No test class was available"));
		Method testMethod = context.getTestMethod().orElseThrow(
				() -> new IllegalStateException("No test method was available"));
		this.delegate.beforeTest(testClass, testMethod.getName());
	}

	@Override
	public void afterEach(TestExtensionContext context) throws Exception {
		this.delegate.afterTest();
	}

	@Override
	public RestDocumentationContext beforeOperation() {
		return this.delegate.beforeOperation();
	}

	@Override
	public boolean supports(ParameterContext parameterContext,
			ExtensionContext extensionContext) throws ParameterResolutionException {
		return RestDocumentationContextProvider.class
				.isAssignableFrom(parameterContext.getParameter().getType());
	}

	@Override
	public Object resolve(ParameterContext parameterContext,
			ExtensionContext extensionContext) throws ParameterResolutionException {
		return this;
	}

}
