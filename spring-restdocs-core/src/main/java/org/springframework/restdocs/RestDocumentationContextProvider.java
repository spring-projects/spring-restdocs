/*
 * Copyright 2012-2016 the original author or authors.
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

/**
 * A {@code RestDocumentationContextProvider} is used to provide access to the
 * {@link RestDocumentationContext}.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
public interface RestDocumentationContextProvider {

	/**
	 * Returns a {@link RestDocumentationContext} for the operation that is about to be
	 * performed.
	 *
	 * @return the context for the operation
	 */
	RestDocumentationContext beforeOperation();

}
