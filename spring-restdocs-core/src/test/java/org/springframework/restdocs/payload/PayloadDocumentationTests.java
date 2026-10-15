/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.payload;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.applyPathPrefix;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link PayloadDocumentation}.
 *
 * @author Andy Wilkinson
 */
class PayloadDocumentationTests {

	@Test
	void applyPathPrefixAppliesPrefixToDescriptorPaths() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo"), fieldWithPath("charlie")));
		assertThat(descriptors.size()).isEqualTo(2);
		assertThat(descriptors.get(0).getPath()).isEqualTo("alpha.bravo");
	}

	@Test
	void applyPathPrefixCopiesIgnored() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.", Arrays.asList(fieldWithPath("bravo").ignored()));
		assertThat(descriptors.size()).isEqualTo(1);
		assertThat(descriptors.get(0).isIgnored()).isTrue();
	}

	@Test
	void applyPathPrefixCopiesOptional() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.", Arrays.asList(fieldWithPath("bravo").optional()));
		assertThat(descriptors.size()).isEqualTo(1);
		assertThat(descriptors.get(0).isOptional()).isTrue();
	}

	@Test
	void applyPathPrefixCopiesDescription() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo").description("Some field")));
		assertThat(descriptors.size()).isEqualTo(1);
		assertThat(descriptors.get(0).getDescription()).isEqualTo("Some field");
	}

	@Test
	void applyPathPrefixCopiesType() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo").type(JsonFieldType.OBJECT)));
		assertThat(descriptors.size()).isEqualTo(1);
		assertThat(descriptors.get(0).getType()).isEqualTo(JsonFieldType.OBJECT);
	}

	@Test
	void applyPathPrefixCopiesAttributes() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo").attributes(key("a").value("alpha"), key("b").value("bravo"))));
		assertThat(descriptors.size()).isEqualTo(1);
		assertThat(descriptors.get(0).getAttributes().size()).isEqualTo(2);
		assertThat(descriptors.get(0).getAttributes().get("a")).isEqualTo("alpha");
		assertThat(descriptors.get(0).getAttributes().get("b")).isEqualTo("bravo");
	}

}
