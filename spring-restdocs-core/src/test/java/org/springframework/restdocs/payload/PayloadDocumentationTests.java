/*
 * Copyright 2014-2016 the original author or authors.
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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
		assertThat(descriptors.size(), is(equalTo(2)));
		assertThat(descriptors.get(0).getPath(), is(equalTo("alpha.bravo")));
	}

	@Test
	public void applyPathPrefixCopiesIgnored() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo").ignored()));
		assertThat(descriptors.size(), is(equalTo(1)));
		assertThat(descriptors.get(0).isIgnored(), is(true));
	}

	@Test
	public void applyPathPrefixCopiesOptional() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo").optional()));
		assertThat(descriptors.size(), is(equalTo(1)));
		assertThat(descriptors.get(0).isOptional(), is(true));
	}

	@Test
	public void applyPathPrefixCopiesDescription() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo").description("Some field")));
		assertThat(descriptors.size(), is(equalTo(1)));
		assertThat(descriptors.get(0).getDescription(),
				is(equalTo((Object) "Some field")));
	}

	@Test
	public void applyPathPrefixCopiesType() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo").type(JsonFieldType.OBJECT)));
		assertThat(descriptors.size(), is(equalTo(1)));
		assertThat(descriptors.get(0).getType(),
				is(equalTo((Object) JsonFieldType.OBJECT)));
	}

	@Test
	public void applyPathPrefixCopiesAttributes() {
		List<FieldDescriptor> descriptors = applyPathPrefix("alpha.",
				Arrays.asList(fieldWithPath("bravo").attributes(key("a").value("alpha"),
						key("b").value("bravo"))));
		assertThat(descriptors.size(), is(equalTo(1)));
		assertThat(descriptors.get(0).getAttributes().size(), is(equalTo(2)));
		assertThat(descriptors.get(0).getAttributes().get("a"),
				is(equalTo((Object) "alpha")));
		assertThat(descriptors.get(0).getAttributes().get("b"),
				is(equalTo((Object) "bravo")));
	}

}
