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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link JsonFieldTypeResolver}.
 *
 * @author Andy Wilkinson
 */
public class JsonFieldTypeResolverTests {

	private final JsonFieldTypeResolver fieldTypeResolver = new JsonFieldTypeResolver();

	@Rule
	public ExpectedException thrownException = ExpectedException.none();

	@Test
	public void arrayField() throws IOException {
		assertFieldType(JsonFieldType.ARRAY, "[]");
	}

	@Test
	public void topLevelArray() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("[]"),
						new ObjectMapper().readValue("[{\"a\":\"alpha\"}]", List.class)),
				equalTo(JsonFieldType.ARRAY));
	}

	@Test
	public void nestedArray() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a[]"),
						createPayload("{\"a\": [{\"b\":\"bravo\"}]}")),
				equalTo(JsonFieldType.ARRAY));
	}

	@Test
	public void arrayNestedBeneathAnArray() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a[].b[]"),
						createPayload("{\"a\": [{\"b\": [ 1, 2 ]}]}")),
				equalTo(JsonFieldType.ARRAY));
	}

	@Test
	public void specificFieldOfObjectInArrayNestedBeneathAnArray() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a[].b[].c"),
						createPayload("{\"a\": [{\"b\": [ {\"c\": 5}, {\"c\": 5}]}]}")),
				equalTo(JsonFieldType.NUMBER));
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
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a.b.c"),
						createPayload("{\"a\":{\"b\":{\"c\":{}}}}")),
				equalTo(JsonFieldType.OBJECT));
	}

	@Test
	public void multipleFieldsWithSameType() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a[].id"),
						createPayload("{\"a\":[{\"id\":1},{\"id\":2}]}")),
				equalTo(JsonFieldType.NUMBER));
	}

	@Test
	public void multipleFieldsWithDifferentTypes() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a[].id"),
						createPayload("{\"a\":[{\"id\":1},{\"id\":true}]}")),
				equalTo(JsonFieldType.VARIES));
	}

	@Test
	public void multipleFieldsWithDifferentTypesAndSometimesAbsent() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a[].id"),
						createPayload("{\"a\":[{\"id\":1},{\"id\":true}, { }]}")),
				equalTo(JsonFieldType.VARIES));
	}

	@Test
	public void multipleFieldsWithDifferentTypesAndSometimesAbsentWhenOptionalResolvesToVaries()
			throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(
						new FieldDescriptor("a[].id").optional(),
						createPayload("{\"a\":[{\"id\":1},{\"id\":true}, { }]}")),
				equalTo(JsonFieldType.VARIES));
	}

	@Test
	public void multipleFieldsWhenSometimesAbsent() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a[].id"),
						createPayload("{\"a\":[{\"id\":1},{ }]}")),
				equalTo(JsonFieldType.NUMBER));
	}

	@Test
	public void multipleFieldsWithDifferentTypesAndSometimesNull() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a[].id"),
						createPayload(
								"{\"a\":[{\"id\":1},{\"id\":true}, {\"id\":null}]}")),
				equalTo(JsonFieldType.VARIES));
	}

	@Test
	public void multipleFieldsWhenNotNullThenNullWhenRequiredHasVariesType()
			throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a[].id"),
						createPayload("{\"a\":[{\"id\":1},{\"id\":null}]}")),
				equalTo(JsonFieldType.VARIES));
	}

	@Test
	public void multipleFieldsWhenNotNullThenNullWhenOptionalHasSpecificType()
			throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(
						new FieldDescriptor("a[].id").optional(),
						createPayload("{\"a\":[{\"id\":1},{\"id\":null}]}")),
				equalTo(JsonFieldType.NUMBER));
	}

	@Test
	public void multipleFieldsWhenNullThenNotNullWhenRequiredHasVariesType()
			throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a[].id"),
						createPayload("{\"a\":[{\"id\":null},{\"id\":1}]}")),
				equalTo(JsonFieldType.VARIES));
	}

	@Test
	public void multipleFieldsWhenNullThenNotNullWhenOptionalHasSpecificType()
			throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(
						new FieldDescriptor("a[].id").optional(),
						createPayload("{\"a\":[{\"id\":null},{\"id\":1}]}")),
				equalTo(JsonFieldType.NUMBER));
	}

	@Test
	public void multipleFieldsWhenEitherNullOrAbsent() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a[].id"),
						createPayload("{\"a\":[{},{\"id\":null}]}")),
				equalTo(JsonFieldType.NULL));
	}

	@Test
	public void multipleFieldsThatAreAllNull() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a[].id"),
						createPayload("{\"a\":[{\"id\":null},{\"id\":null}]}")),
				equalTo(JsonFieldType.NULL));
	}

	@Test
	public void nonExistentSingleFieldProducesFieldDoesNotExistException()
			throws IOException {
		this.thrownException.expect(FieldDoesNotExistException.class);
		this.thrownException.expectMessage(
				"The payload does not contain a field with the path 'a.b'");
		this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a.b"),
				createPayload("{\"a\":{}}"));
	}

	@Test
	public void nonExistentMultipleFieldsProducesFieldDoesNotExistException()
			throws IOException {
		this.thrownException.expect(FieldDoesNotExistException.class);
		this.thrownException.expectMessage(
				"The payload does not contain a field with the path 'a[].b'");
		this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a[].b"),
				createPayload("{\"a\":[{\"c\":1},{\"c\":2}]}"));
	}

	@Test
	public void leafWildcardWithCommonType() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a.*"),
						createPayload("{\"a\": {\"b\": 5, \"c\": 6}}")),
				equalTo(JsonFieldType.NUMBER));
	}

	@Test
	public void leafWildcardWithVaryingType() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a.*"),
						createPayload("{\"a\": {\"b\": 5, \"c\": \"six\"}}")),
				equalTo(JsonFieldType.VARIES));
	}

	@Test
	public void intermediateWildcardWithCommonType() throws IOException {
		assertThat(
				this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a.*.d"),
						createPayload(
								"{\"a\": {\"b\": {\"d\": 4}, \"c\": {\"d\": 5}}}}")),
				equalTo(JsonFieldType.NUMBER));
	}

	@Test
	public void intermediateWildcardWithVaryingType() throws IOException {
		assertThat(this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("a.*.d"),
				createPayload("{\"a\": {\"b\": {\"d\": 4}, \"c\": {\"d\": \"four\"}}}}")),
				equalTo(JsonFieldType.VARIES));
	}

	private void assertFieldType(JsonFieldType expectedType, String jsonValue)
			throws IOException {
		assertThat(this.fieldTypeResolver.resolveFieldType(new FieldDescriptor("field"),
				createSimplePayload(jsonValue)), equalTo(expectedType));
	}

	private Map<String, Object> createSimplePayload(String value) throws IOException {
		return createPayload("{\"field\":" + value + "}");
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> createPayload(String json) throws IOException {
		return new ObjectMapper().readValue(json, Map.class);
	}

}
