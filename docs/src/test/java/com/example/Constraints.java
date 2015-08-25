package com.example;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.restdocs.constraints.ConstraintDescriptions;

public class Constraints {

	@SuppressWarnings("unused")
	// tag::constraints[]
	public void example() {
		ConstraintDescriptions userConstraints = new ConstraintDescriptions(UserInput.class); // <1>
		List<String> descriptions = userConstraints.descriptionsForProperty("name"); // <2>
	}
	
	static class UserInput {
		
		@NotNull
		@Size(min = 1)
		String name;
		
		@NotNull
		@Size(min = 8)
		String password;
	}
	// end::constraints[]
	
}
