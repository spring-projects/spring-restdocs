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

import java.util.List;

/**
 * A strategy used to determine the ordering of cURL parts, and how lines should
 * be broken between those parts.
 *
 * @author Paul Samsotha
 * @since 1.1.1
 * @see CurlLineBreakStrategies
 */
public interface CurlLineBreakStrategy {

	/**
	 * A list of {@link CurlLineGroup}s that will determine the ordering of each group.
	 *
	 * @return return the list of line groups.
	 */
	List<CurlLineGroup> getLinesGroups();

	/**
	 * Returns whether or not individual headers should be separately split in their
	 * own line.
	 *
	 * @return true if the individual headers should have their own line, false if not.
	 */
	boolean splitHeaders();

	/**
	 * Returns whether or not individual multiparts should be separately split in
	 * their own line.
	 *
	 * @return true if the individual parts should have their own line, false if not.
	 */
	boolean splitMultiParts();
}
