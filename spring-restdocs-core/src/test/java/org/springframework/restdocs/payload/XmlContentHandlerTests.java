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

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

/**
 * Tests for {@link XmlContentHandler}.
 *
 * @author Andy Wilkinson
 */
public class XmlContentHandlerTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void topLevelElementCanBeDocumented() {
		String undocumentedContent = createHandler("<a>5</a>").getUndocumentedContent(
				Arrays.asList(fieldWithPath("a").type("a").description("description")));
		assertThat(undocumentedContent, is(nullValue()));
	}

	@Test
	public void nestedElementCanBeDocumentedLeavingAncestors() {
		String undocumentedContent = createHandler("<a><b>5</b></a>")
				.getUndocumentedContent(Arrays.asList(
						fieldWithPath("a/b").type("b").description("description")));
		assertThat(undocumentedContent, is(equalTo(String.format("<a/>%n"))));
	}

	@Test
	public void fieldDescriptorDoesNotDocumentEntireSubsection() {
		String undocumentedContent = createHandler("<a><b>5</b></a>")
				.getUndocumentedContent(Arrays
						.asList(fieldWithPath("a").type("a").description("description")));
		assertThat(undocumentedContent,
				is(equalTo(String.format("<a>%n    <b>5</b>%n</a>%n"))));
	}

	@Test
	public void subsectionDescriptorDocumentsEntireSubsection() {
		String undocumentedContent = createHandler("<a><b>5</b></a>")
				.getUndocumentedContent(Arrays.asList(
						subsectionWithPath("a").type("a").description("description")));
		assertThat(undocumentedContent, is(nullValue()));
	}

	@Test
	public void multipleElementsCanBeInDescendingOrderDocumented() {
		String undocumentedContent = createHandler("<a><b>5</b></a>")
				.getUndocumentedContent(Arrays.asList(
						fieldWithPath("a").type("a").description("description"),
						fieldWithPath("a/b").type("b").description("description")));
		assertThat(undocumentedContent, is(nullValue()));
	}

	@Test
	public void multipleElementsCanBeInAscendingOrderDocumented() {
		String undocumentedContent = createHandler("<a><b>5</b></a>")
				.getUndocumentedContent(Arrays.asList(
						fieldWithPath("a/b").type("b").description("description"),
						fieldWithPath("a").type("a").description("description")));
		assertThat(undocumentedContent, is(nullValue()));
	}

	@Test
	public void failsFastWithNonXmlContent() {
		this.thrown.expect(PayloadHandlingException.class);
		createHandler("non-XML content");
	}

	private XmlContentHandler createHandler(String xml) {
		return new XmlContentHandler(xml.getBytes());
	}

}
