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

import java.io.File;

import org.asciidoctor.Attributes;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for Ruby operation block macro when used in a Gradle build.
 *
 * @author Andy Wilkinson
 */
@RunWith(Parameterized.class)
public class GradleOperationBlockMacroTests extends AbstractOperationBlockMacroTests {

	private final String attributeName;

	public GradleOperationBlockMacroTests(String attributeName) {
		this.attributeName = attributeName;
	}

	@Parameters(name = "{0}")
	public static Object[] parameters() {
		return new Object[] { "projectdir", "gradle-projectdir" };
	}

	protected Attributes getAttributes() {
		Attributes attributes = new Attributes();
		attributes.setAttribute(this.attributeName, new File(temp.getRoot(), "gradle-project").getAbsolutePath());
		return attributes;
	}

	@Override
	protected File getBuildOutputLocation() {
		File outputLocation = new File(temp.getRoot(), "gradle-project/build");
		outputLocation.mkdirs();
		return outputLocation;
	}

	@Override
	protected File getSourceLocation() {
		File sourceLocation = new File(temp.getRoot(), "gradle-project/src/docs/asciidoc");
		if (!sourceLocation.exists()) {
			sourceLocation.mkdirs();
		}
		return sourceLocation;
	}

}
