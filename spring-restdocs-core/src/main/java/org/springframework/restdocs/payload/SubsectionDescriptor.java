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

package org.springframework.restdocs.payload;

/**
 * A description of a subsection, i.e. a field and all of its descendants, in a request or
 * response payload.
 *
 * @author Andy Wilkinson
 * @since 1.2.0
 */
public class SubsectionDescriptor extends FieldDescriptor {

	/**
	 * Creates a new {@code SubsectionDescriptor} describing the subsection with the given
	 * {@code path}.
	 * @param path the path
	 */
	protected SubsectionDescriptor(String path) {
		super(path);
	}

}
