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

import org.springframework.restdocs.payload.JsonFieldPath.PathType;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link JsonFieldPath}.
 *
 * @author Andy Wilkinson
 * @author Jeremy Rickard
 */
public class JsonFieldPathTests {

	@Test
	public void pathTypeOfSingleFieldIsSingle() {
		JsonFieldPath path = JsonFieldPath.compile("a");
		assertThat(path.getType(), is(equalTo(PathType.SINGLE)));
	}

	@Test
	public void pathTypeOfSingleNestedFieldIsSingle() {
		JsonFieldPath path = JsonFieldPath.compile("a.b");
		assertThat(path.getType(), is(equalTo(PathType.SINGLE)));
	}

	@Test
	public void pathTypeOfTopLevelArrayIsSingle() {
		JsonFieldPath path = JsonFieldPath.compile("[]");
		assertThat(path.getType(), is(equalTo(PathType.SINGLE)));
	}

	@Test
	public void pathTypeOfFieldBeneathTopLevelArrayIsMulti() {
		JsonFieldPath path = JsonFieldPath.compile("[]a");
		assertThat(path.getType(), is(equalTo(PathType.MULTI)));
	}

	@Test
	public void pathTypeOfSingleNestedArrayIsSingle() {
		JsonFieldPath path = JsonFieldPath.compile("a[]");
		assertThat(path.getType(), is(equalTo(PathType.SINGLE)));
	}

	@Test
	public void pathTypeOfArrayBeneathNestedFieldsIsSingle() {
		JsonFieldPath path = JsonFieldPath.compile("a.b[]");
		assertThat(path.getType(), is(equalTo(PathType.SINGLE)));
	}

	@Test
	public void pathTypeOfArrayOfArraysIsMulti() {
		JsonFieldPath path = JsonFieldPath.compile("a[][]");
		assertThat(path.getType(), is(equalTo(PathType.MULTI)));
	}

	@Test
	public void pathTypeOfFieldBeneathAnArrayIsMulti() {
		JsonFieldPath path = JsonFieldPath.compile("a[].b");
		assertThat(path.getType(), is(equalTo(PathType.MULTI)));
	}

	@Test
	public void pathTypeOfFieldBeneathTopLevelWildcardIsMulti() {
		JsonFieldPath path = JsonFieldPath.compile("*.a");
		assertThat(path.getType(), is(equalTo(PathType.MULTI)));
	}

	@Test
	public void pathTypeOfFieldBeneathNestedWildcardIsMulti() {
		JsonFieldPath path = JsonFieldPath.compile("a.*.b");
		assertThat(path.getType(), is(equalTo(PathType.MULTI)));
	}

	@Test
	public void pathTypeOfLeafWidlcardIsMulti() {
		JsonFieldPath path = JsonFieldPath.compile("a.*");
		assertThat(path.getType(), is(equalTo(PathType.MULTI)));
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
