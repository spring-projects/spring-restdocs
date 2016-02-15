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
import java.util.Collections;
import java.util.List;

/**
 * Provides access to descriptions of a class's constraints.
 *
 * @author Andy Wilkinson
 */
public class ConstraintDescriptions {

	private final Class<?> clazz;

	private final ConstraintResolver constraintResolver;

	private final ConstraintDescriptionResolver descriptionResolver;

	/**
	 * Create a new {@code ConstraintDescriptions} for the given {@code clazz}.
	 * Constraints will be resolved using a {@link ValidatorConstraintResolver} and
	 * descriptions will be resolved using a
	 * {@link ResourceBundleConstraintDescriptionResolver}.
	 *
	 * @param clazz the class
	 */
	public ConstraintDescriptions(Class<?> clazz) {
		this(clazz, new ValidatorConstraintResolver(),
				new ResourceBundleConstraintDescriptionResolver());
	}

	/**
	 * Create a new {@code ConstraintDescriptions} for the given {@code clazz}.
	 * Constraints will be resolved using the given {@code constraintResolver} and
	 * descriptions will be resolved using a
	 * {@link ResourceBundleConstraintDescriptionResolver}.
	 *
	 * @param clazz the class
	 * @param constraintResolver the constraint resolver
	 */
	public ConstraintDescriptions(Class<?> clazz, ConstraintResolver constraintResolver) {
		this(clazz, constraintResolver,
				new ResourceBundleConstraintDescriptionResolver());
	}

	/**
	 * Create a new {@code ConstraintDescriptions} for the given {@code clazz}.
	 * Constraints will be resolved using a {@link ValidatorConstraintResolver} and
	 * descriptions will be resolved using the given {@code descriptionResolver}.
	 *
	 * @param clazz the class
	 * @param descriptionResolver the description resolver
	 */
	public ConstraintDescriptions(Class<?> clazz,
			ConstraintDescriptionResolver descriptionResolver) {
		this(clazz, new ValidatorConstraintResolver(), descriptionResolver);
	}

	/**
	 * Create a new {@code ConstraintDescriptions} for the given {@code clazz}.
	 * Constraints will be resolved using the given {@code constraintResolver} and
	 * descriptions will be resolved using the given {@code descriptionResolver}.
	 *
	 * @param clazz the class
	 * @param constraintResolver the constraint resolver
	 * @param descriptionResolver the description resolver
	 */
	public ConstraintDescriptions(Class<?> clazz, ConstraintResolver constraintResolver,
			ConstraintDescriptionResolver descriptionResolver) {
		this.clazz = clazz;
		this.constraintResolver = constraintResolver;
		this.descriptionResolver = descriptionResolver;
	}

	/**
	 * Returns a list of the descriptions for the constraints on the given property.
	 *
	 * @param property the property
	 * @return the list of constraint descriptions
	 */
	public List<String> descriptionsForProperty(String property) {
		List<Constraint> constraints = this.constraintResolver
				.resolveForProperty(property, this.clazz);
		List<String> descriptions = new ArrayList<>();
		for (Constraint constraint : constraints) {
			descriptions.add(this.descriptionResolver.resolveDescription(constraint));
		}
		Collections.sort(descriptions);
		return descriptions;
	}
}
