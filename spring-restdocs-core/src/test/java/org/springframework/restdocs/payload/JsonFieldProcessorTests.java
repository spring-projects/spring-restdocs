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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import org.springframework.restdocs.payload.JsonFieldProcessor.ExtractedField;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JsonFieldProcessor}.
 *
 * @author Andy Wilkinson
 */
class JsonFieldProcessorTests {

	private final JsonFieldProcessor fieldProcessor = new JsonFieldProcessor();

	@Test
	void extractTopLevelMapEntry() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("a", "alpha");
		assertThat(this.fieldProcessor.extract("a", payload).getValue()).isEqualTo("alpha");
	}

	@Test
	void extractNestedMapEntry() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo");
		assertThat(this.fieldProcessor.extract("a.b", payload).getValue()).isEqualTo("bravo");
	}

	@Test
	void extractTopLevelArray() {
		List<Map<String, Object>> payload = new ArrayList<>();
		Map<String, Object> bravo = new HashMap<>();
		bravo.put("b", "bravo");
		payload.add(bravo);
		payload.add(bravo);
		assertThat(this.fieldProcessor.extract("[]", payload).getValue()).isEqualTo(payload);
	}

	@Test
	void extractArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> bravo = new HashMap<>();
		bravo.put("b", "bravo");
		List<Map<String, Object>> alpha = Arrays.asList(bravo, bravo);
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a", payload).getValue()).isEqualTo(alpha);
	}

	@Test
	void extractArrayContents() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> bravo = new HashMap<>();
		bravo.put("b", "bravo");
		List<Map<String, Object>> alpha = Arrays.asList(bravo, bravo);
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[]", payload).getValue()).isEqualTo(alpha);
	}

	@Test
	void extractFromItemsInArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put("b", "bravo");
		List<Map<String, Object>> alpha = Arrays.asList(entry, entry);
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[].b", payload).getValue()).isEqualTo(Arrays.asList("bravo", "bravo"));
	}

	@Test
	void extractOccasionallyAbsentFieldFromItemsInArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put("b", "bravo");
		List<Map<String, Object>> alpha = Arrays.asList(entry, new HashMap<String, Object>());
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[].b", payload).getValue())
			.isEqualTo(Arrays.asList("bravo", ExtractedField.ABSENT));
	}

	@Test
	void extractOccasionallyNullFieldFromItemsInArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> nonNullField = new HashMap<>();
		nonNullField.put("b", "bravo");
		Map<String, Object> nullField = new HashMap<>();
		nullField.put("b", null);
		List<Map<String, Object>> alpha = Arrays.asList(nonNullField, nullField);
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[].b", payload).getValue()).isEqualTo(Arrays.asList("bravo", null));
	}

	@Test
	void extractNestedArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, String> entry1 = createEntry("id:1");
		Map<String, String> entry2 = createEntry("id:2");
		Map<String, String> entry3 = createEntry("id:3");
		List<List<Map<String, String>>> alpha = Arrays.asList(Arrays.asList(entry1, entry2), Arrays.asList(entry3));
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[][]", payload).getValue())
			.isEqualTo(Arrays.asList(Arrays.asList(entry1, entry2), Arrays.asList(entry3)));
	}

	@Test
	void extractFromItemsInNestedArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, String> entry1 = createEntry("id:1");
		Map<String, String> entry2 = createEntry("id:2");
		Map<String, String> entry3 = createEntry("id:3");
		List<List<Map<String, String>>> alpha = Arrays.asList(Arrays.asList(entry1, entry2), Arrays.asList(entry3));
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[][].id", payload).getValue()).isEqualTo(Arrays.asList("1", "2", "3"));
	}

	@Test
	void extractArraysFromItemsInNestedArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> entry1 = createEntry("ids", Arrays.asList(1, 2));
		Map<String, Object> entry2 = createEntry("ids", Arrays.asList(3));
		Map<String, Object> entry3 = createEntry("ids", Arrays.asList(4));
		List<List<Map<String, Object>>> alpha = Arrays.asList(Arrays.asList(entry1, entry2), Arrays.asList(entry3));
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[][].ids", payload).getValue())
			.isEqualTo(Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3), Arrays.asList(4)));
	}

	@Test
	void nonExistentTopLevelField() {
		assertThat(this.fieldProcessor.extract("a", Collections.emptyMap()).getValue())
			.isEqualTo(ExtractedField.ABSENT);
	}

	@Test
	void nonExistentNestedField() {
		HashMap<String, Object> payload = new HashMap<>();
		payload.put("a", new HashMap<>());
		assertThat(this.fieldProcessor.extract("a.b", payload).getValue()).isEqualTo(ExtractedField.ABSENT);
	}

	@Test
	void nonExistentNestedFieldWhenParentIsNotAMap() {
		HashMap<String, Object> payload = new HashMap<>();
		payload.put("a", 5);
		assertThat(this.fieldProcessor.extract("a.b", payload).getValue()).isEqualTo(ExtractedField.ABSENT);
	}

	@Test
	void nonExistentFieldWhenParentIsAnArray() {
		HashMap<String, Object> payload = new HashMap<>();
		HashMap<String, Object> alpha = new HashMap<>();
		alpha.put("b", Arrays.asList(new HashMap<String, Object>()));
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a.b.c", payload).getValue()).isEqualTo(ExtractedField.ABSENT);
	}

	@Test
	void nonExistentArrayField() {
		HashMap<String, Object> payload = new HashMap<>();
		assertThat(this.fieldProcessor.extract("a[]", payload).getValue()).isEqualTo(ExtractedField.ABSENT);
	}

	@Test
	void nonExistentArrayFieldAsTypeDoesNotMatch() {
		HashMap<String, Object> payload = new HashMap<>();
		payload.put("a", 5);
		assertThat(this.fieldProcessor.extract("a[]", payload).getValue()).isEqualTo(ExtractedField.ABSENT);
	}

	@Test
	void nonExistentFieldBeneathAnArray() {
		HashMap<String, Object> payload = new HashMap<>();
		HashMap<String, Object> alpha = new HashMap<>();
		alpha.put("b", Arrays.asList(new HashMap<String, Object>()));
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a.b[].id", payload).getValue())
			.isEqualTo(Arrays.asList(ExtractedField.ABSENT));
	}

	@Test
	void removeTopLevelMapEntry() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("a", "alpha");
		this.fieldProcessor.remove("a", payload);
		assertThat(payload.size()).isEqualTo(0);
	}

	@Test
	void mapWithEntriesIsNotRemovedWhenNotAlsoRemovingDescendants() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo");
		this.fieldProcessor.remove("a", payload);
		assertThat(payload.size()).isEqualTo(1);
	}

	@Test
	void removeSubsectionRemovesMapWithEntries() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo");
		this.fieldProcessor.removeSubsection("a", payload);
		assertThat(payload.size()).isEqualTo(0);
	}

	@Test
	void removeNestedMapEntry() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo");
		this.fieldProcessor.remove("a.b", payload);
		assertThat(payload.size()).isEqualTo(0);
	}

	@SuppressWarnings("unchecked")
	@Test
	void removeItemsInArray() {
		Map<String, Object> payload = new ObjectMapper().readValue("{\"a\": [{\"b\":\"bravo\"},{\"b\":\"bravo\"}]}",
				Map.class);
		this.fieldProcessor.remove("a[].b", payload);
		assertThat(payload.size()).isEqualTo(0);
	}

	@SuppressWarnings("unchecked")
	@Test
	void removeItemsInNestedArray() {
		Map<String, Object> payload = new ObjectMapper().readValue("{\"a\": [[{\"id\":1},{\"id\":2}], [{\"id\":3}]]}",
				Map.class);
		this.fieldProcessor.remove("a[][].id", payload);
		assertThat(payload.size()).isEqualTo(0);
	}

	@SuppressWarnings("unchecked")
	@Test
	void removeDoesNotRemoveArrayWithMapEntries() {
		Map<String, Object> payload = new ObjectMapper().readValue("{\"a\": [{\"b\":\"bravo\"},{\"b\":\"bravo\"}]}",
				Map.class);
		this.fieldProcessor.remove("a[]", payload);
		assertThat(payload.size()).isEqualTo(1);
	}

	@SuppressWarnings("unchecked")
	@Test
	void removeDoesNotRemoveArrayWithListEntries() {
		Map<String, Object> payload = new ObjectMapper().readValue("{\"a\": [[2],[3]]}", Map.class);
		this.fieldProcessor.remove("a[]", payload);
		assertThat(payload.size()).isEqualTo(1);
	}

	@SuppressWarnings("unchecked")
	@Test
	void removeRemovesArrayWithOnlyScalarEntries() {
		Map<String, Object> payload = new ObjectMapper().readValue("{\"a\": [\"bravo\", \"charlie\"]}", Map.class);
		this.fieldProcessor.remove("a", payload);
		assertThat(payload.size()).isEqualTo(0);
	}

	@SuppressWarnings("unchecked")
	@Test
	void removeSubsectionRemovesArrayWithMapEntries() {
		Map<String, Object> payload = new ObjectMapper().readValue("{\"a\": [{\"b\":\"bravo\"},{\"b\":\"bravo\"}]}",
				Map.class);
		this.fieldProcessor.removeSubsection("a[]", payload);
		assertThat(payload.size()).isEqualTo(0);
	}

	@SuppressWarnings("unchecked")
	@Test
	void removeSubsectionRemovesArrayWithListEntries() {
		Map<String, Object> payload = new ObjectMapper().readValue("{\"a\": [[2],[3]]}", Map.class);
		this.fieldProcessor.removeSubsection("a[]", payload);
		assertThat(payload.size()).isEqualTo(0);
	}

	@Test
	void extractNestedEntryWithDotInKeys() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a.key", alpha);
		alpha.put("b.key", "bravo");
		assertThat(this.fieldProcessor.extract("['a.key']['b.key']", payload).getValue()).isEqualTo("bravo");
	}

	@SuppressWarnings("unchecked")
	@Test
	void extractNestedEntriesUsingTopLevelWildcard() {
		Map<String, Object> payload = new LinkedHashMap<>();
		Map<String, Object> alpha = new LinkedHashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo1");
		Map<String, Object> charlie = new LinkedHashMap<>();
		charlie.put("b", "bravo2");
		payload.put("c", charlie);
		assertThat((List<String>) this.fieldProcessor.extract("*.b", payload).getValue()).containsExactly("bravo1",
				"bravo2");
	}

	@SuppressWarnings("unchecked")
	@Test
	void extractNestedEntriesUsingMidLevelWildcard() {
		Map<String, Object> payload = new LinkedHashMap<>();
		Map<String, Object> alpha = new LinkedHashMap<>();
		payload.put("a", alpha);
		Map<String, Object> bravo = new LinkedHashMap<>();
		bravo.put("b", "bravo");
		alpha.put("one", bravo);
		alpha.put("two", bravo);
		assertThat((List<String>) this.fieldProcessor.extract("a.*.b", payload).getValue()).containsExactly("bravo",
				"bravo");
	}

	@SuppressWarnings("unchecked")
	@Test
	void extractUsingLeafWildcardMatchingSingleItem() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo1");
		Map<String, Object> charlie = new HashMap<>();
		charlie.put("b", "bravo2");
		payload.put("c", charlie);
		assertThat((List<String>) this.fieldProcessor.extract("a.*", payload).getValue()).containsExactly("bravo1");
	}

	@SuppressWarnings("unchecked")
	@Test
	void extractUsingLeafWildcardMatchingMultipleItems() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo1");
		alpha.put("c", "charlie");
		assertThat((List<String>) this.fieldProcessor.extract("a.*", payload).getValue()).containsExactly("bravo1",
				"charlie");
	}

	@Test
	void removeUsingLeafWildcard() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo1");
		alpha.put("c", "charlie");
		this.fieldProcessor.remove("a.*", payload);
		assertThat(payload.size()).isEqualTo(0);
	}

	@Test
	void removeUsingTopLevelWildcard() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo1");
		alpha.put("c", "charlie");
		this.fieldProcessor.remove("*.b", payload);
		assertThat(alpha).doesNotContainKey("b");
	}

	@Test
	void removeUsingMidLevelWildcard() {
		Map<String, Object> payload = new LinkedHashMap<>();
		Map<String, Object> alpha = new LinkedHashMap<>();
		payload.put("a", alpha);
		payload.put("c", "charlie");
		Map<String, Object> bravo1 = new LinkedHashMap<>();
		bravo1.put("b", "bravo");
		alpha.put("one", bravo1);
		Map<String, Object> bravo2 = new LinkedHashMap<>();
		bravo2.put("b", "bravo");
		alpha.put("two", bravo2);
		this.fieldProcessor.remove("a.*.b", payload);
		assertThat(payload.size()).isEqualTo(1);
		assertThat(payload).containsEntry("c", "charlie");
	}

	@Test
	void hasFieldIsTrueForNonNullFieldInMap() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("a", "alpha");
		assertThat(this.fieldProcessor.hasField("a", payload)).isTrue();
	}

	@Test
	void hasFieldIsTrueForNullFieldInMap() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("a", null);
		assertThat(this.fieldProcessor.hasField("a", payload)).isTrue();
	}

	@Test
	void hasFieldIsFalseForAbsentFieldInMap() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("a", null);
		assertThat(this.fieldProcessor.hasField("b", payload)).isFalse();
	}

	@Test
	void hasFieldIsTrueForNeverNullFieldBeneathArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> nested = new HashMap<>();
		nested.put("b", "bravo");
		payload.put("a", Arrays.asList(nested, nested, nested));
		assertThat(this.fieldProcessor.hasField("a.[].b", payload)).isTrue();
	}

	@Test
	void hasFieldIsTrueForAlwaysNullFieldBeneathArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> nested = new HashMap<>();
		nested.put("b", null);
		payload.put("a", Arrays.asList(nested, nested, nested));
		assertThat(this.fieldProcessor.hasField("a.[].b", payload)).isTrue();
	}

	@Test
	void hasFieldIsFalseForAlwaysAbsentFieldBeneathArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> nested = new HashMap<>();
		nested.put("b", "bravo");
		payload.put("a", Arrays.asList(nested, nested, nested));
		assertThat(this.fieldProcessor.hasField("a.[].c", payload)).isFalse();
	}

	@Test
	void hasFieldIsFalseForOccasionallyAbsentFieldBeneathArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> nested = new HashMap<>();
		nested.put("b", "bravo");
		payload.put("a", Arrays.asList(nested, new HashMap<>(), nested));
		assertThat(this.fieldProcessor.hasField("a.[].b", payload)).isFalse();
	}

	@Test
	void hasFieldIsFalseForOccasionallyNullFieldBeneathArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> fieldPresent = new HashMap<>();
		fieldPresent.put("b", "bravo");
		Map<String, Object> fieldNull = new HashMap<>();
		fieldNull.put("b", null);
		payload.put("a", Arrays.asList(fieldPresent, fieldPresent, fieldNull));
		assertThat(this.fieldProcessor.hasField("a.[].b", payload)).isFalse();
	}

	private Map<String, String> createEntry(String... pairs) {
		Map<String, String> entry = new HashMap<>();
		for (String pair : pairs) {
			String[] components = pair.split(":");
			entry.put(components[0], components[1]);
		}
		return entry;
	}

	private Map<String, Object> createEntry(String key, Object value) {
		Map<String, Object> entry = new HashMap<>();
		entry.put(key, value);
		return entry;
	}

}
