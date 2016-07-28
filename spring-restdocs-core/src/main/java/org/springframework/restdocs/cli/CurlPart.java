/*
 * Copyright 2014-2016 the original author or authors.
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

package org.springframework.restdocs.cli;

/**
 * An enum representing the different type of cURL command line parts.
 *
 * @author Paul Samsotha
 * @since 1.1.1
 */
public enum CurlPart {

	/**
	 * {@code -u} in cURL (for authentication).
	 */
	USER_OPTION,

	/**
	 * {@code -i} in cURL (to show headers).
	 */
	SHOW_HEADER_OPTION,

	/**
	 * The HTTP Method.
	 */
	HTTP_METHOD,

	/**
	 * The headers.
	 */
	HEADERS, MULTIPARTS,

	/**
	 * The request content.
	 */
	CONTENT;
}
