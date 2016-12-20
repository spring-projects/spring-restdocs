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

package org.springframework.restdocs.asciidoctor;

import java.io.File;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link DefaultAttributesPreprocessor}.
 *
 * @author Andy Wilkinson
 */
public class DefaultAttributesPreprocessorTests {

	@Test
	public void snippetsAttributeIsSet() {
		Options options = new Options();
		options.setAttributes(new Attributes("projectdir=../../.."));
		String converted = Asciidoctor.Factory.create().convert("{snippets}", options);
		assertThat(converted,
				containsString("build" + File.separatorChar + "generated-snippets"));
	}

	@Test
	public void snippetsAttributeFromConvertArgumentIsNotOverridden() {
		Options options = new Options();
		options.setAttributes(new Attributes("snippets=custom projectdir=../../.."));
		String converted = Asciidoctor.Factory.create().convert("{snippets}", options);
		assertThat(converted, containsString("custom"));
	}

	@Test
	public void snippetsAttributeFromDocumentPreambleIsNotOverridden() {
		Options options = new Options();
		options.setAttributes(new Attributes("projectdir=../../.."));
		String converted = Asciidoctor.Factory.create()
				.convert(":snippets: custom\n{snippets}", options);
		assertThat(converted, containsString("custom"));
	}

}
