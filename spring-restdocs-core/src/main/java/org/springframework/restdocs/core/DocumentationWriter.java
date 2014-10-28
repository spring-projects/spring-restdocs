/*
 * Copyright 2014 the original author or authors.
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

package org.springframework.restdocs.core;

import java.io.OutputStream;
import java.io.PrintWriter;

public class DocumentationWriter extends PrintWriter {

	private boolean escapeNewline = false;

	public DocumentationWriter(OutputStream stream) {
		super(stream, true);
	}

	public void shellCommand(final DocumentationAction... actions) throws Exception {
		codeBlock("bash", new DocumentationAction() {

			@Override
			public void perform() throws Exception {
				DocumentationWriter.this.print("$ ");
				DocumentationWriter.this.escapeNewline = true;
				try {
					for (DocumentationAction action : actions) {
						action.perform();
					}
				}
				finally {
					DocumentationWriter.this.escapeNewline = false;
				}
			}
		});
	}

	@Override
	public void write(String s) {
		if (this.escapeNewline) {
			s = s.replace("\n", "\\\n");
		}
		super.write(s);
	}

	public void codeBlock(String language, DocumentationAction... actions) throws Exception {
		println();
		if (language != null) {
			println("[source," + language + "]");
		}
		println("----");
		for (DocumentationAction action : actions) {
			action.perform();
		}
		println("----");
		println();
	}

	public interface DocumentationAction {
		void perform() throws Exception;
	}

}
