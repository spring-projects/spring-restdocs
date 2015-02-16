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

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Static factory methods for documenting RESTful APIs using Spring MVC Test
 * 
 * @author Andy Wilkinson
 */
public abstract class RestDocumentation {

	private RestDocumentation() {

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

}
