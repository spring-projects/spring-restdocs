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

package org.springframework.restdocs.asciidoctor.extensions;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link RestDocsSnippetBlockMacro}.
 *
 * @author Gerrit Meier
 */
public class RestDocsSnippetBlockMacroTest {

	@Before
	public void prepareIncludeFiles() throws Exception {
		Files.createDirectories(Paths.get("build/generated-snippets/"));
		Files.copy(Paths.get("src/test/resources/rest_docs_macro.adoc"),
				Paths.get("build/generated-snippets/rest_docs_macro.adoc"), StandardCopyOption.REPLACE_EXISTING);
	}

	@Test
	public void replaceRestDocsSnippetBlockWithFile() {
		Asciidoctor asciidoctor = Asciidoctor.Factory.create();
		asciidoctor.javaExtensionRegistry().blockMacro("snippet", RestDocsSnippetBlockMacro.class);

		assertThat(asciidoctor.convert("snippet::rest_docs_macro.adoc[]", new Options()),
				equalTo("<div class=\"paragraph\">\n<p>test text</p>\n</div>"));
	}

}
