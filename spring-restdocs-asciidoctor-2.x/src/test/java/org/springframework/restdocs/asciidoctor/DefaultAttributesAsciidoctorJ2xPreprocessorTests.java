/*
 * Copyright 2014-2019 the original author or authors.
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

package org.springframework.restdocs.asciidoctor;

import java.io.File;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DefaultAttributesAsciidoctorJ2xPreprocessor}.
 *
 * @author Andy Wilkinson
 */
public class DefaultAttributesAsciidoctorJ2xPreprocessorTests {

	@Test
	public void snippetsAttributeIsSet() {
		String converted = createAsciidoctor().convert("{snippets}", createOptions("projectdir=../../.."));
		assertThat(converted).contains("build" + File.separatorChar + "generated-snippets");
	}

	@Test
	public void snippetsAttributeFromConvertArgumentIsNotOverridden() {
		String converted = createAsciidoctor().convert("{snippets}",
				createOptions("snippets=custom projectdir=../../.."));
		assertThat(converted).contains("custom");
	}

	@Test
	public void snippetsAttributeFromDocumentPreambleIsNotOverridden() {
		String converted = createAsciidoctor().convert(":snippets: custom\n{snippets}",
				createOptions("projectdir=../../.."));
		assertThat(converted).contains("custom");
	}

	private Options createOptions(String attributes) {
		Options options = new Options();
		options.setAttributes(new Attributes(attributes));
		return options;
	}

	private Asciidoctor createAsciidoctor() {
		Asciidoctor asciidoctor = Asciidoctor.Factory.create();
		asciidoctor.javaExtensionRegistry().preprocessor(new DefaultAttributesAsciidoctorJ2xPreprocessor());
		return asciidoctor;
	}

}
