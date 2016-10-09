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

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.spi.ExtensionRegistry;

/**
 * ExtensionRegistry for the {@link RestDocsSnippetBlockMacro} to get registered
 * as an extension in all projects that include the asciidoctor extension module.
 *
 * @author Gerrit Meier
 */
public class RestDocsSnippetBlockMacroExtension implements ExtensionRegistry {

	/**
	 * the name that identifies the block macro in the asciidoctor document
	 * (e.g. <pre><b>restdocs</b>::file_to_include[]</pre>)
	 */
	private static final String BLOCK_NAME = "restdocs";

	@Override
	public void register(Asciidoctor asciidoctor) {
		asciidoctor.javaExtensionRegistry().blockMacro(BLOCK_NAME, RestDocsSnippetBlockMacro.class);
	}
}
