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

package org.springframework.restdocs.payload;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for {@link FieldPath}
 * 
 * @author Andy Wilkinson
 */
public class FieldPathTests {

	@Test
	public void singleFieldIsPrecise() {
		assertTrue(FieldPath.compile("a").isPrecise());
	}

	@Test
	public void singleNestedFieldIsPrecise() {
		assertTrue(FieldPath.compile("a.b").isPrecise());
	}

	@Test
	public void topLevelArrayIsNotPrecise() {
		assertFalse(FieldPath.compile("[]").isPrecise());
	}

	@Test
	public void fieldBeneathTopLevelArrayIsNotPrecise() {
		assertFalse(FieldPath.compile("[]a").isPrecise());
	}

	@Test
	public void arrayIsNotPrecise() {
		assertFalse(FieldPath.compile("a[]").isPrecise());
	}

	@Test
	public void nestedArrayIsNotPrecise() {
		assertFalse(FieldPath.compile("a.b[]").isPrecise());
	}

	@Test
	public void arrayOfArraysIsNotPrecise() {
		assertFalse(FieldPath.compile("a[][]").isPrecise());
	}

	@Test
	public void fieldBeneathAnArrayIsNotPrecise() {
		assertFalse(FieldPath.compile("a[].b").isPrecise());
	}

	@Test
	public void compilationOfSingleElementPath() {
		assertThat(FieldPath.compile("a").getSegments(), contains("a"));
	}

	@Test
	public void compilationOfMultipleElementPath() {
		assertThat(FieldPath.compile("a.b.c").getSegments(), contains("a", "b", "c"));
	}

	@Test
	public void compilationOfPathWithArraysWithNoDotSeparators() {
		assertThat(FieldPath.compile("a[]b[]c").getSegments(),
				contains("a", "[]", "b", "[]", "c"));
	}

	@Test
	public void compilationOfPathWithArraysWithPreAndPostDotSeparators() {
		assertThat(FieldPath.compile("a.[].b.[].c").getSegments(),
				contains("a", "[]", "b", "[]", "c"));
	}

	@Test
	public void compilationOfPathWithArraysWithPreDotSeparators() {
		assertThat(FieldPath.compile("a.[]b.[]c").getSegments(),
				contains("a", "[]", "b", "[]", "c"));
	}

	@Test
	public void compilationOfPathWithArraysWithPostDotSeparators() {
		assertThat(FieldPath.compile("a[].b[].c").getSegments(),
				contains("a", "[]", "b", "[]", "c"));
	}

	@Test
	public void compilationOfPathStartingWithAnArray() {
		assertThat(FieldPath.compile("[]a.b.c").getSegments(),
				contains("[]", "a", "b", "c"));
	}

}
