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

package org.springframework.restdocs.snippet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

/**
 * Tests for {@link RestDocumentationContextPlaceholderResolver}
 *
 * @author Andy Wilkinson
 *
 */
public class RestDocumentationContextPlaceholderResolverTests {

	@Test
	public void dashSeparatedMethodName() throws Exception {
		assertThat(
				createResolver("dashSeparatedMethodName").resolvePlaceholder(
						"method-name"), equalTo("dash-separated-method-name"));
	}

	@Test
	public void underscoreSeparatedMethodName() throws Exception {
		assertThat(
				createResolver("underscoreSeparatedMethodName").resolvePlaceholder(
						"method_name"), equalTo("underscore_separated_method_name"));
	}

	@Test
	public void camelCaseMethodName() throws Exception {
		assertThat(
				createResolver("camelCaseMethodName").resolvePlaceholder("methodName"),
				equalTo("camelCaseMethodName"));
	}

	@Test
	public void stepCount() throws Exception {
		assertThat(createResolver("stepCount").resolvePlaceholder("step"), equalTo("0"));
	}

	private PlaceholderResolver createResolver(String methodName) {
		return new RestDocumentationContextPlaceholderResolver(
				new RestDocumentationContext(null, methodName, null));
	}

}
