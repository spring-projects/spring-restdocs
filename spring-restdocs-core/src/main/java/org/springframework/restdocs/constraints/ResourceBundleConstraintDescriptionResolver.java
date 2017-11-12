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
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.CodePointLength;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Currency;
import org.hibernate.validator.constraints.EAN;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.LuhnCheck;
import org.hibernate.validator.constraints.Mod10Check;
import org.hibernate.validator.constraints.Mod11Check;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.URL;

import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.springframework.util.StringUtils;

/**
 * A {@link ConstraintDescriptionResolver} that resolves constraint descriptions from a
 * {@link ResourceBundle}. The resource bundle's keys are the name of the constraint with
 * {@code .description} appended. For example, the key for the constraint named
 * {@code javax.validation.constraints.NotNull} is
 * {@code javax.validation.constraints.NotNull.description}.
 * <p>
 * Default descriptions are provided for Bean Validation 2.0's constraints:
 *
 * <ul>
 * <li>{@link AssertFalse}
 * <li>{@link AssertTrue}
 * <li>{@link DecimalMax}
 * <li>{@link DecimalMin}
 * <li>{@link Digits}
 * <li>{@link Email}
 * <li>{@link Future}
 * <li>{@link FutureOrPresent}
 * <li>{@link Max}
 * <li>{@link Min}
 * <li>{@link Negative}
 * <li>{@link NegativeOrZero}
 * <li>{@link NotBlank}
 * <li>{@link NotEmpty}
 * <li>{@link NotNull}
 * <li>{@link Null}
 * <li>{@link Past}
 * <li>{@link PastOrPresent}
 * <li>{@link Pattern}
 * <li>{@link Positive}
 * <li>{@link PositiveOrZero}
 * <li>{@link Size}
 * </ul>
 *
 * <p>
 * Default descriptions are also provided for Hibernate Validator's constraints:
 *
 * <ul>
 * <li>{@link CodePointLength}
 * <li>{@link CreditCardNumber}
 * <li>{@link Currency}
 * <li>{@link EAN}
 * <li>{@link org.hibernate.validator.constraints.Email}
 * <li>{@link Length}
 * <li>{@link LuhnCheck}
 * <li>{@link Mod10Check}
 * <li>{@link Mod11Check}
 * <li>{@link org.hibernate.validator.constraints.NotBlank}
 * <li>{@link org.hibernate.validator.constraints.NotEmpty}
 * <li>{@link Range}
 * <li>{@link SafeHtml}
 * <li>{@link URL}
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
			if (replacement == null) {
				return null;
			}
			if (replacement.getClass().isArray()) {
				return StringUtils.arrayToDelimitedString((Object[]) replacement, ", ");
			}
			return replacement.toString();
		}

	}

}
