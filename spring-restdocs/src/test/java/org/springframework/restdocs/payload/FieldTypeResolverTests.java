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

package org.springframework.restdocs.payload;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests for {@link FieldTypeResolver}
 * 
 * @author Andy Wilkinson
 *
 */
public class FieldTypeResolverTests {

	private final FieldTypeResolver fieldTypeResolver = new FieldTypeResolver();

	@Rule
	public ExpectedException thrownException = ExpectedException.none();

	@Test
	public void arrayField() throws IOException {
		assertFieldType(FieldType.ARRAY, "[]");
	}

	@Test
	public void booleanField() throws IOException {
		assertFieldType(FieldType.BOOLEAN, "true");
	}

	@Test
	public void objectField() throws IOException {
		assertFieldType(FieldType.OBJECT, "{}");
	}

	@Test
	public void nullField() throws IOException {
		assertFieldType(FieldType.NULL, "null");
	}

	@Test
	public void numberField() throws IOException {
		assertFieldType(FieldType.NUMBER, "1.2345");
	}

	@Test
	public void stringField() throws IOException {
		assertFieldType(FieldType.STRING, "\"Foo\"");
	}

	@Test
	public void nestedField() throws IOException {
		assertThat(this.fieldTypeResolver.resolveFieldType("a.b.c",
				createPayload("{\"a\":{\"b\":{\"c\":{}}}}")), equalTo(FieldType.OBJECT));
	}

	@Test
	public void nonExistentFieldProducesIllegalArgumentException() throws IOException {
		this.thrownException.expect(IllegalArgumentException.class);
		this.thrownException
				.expectMessage("The payload does not contain a field with the path 'a.b'");
		this.fieldTypeResolver.resolveFieldType("a.b", createPayload("{\"a\":{}}"));
	}

	private void assertFieldType(FieldType expectedType, String jsonValue)
			throws IOException {
		assertThat(this.fieldTypeResolver.resolveFieldType("field",
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
