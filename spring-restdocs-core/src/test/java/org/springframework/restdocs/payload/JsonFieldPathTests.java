/*
 * Copyright 2014-2017 the original author or authors.
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

import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link JsonFieldPath}.
 *
 * @author Andy Wilkinson
 * @author Jeremy Rickard
 * @author Minhyeok Jeong
 */
public class JsonFieldPathTests {

	@Test
	public void singleFieldIsPreciseAndNotAnArray() {
		JsonFieldPath path = JsonFieldPath.compile("a");
		assertTrue(path.isPrecise());
		assertFalse(path.isArray());
	}

	@Test
	public void singleNestedFieldIsPreciseAndNotAnArray() {
		JsonFieldPath path = JsonFieldPath.compile("a.b");
		assertTrue(path.isPrecise());
		assertFalse(path.isArray());
	}

	@Test
	public void topLevelArrayIsNotPreciseAndAnArray() {
		JsonFieldPath path = JsonFieldPath.compile("[]");
		assertFalse(path.isPrecise());
		assertTrue(path.isArray());
	}

	@Test
	public void fieldBeneathTopLevelArrayIsNotPreciseAndNotAnArray() {
		JsonFieldPath path = JsonFieldPath.compile("[]a");
		assertFalse(path.isPrecise());
		assertFalse(path.isArray());
	}

	@Test
	public void arrayIsNotPreciseAndAnArray() {
		JsonFieldPath path = JsonFieldPath.compile("a[]");
		assertFalse(path.isPrecise());
		assertTrue(path.isArray());
	}

	@Test
	public void nestedArrayIsNotPreciseAndAnArray() {
		JsonFieldPath path = JsonFieldPath.compile("a.b[]");
		assertFalse(path.isPrecise());
		assertTrue(path.isArray());
	}

	@Test
	public void nestedArrayStartedWithArrayIsNotPreciseAndAnArray() {
		JsonFieldPath path = JsonFieldPath.compile("[].a[]");
		assertFalse(path.isPrecise());
		assertTrue(path.isArray());
	}

	@Test
	public void arrayOfArraysIsNotPreciseAndIsAnArray() {
		JsonFieldPath path = JsonFieldPath.compile("a[][]");
		assertFalse(path.isPrecise());
		assertTrue(path.isArray());
	}

	@Test
	public void fieldBeneathAnArrayIsNotPreciseAndIsNotAnArray() {
		JsonFieldPath path = JsonFieldPath.compile("a[].b");
		assertFalse(path.isPrecise());
		assertFalse(path.isArray());
	}

	@Test
	public void fieldBeneathTopLevelWildcardIsNotPreciseAndNotAnArray() {
		JsonFieldPath path = JsonFieldPath.compile("*.a");
		assertFalse(path.isPrecise());
		assertFalse(path.isArray());
	}

	@Test
	public void fieldBeneathNestedWildcardIsNotPreciseAndNotAnArray() {
		JsonFieldPath path = JsonFieldPath.compile("a.*.b");
		assertFalse(path.isPrecise());
		assertFalse(path.isArray());
	}

	@Test
	public void compilationOfSingleElementPath() {
		assertThat(JsonFieldPath.compile("a").getSegments(), contains("a"));
	}

	@Test
	public void compilationOfMultipleElementPath() {
		assertThat(JsonFieldPath.compile("a.b.c").getSegments(), contains("a", "b", "c"));
	}

	@Test
	public void compilationOfPathWithArraysWithNoDotSeparators() {
		assertThat(JsonFieldPath.compile("a[]b[]c").getSegments(),
				contains("a", "[]", "b", "[]", "c"));
	}

	@Test
	public void compilationOfPathWithArraysWithPreAndPostDotSeparators() {
		assertThat(JsonFieldPath.compile("a.[].b.[].c").getSegments(),
				contains("a", "[]", "b", "[]", "c"));
	}

	@Test
	public void compilationOfPathWithArraysWithPreDotSeparators() {
		assertThat(JsonFieldPath.compile("a.[]b.[]c").getSegments(),
				contains("a", "[]", "b", "[]", "c"));
	}

	@Test
	public void compilationOfPathWithArraysWithPostDotSeparators() {
		assertThat(JsonFieldPath.compile("a[].b[].c").getSegments(),
				contains("a", "[]", "b", "[]", "c"));
	}

	@Test
	public void compilationOfPathStartingWithAnArray() {
		assertThat(JsonFieldPath.compile("[]a.b.c").getSegments(),
				contains("[]", "a", "b", "c"));
	}

	@Test
	public void compilationOfMultipleElementPathWithBrackets() {
		assertThat(JsonFieldPath.compile("['a']['b']['c']").getSegments(),
				contains("a", "b", "c"));
	}

	@Test
	public void compilationOfMultipleElementPathWithAndWithoutBrackets() {
		assertThat(JsonFieldPath.compile("['a'][].b['c']").getSegments(),
				contains("a", "[]", "b", "c"));
	}

	@Test
	public void compilationOfMultipleElementPathWithAndWithoutBracketsAndEmbeddedDots() {
		assertThat(JsonFieldPath.compile("['a.key'][].b['c']").getSegments(),
				contains("a.key", "[]", "b", "c"));
	}

	@Test
	public void compilationOfPathWithAWildcard() {
		assertThat(JsonFieldPath.compile("a.b.*.c").getSegments(),
				contains("a", "b", "*", "c"));
	}

	@Test
	public void compilationOfPathWithAWildcardInBrackets() {
		assertThat(JsonFieldPath.compile("a.b.['*'].c").getSegments(),
				contains("a", "b", "*", "c"));
	}

}
