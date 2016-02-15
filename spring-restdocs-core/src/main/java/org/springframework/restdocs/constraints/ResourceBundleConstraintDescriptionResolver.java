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

import java.util.Locale;
import java.util.MissingResourceException;
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

import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

/**
 * A {@link ConstraintDescriptionResolver} that resolves constraint descriptions from a
 * {@link ResourceBundle}. The resource bundle's keys are the name of the constraint with
 * {@code .description} appended. For example, the key for the constraint named
 * {@code javax.validation.constraints.NotNull} is
 * {@code javax.validation.constraints.NotNull.description}.
 * <p>
 * Default descriptions are provided for Bean Validation 1.1's constraints:
 *
 * <ul>
 * <li>{@link AssertFalse}
 * <li>{@link AssertTrue}
 * <li>{@link DecimalMax}
 * <li>{@link DecimalMin}
 * <li>{@link Digits}
 * <li>{@link Future}
 * <li>{@link Max}
 * <li>{@link Min}
 * <li>{@link NotNull}
 * <li>{@link Null}
 * <li>{@link Past}
 * <li>{@link Pattern}
 * <li>{@link Size}
 * </ul>
 *
 * @author Andy Wilkinson
 */
public class ResourceBundleConstraintDescriptionResolver
		implements ConstraintDescriptionResolver {

	private final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper(
			"${", "}");

	private final ResourceBundle defaultDescriptions;

	private final ResourceBundle userDescriptions;

	/**
	 * Creates a new {@code ResourceBundleConstraintDescriptionResolver} that will resolve
	 * descriptions by looking them up in a resource bundle with the base name
	 * {@code org.springframework.restdocs.constraints.ConstraintDescriptions} in the
	 * default locale loaded using the thread context class loader.
	 */
	public ResourceBundleConstraintDescriptionResolver() {
		this(getBundle("ConstraintDescriptions"));
	}

	/**
	 * Creates a new {@code ResourceBundleConstraintDescriptionResolver} that will resolve
	 * descriptions by looking them up in the given {@code resourceBundle}.
	 *
	 * @param resourceBundle the resource bundle
	 */
	public ResourceBundleConstraintDescriptionResolver(ResourceBundle resourceBundle) {
		this.defaultDescriptions = getBundle("DefaultConstraintDescriptions");
		this.userDescriptions = resourceBundle;
	}

	private static ResourceBundle getBundle(String name) {
		try {
			return ResourceBundle.getBundle(
					ResourceBundleConstraintDescriptionResolver.class.getPackage()
							.getName() + "." + name,
					Locale.getDefault(), Thread.currentThread().getContextClassLoader());
		}
		catch (MissingResourceException ex) {
			return null;
		}
	}

	@Override
	public String resolveDescription(Constraint constraint) {
		String key = constraint.getName() + ".description";
		return this.propertyPlaceholderHelper.replacePlaceholders(getDescription(key),
				new ConstraintPlaceholderResolver(constraint));
	}

	private String getDescription(String key) {
		try {
			if (this.userDescriptions != null) {
				return this.userDescriptions.getString(key);
			}
		}
		catch (MissingResourceException ex) {
			// Continue and return default description, if available
		}
		return this.defaultDescriptions.getString(key);
	}

	private static final class ConstraintPlaceholderResolver
			implements PlaceholderResolver {

		private final Constraint constraint;

		private ConstraintPlaceholderResolver(Constraint constraint) {
			this.constraint = constraint;
		}

		@Override
		public String resolvePlaceholder(String placeholderName) {
			Object replacement = this.constraint.getConfiguration().get(placeholderName);
			return replacement != null ? replacement.toString() : null;
		}

	}

}
