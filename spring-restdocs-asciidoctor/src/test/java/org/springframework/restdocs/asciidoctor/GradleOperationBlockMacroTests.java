/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.asciidoctor;

import java.io.File;

import org.asciidoctor.Attributes;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for Ruby operation block macro when used in a Gradle build.
 *
 * @author Andy Wilkinson
 */
@ParameterizedClass
@ValueSource(strings = { "projectdir", "gradle-projectdir" })
class GradleOperationBlockMacroTests extends AbstractOperationBlockMacroTests {

	private final String attributeName;

	GradleOperationBlockMacroTests(String attributeName) {
		this.attributeName = attributeName;
	}

	@Override
	protected Attributes getAttributes() {
		Attributes attributes = Attributes.builder()
			.attribute(this.attributeName, new File(this.temp, "gradle-project").getAbsolutePath())
			.build();
		return attributes;
	}

	@Override
	protected File getBuildOutputLocation() {
		File outputLocation = new File(this.temp, "gradle-project/build");
		outputLocation.mkdirs();
		return outputLocation;
	}

	@Override
	protected File getSourceLocation() {
		File sourceLocation = new File(this.temp, "gradle-project/src/docs/asciidoc");
		if (!sourceLocation.exists()) {
			sourceLocation.mkdirs();
		}
		return sourceLocation;
	}

}
