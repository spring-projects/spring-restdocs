/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.snippet;

import org.junit.jupiter.api.Test;

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
class RestDocumentationContextPlaceholderResolverTests {

	@Test
	void kebabCaseMethodName() {
		assertThat(createResolver("dashSeparatedMethodName").resolvePlaceholder("method-name"))
			.isEqualTo("dash-separated-method-name");
	}

	@Test
	void kebabCaseMethodNameWithUpperCaseOpeningSection() {
		assertThat(createResolver("URIDashSeparatedMethodName").resolvePlaceholder("method-name"))
			.isEqualTo("uri-dash-separated-method-name");
	}

	@Test
	void kebabCaseMethodNameWithUpperCaseMidSection() {
		assertThat(createResolver("dashSeparatedMethodNameWithURIInIt").resolvePlaceholder("method-name"))
			.isEqualTo("dash-separated-method-name-with-uri-in-it");
	}

	@Test
	void kebabCaseMethodNameWithUpperCaseEndSection() {
		assertThat(createResolver("dashSeparatedMethodNameWithURI").resolvePlaceholder("method-name"))
			.isEqualTo("dash-separated-method-name-with-uri");
	}

	@Test
	void snakeCaseMethodName() {
		assertThat(createResolver("underscoreSeparatedMethodName").resolvePlaceholder("method_name"))
			.isEqualTo("underscore_separated_method_name");
	}

	@Test
	void snakeCaseMethodNameWithUpperCaseOpeningSection() {
		assertThat(createResolver("URIUnderscoreSeparatedMethodName").resolvePlaceholder("method_name"))
			.isEqualTo("uri_underscore_separated_method_name");
	}

	@Test
	void snakeCaseMethodNameWithUpperCaseMidSection() {
		assertThat(createResolver("underscoreSeparatedMethodNameWithURIInIt").resolvePlaceholder("method_name"))
			.isEqualTo("underscore_separated_method_name_with_uri_in_it");
	}

	@Test
	void snakeCaseMethodNameWithUpperCaseEndSection() {
		assertThat(createResolver("underscoreSeparatedMethodNameWithURI").resolvePlaceholder("method_name"))
			.isEqualTo("underscore_separated_method_name_with_uri");
	}

	@Test
	void camelCaseMethodName() {
		assertThat(createResolver("camelCaseMethodName").resolvePlaceholder("methodName"))
			.isEqualTo("camelCaseMethodName");
	}

	@Test
	void kebabCaseClassName() {
		assertThat(createResolver().resolvePlaceholder("class-name"))
			.isEqualTo("rest-documentation-context-placeholder-resolver-tests");
	}

	@Test
	void snakeCaseClassName() {
		assertThat(createResolver().resolvePlaceholder("class_name"))
			.isEqualTo("rest_documentation_context_placeholder_resolver_tests");
	}

	@Test
	void camelCaseClassName() {
		assertThat(createResolver().resolvePlaceholder("ClassName"))
			.isEqualTo("RestDocumentationContextPlaceholderResolverTests");
	}

	@Test
	void stepCount() {
		assertThat(createResolver("stepCount").resolvePlaceholder("step")).isEqualTo("1");
	}

	private PlaceholderResolver createResolver() {
		return createResolver("example");
	}

	private PlaceholderResolver createResolver(String methodName) {
		return new RestDocumentationContextPlaceholderResolver(createContext(methodName));
	}

	private RestDocumentationContext createContext(String methodName) {
		ManualRestDocumentation manualRestDocumentation = new ManualRestDocumentation("build");
		manualRestDocumentation.beforeTest(getClass(), methodName);
		RestDocumentationContext context = manualRestDocumentation.beforeOperation();
		return context;
	}

}
