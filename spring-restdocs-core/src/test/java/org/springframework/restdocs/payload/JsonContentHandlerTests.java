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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link JsonContentHandler}.
 *
 * @author Andy Wilkinson
 */
public class JsonContentHandlerTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void typeForFieldWithNullValueMustMatch() throws IOException {
		this.thrown.expect(FieldTypesDoNotMatchException.class);
		new JsonContentHandler("{\"a\": null}".getBytes())
				.determineFieldType(new FieldDescriptor("a").type(JsonFieldType.STRING));
	}

	@Test
	public void typeForFieldWithNotNullAndThenNullValueMustMatch() throws IOException {
		this.thrown.expect(FieldTypesDoNotMatchException.class);
		new JsonContentHandler("{\"a\":[{\"id\":1},{\"id\":null}]}".getBytes())
				.determineFieldType(
						new FieldDescriptor("a[].id").type(JsonFieldType.STRING));
	}

	@Test
	public void typeForFieldWithNullAndThenNotNullValueMustMatch() throws IOException {
		this.thrown.expect(FieldTypesDoNotMatchException.class);
		new JsonContentHandler("{\"a\":[{\"id\":null},{\"id\":1}]}".getBytes())
				.determineFieldType(
						new FieldDescriptor("a.[].id").type(JsonFieldType.STRING));
	}

	@Test
	public void typeForOptionalFieldWithNumberAndThenNullValueIsNumber()
			throws IOException {
		Object fieldType = new JsonContentHandler(
				"{\"a\":[{\"id\":1},{\"id\":null}]}\"".getBytes())
						.determineFieldType(new FieldDescriptor("a[].id").optional());
		assertThat((JsonFieldType) fieldType, is(equalTo(JsonFieldType.NUMBER)));
	}

	@Test
	public void typeForOptionalFieldWithNullAndThenNumberIsNumber() throws IOException {
		Object fieldType = new JsonContentHandler(
				"{\"a\":[{\"id\":null},{\"id\":1}]}".getBytes())
						.determineFieldType(new FieldDescriptor("a[].id").optional());
		assertThat((JsonFieldType) fieldType, is(equalTo(JsonFieldType.NUMBER)));
	}

	@Test
	public void typeForFieldWithNumberAndThenNullValueIsVaries() throws IOException {
		Object fieldType = new JsonContentHandler(
				"{\"a\":[{\"id\":1},{\"id\":null}]}\"".getBytes())
						.determineFieldType(new FieldDescriptor("a[].id"));
		assertThat((JsonFieldType) fieldType, is(equalTo(JsonFieldType.VARIES)));
	}

	@Test
	public void typeForFieldWithNullAndThenNumberIsVaries() throws IOException {
		Object fieldType = new JsonContentHandler(
				"{\"a\":[{\"id\":null},{\"id\":1}]}".getBytes())
						.determineFieldType(new FieldDescriptor("a[].id"));
		assertThat((JsonFieldType) fieldType, is(equalTo(JsonFieldType.VARIES)));
	}

	@Test
	public void typeForOptionalFieldWithNullValueCanBeProvidedExplicitly()
			throws IOException {
		Object fieldType = new JsonContentHandler("{\"a\": null}".getBytes())
				.determineFieldType(
						new FieldDescriptor("a").type(JsonFieldType.STRING).optional());
		assertThat((JsonFieldType) fieldType, is(equalTo(JsonFieldType.STRING)));
	}

	@Test
	public void failsFastWithNonJsonContent() {
		this.thrown.expect(PayloadHandlingException.class);
		new JsonContentHandler("Non-JSON content".getBytes());
	}

}
