/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.constraints;

import java.util.Map;

/**
 * A constraint.
 *
 * @author Andy Wilkinson
 */
public class Constraint {

	private final String name;

	private final Map<String, Object> configuration;

	/**
	 * Creates a new {@code Constraint} with the given {@code name} and
	 * {@code configuration}.
	 * @param name the name
	 * @param configuration the configuration
	 */
	public Constraint(String name, Map<String, Object> configuration) {
		this.name = name;
		this.configuration = configuration;
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

}
