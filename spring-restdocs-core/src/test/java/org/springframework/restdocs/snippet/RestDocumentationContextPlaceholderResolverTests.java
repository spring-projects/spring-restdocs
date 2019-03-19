/*
 * Copyright 2014-2018 the original author or authors.
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

package org.springframework.restdocs.snippet;

import org.junit.Test;

import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RestDocumentationContextPlaceholderResolver}.
 *
 * @author Andy Wilkinson
 *
 */
public class RestDocumentationContextPlaceholderResolverTests {

	@Test
	public void kebabCaseMethodName() throws Exception {
		assertThat(createResolver("dashSeparatedMethodName")
				.resolvePlaceholder("method-name"))
						.isEqualTo("dash-separated-method-name");
	}

	@Test
	public void snakeCaseMethodName() throws Exception {
		assertThat(createResolver("underscoreSeparatedMethodName")
				.resolvePlaceholder("method_name"))
						.isEqualTo("underscore_separated_method_name");
	}

	@Test
	public void camelCaseMethodName() throws Exception {
		assertThat(createResolver("camelCaseMethodName").resolvePlaceholder("methodName"))
				.isEqualTo("camelCaseMethodName");
	}

	@Test
	public void kebabCaseClassName() throws Exception {
		assertThat(createResolver().resolvePlaceholder("class-name"))
				.isEqualTo("rest-documentation-context-placeholder-resolver-tests");
	}

	@Test
	public void snakeCaseClassName() throws Exception {
		assertThat(createResolver().resolvePlaceholder("class_name"))
				.isEqualTo("rest_documentation_context_placeholder_resolver_tests");
	}

	@Test
	public void camelCaseClassName() throws Exception {
		assertThat(createResolver().resolvePlaceholder("ClassName"))
				.isEqualTo("RestDocumentationContextPlaceholderResolverTests");
	}

	@Test
	public void stepCount() throws Exception {
		assertThat(createResolver("stepCount").resolvePlaceholder("step")).isEqualTo("1");
	}

	private PlaceholderResolver createResolver() {
		return createResolver(null);
	}

	private PlaceholderResolver createResolver(String methodName) {
		return new RestDocumentationContextPlaceholderResolver(createContext(methodName));
	}

	private RestDocumentationContext createContext(String methodName) {
		ManualRestDocumentation manualRestDocumentation = new ManualRestDocumentation(
				"build");
		manualRestDocumentation.beforeTest(getClass(), methodName);
		RestDocumentationContext context = manualRestDocumentation.beforeOperation();
		return context;
	}

}
