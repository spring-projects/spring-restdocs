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

package org.springframework.restdocs.testfixtures.jupiter;

import java.io.File;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * {@link ParameterResolver} for {@link SnippetTest @SnippetTest}.
 *
 * @author Andy Wilkinson
 */
class SnippetTestExtension implements ParameterResolver {

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		Class<?> parameterType = parameterContext.getParameter().getType();
		return OperationBuilder.class.equals(parameterType);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
		return getStore(extensionContext).getOrComputeIfAbsent(OperationBuilder.class,
				(key) -> new OperationBuilder(determineOutputDirectory(extensionContext),
						determineOperationName(extensionContext)));
	}

	private Store getStore(ExtensionContext extensionContext) {
		return extensionContext.getStore(Namespace.create(getClass()));
	}

	private File determineOutputDirectory(ExtensionContext extensionContext) {
		return new File("build/" + extensionContext.getRequiredTestClass().getSimpleName());
	}

	private String determineOperationName(ExtensionContext extensionContext) {
		String operationName = extensionContext.getRequiredTestMethod().getName();
		int index = operationName.indexOf('[');
		if (index > 0) {
			operationName = operationName.substring(0, index);
		}
		return operationName;
	}

}
