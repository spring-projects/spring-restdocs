/*
 * Copyright 2014-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.payload;

import org.junit.jupiter.api.Test;

import org.springframework.restdocs.payload.JsonFieldPath.PathType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JsonFieldPath}.
 *
 * @author Andy Wilkinson
 * @author Jeremy Rickard
 */
class JsonFieldPathTests {

	@Test
	void pathTypeOfSingleFieldIsSingle() {
		JsonFieldPath path = JsonFieldPath.compile("a");
		assertThat(path.getType()).isEqualTo(PathType.SINGLE);
	}

	@Test
	void pathTypeOfSingleNestedFieldIsSingle() {
		JsonFieldPath path = JsonFieldPath.compile("a.b");
		assertThat(path.getType()).isEqualTo(PathType.SINGLE);
	}

	@Test
	void pathTypeOfTopLevelArrayIsSingle() {
		JsonFieldPath path = JsonFieldPath.compile("[]");
		assertThat(path.getType()).isEqualTo(PathType.SINGLE);
	}

	@Test
	void pathTypeOfFieldBeneathTopLevelArrayIsMulti() {
		JsonFieldPath path = JsonFieldPath.compile("[]a");
		assertThat(path.getType()).isEqualTo(PathType.MULTI);
	}

	@Test
	void pathTypeOfSingleNestedArrayIsSingle() {
		JsonFieldPath path = JsonFieldPath.compile("a[]");
		assertThat(path.getType()).isEqualTo(PathType.SINGLE);
	}

	@Test
	void pathTypeOfArrayBeneathNestedFieldsIsSingle() {
		JsonFieldPath path = JsonFieldPath.compile("a.b[]");
		assertThat(path.getType()).isEqualTo(PathType.SINGLE);
	}

	@Test
	void pathTypeOfArrayOfArraysIsMulti() {
		JsonFieldPath path = JsonFieldPath.compile("a[][]");
		assertThat(path.getType()).isEqualTo(PathType.MULTI);
	}

	@Test
	void pathTypeOfFieldBeneathAnArrayIsMulti() {
		JsonFieldPath path = JsonFieldPath.compile("a[].b");
		assertThat(path.getType()).isEqualTo(PathType.MULTI);
	}

	@Test
	void pathTypeOfFieldBeneathTopLevelWildcardIsMulti() {
		JsonFieldPath path = JsonFieldPath.compile("*.a");
		assertThat(path.getType()).isEqualTo(PathType.MULTI);
	}

	@Test
	void pathTypeOfFieldBeneathNestedWildcardIsMulti() {
		JsonFieldPath path = JsonFieldPath.compile("a.*.b");
		assertThat(path.getType()).isEqualTo(PathType.MULTI);
	}

	@Test
	void pathTypeOfLeafWidlcardIsMulti() {
		JsonFieldPath path = JsonFieldPath.compile("a.*");
		assertThat(path.getType()).isEqualTo(PathType.MULTI);
	}

	@Test
	void compilationOfSingleElementPath() {
		assertThat(JsonFieldPath.compile("a").getSegments()).containsExactly("a");
	}

	@Test
	void compilationOfMultipleElementPath() {
		assertThat(JsonFieldPath.compile("a.b.c").getSegments()).containsExactly("a", "b", "c");
	}

	@Test
	void compilationOfPathWithArraysWithNoDotSeparators() {
		assertThat(JsonFieldPath.compile("a[]b[]c").getSegments()).containsExactly("a", "[]", "b", "[]", "c");
	}

	@Test
	void compilationOfPathWithArraysWithPreAndPostDotSeparators() {
		assertThat(JsonFieldPath.compile("a.[].b.[].c").getSegments()).containsExactly("a", "[]", "b", "[]", "c");
	}

	@Test
	void compilationOfPathWithArraysWithPreDotSeparators() {
		assertThat(JsonFieldPath.compile("a.[]b.[]c").getSegments()).containsExactly("a", "[]", "b", "[]", "c");
	}

	@Test
	void compilationOfPathWithArraysWithPostDotSeparators() {
		assertThat(JsonFieldPath.compile("a[].b[].c").getSegments()).containsExactly("a", "[]", "b", "[]", "c");
	}

	@Test
	void compilationOfPathStartingWithAnArray() {
		assertThat(JsonFieldPath.compile("[]a.b.c").getSegments()).containsExactly("[]", "a", "b", "c");
	}

	@Test
	void compilationOfMultipleElementPathWithBrackets() {
		assertThat(JsonFieldPath.compile("['a']['b']['c']").getSegments()).containsExactly("a", "b", "c");
	}

	@Test
	void compilationOfMultipleElementPathWithAndWithoutBrackets() {
		assertThat(JsonFieldPath.compile("['a'][].b['c']").getSegments()).containsExactly("a", "[]", "b", "c");
	}

	@Test
	void compilationOfMultipleElementPathWithAndWithoutBracketsAndEmbeddedDots() {
		assertThat(JsonFieldPath.compile("['a.key'][].b['c']").getSegments()).containsExactly("a.key", "[]", "b", "c");
	}

	@Test
	void compilationOfPathWithAWildcard() {
		assertThat(JsonFieldPath.compile("a.b.*.c").getSegments()).containsExactly("a", "b", "*", "c");
	}

	@Test
	void compilationOfPathWithAWildcardInBrackets() {
		assertThat(JsonFieldPath.compile("a.b.['*'].c").getSegments()).containsExactly("a", "b", "*", "c");
	}

}
