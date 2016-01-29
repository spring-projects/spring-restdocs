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

package org.springframework.restdocs.snippet;

import java.io.IOException;
import java.io.Writer;

import org.springframework.restdocs.RestDocumentationContext;

/**
 * A {@code WriterResolver} is used to access the {@link Writer} that should be used to
 * write a snippet for an operation that is being documented.
 *
 * @author Andy Wilkinson
 */
public interface WriterResolver {

	/**
	 * Returns a writer that can be used to write the snippet with the given name for the
	 * operation with the given name.
	 *
	 * @param operationName the name of the operation that is being documented
	 * @param snippetName the name of the snippet
	 * @param restDocumentationContext the current documentation context
	 * @return the writer
	 * @throws IOException if a writer cannot be resolved
	 */
	Writer resolve(String operationName, String snippetName,
			RestDocumentationContext restDocumentationContext) throws IOException;

	/**
	 * Configures the encoding that should be used by any writers produced by this
	 * resolver.
	 *
	 * @param encoding the encoding
	 * @deprecated since 1.1.0 in favour of configuring the encoding when to resolver is
	 * created
	 */
	@Deprecated
	void setEncoding(String encoding);

}
