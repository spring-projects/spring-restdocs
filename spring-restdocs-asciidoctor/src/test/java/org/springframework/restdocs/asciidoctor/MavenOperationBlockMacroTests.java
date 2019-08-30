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
import java.io.IOException;

import org.asciidoctor.Attributes;
import org.junit.After;
import org.junit.Before;

/**
 * Tests for Ruby operation block macro when used in a Maven build.
 *
 * @author Andy Wilkinson
 */
public class MavenOperationBlockMacroTests extends AbstractOperationBlockMacroTests {

	@Before
	public void setMavenHome() {
		System.setProperty("maven.home", "maven-home");
	}

	@After
	public void clearMavenHome() {
		System.clearProperty("maven.home");
	}

	protected Attributes getAttributes() {
		try {
			File sourceLocation = getSourceLocation();
			new File(sourceLocation.getParentFile().getParentFile().getParentFile(), "pom.xml").createNewFile();
			Attributes attributes = new Attributes();
			attributes.setAttribute("docdir", sourceLocation.getAbsolutePath());
			return attributes;
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	protected File getBuildOutputLocation() {
		File outputLocation = new File(temp.getRoot(), "maven-project/target");
		outputLocation.mkdirs();
		return outputLocation;
	}

	@Override
	protected File getSourceLocation() {
		File sourceLocation = new File(temp.getRoot(), "maven-project/src/main/asciidoc");
		if (!sourceLocation.exists()) {
			sourceLocation.mkdirs();
		}
		return sourceLocation;
	}

}
