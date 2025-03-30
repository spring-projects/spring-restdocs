/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.constraints;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A constraint.
 *
 * @author Andy Wilkinson
 */
public class Constraint {

	private final String name;

	private final Map<String, Object> configuration;

	private final Set<Class<?>> groups;

	/**
	 * Creates a new {@code Constraint} with the given {@code name} and
	 * {@code configuration}.
	 * @param name the name
	 * @param configuration the configuration
	 */
	public Constraint(String name, Map<String, Object> configuration) {
		this.name = name;
		this.configuration = configuration;
		this.groups = Collections.emptySet();
	}

	/**
	 * Creates a new {@code Constraint} with the given {@code name} and
	 * {@code configuration}.
	 * @param name the name
	 * @param configuration the configuration
	 * @param groups the groups
	 */
	public Constraint(String name, Map<String, Object> configuration, Set<Class<?>> groups) {
		this.name = name;
		this.configuration = configuration;
		this.groups = groups;
	}

	/**
	 * Returns the name of the constraint.
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the configuration of the constraint.
	 * @return the configuration
	 */
	public Map<String, Object> getConfiguration() {
		return this.configuration;
	}

	/**
	 * Returns the groups of the constraint.
	 * @return the groups
	 */
	public Set<Class<?>> getGroups() {
		return this.groups;
	}

}
