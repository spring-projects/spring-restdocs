/*
 * Copyright 2014-present the original author or authors.
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

package org.springframework.restdocs.templates.mustache;

import java.io.IOException;
import java.io.Writer;

import org.springframework.restdocs.mustache.Mustache.Lambda;
import org.springframework.restdocs.mustache.Template.Fragment;

/**
 * A {@link Lambda} that escapes {@code |} characters so that the do not break the table's
 * formatting.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public final class AsciidoctorTableCellContentLambda implements Lambda {

	@Override
	public void execute(Fragment fragment, Writer writer) throws IOException {
		String output = fragment.execute();
		for (int i = 0; i < output.length(); i++) {
			char current = output.charAt(i);
			if (current == '|' && (i == 0 || output.charAt(i - 1) != '\\')) {
				writer.append('\\');
			}
			writer.append(current);
		}
	}

}
