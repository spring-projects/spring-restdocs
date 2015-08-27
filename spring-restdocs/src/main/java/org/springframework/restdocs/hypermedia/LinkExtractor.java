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

package org.springframework.restdocs.hypermedia;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.operation.OperationResponse;

/**
 * A {@code LinkExtractor} is used to extract {@link Link links} from a JSON response. The
 * expected format of the links in the response is determined by the implementation.
 *
 * @author Andy Wilkinson
 *
 */
public interface LinkExtractor {

	/**
	 * Extract the links from the given {@code response}, returning a {@code Map} of links
	 * where the keys are the link rels.
	 *
	 * @param response The response from which the links are to be extracted
	 * @return The extracted links, keyed by rel
	 * @throws IOException if link extraction fails
	 */
	Map<String, List<Link>> extractLinks(OperationResponse response) throws IOException;

}
