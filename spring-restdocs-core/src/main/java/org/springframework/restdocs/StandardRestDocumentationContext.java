/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Standard implementation of {@link RestDocumentationContext}.
 *
 * @author Andy Wilkinson
 */
final class StandardRestDocumentationContext implements RestDocumentationContext {

	private final AtomicInteger stepCount = new AtomicInteger(0);

	private final Class<?> testClass;

	private final String testMethodName;

	private final File outputDirectory;

	StandardRestDocumentationContext(Class<?> testClass, String testMethodName, File outputDirectory) {
		this.testClass = testClass;
		this.testMethodName = testMethodName;
		this.outputDirectory = outputDirectory;
	}

	@Override
	public Class<?> getTestClass() {
		return this.testClass;
	}

	@Override
	public String getTestMethodName() {
		return this.testMethodName;
	}

	int getAndIncrementStepCount() {
		return this.stepCount.getAndIncrement();
	}

	@Override
	public int getStepCount() {
		return this.stepCount.get();
	}

	@Override
	public File getOutputDirectory() {
		return this.outputDirectory;
	}

}
