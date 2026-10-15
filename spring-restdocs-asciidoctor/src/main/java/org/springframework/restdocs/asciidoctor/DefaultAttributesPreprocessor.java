/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.asciidoctor;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Preprocessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.asciidoctor.extension.Reader;

/**
 * {@link Preprocessor} that sets defaults for REST Docs-related {@link Document}
 * attributes.
 *
 * @author Andy Wilkinson
 */
final class DefaultAttributesPreprocessor extends Preprocessor {

	private final SnippetsDirectoryResolver snippetsDirectoryResolver = new SnippetsDirectoryResolver();

	@Override
	public Reader process(Document document, PreprocessorReader reader) {
		document.setAttribute("snippets", this.snippetsDirectoryResolver.getSnippetsDirectory(document.getAttributes()),
				false);
		return reader;
	}

}
