/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.constraints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

	private final Class<?>[] groups;

	private final Validator validator;

	/**
	 * Creates a new {@code ValidatorConstraintResolver} that will use a {@link Validator}
	 * in its default configuration to resolve constraints.
	 * @param groups the validation groups to consider when resolving constraints
	 * @see Validation#buildDefaultValidatorFactory()
	 * @see ValidatorFactory#getValidator()
	 */
	public ValidatorConstraintResolver(Class<?>... groups) {
		this(Validation.buildDefaultValidatorFactory().getValidator(), groups);
	}

	/**
	 * Creates a new {@code ValidatorConstraintResolver} that will use the given
	 * {@code Validator} to resolve constraints.
	 * @param validator the validator
	 * @param groups the validation groups to consider when resolving constraints.
	 */
	public ValidatorConstraintResolver(Validator validator, Class<?>... groups) {
		this.validator = validator;
		this.groups = groups;
	}

	@Override
	public List<Constraint> resolveForProperty(String property, Class<?> clazz) {
		List<Constraint> constraints = new ArrayList<>();
		for (ConstraintDescriptor<?> constraintDescriptor : getConstraintDescriptors(property, clazz)) {
			constraints.add(new Constraint(constraintDescriptor.getAnnotation().annotationType().getName(),
					constraintDescriptor.getAttributes()));
		}
		return constraints;
	}

	private Set<ConstraintDescriptor<?>> getConstraintDescriptors(String property, Class<?> clazz) {
		BeanDescriptor beanDescriptor = this.validator.getConstraintsForClass(clazz);
		PropertyDescriptor propertyDescriptor = beanDescriptor.getConstraintsForProperty(property);
		if (propertyDescriptor != null) {
			return propertyDescriptor.findConstraints()
				.unorderedAndMatchingGroups(this.groups)
				.getConstraintDescriptors();
		}
		return Collections.emptySet();
	}

}
