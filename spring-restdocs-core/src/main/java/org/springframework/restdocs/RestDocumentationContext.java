/*
 * Copyright 2014-2017 the original author or authors.
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
	 *
	 * @return The test class
	 */
	Class<?> getTestClass();

	/**
	 * Returns the name of the test method that is currently executing.
	 *
	 * @return The name of the test method
	 */
	String getTestMethodName();

	/**
	 * Returns the current step count.
	 *
	 * @return The current step count
	 */
	int getStepCount();

	/**
	 * Returns the output directory to which generated snippets should be written.
	 *
	 * @return the output directory
	 */
	File getOutputDirectory();

}
