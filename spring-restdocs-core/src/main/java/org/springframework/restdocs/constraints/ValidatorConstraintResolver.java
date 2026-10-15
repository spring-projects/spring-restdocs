/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.constraints;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.metadata.BeanDescriptor;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.metadata.PropertyDescriptor;

/**
 * A {@link ConstraintResolver} that uses a Bean Validation {@link Validator} to resolve
 * constraints. The name of the constraint is the fully-qualified class name of the
 * constraint annotation. For example, a {@link NotNull} constraint will be named
 * {@code jakarta.validation.constraints.NotNull}.
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
	 * @param validator the validator
	 */
	public ValidatorConstraintResolver(Validator validator) {
		this.validator = validator;
	}

	@Override
	public List<Constraint> resolveForProperty(String property, Class<?> clazz) {
		List<Constraint> constraints = new ArrayList<>();
		BeanDescriptor beanDescriptor = this.validator.getConstraintsForClass(clazz);
		PropertyDescriptor propertyDescriptor = beanDescriptor.getConstraintsForProperty(property);
		if (propertyDescriptor != null) {
			for (ConstraintDescriptor<?> constraintDescriptor : propertyDescriptor.getConstraintDescriptors()) {
				constraints.add(new Constraint(constraintDescriptor.getAnnotation().annotationType().getName(),
						constraintDescriptor.getAttributes()));
			}
		}
		return constraints;
	}

}
