/*
 * Copyright 2014-2024 the original author or authors.
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

package org.springframework.restdocs.constraints;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import org.assertj.core.api.Condition;
import org.assertj.core.description.TextDescription;
import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ValidatorConstraintResolver}.
 *
 * @author Andy Wilkinson
 */
public class ValidatorConstraintResolverTests {

	private final ValidatorConstraintResolver resolver = new ValidatorConstraintResolver();

	@Test
	public void singleFieldConstraint() {
		List<Constraint> constraints = this.resolver.resolveForProperty("single", ConstrainedFields.class);
		assertThat(constraints).hasSize(1);
		assertThat(constraints.get(0).getName()).isEqualTo(NotNull.class.getName());
	}

	@Test
	public void singleGroupedFieldConstraint() {
		List<Constraint> constraints = this.resolver.resolveForProperty("singleGrouped", ConstrainedFields.class);
		assertThat(constraints).hasSize(1);
		assertThat(constraints.get(0)).is(constraint(NotNull.class).groups(Serializable.class));
	}

	@Test
	public void multipleFieldConstraints() {
		List<Constraint> constraints = this.resolver.resolveForProperty("multiple", ConstrainedFields.class);
		assertThat(constraints).hasSize(2);
		assertThat(constraints.get(0)).is(constraint(NotNull.class));
		assertThat(constraints.get(1)).is(constraint(Size.class).config("min", 8).config("max", 16));
	}

	@Test
	public void noFieldConstraints() {
		List<Constraint> constraints = this.resolver.resolveForProperty("none", ConstrainedFields.class);
		assertThat(constraints).hasSize(0);
	}

	@Test
	public void compositeConstraint() {
		List<Constraint> constraints = this.resolver.resolveForProperty("composite", ConstrainedFields.class);
		assertThat(constraints).hasSize(1);
	}

	private ConstraintCondition constraint(final Class<? extends Annotation> annotation) {
		return new ConstraintCondition(annotation);
	}

	private static final class ConstrainedFields {

		@NotNull
		private String single;

		@NotNull(groups = Serializable.class)
		private String singleGrouped;

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
	@jakarta.validation.Constraint(validatedBy = {})
	private @interface CompositeConstraint {

		String message() default "Must be null or not blank";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};

	}

	private static final class ConstraintCondition extends Condition<Constraint> {

		private final Class<?> annotation;

		private final Map<String, Object> configuration = new HashMap<>();

		private final Set<Class<?>> groups = new HashSet<>();

		private ConstraintCondition(Class<?> annotation) {
			this.annotation = annotation;
			as(new TextDescription("Constraint named %s with configuration %s and groups %s", this.annotation,
					this.configuration, this.groups));
		}

		private ConstraintCondition config(String key, Object value) {
			this.configuration.put(key, value);
			return this;
		}

		private ConstraintCondition groups(Class<?>... groups) {
			this.groups.addAll(List.of(groups));
			return this;
		}

		@Override
		public boolean matches(Constraint constraint) {
			if (!constraint.getName().equals(this.annotation.getName())) {
				return false;
			}
			for (Entry<String, Object> entry : this.configuration.entrySet()) {
				if (!constraint.getConfiguration().get(entry.getKey()).equals(entry.getValue())) {
					return false;
				}
			}
			for (Class<?> group : this.groups) {
				if (!constraint.getGroups().contains(group)) {
					return false;
				}
			}
			return true;
		}

	}

}
