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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link JsonFieldProcessor}.
 *
 * @author Andy Wilkinson
 */
public class JsonFieldProcessorTests {

	private final JsonFieldProcessor fieldProcessor = new JsonFieldProcessor();

	@Test
	public void extractTopLevelMapEntry() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("a", "alpha");
		assertThat(this.fieldProcessor.extract("a", payload).getValue(),
				equalTo((Object) "alpha"));
	}

	@Test
	public void extractNestedMapEntry() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo");
		assertThat(this.fieldProcessor.extract("a.b", payload).getValue(),
				equalTo((Object) "bravo"));
	}

	@Test
	public void extractTopLevelArray() {
		List<Map<String, Object>> payload = new ArrayList<>();
		Map<String, Object> bravo = new HashMap<>();
		bravo.put("b", "bravo");
		payload.add(bravo);
		payload.add(bravo);
		assertThat(this.fieldProcessor.extract("[]", payload).getValue(),
				equalTo((Object) payload));
	}

	@Test
	public void extractArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> bravo = new HashMap<>();
		bravo.put("b", "bravo");
		List<Map<String, Object>> alpha = Arrays.asList(bravo, bravo);
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a", payload).getValue(),
				equalTo((Object) alpha));
	}

	@Test
	public void extractArrayContents() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> bravo = new HashMap<>();
		bravo.put("b", "bravo");
		List<Map<String, Object>> alpha = Arrays.asList(bravo, bravo);
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[]", payload).getValue(),
				equalTo((Object) alpha));
	}

	@Test
	public void extractFromItemsInArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put("b", "bravo");
		List<Map<String, Object>> alpha = Arrays.asList(entry, entry);
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[].b", payload).getValue(),
				equalTo((Object) Arrays.asList("bravo", "bravo")));
	}

	@Test
	public void extractOccasionallyAbsentFieldFromItemsInArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put("b", "bravo");
		List<Map<String, Object>> alpha = Arrays.asList(entry,
				new HashMap<String, Object>());
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[].b", payload).getValue(),
				equalTo((Object) Arrays.asList("bravo")));
	}

	@Test
	public void extractOccasionallyNullFieldFromItemsInArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> nonNullField = new HashMap<>();
		nonNullField.put("b", "bravo");
		Map<String, Object> nullField = new HashMap<>();
		nullField.put("b", null);
		List<Map<String, Object>> alpha = Arrays.asList(nonNullField, nullField);
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[].b", payload).getValue(),
				equalTo((Object) Arrays.asList("bravo", null)));
	}

	@Test
	public void extractNestedArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, String> entry1 = createEntry("id:1");
		Map<String, String> entry2 = createEntry("id:2");
		Map<String, String> entry3 = createEntry("id:3");
		List<List<Map<String, String>>> alpha = Arrays
				.asList(Arrays.asList(entry1, entry2), Arrays.asList(entry3));
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[][]", payload).getValue(),
				equalTo((Object) Arrays.asList(Arrays.asList(entry1, entry2),
						Arrays.asList(entry3))));
	}

	@Test
	public void extractFromItemsInNestedArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, String> entry1 = createEntry("id:1");
		Map<String, String> entry2 = createEntry("id:2");
		Map<String, String> entry3 = createEntry("id:3");
		List<List<Map<String, String>>> alpha = Arrays
				.asList(Arrays.asList(entry1, entry2), Arrays.asList(entry3));
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[][].id", payload).getValue(),
				equalTo((Object) Arrays.asList("1", "2", "3")));
	}

	@Test
	public void extractArraysFromItemsInNestedArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> entry1 = createEntry("ids", Arrays.asList(1, 2));
		Map<String, Object> entry2 = createEntry("ids", Arrays.asList(3));
		Map<String, Object> entry3 = createEntry("ids", Arrays.asList(4));
		List<List<Map<String, Object>>> alpha = Arrays
				.asList(Arrays.asList(entry1, entry2), Arrays.asList(entry3));
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract("a[][].ids", payload).getValue(),
				equalTo((Object) Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3),
						Arrays.asList(4))));
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentTopLevelField() {
		this.fieldProcessor.extract("a", Collections.emptyMap());
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentNestedField() {
		HashMap<String, Object> payload = new HashMap<>();
		payload.put("a", new HashMap<String, Object>());
		this.fieldProcessor.extract("a.b", payload);
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentNestedFieldWhenParentIsNotAMap() {
		HashMap<String, Object> payload = new HashMap<>();
		payload.put("a", 5);
		this.fieldProcessor.extract("a.b", payload);
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentFieldWhenParentIsAnArray() {
		HashMap<String, Object> payload = new HashMap<>();
		HashMap<String, Object> alpha = new HashMap<>();
		alpha.put("b", Arrays.asList(new HashMap<String, Object>()));
		payload.put("a", alpha);
		this.fieldProcessor.extract("a.b.c", payload);
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentArrayField() {
		HashMap<String, Object> payload = new HashMap<>();
		this.fieldProcessor.extract("a[]", payload);
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentArrayFieldAsTypeDoesNotMatch() {
		HashMap<String, Object> payload = new HashMap<>();
		payload.put("a", 5);
		this.fieldProcessor.extract("a[]", payload);
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentFieldBeneathAnArray() {
		HashMap<String, Object> payload = new HashMap<>();
		HashMap<String, Object> alpha = new HashMap<>();
		alpha.put("b", Arrays.asList(new HashMap<String, Object>()));
		payload.put("a", alpha);
		this.fieldProcessor.extract("a.b[].id", payload);
	}

	@Test
	public void removeTopLevelMapEntry() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("a", "alpha");
		this.fieldProcessor.remove("a", payload);
		assertThat(payload.size(), equalTo(0));
	}

	@Test
	public void mapWithEntriesIsNotRemovedWhenNotAlsoRemovingDescendants() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo");
		this.fieldProcessor.remove("a", payload);
		assertThat(payload.size(), equalTo(1));
	}

	@Test
	public void removeSubsectionRemovesMapWithEntries() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo");
		this.fieldProcessor.removeSubsection("a", payload);
		assertThat(payload.size(), equalTo(0));
	}

	@Test
	public void removeNestedMapEntry() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo");
		this.fieldProcessor.remove("a.b", payload);
		assertThat(payload.size(), equalTo(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void removeItemsInArray() throws IOException {
		Map<String, Object> payload = new ObjectMapper()
				.readValue("{\"a\": [{\"b\":\"bravo\"},{\"b\":\"bravo\"}]}", Map.class);
		this.fieldProcessor.remove("a[].b", payload);
		assertThat(payload.size(), equalTo(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void removeItemsInNestedArray() throws IOException {
		Map<String, Object> payload = new ObjectMapper()
				.readValue("{\"a\": [[{\"id\":1},{\"id\":2}], [{\"id\":3}]]}", Map.class);
		this.fieldProcessor.remove("a[][].id", payload);
		assertThat(payload.size(), equalTo(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void removeDoesNotRemoveArrayWithMapEntries() throws IOException {
		Map<String, Object> payload = new ObjectMapper()
				.readValue("{\"a\": [{\"b\":\"bravo\"},{\"b\":\"bravo\"}]}", Map.class);
		this.fieldProcessor.remove("a[]", payload);
		assertThat(payload.size(), equalTo(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void removeDoesNotRemoveArrayWithListEntries() throws IOException {
		Map<String, Object> payload = new ObjectMapper().readValue("{\"a\": [[2],[3]]}",
				Map.class);
		this.fieldProcessor.remove("a[]", payload);
		assertThat(payload.size(), equalTo(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void removeRemovesArrayWithOnlyScalarEntries() throws IOException {
		Map<String, Object> payload = new ObjectMapper()
				.readValue("{\"a\": [\"bravo\", \"charlie\"]}", Map.class);
		this.fieldProcessor.remove("a", payload);
		assertThat(payload.size(), equalTo(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void removeSubsectionRemovesArrayWithMapEntries() throws IOException {
		Map<String, Object> payload = new ObjectMapper()
				.readValue("{\"a\": [{\"b\":\"bravo\"},{\"b\":\"bravo\"}]}", Map.class);
		this.fieldProcessor.removeSubsection("a[]", payload);
		assertThat(payload.size(), equalTo(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void removeSubsectionRemovesArrayWithListEntries() throws IOException {
		Map<String, Object> payload = new ObjectMapper().readValue("{\"a\": [[2],[3]]}",
				Map.class);
		this.fieldProcessor.removeSubsection("a[]", payload);
		assertThat(payload.size(), equalTo(0));
	}

	@Test
	public void extractNestedEntryWithDotInKeys() throws IOException {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a.key", alpha);
		alpha.put("b.key", "bravo");
		assertThat(this.fieldProcessor.extract("['a.key']['b.key']", payload).getValue(),
				equalTo((Object) "bravo"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void extractNestedEntriesUsingTopLevelWildcard() throws IOException {
		Map<String, Object> payload = new LinkedHashMap<>();
		Map<String, Object> alpha = new LinkedHashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo1");
		Map<String, Object> charlie = new LinkedHashMap<>();
		charlie.put("b", "bravo2");
		payload.put("c", charlie);
		assertThat((List<String>) this.fieldProcessor.extract("*.b", payload).getValue(),
				contains("bravo1", "bravo2"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void extractNestedEntriesUsingMidLevelWildcard() throws IOException {
		Map<String, Object> payload = new LinkedHashMap<>();
		Map<String, Object> alpha = new LinkedHashMap<>();
		payload.put("a", alpha);
		Map<String, Object> bravo = new LinkedHashMap<>();
		bravo.put("b", "bravo");
		alpha.put("one", bravo);
		alpha.put("two", bravo);
		assertThat(
				(List<String>) this.fieldProcessor.extract("a.*.b", payload).getValue(),
				contains("bravo", "bravo"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void extractUsingLeafWildcardMatchingSingleItem() throws IOException {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo1");
		Map<String, Object> charlie = new HashMap<>();
		charlie.put("b", "bravo2");
		payload.put("c", charlie);
		assertThat((List<String>) this.fieldProcessor.extract("a.*", payload).getValue(),
				contains("bravo1"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void extractUsingLeafWildcardMatchingMultipleItems() throws IOException {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo1");
		alpha.put("c", "charlie");
		assertThat((List<String>) this.fieldProcessor.extract("a.*", payload).getValue(),
				contains("bravo1", "charlie"));
	}

	@Test
	public void removeUsingLeafWildcard() throws IOException {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo1");
		alpha.put("c", "charlie");
		this.fieldProcessor.remove("a.*", payload);
		assertThat(payload.size(), equalTo(0));
	}

	@Test
	public void removeUsingTopLevelWildcard() throws IOException {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo1");
		alpha.put("c", "charlie");
		this.fieldProcessor.remove("*.b", payload);
		assertThat(alpha, not(hasKey("b")));
	}

	@Test
	public void removeUsingMidLevelWildcard() throws IOException {
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
		assertThat(payload.size(), equalTo(1));
		assertThat(payload, hasEntry("c", (Object) "charlie"));
	}

	@Test
	public void hasFieldIsTrueForNonNullFieldInMap() throws Exception {
		Map<String, Object> payload = new HashMap<>();
		payload.put("a", "alpha");
		assertThat(this.fieldProcessor.hasField("a", payload), is(true));
	}

	@Test
	public void hasFieldIsTrueForNullFieldInMap() throws Exception {
		Map<String, Object> payload = new HashMap<>();
		payload.put("a", null);
		assertThat(this.fieldProcessor.hasField("a", payload), is(true));
	}

	@Test
	public void hasFieldIsFalseForAbsentFieldInMap() throws Exception {
		Map<String, Object> payload = new HashMap<>();
		payload.put("a", null);
		assertThat(this.fieldProcessor.hasField("b", payload), is(false));
	}

	@Test
	public void hasFieldIsTrueForNeverNullFieldBeneathArray() throws Exception {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> nested = new HashMap<>();
		nested.put("b", "bravo");
		payload.put("a", Arrays.asList(nested, nested, nested));
		assertThat(this.fieldProcessor.hasField("a.[].b", payload), is(true));
	}

	@Test
	public void hasFieldIsTrueForAlwaysNullFieldBeneathArray() throws Exception {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> nested = new HashMap<>();
		nested.put("b", null);
		payload.put("a", Arrays.asList(nested, nested, nested));
		assertThat(this.fieldProcessor.hasField("a.[].b", payload), is(true));
	}

	@Test
	public void hasFieldIsFalseForAlwaysAbsentFieldBeneathArray() throws Exception {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> nested = new HashMap<>();
		nested.put("b", "bravo");
		payload.put("a", Arrays.asList(nested, nested, nested));
		assertThat(this.fieldProcessor.hasField("a.[].c", payload), is(false));
	}

	@Test
	public void hasFieldIsFalseForOccasionallyAbsentFieldBeneathArray() throws Exception {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> nested = new HashMap<>();
		nested.put("b", "bravo");
		payload.put("a", Arrays.asList(nested, new HashMap<>(), nested));
		assertThat(this.fieldProcessor.hasField("a.[].b", payload), is(false));
	}

	@Test
	public void hasFieldIsFalseForOccasionallyNullFieldBeneathArray() throws Exception {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> fieldPresent = new HashMap<>();
		fieldPresent.put("b", "bravo");
		Map<String, Object> fieldNull = new HashMap<>();
		fieldNull.put("b", null);
		payload.put("a", Arrays.asList(fieldPresent, fieldPresent, fieldNull));
		assertThat(this.fieldProcessor.hasField("a.[].b", payload), is(false));
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
