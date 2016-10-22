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

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Preprocessor;
import org.asciidoctor.extension.PreprocessorReader;

/**
 * {@link Preprocessor} that sets defaults for REST Docs-related {@link Document}
 * attributes.
 *
 * @author Andy Wilkinson
 */
final class DefaultAttributesPreprocessor extends Preprocessor {

	private final SnippetsDirectoryResolver snippetsDirectoryResolver = new SnippetsDirectoryResolver();

	@Override
	public PreprocessorReader process(Document document, PreprocessorReader reader) {
		document.setAttr("snippets", this.snippetsDirectoryResolver
				.getSnippetsDirectory(document.getAttributes()), false);
		return reader;
	}

}
