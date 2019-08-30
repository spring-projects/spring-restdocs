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

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.spi.ExtensionRegistry;

/**
 * AsciidoctorJ 1.6 {@link ExtensionRegistry} for Spring REST Docs.
 *
 * @author Andy Wilkinson
 */
public final class RestDocsAsciidoctorJ16ExtensionRegistry implements ExtensionRegistry {

	@Override
	public void register(Asciidoctor asciidoctor) {
		if (!asciidoctorJ16()) {
			return;
		}
		asciidoctor.javaExtensionRegistry().preprocessor(new DefaultAttributesAsciidoctorJ16Preprocessor());
		asciidoctor.rubyExtensionRegistry()
				.loadClass(RestDocsAsciidoctorJ16ExtensionRegistry.class
						.getResourceAsStream("/extensions/operation_block_macro.rb"))
				.blockMacro("operation", "OperationBlockMacro");
	}

	private boolean asciidoctorJ16() {
		try {
			return Class.forName("org.asciidoctor.extension.JavaExtensionRegistry").isInterface();
		}
		catch (Throwable ex) {
			return false;
		}
	}

}
