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

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.extension.RubyExtensionRegistry;
import org.asciidoctor.extension.spi.ExtensionRegistry;

/**
 * Asciidoctor {@link ExtensionRegistry} for Spring REST Docs.
 *
 * @author Andy Wilkinson
 */
public final class RestDocsExtensionRegistry implements ExtensionRegistry {

	@Override
	public void register(Asciidoctor asciidoctor) {
		JavaExtensionRegistry registry = asciidoctor.javaExtensionRegistry();
		registry.preprocessor(new DefaultAttributesPreprocessor());

		RubyExtensionRegistry rubyExtensionRegistry = asciidoctor.rubyExtensionRegistry();
		rubyExtensionRegistry
				.loadClass(RestDocsExtensionRegistry.class
						.getResourceAsStream("/extensions/operation_block_macro.rb"))
				.blockMacro("operation", "OperationBlockMacro");

	}

}
