/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs;

import java.io.File;

/**
 * {@code RestDocumentationContext} encapsulates the context in which the documentation of
 * a RESTful API is being performed.
 *
 * @author Andy Wilkinson
 */
public interface RestDocumentationContext {

	/**
	 * Returns the class whose tests are currently executing.
	 * @return the test class
	 */
	Class<?> getTestClass();

	/**
	 * Returns the name of the test method that is currently executing.
	 * @return the name of the test method
	 */
	String getTestMethodName();

	/**
	 * Returns the current step count.
	 * @return the current step count
	 */
	int getStepCount();

	/**
	 * Returns the output directory to which generated snippets should be written.
	 * @return the output directory
	 */
	File getOutputDirectory();

}
