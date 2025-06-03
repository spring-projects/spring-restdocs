/*
 * Copyright 2014-2025 the original author or authors.
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

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link JsonFieldTypesDiscoverer}.
 *
 * @author Andy Wilkinson
 */
class JsonFieldTypesDiscovererTests {

	private final JsonFieldTypesDiscoverer fieldTypeDiscoverer = new JsonFieldTypesDiscoverer();

	@Test
	void arrayField() throws IOException {
		assertThat(discoverFieldTypes("[]")).containsExactly(JsonFieldType.ARRAY);
	}

	@Test
	void topLevelArray() throws IOException {
		assertThat(discoverFieldTypes("[]", "[{\"a\":\"alpha\"}]")).containsExactly(JsonFieldType.ARRAY);
	}

	@Test
	void nestedArray() throws IOException {
		assertThat(discoverFieldTypes("a[]", "{\"a\": [{\"b\":\"bravo\"}]}")).containsExactly(JsonFieldType.ARRAY);
	}

	@Test
	void arrayNestedBeneathAnArray() throws IOException {
		assertThat(discoverFieldTypes("a[].b[]", "{\"a\": [{\"b\": [ 1, 2 ]}]}")).containsExactly(JsonFieldType.ARRAY);
	}

	@Test
	void specificFieldOfObjectInArrayNestedBeneathAnArray() throws IOException {
		assertThat(discoverFieldTypes("a[].b[].c", "{\"a\": [{\"b\": [ {\"c\": 5}, {\"c\": 5}]}]}"))
			.containsExactly(JsonFieldType.NUMBER);
	}

	@Test
	void booleanField() throws IOException {
		assertThat(discoverFieldTypes("true")).containsExactly(JsonFieldType.BOOLEAN);
	}

	@Test
	void objectField() throws IOException {
		assertThat(discoverFieldTypes("{}")).containsExactly(JsonFieldType.OBJECT);
	}

	@Test
	void nullField() throws IOException {
		assertThat(discoverFieldTypes("null")).containsExactly(JsonFieldType.NULL);
	}

	@Test
	void numberField() throws IOException {
		assertThat(discoverFieldTypes("1.2345")).containsExactly(JsonFieldType.NUMBER);
	}

	@Test
	void stringField() throws IOException {
		assertThat(discoverFieldTypes("\"Foo\"")).containsExactly(JsonFieldType.STRING);
	}

	@Test
	void nestedField() throws IOException {
		assertThat(discoverFieldTypes("a.b.c", "{\"a\":{\"b\":{\"c\":{}}}}")).containsExactly(JsonFieldType.OBJECT);
	}

	@Test
	void multipleFieldsWithSameType() throws IOException {
		assertThat(discoverFieldTypes("a[].id", "{\"a\":[{\"id\":1},{\"id\":2}]}"))
			.containsExactly(JsonFieldType.NUMBER);
	}

	@Test
	void multipleFieldsWithDifferentTypes() throws IOException {
		assertThat(discoverFieldTypes("a[].id", "{\"a\":[{\"id\":1},{\"id\":true}]}"))
			.containsExactlyInAnyOrder(JsonFieldType.NUMBER, JsonFieldType.BOOLEAN);
	}

	@Test
	void multipleFieldsWithDifferentTypesAndSometimesAbsent() throws IOException {
		assertThat(discoverFieldTypes("a[].id", "{\"a\":[{\"id\":1},{\"id\":true}, {}]}"))
			.containsExactlyInAnyOrder(JsonFieldType.NUMBER, JsonFieldType.BOOLEAN, JsonFieldType.NULL);
	}

	@Test
	void multipleFieldsWhenSometimesAbsent() throws IOException {
		assertThat(discoverFieldTypes("a[].id", "{\"a\":[{\"id\":1},{\"id\":2}, {}]}"))
			.containsExactlyInAnyOrder(JsonFieldType.NUMBER, JsonFieldType.NULL);
	}

	@Test
	void multipleFieldsWhenSometimesNull() throws IOException {
		assertThat(discoverFieldTypes("a[].id", "{\"a\":[{\"id\":1},{\"id\":2}, {\"id\":null}]}"))
			.containsExactlyInAnyOrder(JsonFieldType.NUMBER, JsonFieldType.NULL);
	}

	@Test
	void multipleFieldsWithDifferentTypesAndSometimesNull() throws IOException {
		assertThat(discoverFieldTypes("a[].id", "{\"a\":[{\"id\":1},{\"id\":true}, {\"id\":null}]}"))
			.containsExactlyInAnyOrder(JsonFieldType.NUMBER, JsonFieldType.BOOLEAN, JsonFieldType.NULL);
	}

	@Test
	void multipleFieldsWhenEitherNullOrAbsent() throws IOException {
		assertThat(discoverFieldTypes("a[].id", "{\"a\":[{},{\"id\":null}]}"))
			.containsExactlyInAnyOrder(JsonFieldType.NULL);
	}

	@Test
	void multipleFieldsThatAreAllNull() throws IOException {
		assertThat(discoverFieldTypes("a[].id", "{\"a\":[{\"id\":null},{\"id\":null}]}"))
			.containsExactlyInAnyOrder(JsonFieldType.NULL);
	}

	@Test
	void nonExistentSingleFieldProducesFieldDoesNotExistException() {
		assertThatExceptionOfType(FieldDoesNotExistException.class)
			.isThrownBy(() -> discoverFieldTypes("a.b", "{\"a\":{}}"))
			.withMessage("The payload does not contain a field with the path 'a.b'");
	}

	@Test
	void nonExistentMultipleFieldsProducesFieldDoesNotExistException() {
		assertThatExceptionOfType(FieldDoesNotExistException.class)
			.isThrownBy(() -> discoverFieldTypes("a[].b", "{\"a\":[{\"c\":1},{\"c\":2}]}"))
			.withMessage("The payload does not contain a field with the path 'a[].b'");
	}

	@Test
	void leafWildcardWithCommonType() throws IOException {
		assertThat(discoverFieldTypes("a.*", "{\"a\": {\"b\": 5, \"c\": 6}}"))
			.containsExactlyInAnyOrder(JsonFieldType.NUMBER);
	}

	@Test
	void leafWildcardWithVaryingType() throws IOException {
		assertThat(discoverFieldTypes("a.*", "{\"a\": {\"b\": 5, \"c\": \"six\"}}"))
			.containsExactlyInAnyOrder(JsonFieldType.NUMBER, JsonFieldType.STRING);
	}

	@Test
	void intermediateWildcardWithCommonType() throws IOException {
		assertThat(discoverFieldTypes("a.*.d", "{\"a\": {\"b\": {\"d\": 4}, \"c\": {\"d\": 5}}}}"))
			.containsExactlyInAnyOrder(JsonFieldType.NUMBER);
	}

	@Test
	void intermediateWildcardWithVaryingType() throws IOException {
		assertThat(discoverFieldTypes("a.*.d", "{\"a\": {\"b\": {\"d\": 4}, \"c\": {\"d\": \"four\"}}}}"))
			.containsExactlyInAnyOrder(JsonFieldType.NUMBER, JsonFieldType.STRING);
	}

	private JsonFieldTypes discoverFieldTypes(String value) throws IOException {
		return discoverFieldTypes("field", "{\"field\":" + value + "}");
	}

	private JsonFieldTypes discoverFieldTypes(String path, String json) throws IOException {
		return this.fieldTypeDiscoverer.discoverFieldTypes(path, new ObjectMapper().readValue(json, Object.class));
	}

}
