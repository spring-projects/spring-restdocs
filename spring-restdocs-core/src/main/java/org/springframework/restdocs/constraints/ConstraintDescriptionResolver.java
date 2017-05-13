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

package org.springframework.restdocs.constraints;

/**
 * Resolves a description for a {@link Constraint}.
 *
 * @author Andy Wilkinson
 *
 */
public interface ConstraintDescriptionResolver {

	/**
	 * Resolves the description for the given {@code constraint}.
	 *
	 * @param constraint the constraint
	 * @return the description or null if no description is available
	 */
	String resolveDescription(Constraint constraint);
}
