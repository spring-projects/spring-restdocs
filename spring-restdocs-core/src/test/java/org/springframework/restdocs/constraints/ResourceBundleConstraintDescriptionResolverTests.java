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
import java.math.BigDecimal;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListResourceBundle;
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

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.EAN;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.LuhnCheck;
import org.hibernate.validator.constraints.Mod10Check;
import org.hibernate.validator.constraints.Mod11Check;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.SafeHtml;
import org.junit.Test;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

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
		assertThat(constraintDescriptionForField("assertFalse"),
				is(equalTo("Must be false")));
	}

	@Test
	public void defaultMessageAssertTrue() {
		assertThat(constraintDescriptionForField("assertTrue"),
				is(equalTo("Must be true")));
	}

	@Test
	public void defaultMessageDecimalMax() {
		assertThat(constraintDescriptionForField("decimalMax"),
				is(equalTo("Must be at most 9.875")));
	}

	@Test
	public void defaultMessageDecimalMin() {
		assertThat(constraintDescriptionForField("decimalMin"),
				is(equalTo("Must be at least 1.5")));
	}

	@Test
	public void defaultMessageDigits() {
		assertThat(constraintDescriptionForField("digits"), is(
				equalTo("Must have at most 2 integral digits and 5 fractional digits")));
	}

	@Test
	public void defaultMessageFuture() {
		assertThat(constraintDescriptionForField("future"),
				is(equalTo("Must be in the future")));
	}

	@Test
	public void defaultMessageMax() {
		assertThat(constraintDescriptionForField("max"),
				is(equalTo("Must be at most 10")));
	}

	@Test
	public void defaultMessageMin() {
		assertThat(constraintDescriptionForField("min"),
				is(equalTo("Must be at least 10")));
	}

	@Test
	public void defaultMessageNotNull() {
		assertThat(constraintDescriptionForField("notNull"),
				is(equalTo("Must not be null")));
	}

	@Test
	public void defaultMessageNull() {
		assertThat(constraintDescriptionForField("nul"), is(equalTo("Must be null")));
	}

	@Test
	public void defaultMessagePast() {
		assertThat(constraintDescriptionForField("past"),
				is(equalTo("Must be in the past")));
	}

	@Test
	public void defaultMessagePattern() {
		assertThat(constraintDescriptionForField("pattern"),
				is(equalTo("Must match the regular expression '[A-Z][a-z]+'")));
	}

	@Test
	public void defaultMessageSize() {
		assertThat(constraintDescriptionForField("size"),
				is(equalTo("Size must be between 2 and 10 inclusive")));
	}

	@Test
	public void defaultMessageCreditCardNumber() {
		assertThat(constraintDescriptionForField("creditCardNumber"),
				is(equalTo("Must be a well-formed credit card number")));
	}

	@Test
	public void defaultMessageEan() {
		assertThat(constraintDescriptionForField("ean"),
				is(equalTo("Must be a well-formed EAN13 number")));
	}

	@Test
	public void defaultMessageEmail() {
		assertThat(constraintDescriptionForField("email"),
				is(equalTo("Must be a well-formed email address")));
	}

	@Test
	public void defaultMessageLength() {
		assertThat(constraintDescriptionForField("length"),
				is(equalTo("Length must be between 2 and 10 inclusive")));
	}

	@Test
	public void defaultMessageLuhnCheck() {
		assertThat(constraintDescriptionForField("luhnCheck"),
				is(equalTo("Must pass the Luhn Modulo 10 checksum algorithm")));
	}

	@Test
	public void defaultMessageMod10Check() {
		assertThat(constraintDescriptionForField("mod10Check"),
				is(equalTo("Must pass the Mod10 checksum algorithm")));
	}

	@Test
	public void defaultMessageMod11Check() {
		assertThat(constraintDescriptionForField("mod11Check"),
				is(equalTo("Must pass the Mod11 checksum algorithm")));
	}

	@Test
	public void defaultMessageNotBlank() {
		assertThat(constraintDescriptionForField("notBlank"),
				is(equalTo("Must not be blank")));
	}

	@Test
	public void defaultMessageNotEmpty() {
		assertThat(constraintDescriptionForField("notEmpty"),
				is(equalTo("Must not be empty")));
	}

	@Test
	public void defaultMessageRange() {
		assertThat(constraintDescriptionForField("range"),
				is(equalTo("Must be at least 10 and at most 100")));
	}

	@Test
	public void defaultMessageSafeHtml() {
		assertThat(constraintDescriptionForField("safeHtml"),
				is(equalTo("Must be safe HTML")));
	}

	@Test
	public void defaultMessageUrl() {
		assertThat(constraintDescriptionForField("url"),
				is(equalTo("Must be a well-formed URL")));
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

	private String constraintDescriptionForField(String name) {
		return this.resolver.resolveDescription(getConstraintFromField(name));
	}

	private Constraint getConstraintFromField(String name) {
		Annotation[] annotations = ReflectionUtils.findField(Constrained.class, name)
				.getAnnotations();
		Assert.isTrue(annotations.length == 1);
		return new Constraint(annotations[0].annotationType().getName(),
				AnnotationUtils.getAnnotationAttributes(annotations[0]));
	}

	private static class Constrained {

		@AssertFalse
		private boolean assertFalse;

		@AssertTrue
		private boolean assertTrue;

		@DecimalMax("9.875")
		private BigDecimal decimalMax;

		@DecimalMin("1.5")
		private BigDecimal decimalMin;

		@Digits(integer = 2, fraction = 5)
		private String digits;

		@Future
		private Date future;

		@Max(10)
		private int max;

		@Min(10)
		private int min;

		@NotNull
		private String notNull;

		@Null
		private String nul;

		@Past
		private Date past;

		@Pattern(regexp = "[A-Z][a-z]+")
		private String pattern;

		@Size(min = 2, max = 10)
		private List<String> size;

		@CreditCardNumber
		private String creditCardNumber;

		@EAN
		private String ean;

		@Email
		private String email;

		@Length(min = 2, max = 10)
		private String length;

		@LuhnCheck
		private String luhnCheck;

		@Mod10Check
		private String mod10Check;

		@Mod11Check
		private String mod11Check;

		@NotBlank
		private String notBlank;

		@NotEmpty
		private String notEmpty;

		@Range(min = 10, max = 100)
		private int range;

		@SafeHtml
		private String safeHtml;

		@org.hibernate.validator.constraints.URL
		private String url;
	}

}
