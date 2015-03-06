/*
 * Copyright 2014-2015 the original author or authors.
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

package org.springframework.restdocs.snippet;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;
import org.springframework.restdocs.snippet.DocumentationWriter.DocumentationAction;

/**
 * Tests for {@link AsciidoctorWriter}
 * 
 * @author Andy Wilkinson
 */
public class AsciidoctorWriterTests {

	private Writer output = new StringWriter();

	private DocumentationWriter documentationWriter = new AsciidoctorWriter(this.output);

	@Test
	public void codeBlock() throws Exception {
		this.documentationWriter.codeBlock("java", new DocumentationAction() {

			@Override
			public void perform() throws IOException {
				AsciidoctorWriterTests.this.documentationWriter.println("foo");
			}
		});

		String expectedOutput = String.format("%n[source,java]%n----%nfoo%n----%n%n");
		assertEquals(expectedOutput, this.output.toString());
	}

	@Test
	public void shellCommand() throws Exception {
		this.documentationWriter.shellCommand(new DocumentationAction() {

			@Override
			public void perform() throws IOException {
				AsciidoctorWriterTests.this.documentationWriter.println("foo");
			}
		});

		String expectedOutput = String.format("%n[source,bash]%n----%n$ foo%n----%n%n");
		assertEquals(expectedOutput, this.output.toString());
	}
}
