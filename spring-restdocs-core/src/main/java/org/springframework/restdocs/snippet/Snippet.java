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

import java.io.IOException;

import org.springframework.restdocs.operation.Operation;

/**
 * A {@link Snippet} is used to document aspects of a call to a RESTful API.
 *
 * @author Andy Wilkinson
 */
public interface Snippet {

	/**
	 * Documents the call to the RESTful API described by the given {@code operation}.
	 *
	 * @param operation the API operation
	 * @throws IOException if a failure occurs will documenting the operation
	 */
	void document(Operation operation) throws IOException;

}
