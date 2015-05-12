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

package org.springframework.restdocs;

import org.springframework.restdocs.config.RestDocumentationConfigurer;
import org.springframework.restdocs.response.ResponsePostProcessor;
import org.springframework.restdocs.response.ResponsePostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;

/**
 * Static factory methods for documenting RESTful APIs using Spring MVC Test
 * 
 * @author Andy Wilkinson
 */
public abstract class RestDocumentation {

	private RestDocumentation() {

	}

	/**
	 * Provides access to a {@link MockMvcConfigurer} that can be used to configure the
	 * REST documentation when building a {@link MockMvc} instance.
	 * @see ConfigurableMockMvcBuilder#apply(MockMvcConfigurer)
	 * @return the configurer
	 */
	public static RestDocumentationConfigurer documentationConfiguration() {
		return new RestDocumentationConfigurer();
	}

	/**
	 * Documents the API call to the given {@code outputDir}.
	 * 
	 * @param outputDir The directory to which the documentation will be written
	 * @return a Mock MVC {@code ResultHandler} that will produce the documentation
	 * @see MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
	 * @see ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
	 */
	public static RestDocumentationResultHandler document(String outputDir) {
		return new RestDocumentationResultHandler(outputDir);
	}

	/**
	 * Enables the modification of the response in a {@link MvcResult} prior to it being
	 * documented. The modification is performed using the given
	 * {@code responsePostProcessors}.
	 * 
	 * @param responsePostProcessors the post-processors to use to modify the response
	 * @return the response modifier
	 * @see ResponsePostProcessors
	 */
	public static ResponseModifier modifyResponseTo(
			ResponsePostProcessor... responsePostProcessors) {
		return new ResponseModifier(responsePostProcessors);
	}

}
