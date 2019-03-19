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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.applyPathPrefix;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

/**
 * Tests for {@link PayloadDocumentation}.
 *
 * @author Andy Wilkinson
 */
public class PayloadDocumentationTests {

	@Test
	public void applyPathPrefixAppliesPrefixToDescriptorPaths() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo"), fieldWithPath("charlie")));
		assertThat(descriptors.size()).isEqualTo(2);
		assertThat(descriptors.get(0).getPath()).isEqualTo("alpha.bravo");
	}

	@Test
	public void applyPathPrefixCopiesIgnored() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo").ignored()));
		assertThat(descriptors.size()).isEqualTo(1);
		assertThat(descriptors.get(0).isIgnored()).isTrue();
	}

	@Test
	public void applyPathPrefixCopiesOptional() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo").optional()));
		assertThat(descriptors.size()).isEqualTo(1);
		assertThat(descriptors.get(0).isOptional()).isTrue();
	}

	@Test
	public void applyPathPrefixCopiesDescription() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo").description("Some field")));
		assertThat(descriptors.size()).isEqualTo(1);
		assertThat(descriptors.get(0).getDescription()).isEqualTo("Some field");
	}

	@Test
	public void applyPathPrefixCopiesType() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo").type(JsonFieldType.OBJECT)));
		assertThat(descriptors.size()).isEqualTo(1);
		assertThat(descriptors.get(0).getType()).isEqualTo(JsonFieldType.OBJECT);
	}

	@Test
	public void applyPathPrefixCopiesAttributes() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo").attributes(key("a").value("alpha"),
						key("b").value("bravo"))));
		assertThat(descriptors.size()).isEqualTo(1);
		assertThat(descriptors.get(0).getAttributes().size()).isEqualTo(2);
		assertThat(descriptors.get(0).getAttributes().get("a")).isEqualTo("alpha");
		assertThat(descriptors.get(0).getAttributes().get("b")).isEqualTo("bravo");
	}

}
