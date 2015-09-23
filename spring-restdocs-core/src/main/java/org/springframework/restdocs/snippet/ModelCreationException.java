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

package org.springframework.restdocs.snippet;

/**
 * An exception that can be thrown by a {@link TemplatedSnippet} to indicate that a
 * failure has occurred during model creation.
 *
 * @author Andy Wilkinson
 * @see TemplatedSnippet#createModel(org.springframework.restdocs.operation.Operation)
 */
@SuppressWarnings("serial")
public class ModelCreationException extends RuntimeException {

	/**
	 * Creates a new {@code ModelCreationException} with the given {@code cause}.
	 *
	 * @param cause the cause
	 */
	public ModelCreationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new {@code ModelCreationException} with the given {@code message} and
	 * {@code cause}.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public ModelCreationException(String message, Throwable cause) {
		super(message, cause);
	}

}
