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

package org.springframework.restdocs.response;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * A {@code ResponsePostProcessor} is used to modify the response received from a MockMvc
 * call prior to the response being documented.
 * 
 * @author Andy Wilkinson
 */
public interface ResponsePostProcessor {

	/**
	 * Post-processes the given {@code response}, returning a, possibly new,
	 * {@link MockHttpServletResponse} that should now be used.
	 * 
	 * @param response The response to post-process
	 * @return The result of the post-processing
	 * @throws Exception if a failure occurs during the post-processing
	 */
	MockHttpServletResponse postProcess(MockHttpServletResponse response)
			throws Exception;
}
