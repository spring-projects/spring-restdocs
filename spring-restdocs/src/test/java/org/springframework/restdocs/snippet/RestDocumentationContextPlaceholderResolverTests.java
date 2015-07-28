package org.springframework.restdocs.snippet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.Test;
import org.springframework.restdocs.config.RestDocumentationContext;
import org.springframework.restdocs.config.RestDocumentationContextPlaceholderResolver;
import org.springframework.test.context.TestContext;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

/**
 * Tests for {@link RestDocumentationContextPlaceholderResolver}
 * 
 * @author Andy Wilkinson
 *
 */
public class RestDocumentationContextPlaceholderResolverTests {

	private final TestContext testContext = mock(TestContext.class);

	private final RestDocumentationContext context = new RestDocumentationContext(
			this.testContext);

	private final PlaceholderResolver resolver = new RestDocumentationContextPlaceholderResolver(
			this.context);

	@Test
	public void dashSeparatedMethodName() throws Exception {
		when(this.testContext.getTestMethod()).thenReturn(
				getClass().getMethod("dashSeparatedMethodName"));
		assertThat(this.resolver.resolvePlaceholder("method-name"),
				equalTo("dash-separated-method-name"));
	}

	@Test
	public void underscoreSeparatedMethodName() throws Exception {
		when(this.testContext.getTestMethod()).thenReturn(
				getClass().getMethod("underscoreSeparatedMethodName"));
		assertThat(this.resolver.resolvePlaceholder("method_name"),
				equalTo("underscore_separated_method_name"));
	}

	@Test
	public void camelCaseMethodName() throws Exception {
		Method method = getClass().getMethod("camelCaseMethodName");
		when(this.testContext.getTestMethod()).thenReturn(method);
		assertThat(this.resolver.resolvePlaceholder("methodName"),
				equalTo("camelCaseMethodName"));
	}

	@Test
	public void stepCount() throws Exception {
		assertThat(this.resolver.resolvePlaceholder("step"), equalTo("0"));
	}

}
