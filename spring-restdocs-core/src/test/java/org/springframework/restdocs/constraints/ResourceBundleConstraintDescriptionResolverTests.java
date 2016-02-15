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

package org.springframework.restdocs.constraints;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.ResourceBundle;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ResourceBundleConstraintDescriptionResolver}.
 *
 * @author Andy Wilkinson
 */
public class ResourceBundleConstraintDescriptionResolverTests {

	private final ResourceBundleConstraintDescriptionResolver resolver = new ResourceBundleConstraintDescriptionResolver();

	@Test
	public void defaultMessageAssertFalse() {
		String description = this.resolver.resolveDescription(new Constraint(
				AssertFalse.class.getName(), Collections.<String, Object>emptyMap()));
		assertThat(description, is(equalTo("Must be false")));
	}

	@Test
	public void defaultMessageAssertTrue() {
		String description = this.resolver.resolveDescription(new Constraint(
				AssertTrue.class.getName(), Collections.<String, Object>emptyMap()));
		assertThat(description, is(equalTo("Must be true")));
	}

	@Test
	public void defaultMessageDecimalMax() {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put("value", "9.875");
		String description = this.resolver.resolveDescription(
				new Constraint(DecimalMax.class.getName(), configuration));
		assertThat(description, is(equalTo("Must be at most 9.875")));
	}

	@Test
	public void defaultMessageDecimalMin() {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put("value", "1.5");
		String description = this.resolver.resolveDescription(
				new Constraint(DecimalMin.class.getName(), configuration));
		assertThat(description, is(equalTo("Must be at least 1.5")));
	}

	@Test
	public void defaultMessageDigits() {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put("integer", "2");
		configuration.put("fraction", "5");
		String description = this.resolver.resolveDescription(
				new Constraint(Digits.class.getName(), configuration));
		assertThat(description, is(equalTo(
				"Must have at most 2 integral digits and 5 " + "fractional digits")));
	}

	@Test
	public void defaultMessageFuture() {
		String description = this.resolver.resolveDescription(new Constraint(
				Future.class.getName(), Collections.<String, Object>emptyMap()));
		assertThat(description, is(equalTo("Must be in the future")));
	}

	@Test
	public void defaultMessageMax() {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put("value", 10);
		String description = this.resolver
				.resolveDescription(new Constraint(Max.class.getName(), configuration));
		assertThat(description, is(equalTo("Must be at most 10")));
	}

	@Test
	public void defaultMessageMin() {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put("value", 10);
		String description = this.resolver
				.resolveDescription(new Constraint(Min.class.getName(), configuration));
		assertThat(description, is(equalTo("Must be at least 10")));
	}

	@Test
	public void defaultMessageNotNull() {
		String description = this.resolver.resolveDescription(new Constraint(
				NotNull.class.getName(), Collections.<String, Object>emptyMap()));
		assertThat(description, is(equalTo("Must not be null")));
	}

	@Test
	public void defaultMessageNull() {
		String description = this.resolver.resolveDescription(new Constraint(
				Null.class.getName(), Collections.<String, Object>emptyMap()));
		assertThat(description, is(equalTo("Must be null")));
	}

	@Test
	public void defaultMessagePast() {
		String description = this.resolver.resolveDescription(new Constraint(
				Past.class.getName(), Collections.<String, Object>emptyMap()));
		assertThat(description, is(equalTo("Must be in the past")));
	}

	@Test
	public void defaultMessagePattern() {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put("regexp", "[A-Z][a-z]+");
		String description = this.resolver.resolveDescription(
				new Constraint(Pattern.class.getName(), configuration));
		assertThat(description,
				is(equalTo("Must match the regular expression '[A-Z][a-z]+'")));
	}

	@Test
	public void defaultMessageSize() {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put("min", 2);
		configuration.put("max", 10);
		String description = this.resolver
				.resolveDescription(new Constraint(Size.class.getName(), configuration));
		assertThat(description, is(equalTo("Size must be between 2 and 10 inclusive")));
	}

	@Test
	public void customMessage() {
		Thread.currentThread().setContextClassLoader(new ClassLoader() {

			@Override
			public URL getResource(String name) {
				if (name.startsWith(
						"org/springframework/restdocs/constraints/ConstraintDescriptions")) {
					return super.getResource(
							"org/springframework/restdocs/constraints/TestConstraintDescriptions.properties");
				}
				return super.getResource(name);
			}

		});

		try {
			String description = new ResourceBundleConstraintDescriptionResolver()
					.resolveDescription(new Constraint(NotNull.class.getName(),
							Collections.<String, Object>emptyMap()));
			assertThat(description, is(equalTo("Should not be null")));

		}
		finally {
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
		}
	}

	@Test
	public void customResourceBundle() {
		ResourceBundle bundle = new ListResourceBundle() {

			@Override
			protected Object[][] getContents() {
				return new String[][] {
						{ NotNull.class.getName() + ".description", "Not null" } };
			}

		};
		String description = new ResourceBundleConstraintDescriptionResolver(bundle)
				.resolveDescription(new Constraint(NotNull.class.getName(),
						Collections.<String, Object>emptyMap()));
		assertThat(description, is(equalTo("Not null")));
	}

}
