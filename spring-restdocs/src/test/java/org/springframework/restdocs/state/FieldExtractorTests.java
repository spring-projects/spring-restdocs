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

package org.springframework.restdocs.state;

import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.state.Path.path;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.FileCopyUtils;

/**
 * Tests for {@link FieldExtractor}.
 *
 * @author Andreas Evers
 */
public class FieldExtractorTests {

	private final FieldExtractor fieldExtractor = new FieldExtractor();

	@Test
	public void singleField() throws IOException {
		Map<Path, Field> fields = this.fieldExtractor
				.extractFields(createResponse("single-field"));
		assertFields(Arrays.asList(new Field(path("alpha"), "alpha-value")), fields);
	}

	@Test
	public void multipleFields() throws IOException {
		Map<Path, Field> fields = this.fieldExtractor
				.extractFields(createResponse("multiple-fields"));
		assertFields(Arrays.asList(new Field(path("alpha"), "alpha-value"), new Field(
				path("bravo"), 123), new Field(path("charlie"), createMap()), new Field(
				path("delta"), createList()), new Field(path("echo"),
				createListWithMaps())), fields);

	}

	private Map<String, Object> createMap() {
		Map<String, Object> hashMap = new HashMap<>();
		hashMap.put("one", 456);
		hashMap.put("two", "two-value");
		return hashMap;
	}

	private List<String> createList() {
		List<String> arrayList = new ArrayList<>();
		arrayList.add("delta-value-1");
		arrayList.add("delta-value-2");
		return arrayList;
	}

	private List<Map<String, Object>> createListWithMaps() {
		List<Map<String, Object>> arrayList = new ArrayList<>();
		Map<String, Object> hashMap1 = new HashMap<>();
		hashMap1.put("one", 789);
		hashMap1.put("two", "two-value");
		arrayList.add(hashMap1);
		Map<String, Object> hashMap2 = new HashMap<>();
		hashMap2.put("one", 987);
		hashMap2.put("two", "value-two");
		arrayList.add(hashMap2);
		return arrayList;
	}

	@Test
	public void multipleFieldsAndLinks() throws IOException {
		Map<Path, Field> fields = this.fieldExtractor
				.extractFields(createResponse("multiple-fields-and-links"));
		assertFields(Arrays.asList(new Field(path("beta"), "beta-value"), new Field(
				path("charlie"), "charlie-value")), fields);
	}

	@Test
	public void multipleFieldsAndEmbedded() throws IOException {
		Map<Path, Field> fields = this.fieldExtractor
				.extractFields(createResponse("multiple-fields-and-embedded"));
		assertFields(Arrays.asList(new Field(path("beta"), "beta-value"), new Field(
				path("charlie"), "charlie-value")), fields);
	}

	@Test
	public void multipleFieldsAndEmbeddedAndLinks() throws IOException {
		Map<Path, Field> fields = this.fieldExtractor
				.extractFields(createResponse("multiple-fields-and-embedded-and-links"));
		assertFields(Arrays.asList(new Field(path("beta"), "beta-value"), new Field(
				path("charlie"), "charlie-value")), fields);
	}

	@Test
	public void noFields() throws IOException {
		Map<Path, Field> fields = this.fieldExtractor
				.extractFields(createResponse("no-fields"));
		assertFields(Collections.<Field> emptyList(), fields);
	}

	private void assertFields(List<Field> expectedFields, Map<Path, Field> actualFields) {
		Map<Path, Field> expectedFieldsByName = new HashMap<>();
		for (Field expectedField : expectedFields) {
			expectedFieldsByName.put(expectedField.getPath(), expectedField);
		}
		assertEquals(expectedFieldsByName, actualFields);
	}

	private MockHttpServletResponse createResponse(String contentName) throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		FileCopyUtils.copy(new FileReader(getPayloadFile(contentName)),
				response.getWriter());
		return response;
	}

	private File getPayloadFile(String name) {
		return new File("src/test/resources/field-payloads/" + name + ".json");
	}
}
