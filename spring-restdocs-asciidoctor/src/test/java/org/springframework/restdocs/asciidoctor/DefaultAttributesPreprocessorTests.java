/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.asciidoctor;

import java.io.File;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DefaultAttributesPreprocessor}.
 *
 * @author Andy Wilkinson
 */
class DefaultAttributesPreprocessorTests {

	@Test
	void snippetsAttributeIsSet() {
		String converted = createAsciidoctor().convert("{snippets}", createOptions("projectdir=../../.."));
		assertThat(converted).contains("build" + File.separatorChar + "generated-snippets");
	}

	@Test
	void snippetsAttributeFromConvertArgumentIsNotOverridden() {
		String converted = createAsciidoctor().convert("{snippets}",
				createOptions("snippets=custom projectdir=../../.."));
		assertThat(converted).contains("custom");
	}

	@Test
	void snippetsAttributeFromDocumentPreambleIsNotOverridden() {
		String converted = createAsciidoctor().convert(":snippets: custom\n{snippets}",
				createOptions("projectdir=../../.."));
		assertThat(converted).contains("custom");
	}

	private Options createOptions(String attributes) {
		Options options = Options.builder().build();
		options.setAttributes(Attributes.builder().arguments(attributes).build());
		return options;
	}

	private Asciidoctor createAsciidoctor() {
		Asciidoctor asciidoctor = Asciidoctor.Factory.create();
		asciidoctor.javaExtensionRegistry().preprocessor(new DefaultAttributesPreprocessor());
		return asciidoctor;
	}

}
