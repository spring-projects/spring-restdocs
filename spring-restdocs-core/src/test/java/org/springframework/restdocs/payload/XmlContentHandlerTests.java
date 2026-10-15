/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.payload;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

/**
 * Tests for {@link XmlContentHandler}.
 *
 * @author Andy Wilkinson
 */
public class XmlContentHandlerTests {

	@Test
	public void topLevelElementCanBeDocumented() {
		List<FieldDescriptor> descriptors = Arrays.asList(fieldWithPath("a").type("a").description("description"));
		String undocumentedContent = createHandler("<a>5</a>", descriptors).getUndocumentedContent();
		assertThat(undocumentedContent).isNull();
	}

	@Test
	public void nestedElementCanBeDocumentedLeavingAncestors() {
		List<FieldDescriptor> descriptors = Arrays.asList(fieldWithPath("a/b").type("b").description("description"));
		String undocumentedContent = createHandler("<a><b>5</b></a>", descriptors).getUndocumentedContent();
		assertThat(undocumentedContent).isEqualTo(String.format("<a/>%n"));
	}

	@Test
	public void fieldDescriptorDoesNotDocumentEntireSubsection() {
		List<FieldDescriptor> descriptors = Arrays.asList(fieldWithPath("a").type("a").description("description"));
		String undocumentedContent = createHandler("<a><b>5</b></a>", descriptors).getUndocumentedContent();
		assertThat(undocumentedContent).isEqualTo(String.format("<a>%n    <b>5</b>%n</a>%n"));
	}

	@Test
	public void subsectionDescriptorDocumentsEntireSubsection() {
		List<FieldDescriptor> descriptors = Arrays.asList(subsectionWithPath("a").type("a").description("description"));
		String undocumentedContent = createHandler("<a><b>5</b></a>", descriptors).getUndocumentedContent();
		assertThat(undocumentedContent).isNull();
	}

	@Test
	public void multipleElementsCanBeInDescendingOrderDocumented() {
		List<FieldDescriptor> descriptors = Arrays.asList(fieldWithPath("a").type("a").description("description"),
				fieldWithPath("a/b").type("b").description("description"));
		String undocumentedContent = createHandler("<a><b>5</b></a>", descriptors).getUndocumentedContent();
		assertThat(undocumentedContent).isNull();
	}

	@Test
	public void multipleElementsCanBeInAscendingOrderDocumented() {
		List<FieldDescriptor> descriptors = Arrays.asList(fieldWithPath("a/b").type("b").description("description"),
				fieldWithPath("a").type("a").description("description"));
		String undocumentedContent = createHandler("<a><b>5</b></a>", descriptors).getUndocumentedContent();
		assertThat(undocumentedContent).isNull();
	}

	@Test
	public void failsFastWithNonXmlContent() {
		assertThatExceptionOfType(PayloadHandlingException.class)
			.isThrownBy(() -> createHandler("non-XML content", Collections.emptyList()));
	}

	@Test
	public void xmlWithDoctypeIsRejected() {
		assertThatExceptionOfType(PayloadHandlingException.class).isThrownBy(
				() -> createHandler("<!DOCTYPE foo [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]><foo>&xxe;</foo>",
						Collections.emptyList()));
	}

	private XmlContentHandler createHandler(String xml, List<FieldDescriptor> descriptors) {
		return new XmlContentHandler(xml.getBytes(), descriptors);
	}

}
