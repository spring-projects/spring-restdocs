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

import java.util.ArrayList;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

/**
 * A {@link ConstraintResolver} that uses a Bean Validation {@link Validator} to resolve
 * constraints. The name of the constraint is the fully-qualified class name of the
 * constraint annotation. For example, a {@link NotNull} constraint will be named
 * {@code javax.validation.constraints.NotNull}.
 *
 * @author Andy Wilkinson
 *
 */
public class ValidatorConstraintResolver implements ConstraintResolver {

	private final Validator validator;

	/**
	 * Creates a new {@code ValidatorConstraintResolver} that will use a {@link Validator}
	 * in its default configuration to resolve constraints.
	 *
	 * @see Validation#buildDefaultValidatorFactory()
	 * @see ValidatorFactory#getValidator()
	 */
	public ValidatorConstraintResolver() {
		this(Validation.buildDefaultValidatorFactory().getValidator());
	}

	/**
	 * Creates a new {@code ValidatorConstraintResolver} that will use the given
	 * {@code Validator} to resolve constraints.
	 *
	 * @param validator the validator
	 */
	public ValidatorConstraintResolver(Validator validator) {
		this.validator = validator;
	}

	@Override
	public List<Constraint> resolveForProperty(String property, Class<?> clazz) {
		List<Constraint> constraints = new ArrayList<>();
		BeanDescriptor beanDescriptor = this.validator.getConstraintsForClass(clazz);
		PropertyDescriptor propertyDescriptor = beanDescriptor
				.getConstraintsForProperty(property);
		if (propertyDescriptor != null) {
			for (ConstraintDescriptor<?> constraintDescriptor : propertyDescriptor
					.getConstraintDescriptors()) {
				constraints.add(new Constraint(
						constraintDescriptor.getAnnotation().annotationType().getName(),
						constraintDescriptor.getAttributes()));
			}
		}
		return constraints;
	}
}
