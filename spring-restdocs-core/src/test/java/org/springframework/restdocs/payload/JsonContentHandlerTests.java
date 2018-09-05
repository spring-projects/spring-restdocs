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

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JsonContentHandler}.
 *
 * @author Andy Wilkinson
 * @author Mathias Düsterhöft
 */
public class JsonContentHandlerTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void failsFastWithNonJsonContent() {
		this.thrown.expect(PayloadHandlingException.class);
		new JsonContentHandler("Non-JSON content".getBytes());
	}

	@Test
	public void describedFieldThatIsNotPresentIsConsideredMissing() {
		List<FieldDescriptor> missingFields = new JsonContentHandler(
				"{\"a\": \"alpha\", \"b\":\"bravo\"}".getBytes())
						.findMissingFields(Arrays.asList(new FieldDescriptor("a"),
								new FieldDescriptor("b"), new FieldDescriptor("c")));
		assertThat(missingFields.size()).isEqualTo(1);
		assertThat(missingFields.get(0).getPath()).isEqualTo("c");
	}

	@Test
	public void describedOptionalFieldThatIsNotPresentIsNotConsideredMissing() {
		List<FieldDescriptor> missingFields = new JsonContentHandler(
				"{\"a\": \"alpha\", \"b\":\"bravo\"}".getBytes()).findMissingFields(
						Arrays.asList(new FieldDescriptor("a"), new FieldDescriptor("b"),
								new FieldDescriptor("c").optional()));
		assertThat(missingFields.size()).isEqualTo(0);
	}

	@Test
	public void describedFieldThatIsNotPresentNestedBeneathOptionalFieldThatIsPresentIsConsideredMissing() {
		List<FieldDescriptor> missingFields = new JsonContentHandler(
				"{\"a\":\"alpha\",\"b\":\"bravo\"}".getBytes()).findMissingFields(
						Arrays.asList(new FieldDescriptor("a").optional(),
								new FieldDescriptor("b"), new FieldDescriptor("a.c")));
		assertThat(missingFields.size()).isEqualTo(1);
		assertThat(missingFields.get(0).getPath()).isEqualTo("a.c");
	}

	@Test
	public void describedFieldThatIsNotPresentNestedBeneathOptionalFieldThatIsNotPresentIsNotConsideredMissing() {
		List<FieldDescriptor> missingFields = new JsonContentHandler(
				"{\"b\":\"bravo\"}".getBytes()).findMissingFields(
						Arrays.asList(new FieldDescriptor("a").optional(),
								new FieldDescriptor("b"), new FieldDescriptor("a.c")));
		assertThat(missingFields.size()).isEqualTo(0);
	}

	@Test
	public void describedFieldThatIsNotPresentNestedBeneathOptionalArrayThatIsEmptyIsNotConsideredMissing() {
		List<FieldDescriptor> missingFields = new JsonContentHandler(
				"{\"outer\":[]}".getBytes())
						.findMissingFields(Arrays.asList(new FieldDescriptor("outer"),
								new FieldDescriptor("outer[]").optional(),
								new FieldDescriptor("outer[].inner")));
		assertThat(missingFields.size()).isEqualTo(0);
	}

	@Test
	public void describedSometimesPresentFieldThatIsChildOfSometimesPresentOptionalArrayIsNotConsideredMissing() {
		List<FieldDescriptor> missingFields = new JsonContentHandler(
				"{\"a\":[ {\"b\": \"bravo\"}, {\"b\": \"bravo\", \"c\": { \"d\": \"delta\"}}]}"
						.getBytes()).findMissingFields(
								Arrays.asList(new FieldDescriptor("a.[].c").optional(),
										new FieldDescriptor("a.[].c.d")));
		assertThat(missingFields.size()).isEqualTo(0);
	}

	@Test
	public void describedMissingFieldThatIsChildOfNestedOptionalArrayThatIsEmptyIsNotConsideredMissing() {
		List<FieldDescriptor> missingFields = new JsonContentHandler(
				"{\"a\":[{\"b\":[]}]}".getBytes()).findMissingFields(
						Arrays.asList(new FieldDescriptor("a.[].b").optional(),
								new FieldDescriptor("a.[].b.[]").optional(),
								new FieldDescriptor("a.[].b.[].c")));
		assertThat(missingFields.size()).isEqualTo(0);
	}

	@Test
	public void describedMissingFieldThatIsChildOfNestedOptionalArrayThatContainsAnObjectIsConsideredMissing() {
		List<FieldDescriptor> missingFields = new JsonContentHandler(
				"{\"a\":[{\"b\":[{}]}]}".getBytes()).findMissingFields(
						Arrays.asList(new FieldDescriptor("a.[].b").optional(),
								new FieldDescriptor("a.[].b.[]").optional(),
								new FieldDescriptor("a.[].b.[].c")));
		assertThat(missingFields.size()).isEqualTo(1);
		assertThat(missingFields.get(0).getPath()).isEqualTo("a.[].b.[].c");
	}

}
