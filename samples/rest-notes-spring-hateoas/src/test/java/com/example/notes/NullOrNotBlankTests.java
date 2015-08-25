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

package com.example.notes;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Test;


public class NullOrNotBlankTests {

	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	public void nullValue() {
		Set<ConstraintViolation<Constrained>> violations = validator.validate(new Constrained(null));
		assertThat(violations.size(), is(0));
	}

	@Test
	public void zeroLengthValue() {
		Set<ConstraintViolation<Constrained>> violations = validator.validate(new Constrained(""));
		assertThat(violations.size(), is(2));
	}

	@Test
	public void blankValue() {
		Set<ConstraintViolation<Constrained>> violations = validator.validate(new Constrained("   "));
		assertThat(violations.size(), is(2));
	}

	@Test
	public void nonBlankValue() {
		Set<ConstraintViolation<Constrained>> violations = validator.validate(new Constrained("test"));
		assertThat(violations.size(), is(0));
	}

	static class Constrained {

		@NullOrNotBlank
		private final String value;

		public Constrained(String value) {
			this.value = value;
		}

	}

}
