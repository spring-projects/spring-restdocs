/*
 * Copyright 2014-2018 the original author or authors.
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
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JsonFieldTypeResolver}.
 *
 * @author Andy Wilkinson
 * @author Mathias Düsterhöft
 */
public class JsonFieldTypeResolverTests {

	@Rule
	public ExpectedException thrownException = ExpectedException.none();

	@Test
	public void typeForFieldWithNullValueMustMatch() throws IOException {
		this.thrownException.expect(FieldTypesDoNotMatchException.class);
		determineFieldType("{\"a\": null}",
				new FieldDescriptor("a").type(JsonFieldType.STRING));
	}

	@Test
	public void typeForFieldWithNotNullAndThenNullValueMustMatch() throws IOException {
		this.thrownException.expect(FieldTypesDoNotMatchException.class);
		determineFieldType("{\"a\":[{\"id\":1},{\"id\":null}]}",
				new FieldDescriptor("a[].id").type(JsonFieldType.STRING));
	}

	@Test
	public void typeForFieldWithNullAndThenNotNullValueMustMatch() throws IOException {
		this.thrownException.expect(FieldTypesDoNotMatchException.class);
		determineFieldType("{\"a\":[{\"id\":null},{\"id\":1}]}",
				new FieldDescriptor("a.[].id").type(JsonFieldType.STRING));
	}

	@Test
	public void typeForOptionalFieldWithNumberAndThenNullValueIsNumber()
			throws IOException {
		Object fieldType = determineFieldType("{\"a\":[{\"id\":1},{\"id\":null}]}\"",
				new FieldDescriptor("a[].id").optional());
		assertThat(fieldType).isEqualTo(JsonFieldType.NUMBER);
	}

	@Test
	public void typeForOptionalFieldWithNullAndThenNumberIsNumber() throws IOException {
		Object fieldType = determineFieldType("{\"a\":[{\"id\":null},{\"id\":1}]}",
				new FieldDescriptor("a[].id").optional());
		assertThat(fieldType).isEqualTo(JsonFieldType.NUMBER);
	}

	@Test
	public void typeForFieldWithNumberAndThenNullValueIsVaries() throws IOException {
		Object fieldType = determineFieldType("{\"a\":[{\"id\":1},{\"id\":null}]}\"",
				new FieldDescriptor("a[].id"));
		assertThat(fieldType).isEqualTo(JsonFieldType.VARIES);
	}

	@Test
	public void typeForFieldWithNullAndThenNumberIsVaries() throws IOException {
		Object fieldType = determineFieldType("{\"a\":[{\"id\":null},{\"id\":1}]}",
				new FieldDescriptor("a[].id"));
		assertThat(fieldType).isEqualTo(JsonFieldType.VARIES);
	}

	@Test
	public void typeForOptionalFieldWithNullValueCanBeProvidedExplicitly()
			throws IOException {
		Object fieldType = determineFieldType("{\"a\": null}",
				new FieldDescriptor("a").type(JsonFieldType.STRING).optional());
		assertThat(fieldType).isEqualTo(JsonFieldType.STRING);
	}

	@Test
	public void arrayField() throws IOException {
		assertFieldType(JsonFieldType.ARRAY, "[]");
	}

	@Test
	public void topLevelArray() throws IOException {
		assertThat(new JsonFieldTypeResolver(
				new ObjectMapper().readValue("[{\"a\":\"alpha\"}]", List.class))
						.resolveFieldType(new FieldDescriptor("[]")))
								.isEqualTo(JsonFieldType.ARRAY);
	}

	@Test
	public void nestedArray() throws IOException {
		assertThat(resolveFieldType("{\"a\": [{\"b\":\"bravo\"}]}", "a[]"))
				.isEqualTo(JsonFieldType.ARRAY);
	}

	@Test
	public void arrayNestedBeneathAnArray() throws IOException {
		assertThat(resolveFieldType("{\"a\": [{\"b\": [ 1, 2 ]}]}", "a[].b[]"))
				.isEqualTo(JsonFieldType.ARRAY);
	}

	@Test
	public void specificFieldOfObjectInArrayNestedBeneathAnArray() throws IOException {
		assertThat(resolveFieldType("{\"a\": [{\"b\": [ {\"c\": 5}, {\"c\": 5}]}]}",
				"a[].b[].c")).isEqualTo(JsonFieldType.NUMBER);
	}

	@Test
	public void booleanField() throws IOException {
		assertFieldType(JsonFieldType.BOOLEAN, "true");
	}

	@Test
	public void objectField() throws IOException {
		assertFieldType(JsonFieldType.OBJECT, "{}");
	}

	@Test
	public void nullField() throws IOException {
		assertFieldType(JsonFieldType.NULL, "null");
	}

	@Test
	public void numberField() throws IOException {
		assertFieldType(JsonFieldType.NUMBER, "1.2345");
	}

	@Test
	public void stringField() throws IOException {
		assertFieldType(JsonFieldType.STRING, "\"Foo\"");
	}

	@Test
	public void nestedField() throws IOException {
		assertThat(fieldTypeResolver("{\"a\":{\"b\":{\"c\":{}}}}")
				.resolveFieldType(new FieldDescriptor("a.b.c")))
						.isEqualTo(JsonFieldType.OBJECT);
	}

	@Test
	public void multipleFieldsWithSameType() throws IOException {
		assertThat(fieldTypeResolver("{\"a\":[{\"id\":1},{\"id\":2}]}")
				.resolveFieldType(new FieldDescriptor("a[].id")))
						.isEqualTo(JsonFieldType.NUMBER);
	}

	@Test
	public void multipleFieldsWithDifferentTypes() throws IOException {
		assertThat(fieldTypeResolver("{\"a\":[{\"id\":1},{\"id\":true}]}")
				.resolveFieldType(new FieldDescriptor("a[].id")))
						.isEqualTo(JsonFieldType.VARIES);
	}

	@Test
	public void multipleFieldsWithDifferentTypesAndSometimesAbsent() throws IOException {
		assertThat(resolveFieldType("{\"a\":[{\"id\":1},{\"id\":true}, { }]}", "a[].id"))
				.isEqualTo(JsonFieldType.VARIES);
	}

	@Test
	public void multipleFieldsWithDifferentTypesAndSometimesAbsentWhenOptionalResolvesToVaries()
			throws IOException {
		assertThat(fieldTypeResolver("{\"a\":[{\"id\":1},{\"id\":true}, { }]}")
				.resolveFieldType(new FieldDescriptor("a[].id").optional()))
						.isEqualTo(JsonFieldType.VARIES);
	}

	@Test
	public void multipleFieldsWhenSometimesAbsent() throws IOException {
		assertThat(resolveFieldType("{\"a\":[{\"id\":1},{ }]}", "a[].id"))
				.isEqualTo(JsonFieldType.NUMBER);
	}

	@Test
	public void multipleFieldsWithDifferentTypesAndSometimesNull() throws IOException {
		assertThat(resolveFieldType("{\"a\":[{\"id\":1},{\"id\":true}, {\"id\":null}]}",
				"a[].id")).isEqualTo(JsonFieldType.VARIES);
	}

	@Test
	public void multipleFieldsWhenNotNullThenNullWhenRequiredHasVariesType()
			throws IOException {
		assertThat(resolveFieldType("{\"a\":[{\"id\":1},{\"id\":null}]}", "a[].id"))
				.isEqualTo(JsonFieldType.VARIES);
	}

	@Test
	public void multipleFieldsWhenNotNullThenNullWhenOptionalHasSpecificType()
			throws IOException {
		assertThat(fieldTypeResolver("{\"a\":[{\"id\":1},{\"id\":null}]}")
				.resolveFieldType(new FieldDescriptor("a[].id").optional()))
						.isEqualTo(JsonFieldType.NUMBER);
	}

	@Test
	public void multipleFieldsWhenNullThenNotNullWhenRequiredHasVariesType()
			throws IOException {
		assertThat(resolveFieldType("{\"a\":[{\"id\":null},{\"id\":1}]}", "a[].id"))
				.isEqualTo(JsonFieldType.VARIES);
	}

	@Test
	public void multipleFieldsWhenNullThenNotNullWhenOptionalHasSpecificType()
			throws IOException {
		assertThat(fieldTypeResolver("{\"a\":[{\"id\":null},{\"id\":1}]}")
				.resolveFieldType(new FieldDescriptor("a[].id").optional()))
						.isEqualTo(JsonFieldType.NUMBER);
	}

	@Test
	public void multipleFieldsWhenEitherNullOrAbsent() throws IOException {
		assertThat(resolveFieldType("{\"a\":[{},{\"id\":null}]}", "a[].id"))
				.isEqualTo(JsonFieldType.NULL);
	}

	@Test
	public void multipleFieldsThatAreAllNull() throws IOException {
		assertThat(resolveFieldType("{\"a\":[{\"id\":null},{\"id\":null}]}", "a[].id"))
				.isEqualTo(JsonFieldType.NULL);
	}

	@Test
	public void nonExistentSingleFieldProducesFieldDoesNotExistException()
			throws IOException {
		this.thrownException.expect(FieldDoesNotExistException.class);
		this.thrownException.expectMessage(
				"The payload does not contain a field with the path 'a.b'");
		resolveFieldType("{\"a\":{}}", "a.b");
	}

	@Test
	public void nonExistentMultipleFieldsProducesFieldDoesNotExistException()
			throws IOException {
		this.thrownException.expect(FieldDoesNotExistException.class);
		this.thrownException.expectMessage(
				"The payload does not contain a field with the path 'a[].b'");
		resolveFieldType("{\"a\":[{\"c\":1},{\"c\":2}]}", "a[].b");
	}

	@Test
	public void leafWildcardWithCommonType() throws IOException {
		assertThat(resolveFieldType("{\"a\": {\"b\": 5, \"c\": 6}}", "a.*"))
				.isEqualTo(JsonFieldType.NUMBER);
	}

	@Test
	public void leafWildcardWithVaryingType() throws IOException {
		assertThat(resolveFieldType("{\"a\": {\"b\": 5, \"c\": \"six\"}}", "a.*"))
				.isEqualTo(JsonFieldType.VARIES);
	}

	@Test
	public void intermediateWildcardWithCommonType() throws IOException {
		assertThat(resolveFieldType("{\"a\": {\"b\": {\"d\": 4}, \"c\": {\"d\": 5}}}}",
				"a.*.d")).isEqualTo(JsonFieldType.NUMBER);
	}

	@Test
	public void intermediateWildcardWithVaryingType() throws IOException {
		assertThat(resolveFieldType(
				"{\"a\": {\"b\": {\"d\": 4}, \"c\": {\"d\": \"four\"}}}}", "a.*.d"))
						.isEqualTo(JsonFieldType.VARIES);
	}

	private void assertFieldType(JsonFieldType expectedType, String jsonValue)
			throws IOException {
		assertThat(new JsonFieldTypeResolver(createSimplePayload(jsonValue))
				.resolveFieldType(new FieldDescriptor("field"))).isEqualTo(expectedType);
	}

	private Map<String, Object> createSimplePayload(String value) throws IOException {
		return createPayload("{\"field\":" + value + "}");
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> createPayload(String json) throws IOException {
		return new ObjectMapper().readValue(json, Map.class);
	}

	private Object determineFieldType(String json, FieldDescriptor fieldDescriptor)
			throws IOException {
		return new JsonFieldTypeResolver(createPayload(json))
				.determineFieldType(fieldDescriptor);
	}

	private JsonFieldTypeResolver fieldTypeResolver(String json) throws IOException {
		return new JsonFieldTypeResolver(createPayload(json));
	}

	private JsonFieldType resolveFieldType(String json, String path) throws IOException {
		return fieldTypeResolver(json).resolveFieldType(new FieldDescriptor(path));
	}

}
