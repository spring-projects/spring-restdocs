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

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import org.springframework.restdocs.payload.JsonFieldProcessor.ExtractedField;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JsonFieldPaths}.
 *
 * @author Andy Wilkinson
 */
class JsonFieldPathsTests {

	@Test
	void noUncommonPathsForSingleItem() {
		assertThat(
				JsonFieldPaths.from(Arrays.asList(json("{\"a\": 1, \"b\": [ { \"c\": 2}, {\"c\": 3}, {\"c\": null}]}")))
					.getUncommon())
			.isEmpty();
	}

	@Test
	void noUncommonPathsForMultipleIdenticalItems() {
		Object item = json("{\"a\": 1, \"b\": [ { \"c\": 2}, {\"c\": 3} ]}");
		assertThat(JsonFieldPaths.from(Arrays.asList(item, item)).getUncommon()).isEmpty();
	}

	@Test
	void noUncommonPathsForMultipleMatchingItemsWithDifferentScalarValues() {
		assertThat(JsonFieldPaths.from(Arrays.asList(json("{\"a\": 1, \"b\": [ { \"c\": 2}, {\"c\": 3} ]}"),
				json("{\"a\": 4, \"b\": [ { \"c\": 5}, {\"c\": 6} ]}")))
			.getUncommon()).isEmpty();
	}

	@Test
	void missingEntryInMapIsIdentifiedAsUncommon() {
		assertThat(
				JsonFieldPaths.from(Arrays.asList(json("{\"a\": 1}"), json("{\"a\": 1}"), json("{\"a\": 1, \"b\": 2}")))
					.getUncommon())
			.containsExactly("b");
	}

	@Test
	void missingEntryInNestedMapIsIdentifiedAsUncommon() {
		assertThat(JsonFieldPaths.from(Arrays.asList(json("{\"a\": 1, \"b\": {\"c\": 1}}"),
				json("{\"a\": 1, \"b\": {\"c\": 1}}"), json("{\"a\": 1, \"b\": {\"c\": 1, \"d\": 2}}")))
			.getUncommon()).containsExactly("b.d");
	}

	@Test
	void missingEntriesInNestedMapAreIdentifiedAsUncommon() {
		assertThat(JsonFieldPaths.from(Arrays.asList(json("{\"a\": 1, \"b\": {\"c\": 1}}"),
				json("{\"a\": 1, \"b\": {\"c\": 1}}"), json("{\"a\": 1, \"b\": {\"d\": 2}}")))
			.getUncommon()).containsExactly("b.c", "b.d");
	}

	@Test
	void absentItemFromFieldExtractionCausesAllPresentFieldsToBeIdentifiedAsUncommon() {
		assertThat(JsonFieldPaths
			.from(Arrays.asList(ExtractedField.ABSENT, json("{\"a\": 1, \"b\": {\"c\": 1}}"),
					json("{\"a\": 1, \"b\": {\"c\": 1}}"), json("{\"a\": 1, \"b\": {\"d\": 2}}")))
			.getUncommon()).containsExactly("", "a", "b", "b.c", "b.d");
	}

	@Test
	void missingEntryBeneathArrayIsIdentifiedAsUncommon() {
		assertThat(JsonFieldPaths
			.from(Arrays.asList(json("[{\"b\": 1}]"), json("[{\"b\": 1}]"), json("[{\"b\": 1, \"c\": 2}]")))
			.getUncommon()).containsExactly("[].c");
	}

	@Test
	void missingEntryBeneathNestedArrayIsIdentifiedAsUncommon() {
		assertThat(JsonFieldPaths.from(Arrays.asList(json("{\"a\": [{\"b\": 1}]}"), json("{\"a\": [{\"b\": 1}]}"),
				json("{\"a\": [{\"b\": 1, \"c\": 2}]}")))
			.getUncommon()).containsExactly("a.[].c");
	}

	private Object json(String json) {
		return new ObjectMapper().readValue(json, Object.class);
	}

}
