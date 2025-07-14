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
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.util.AnnotationUtils;

import org.springframework.core.io.FileSystemResource;
import org.springframework.restdocs.templates.TemplateEngine;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.restdocs.templates.TemplateResourceResolver;
import org.springframework.restdocs.templates.mustache.MustacheTemplateEngine;
import org.springframework.restdocs.testfixtures.jupiter.RenderedSnippetTest.Format;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * {@link TestTemplateInvocationContextProvider} for
 * {@link RenderedSnippetTest @RenderedSnippetTest} and
 * {@link SnippetTemplate @SnippetTemplate}.
 *
 * @author Andy Wilkinson
 */
class RenderedSnippetTestExtension implements TestTemplateInvocationContextProvider {

	@Override
	public boolean supportsTestTemplate(ExtensionContext context) {
		return true;
	}

	@Override
	public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
		return AnnotationUtils.findAnnotation(context.getRequiredTestMethod(), RenderedSnippetTest.class)
			.map((renderedSnippetTest) -> Stream.of(renderedSnippetTest.format())
				.map(Format::templateFormat)
				.map(SnippetTestInvocationContext::new)
				.map(TestTemplateInvocationContext.class::cast))
			.orElseThrow();
	}

	static class SnippetTestInvocationContext implements TestTemplateInvocationContext {

		private final TemplateFormat templateFormat;

		SnippetTestInvocationContext(TemplateFormat templateFormat) {
			this.templateFormat = templateFormat;
		}

		@Override
		public List<Extension> getAdditionalExtensions() {
			return List.of(new RenderedSnippetTestParameterResolver(this.templateFormat));
		}

		@Override
		public String getDisplayName(int invocationIndex) {
			return this.templateFormat.getId();
		}

	}

	static class RenderedSnippetTestParameterResolver implements ParameterResolver {

		private final TemplateFormat templateFormat;

		RenderedSnippetTestParameterResolver(TemplateFormat templateFormat) {
			this.templateFormat = templateFormat;
		}

		@Override
		public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
				throws ParameterResolutionException {
			Class<?> parameterType = parameterContext.getParameter().getType();
			return AssertableSnippets.class.equals(parameterType) || OperationBuilder.class.equals(parameterType)
					|| TemplateFormat.class.equals(parameterType);
		}

		@Override
		public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
			Class<?> parameterType = parameterContext.getParameter().getType();
			if (AssertableSnippets.class.equals(parameterType)) {
				return getStore(extensionContext).getOrComputeIfAbsent(AssertableSnippets.class,
						(key) -> new AssertableSnippets(determineOutputDirectory(extensionContext),
								determineOperationName(extensionContext), this.templateFormat));
			}
			if (TemplateFormat.class.equals(parameterType)) {
				return this.templateFormat;
			}
			return getStore(extensionContext).getOrComputeIfAbsent(OperationBuilder.class, (key) -> {
				OperationBuilder operationBuilder = new OperationBuilder(determineOutputDirectory(extensionContext),
						determineOperationName(extensionContext), this.templateFormat);
				AnnotationUtils.findAnnotation(extensionContext.getRequiredTestMethod(), SnippetTemplate.class)
					.ifPresent((snippetTemplate) -> {
						TemplateResourceResolver resolver = mock(TemplateResourceResolver.class);
						given(resolver.resolveTemplateResource(snippetTemplate.snippet()))
							.willReturn(snippetResource(snippetTemplate.template(), this.templateFormat));
						operationBuilder.attribute(TemplateEngine.class.getName(),
								new MustacheTemplateEngine(resolver));
					});

				return operationBuilder;
			});
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

		private FileSystemResource snippetResource(String name, TemplateFormat templateFormat) {
			return new FileSystemResource(
					"src/test/resources/custom-snippet-templates/" + templateFormat.getId() + "/" + name + ".snippet");
		}

	}

}
