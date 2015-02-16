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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * A {@link PrintWriter} that provides additional methods which are useful for producing
 * API documentation.
 * 
 * @author Andy Wilkinson
 */
public abstract class DocumentationWriter extends PrintWriter {

	protected DocumentationWriter(Writer writer) {
		super(writer, true);
	}

	/**
	 * Calls the given {@code action} to document a shell command. Any prefix necessary
	 * for the documentation format is written prior to calling the {@code action}. Having
	 * called the action, any necessary suffix is then written.
	 * 
	 * @param action the action that will produce the shell command
	 * @throws IOException if the documentation fails
	 */
	public abstract void shellCommand(DocumentationAction action) throws IOException;

	/**
	 * Calls the given {@code action} to document a code block. The code block will be
	 * annotated as containing code written in the given {@code language}. Any prefix
	 * necessary for the documentation format is written prior to calling the
	 * {@code action}. Having called the action, any necessary suffix is the written.
	 * 
	 * @param language the language in which the code is written
	 * @param action the action that will produce the code
	 * @throws IOException if the documentation fails
	 */
	public abstract void codeBlock(String language, DocumentationAction action)
			throws IOException;

	/**
	 * Encapsulates an action that outputs some documentation. Typically implemented as a
	 * lamda or, pre-Java 8, as an anonymous inner class.
	 * 
	 * @author Andy Wilkinson
	 * @see DocumentationWriter#shellCommand
	 * @see DocumentationWriter#codeBlock
	 */
	public interface DocumentationAction {

		/**
		 * Perform the encapsulated action
		 * 
		 * @throws IOException if the action fails
		 */
		void perform() throws IOException;
	}
}
