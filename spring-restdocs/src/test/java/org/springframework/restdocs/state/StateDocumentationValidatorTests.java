package org.springframework.restdocs.state;

import static java.util.Arrays.asList;
import static org.springframework.restdocs.state.FieldSnippetResultHandler.Type.REQUEST;
import static org.springframework.restdocs.state.Path.path;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Test;

public class StateDocumentationValidatorTests {

	SortedSet<Path> actualFields = new TreeSet<Path>();

	SortedSet<Path> expectedFields = new TreeSet<Path>();

	StateDocumentationValidator validator = new StateDocumentationValidator(REQUEST);

	@After
	public void cleanup() {
		this.actualFields = new TreeSet<Path>();
		this.expectedFields = new TreeSet<Path>();
	}

	@Test
	public void equalFields() {
		this.actualFields = new TreeSet<Path>(asList(path("alpha"), path("bravo"),
				path("bravo", "marco"), path("bravo", "polo"), path("charlie"),
				path("charlie", "marco"), path("charlie", "marco", "alpha")));
		this.expectedFields = new TreeSet<Path>(asList(path("alpha"), path("bravo"),
				path("bravo", "marco"), path("bravo", "polo"), path("charlie"),
				path("charlie", "marco"), path("charlie", "marco", "alpha")));
		this.validator.validateFields(this.actualFields, this.expectedFields);
	}

	@Test(expected = AssertionError.class)
	public void sameLevelButMoreDocumented() {
		this.actualFields = new TreeSet<Path>(asList(path("alpha")));
		this.expectedFields = new TreeSet<Path>(asList(path("alpha"), path("bravo")));
		this.validator.validateFields(this.actualFields, this.expectedFields);
	}

	@Test(expected = AssertionError.class)
	public void sameLevelButMoreActuals() {
		this.actualFields = new TreeSet<Path>(asList(path("alpha"), path("bravo")));
		this.expectedFields = new TreeSet<Path>(asList(path("alpha")));
		this.validator.validateFields(this.actualFields, this.expectedFields);
	}

	@Test(expected = AssertionError.class)
	public void moreDocumentedButParentPresent() {
		this.actualFields = new TreeSet<Path>(asList(path("alpha"), path("bravo"),
				path("bravo", "marco"), path("bravo", "polo"), path("charlie"),
				path("charlie", "marco")));
		this.expectedFields = new TreeSet<Path>(asList(path("alpha"), path("bravo"),
				path("bravo", "marco"), path("bravo", "polo"), path("charlie"),
				path("charlie", "marco"), path("charlie", "marco", "alpha")));
		this.validator.validateFields(this.actualFields, this.expectedFields);
	}

	@Test
	public void moreActualsButParentPresent() {
		this.actualFields = new TreeSet<Path>(asList(path("alpha"), path("bravo"),
				path("bravo", "marco"), path("bravo", "polo"), path("charlie"),
				path("charlie", "marco"), path("charlie", "marco", "alpha")));
		this.expectedFields = new TreeSet<Path>(asList(path("alpha"), path("bravo"),
				path("bravo", "marco"), path("charlie"), path("charlie", "marco")));
		this.validator.validateFields(this.actualFields, this.expectedFields);
	}

	@Test
	public void documentationSkippedLevel() {
		this.actualFields = new TreeSet<Path>(asList(path("alpha"), path("bravo"),
				path("bravo", "marco"), path("bravo", "polo"), path("charlie"),
				path("charlie", "marco"), path("charlie", "marco", "alpha")));
		this.expectedFields = new TreeSet<Path>(asList(path("alpha"), path("bravo"),
				path("bravo", "marco"), path("bravo", "polo"), path("charlie"),
				path("charlie", "marco", "alpha")));
		this.validator.validateFields(this.actualFields, this.expectedFields);
	}

	@Test(expected = AssertionError.class)
	public void moreActualsWithoutParentPresent() {
		this.actualFields = new TreeSet<Path>(asList(path("alpha"), path("bravo"),
				path("bravo", "marco"), path("bravo", "polo"), path("charlie")));
		this.expectedFields = new TreeSet<Path>(asList(path("alpha"), path("bravo"),
				path("bravo", "marco"), path("bravo", "polo"), path("charlie"),
				path("charlie", "marco"), path("charlie", "marco", "alpha")));
		this.validator.validateFields(this.actualFields, this.expectedFields);
	}
}
