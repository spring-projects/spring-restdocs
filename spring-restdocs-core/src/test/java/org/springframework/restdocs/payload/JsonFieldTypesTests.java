/*
 * Copyright 2014-2018 the original author or authors.
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

import java.util.EnumSet;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JsonFieldTypes}.
 *
 * @author Andy Wilkinson
 */
public class JsonFieldTypesTests {

	@Test
	public void singleTypeCoalescesToThatType() {
		assertThat(new JsonFieldTypes(JsonFieldType.NUMBER).coalesce(false))
				.isEqualTo(JsonFieldType.NUMBER);
	}

	@Test
	public void singleTypeCoalescesToThatTypeWhenOptional() {
		assertThat(new JsonFieldTypes(JsonFieldType.NUMBER).coalesce(true))
				.isEqualTo(JsonFieldType.NUMBER);
	}

	@Test
	public void multipleTypesCoalescesToVaries() {
		assertThat(
				new JsonFieldTypes(EnumSet.of(JsonFieldType.ARRAY, JsonFieldType.NUMBER))
						.coalesce(false)).isEqualTo(JsonFieldType.VARIES);
	}

	@Test
	public void nullAndNonNullTypesCoalescesToVaries() {
		assertThat(new JsonFieldTypes(EnumSet.of(JsonFieldType.ARRAY, JsonFieldType.NULL))
				.coalesce(false)).isEqualTo(JsonFieldType.VARIES);
	}

	@Test
	public void nullAndNonNullTypesCoalescesToNonNullTypeWhenOptional() {
		assertThat(new JsonFieldTypes(EnumSet.of(JsonFieldType.ARRAY, JsonFieldType.NULL))
				.coalesce(true)).isEqualTo(JsonFieldType.ARRAY);
	}

}
