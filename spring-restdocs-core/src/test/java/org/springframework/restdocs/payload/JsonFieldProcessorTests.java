/*
 * Copyright 2014-2016 the original author or authors.
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
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
		assertThat(this.fieldProcessor.extract(JsonFieldPath.compile("a"), payload),
				equalTo((Object) "alpha"));
	}

	@Test
	public void extractNestedMapEntry() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo");
		assertThat(this.fieldProcessor.extract(JsonFieldPath.compile("a.b"), payload),
				equalTo((Object) "bravo"));
	}

	@Test
	public void extractArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> bravo = new HashMap<>();
		bravo.put("b", "bravo");
		List<Map<String, Object>> alpha = Arrays.asList(bravo, bravo);
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract(JsonFieldPath.compile("a"), payload),
				equalTo((Object) alpha));
	}

	@Test
	public void extractArrayContents() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> bravo = new HashMap<>();
		bravo.put("b", "bravo");
		List<Map<String, Object>> alpha = Arrays.asList(bravo, bravo);
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract(JsonFieldPath.compile("a[]"), payload),
				equalTo((Object) alpha));
	}

	@Test
	public void extractFromItemsInArray() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put("b", "bravo");
		List<Map<String, Object>> alpha = Arrays.asList(entry, entry);
		payload.put("a", alpha);
		assertThat(this.fieldProcessor.extract(JsonFieldPath.compile("a[].b"), payload),
				equalTo((Object) Arrays.asList("bravo", "bravo")));
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
		assertThat(this.fieldProcessor.extract(JsonFieldPath.compile("a[][]"), payload),
				equalTo((Object) Arrays.asList(entry1, entry2, entry3)));
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
		assertThat(
				this.fieldProcessor.extract(JsonFieldPath.compile("a[][].id"), payload),
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
		assertThat(
				this.fieldProcessor.extract(JsonFieldPath.compile("a[][].ids"), payload),
				equalTo((Object) Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3),
						Arrays.asList(4))));
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentTopLevelField() {
		this.fieldProcessor.extract(JsonFieldPath.compile("a"),
				new HashMap<String, Object>());
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentNestedField() {
		HashMap<String, Object> payload = new HashMap<>();
		payload.put("a", new HashMap<String, Object>());
		this.fieldProcessor.extract(JsonFieldPath.compile("a.b"), payload);
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentNestedFieldWhenParentIsNotAMap() {
		HashMap<String, Object> payload = new HashMap<>();
		payload.put("a", 5);
		this.fieldProcessor.extract(JsonFieldPath.compile("a.b"), payload);
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentFieldWhenParentIsAnArray() {
		HashMap<String, Object> payload = new HashMap<>();
		HashMap<String, Object> alpha = new HashMap<>();
		alpha.put("b", Arrays.asList(new HashMap<String, Object>()));
		payload.put("a", alpha);
		this.fieldProcessor.extract(JsonFieldPath.compile("a.b.c"), payload);
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentArrayField() {
		HashMap<String, Object> payload = new HashMap<>();
		this.fieldProcessor.extract(JsonFieldPath.compile("a[]"), payload);
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentArrayFieldAsTypeDoesNotMatch() {
		HashMap<String, Object> payload = new HashMap<>();
		payload.put("a", 5);
		this.fieldProcessor.extract(JsonFieldPath.compile("a[]"), payload);
	}

	@Test(expected = FieldDoesNotExistException.class)
	public void nonExistentFieldBeneathAnArray() {
		HashMap<String, Object> payload = new HashMap<>();
		HashMap<String, Object> alpha = new HashMap<>();
		alpha.put("b", Arrays.asList(new HashMap<String, Object>()));
		payload.put("a", alpha);
		this.fieldProcessor.extract(JsonFieldPath.compile("a.b[].id"), payload);
	}

	@Test
	public void removeTopLevelMapEntry() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("a", "alpha");
		this.fieldProcessor.remove(JsonFieldPath.compile("a"), payload);
		assertThat(payload.size(), equalTo(0));
	}

	@Test
	public void removeNestedMapEntry() {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a", alpha);
		alpha.put("b", "bravo");
		this.fieldProcessor.remove(JsonFieldPath.compile("a.b"), payload);
		assertThat(payload.size(), equalTo(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void removeItemsInArray() throws IOException {
		Map<String, Object> payload = new ObjectMapper()
				.readValue("{\"a\": [{\"b\":\"bravo\"},{\"b\":\"bravo\"}]}", Map.class);
		this.fieldProcessor.remove(JsonFieldPath.compile("a[].b"), payload);
		assertThat(payload.size(), equalTo(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void removeItemsInNestedArray() throws IOException {
		Map<String, Object> payload = new ObjectMapper()
				.readValue("{\"a\": [[{\"id\":1},{\"id\":2}], [{\"id\":3}]]}", Map.class);
		this.fieldProcessor.remove(JsonFieldPath.compile("a[][].id"), payload);
		assertThat(payload.size(), equalTo(0));
	}

	@Test
	public void extractNestedEntryWithDotInKeys() throws IOException {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> alpha = new HashMap<>();
		payload.put("a.key", alpha);
		alpha.put("b.key", "bravo");
		assertThat(this.fieldProcessor
				.extract(JsonFieldPath.compile("['a.key']['b.key']"), payload),
				equalTo((Object) "bravo"));
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
