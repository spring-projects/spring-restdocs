/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.asciidoctor;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;

/**
 * {@link ExtensionRegistry} for Spring REST Docs.
 *
 * @author Andy Wilkinson
 */
public final class RestDocsExtensionRegistry implements ExtensionRegistry {

	@Override
	public void register(Asciidoctor asciidoctor) {
		asciidoctor.javaExtensionRegistry().preprocessor(new DefaultAttributesPreprocessor());
		asciidoctor.rubyExtensionRegistry()
			.loadClass(RestDocsExtensionRegistry.class.getResourceAsStream("/extensions/operation_block_macro.rb"))
			.blockMacro("operation", "OperationBlockMacro");
	}

}
