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

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.restdocs.snippet.SnippetException;

/**
 * Tests for {@link FieldValidator}
 * 
 * @author Andy Wilkinson
 */
public class FieldValidatorTests {

	private final FieldValidator fieldValidator = new FieldValidator();

	@Rule
	public ExpectedException thrownException = ExpectedException.none();

	private StringReader listPayload = new StringReader(
			"[{\"a\":1},{\"a\":2},{\"b\":{\"c\":3}}]");

	private StringReader payload = new StringReader(
			"{\"a\":{\"b\":{},\"c\":true,\"d\":[{\"e\":1},{\"e\":2}]}}");

	@Test
	public void noMissingFieldsAllFieldsDocumented() throws IOException {
		this.fieldValidator.validate(this.payload, Arrays.asList(new FieldDescriptor(
				"a.b"), new FieldDescriptor("a.c"), new FieldDescriptor("a.d[].e"),
				new FieldDescriptor("a.d"), new FieldDescriptor("a")));
	}

	@Test
	public void optionalFieldsAreNotReportedMissing() throws IOException {
		this.fieldValidator.validate(this.payload, Arrays.asList(
				new FieldDescriptor("a"), new FieldDescriptor("a.b"),
				new FieldDescriptor("a.c"), new FieldDescriptor("a.d"),
				new FieldDescriptor("y").optional()));
	}

	@Test
	public void parentIsDocumentedWhenAllChildrenAreDocumented() throws IOException {
		this.fieldValidator.validate(this.payload, Arrays.asList(new FieldDescriptor(
				"a.b"), new FieldDescriptor("a.c"), new FieldDescriptor("a.d[].e")));
	}

	@Test
	public void childIsDocumentedWhenParentIsDocumented() throws IOException {
		this.fieldValidator.validate(this.payload,
				Arrays.asList(new FieldDescriptor("a")));
	}

	@Test
	public void missingField() throws IOException {
		this.thrownException.expect(SnippetException.class);
		this.thrownException
				.expectMessage(equalTo("Fields with the following paths were not found"
						+ " in the payload: [y, z]"));
		this.fieldValidator.validate(this.payload, Arrays.asList(
				new FieldDescriptor("a"), new FieldDescriptor("a.b"),
				new FieldDescriptor("y"), new FieldDescriptor("z")));
	}

	@Test
	public void undocumentedField() throws IOException {
		this.thrownException.expect(SnippetException.class);
		this.thrownException.expectMessage(equalTo(String
				.format("The following parts of the payload were not"
						+ " documented:%n{%n  \"a\" : {%n    \"c\" : true%n  }%n}")));
		this.fieldValidator.validate(this.payload,
				Arrays.asList(new FieldDescriptor("a.b"), new FieldDescriptor("a.d")));
	}

	@Test
	public void listPayloadNoMissingFieldsAllFieldsDocumented() throws IOException {
		this.fieldValidator.validate(this.listPayload, Arrays.asList(new FieldDescriptor(
				"[]b.c"), new FieldDescriptor("[]b"), new FieldDescriptor("[]a"),
				new FieldDescriptor("[]")));
	}

	@Test
	public void listPayloadParentIsDocumentedWhenAllChildrenAreDocumented()
			throws IOException {
		this.fieldValidator.validate(this.listPayload,
				Arrays.asList(new FieldDescriptor("[]b.c"), new FieldDescriptor("[]a")));
	}

	@Test
	public void listPayloadChildIsDocumentedWhenParentIsDocumented() throws IOException {
		this.fieldValidator.validate(this.listPayload,
				Arrays.asList(new FieldDescriptor("[]")));
	}
}
