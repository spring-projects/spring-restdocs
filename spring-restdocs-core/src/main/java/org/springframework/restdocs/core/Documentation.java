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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.springframework.restdocs.core.DocumentationWriter.DocumentationAction;

public abstract class Documentation {

	public static void document(String path, DocumentationAction action) throws Exception {
		PrintStream printStream = createPrintStream(path);
		try {
			DocumentationContext.set(new DocumentationContext(printStream));
			action.perform();
		}
		finally {
			DocumentationContext.set(null);
			printStream.close();
		}
	}

	private static PrintStream createPrintStream(String name)
			throws FileNotFoundException {
		File outputFile = new File(name);
		if (!outputFile.isAbsolute()) {
			outputFile = makeAbsolute(outputFile);
		}
		outputFile.getParentFile().mkdirs();

		return new PrintStream(new FileOutputStream(outputFile));
	}

	private static File makeAbsolute(File outputFile) {
		return new File(new DocumentationProperties().getOutputDir(),
				outputFile.getPath());
	}
}
