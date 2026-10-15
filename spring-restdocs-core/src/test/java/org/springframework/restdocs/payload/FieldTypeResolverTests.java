/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.payload;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link FieldTypeResolver}.
 *
 * @author Mathias Düsterhöft
 */
public class FieldTypeResolverTests {

	@Test
	public void whenForContentWithDescriptorsCalledWithJsonContentThenReturnsJsonFieldTypeResolver() {
		assertThat(FieldTypeResolver.forContentWithDescriptors("{\"field\": \"value\"}".getBytes(),
				MediaType.APPLICATION_JSON, Collections.emptyList()))
			.isInstanceOf(JsonContentHandler.class);
	}

	@Test
	public void whenForContentWithDescriptorsCalledWithXmlContentThenReturnsXmlContentHandler() {
		assertThat(FieldTypeResolver.forContentWithDescriptors("<a><b>5</b></a>".getBytes(), MediaType.APPLICATION_XML,
				Collections.emptyList()))
			.isInstanceOf(XmlContentHandler.class);
	}

	@Test
	public void whenForContentWithDescriptorsIsCalledWithInvalidContentThenExceptionIsThrown() {
		assertThatExceptionOfType(PayloadHandlingException.class).isThrownBy(() -> FieldTypeResolver
			.forContentWithDescriptors("some".getBytes(), MediaType.APPLICATION_XML, Collections.emptyList()));
	}

}
