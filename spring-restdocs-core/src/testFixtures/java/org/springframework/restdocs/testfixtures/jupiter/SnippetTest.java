/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.testfixtures.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.restdocs.snippet.Snippet;

/**
 * Signals that a method is a test of a {@link Snippet}. Typically used to test scenarios
 * where a failure occurs before the snippet is rendered. To test snippet rendering, use
 * {@link RenderedSnippetTest}.
 * <p>
 * A snippet test method can inject the following types:
 * <ul>
 * <li>{@link OperationBuilder}</li>
 * </ul>
 *
 * @author Andy Wilkinson
 */
@Test
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SnippetTestExtension.class)
public @interface SnippetTest {

}
