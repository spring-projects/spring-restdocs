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

import java.util.List;

/**
 * An abstraction for resolving a class's constraints.
 *
 * @author Andy Wilkinson
 */
public interface ConstraintResolver {

	/**
	 * Resolves and returns the constraints for the given {@code property} on the given
	 * {@code clazz}. If there are no constraints, an empty list is returned.
	 *
	 * @param property the property
	 * @param clazz the class
	 * @return the list of constraints, never {@code null}
	 */
	List<Constraint> resolveForProperty(String property, Class<?> clazz);

}
