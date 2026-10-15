/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.templates.mustache;

import java.io.IOException;
import java.io.Writer;

import com.samskivert.mustache.Mustache.Lambda;
import com.samskivert.mustache.Template.Fragment;

/**
 * A {@link Lambda} that escapes {@code |} characters so that the do not break the table's
 * formatting.
 *
 * @author Andy Wilkinson
 * @since 4.0.0
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
