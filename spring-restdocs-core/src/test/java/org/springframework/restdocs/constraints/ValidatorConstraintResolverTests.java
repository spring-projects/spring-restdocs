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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;
import org.hibernate.validator.constraints.NotBlank;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ValidatorConstraintResolver}.
 *
 * @author Andy Wilkinson
 */
public class ValidatorConstraintResolverTests {

	private final ValidatorConstraintResolver resolver = new ValidatorConstraintResolver();

	@Test
	public void singleFieldConstraint() {
		List<Constraint> constraints = this.resolver.resolveForProperty("single",
				ConstrainedFields.class);
		assertThat(constraints, hasSize(1));
		assertThat(constraints.get(0).getName(), is(NotNull.class.getName()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void multipleFieldConstraints() {
		List<Constraint> constraints = this.resolver.resolveForProperty("multiple",
				ConstrainedFields.class);
		assertThat(constraints, hasSize(2));
		assertThat(constraints, containsInAnyOrder(constraint(NotNull.class),
				constraint(Size.class).config("min", 8).config("max", 16)));
	}

	@Test
	public void noFieldConstraints() {
		List<Constraint> constraints = this.resolver.resolveForProperty("none",
				ConstrainedFields.class);
		assertThat(constraints, hasSize(0));
	}

	@Test
	public void compositeConstraint() {
		List<Constraint> constraints = this.resolver.resolveForProperty("composite",
				ConstrainedFields.class);
		assertThat(constraints, hasSize(1));
	}

	private ConstraintMatcher constraint(final Class<? extends Annotation> annotation) {
		return new ConstraintMatcher(annotation);
	}

	private static class ConstrainedFields {

		@NotNull
		private String single;

		@NotNull
		@Size(min = 8, max = 16)
		private String multiple;

		@SuppressWarnings("unused")
		private String none;

		@CompositeConstraint
		private String composite;
	}

	@ConstraintComposition(CompositionType.OR)
	@Null
	@NotBlank
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@javax.validation.Constraint(validatedBy = {})
	private @interface CompositeConstraint {

		String message() default "Must be null or not blank";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};

	}

	private static final class ConstraintMatcher extends BaseMatcher<Constraint> {

		private final Class<?> annotation;

		private final Map<String, Object> configuration = new HashMap<>();

		private ConstraintMatcher(Class<?> annotation) {
			this.annotation = annotation;
		}

		public ConstraintMatcher config(String key, Object value) {
			this.configuration.put(key, value);
			return this;
		}

		@Override
		public boolean matches(Object item) {
			if (!(item instanceof Constraint)) {
				return false;
			}
			Constraint constraint = (Constraint) item;
			if (!constraint.getName().equals(this.annotation.getName())) {
				return false;
			}
			for (Entry<String, Object> entry : this.configuration.entrySet()) {
				if (!constraint.getConfiguration().get(entry.getKey())
						.equals(entry.getValue())) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("Constraint named " + this.annotation.getName()
					+ " with configuration " + this.configuration);
		}
	}
}
