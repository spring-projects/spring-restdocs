/*
 * Copyright 2016 the original author or authors.
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

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link RestDocsSnippetBlockMacro}.
 *
 * @author Gerrit Meier
 */
public class RestDocsSnippetBlockMacroTest {

    @Test
    public void replaceRestDocsSnippetBlockWithFile() {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        asciidoctor.javaExtensionRegistry().blockMacro("restdocs", RestDocsSnippetBlockMacro.class);

        assertThat(asciidoctor.convert("restdocs::rest_docs_macro.adoc[]", new Options()),
                equalTo("<div class=\"paragraph\">\n<p>test text</p>\n</div>"));
    }

}