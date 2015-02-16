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

import java.io.Writer;

/**
 * A {@link DocumentationWriter} that produces output in <a
 * href="http://asciidoctor.org">Asciidoctor</a>.
 * 
 * @author Andy Wilkinson
 */
public class AsciidoctorWriter extends DocumentationWriter {

	/**
	 * Creates a new {@code AsciidoctorWriter} that will write to the given {@code writer}
	 * @param writer The writer to which output will be written
	 */
	public AsciidoctorWriter(Writer writer) {
		super(writer);
	}

	@Override
	public void shellCommand(final DocumentationAction action) throws Exception {
		codeBlock("bash", new DocumentationAction() {

			@Override
			public void perform() throws Exception {
				AsciidoctorWriter.this.print("$ ");
				action.perform();
			}
		});
	}

	@Override
	public void codeBlock(String language, DocumentationAction action) throws Exception {
		println();
		if (language != null) {
			println("[source," + language + "]");
		}
		println("----");
		action.perform();
		println("----");
		println();
	}

}
