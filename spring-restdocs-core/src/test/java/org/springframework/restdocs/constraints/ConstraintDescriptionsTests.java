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

import java.math.BigDecimal;
import java.util.Date;

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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ConstraintDescriptions}.
 *
 * @author Andy Wilkinson
 */
public class ConstraintDescriptionsTests {

	private final ConstraintDescriptions constraintDescriptions = new ConstraintDescriptions(
			Constrained.class);

	@Test
	public void assertFalse() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("assertFalse"),
				contains("Must be false"));
	}

	@Test
	public void assertTrue() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("assertTrue"),
				contains("Must be true"));
	}

	@Test
	public void decimalMax() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("decimalMax"),
				contains("Must be at most 9.875"));
	}

	@Test
	public void decimalMin() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("decimalMin"),
				contains("Must be at least 1.5"));
	}

	@Test
	public void digits() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("digits"),
				contains("Must have at most 2 integral digits and 5 fractional digits"));
	}

	@Test
	public void future() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("future"),
				contains("Must be in the future"));
	}

	@Test
	public void max() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("max"),
				contains("Must be at most 10"));
	}

	@Test
	public void min() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("min"),
				contains("Must be at least 5"));
	}

	@Test
	public void notNull() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("notNull"),
				contains("Must not be null"));
	}

	@Test
	public void nul() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("nul"),
				contains("Must be null"));
	}

	@Test
	public void past() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("past"),
				contains("Must be in the past"));
	}

	@Test
	public void pattern() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("pattern"),
				contains("Must match the regular expression '[A-Z][a-z]+'"));
	}

	@Test
	public void size() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("size"),
				contains("Size must be between 0 and 10 inclusive"));
	}

	@Test
	public void sizeList() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("sizeList"),
				contains("Size must be between 1 and 4 inclusive",
						"Size must be between 8 and 10 inclusive"));
	}

	@Test
	public void unconstrained() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("unconstrained"),
				hasSize(0));
	}

	@Test
	public void nonExistentProperty() {
		assertThat(this.constraintDescriptions.descriptionsForProperty("doesNotExist"),
				hasSize(0));
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

		@Digits(fraction = 5, integer = 2)
		private BigDecimal digits;

		@Future
		private Date future;

		@NotNull
		private String notNull;

		@Max(10)
		private int max;

		@Min(5)
		private int min;

		@Null
		private String nul;

		@Past
		private Date past;

		@Pattern(regexp = "[A-Z][a-z]+")
		private String pattern;

		@Size(min = 0, max = 10)
		private String size;

		@Size.List({ @Size(min = 1, max = 4), @Size(min = 8, max = 10) })
		private String sizeList;

		@SuppressWarnings("unused")
		private String unconstrained;
	}

}
