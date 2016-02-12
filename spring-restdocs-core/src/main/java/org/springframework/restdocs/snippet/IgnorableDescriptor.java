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
 * Base class for descriptors for items that can be ignored.
 *
 * @param <T> the type of the descriptor
 * @author Andy Wilkinson
 */
public abstract class IgnorableDescriptor<T extends IgnorableDescriptor<T>>
		extends AbstractDescriptor<T> {

	private boolean ignored = false;

	/**
	 * Marks the described item as being ignored. Ignored items are not included in the
	 * generated documentation.
	 *
	 * @return the descriptor
	 */
	@SuppressWarnings("unchecked")
	public final T ignored() {
		this.ignored = true;
		return (T) this;
	}

	/**
	 * Returns whether or not the item being described should be ignored and, therefore,
	 * should not be included in the documentation.
	 *
	 * @return {@code true} if the item should be ignored, otherwise {@code false}.
	 */
	public final boolean isIgnored() {
		return this.ignored;
	}

}
