/*
 * Copyright 2014-present the original author or authors.
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
		return getStore(extensionContext).computeIfAbsent(OperationBuilder.class,
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
