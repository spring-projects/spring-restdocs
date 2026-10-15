/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.payload;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JsonFieldTypes}.
 *
 * @author Andy Wilkinson
 */
class JsonFieldTypesTests {

	@Test
	void singleTypeCoalescesToThatType() {
		assertThat(new JsonFieldTypes(JsonFieldType.NUMBER).coalesce(false)).isEqualTo(JsonFieldType.NUMBER);
	}

	@Test
	void singleTypeCoalescesToThatTypeWhenOptional() {
		assertThat(new JsonFieldTypes(JsonFieldType.NUMBER).coalesce(true)).isEqualTo(JsonFieldType.NUMBER);
	}

	@Test
	void multipleTypesCoalescesToVaries() {
		assertThat(new JsonFieldTypes(EnumSet.of(JsonFieldType.ARRAY, JsonFieldType.NUMBER)).coalesce(false))
			.isEqualTo(JsonFieldType.VARIES);
	}

	@Test
	void nullAndNonNullTypesCoalescesToVaries() {
		assertThat(new JsonFieldTypes(EnumSet.of(JsonFieldType.ARRAY, JsonFieldType.NULL)).coalesce(false))
			.isEqualTo(JsonFieldType.VARIES);
	}

	@Test
	void nullAndNonNullTypesCoalescesToNonNullTypeWhenOptional() {
		assertThat(new JsonFieldTypes(EnumSet.of(JsonFieldType.ARRAY, JsonFieldType.NULL)).coalesce(true))
			.isEqualTo(JsonFieldType.ARRAY);
	}

}
