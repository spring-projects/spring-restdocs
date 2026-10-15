/*
 * Copyright 2014-present the original author or authors.
 */

package org.springframework.restdocs.config;

import java.util.Map;

import org.jspecify.annotations.Nullable;

import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

/**
 * A configurer that can be used to configure the default operation preprocessors.
 *
 * @param <PARENT> the type of the configurer's parent
 * @param <TYPE> the concrete type of the configurer to be returned from chained methods
 * @author Filip Hrisafov
 * @author Andy Wilkinson
 * @since 2.0.0
 */
public abstract class OperationPreprocessorsConfigurer<PARENT, TYPE> extends AbstractNestedConfigurer<PARENT> {

	private @Nullable OperationRequestPreprocessor defaultOperationRequestPreprocessor;

	private @Nullable OperationResponsePreprocessor defaultOperationResponsePreprocessor;

	/**
	 * Creates a new {@code OperationPreprocessorConfigurer} with the given
	 * {@code parent}.
	 * @param parent the parent
	 */
	protected OperationPreprocessorsConfigurer(PARENT parent) {
		super(parent);
	}

	@Override
	public void apply(Map<String, Object> configuration, RestDocumentationContext context) {
		configuration.put(RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_OPERATION_REQUEST_PREPROCESSOR,
				this.defaultOperationRequestPreprocessor);
		configuration.put(RestDocumentationGenerator.ATTRIBUTE_NAME_DEFAULT_OPERATION_RESPONSE_PREPROCESSOR,
				this.defaultOperationResponsePreprocessor);
	}

	/**
	 * Configures the default operation request preprocessors.
	 * @param preprocessors the preprocessors
	 * @return {@code this}
	 */
	@SuppressWarnings("unchecked")
	public TYPE withRequestDefaults(OperationPreprocessor... preprocessors) {
		this.defaultOperationRequestPreprocessor = Preprocessors.preprocessRequest(preprocessors);
		return (TYPE) this;
	}

	/**
	 * Configures the default operation response preprocessors.
	 * @param preprocessors the preprocessors
	 * @return {@code this}
	 */
	@SuppressWarnings("unchecked")
	public TYPE withResponseDefaults(OperationPreprocessor... preprocessors) {
		this.defaultOperationResponsePreprocessor = Preprocessors.preprocessResponse(preprocessors);
		return (TYPE) this;
	}

}
