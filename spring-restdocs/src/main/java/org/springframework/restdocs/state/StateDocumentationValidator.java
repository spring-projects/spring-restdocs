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

package org.springframework.restdocs.state;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.restdocs.state.FieldSnippetResultHandler.Type;

/**
 * Validator which verifies if fields are documented correctly. All fields need to be
 * documented except nested fields. For those fields it is sufficient that only the parent
 * is documented. In case there are fields documented that don't appear in the actual
 * request or response, the validation will fail.
 * 
 * @author Andreas Evers
 */
public class StateDocumentationValidator {

	private final Type type;

	public StateDocumentationValidator(Type type) {
		this.type = type;
	}

	public void validateFields(SortedSet<Path> actualFields,
			SortedSet<Path> expectedFields) {
		Set<Path> undocumentedFields = new HashSet<Path>(actualFields);
		Set<Path> ignoredFields = new HashSet<Path>();
		undocumentedFields.removeAll(expectedFields);
		for (Path path : undocumentedFields) {
			if (path.getSteps().size() > 1) {
				Path wrappingPath = new Path(path);
				wrappingPath.getSteps().remove(wrappingPath.getSteps().size() - 1);
				if (actualFields.contains(wrappingPath)) {
					ignoredFields.add(path);
				}
			}
		}
		undocumentedFields.removeAll(ignoredFields);

		Set<Path> missingFields = new HashSet<Path>(expectedFields);
		missingFields.removeAll(actualFields);

		if (!undocumentedFields.isEmpty() || !missingFields.isEmpty()) {
			String message = "";
			if (!undocumentedFields.isEmpty()) {
				message += "Fields with the following paths were not documented: "
						+ undocumentedFields;
			}
			if (!missingFields.isEmpty()) {
				message += "Fields with the following paths were not found in the "
						+ this.type.toString().toLowerCase() + ": " + missingFields;
			}
			fail(message);
		}
	}
}
