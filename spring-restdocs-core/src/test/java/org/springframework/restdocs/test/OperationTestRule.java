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

package org.springframework.restdocs.test;

import java.io.File;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Abstract base class for Operation-related {@link TestRule TestRules}.
 *
 * @author Andy Wilkinson
 */
abstract class OperationTestRule implements TestRule {

	@Override
	public final Statement apply(Statement base, Description description) {
		return apply(base, determineOutputDirectory(description),
				determineOperationName(description));
	}

	private File determineOutputDirectory(Description description) {
		return new File("build/" + description.getTestClass().getSimpleName());
	}

	private String determineOperationName(Description description) {
		String operationName = description.getMethodName();
		int index = operationName.indexOf('[');
		if (index > 0) {
			operationName = operationName.substring(0, index);
		}
		return operationName;
	}

	protected abstract Statement apply(Statement base, File outputDirectory,
			String operationName);

}
