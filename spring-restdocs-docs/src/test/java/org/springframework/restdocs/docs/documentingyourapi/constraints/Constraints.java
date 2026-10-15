/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.docs.documentingyourapi.constraints;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.restdocs.constraints.ConstraintDescriptions;

class Constraints {

	List<String> describeNameConstraints() {
		ConstraintDescriptions userConstraints = new ConstraintDescriptions(UserInput.class); // <1>
		return userConstraints.descriptionsForProperty("name"); // <2>
	}

	static class UserInput {

		@NotNull
		@Size(min = 1)
		String name;

		@NotNull
		@Size(min = 8)
		String password;

	}

}
