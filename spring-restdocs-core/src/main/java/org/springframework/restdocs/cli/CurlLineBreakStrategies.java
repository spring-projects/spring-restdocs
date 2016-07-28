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

import java.util.Arrays;
import java.util.List;

/**
 * Static factory methods to create {@link CurlLineBreakStrategy}s, which determine
 * how line breaking should occur in the cURL snippet.
 *
 * @author Paul Samsotha
 * @since 1.1.1
 */
public final class CurlLineBreakStrategies {

	private CurlLineBreakStrategies() {

	}

	/**
	 * A factory method that returns the default line-breaking strategy. No new lines
	 * will be appended. Everything will be on one line. Using this strategy, you will
	 * content like:
	 *
	 * <pre>
	 * $ curl 'http://localhost/foo' -i -X POST -H 'X-Header-One: ONE' -d 'Some Content'
	 * </pre>
	 *
	 * @return the default line break strategy.
	 */
	public static CurlLineBreakStrategy none() {
		return new DefaultCurlLineBreakStrategy();
	}

	/**
	 * A factory method that returns a line-breaking strategy that only adds line
	 * breaks for headers. Using this strategy, you should see a snippet like:
	 *
	 * <pre>
	 *  $ curl 'http://localhost/foo' -i -X POST  -d 'Some Content' \
	 *  -H 'X-Header-One: ONE' \
	 *  -H 'X-Header-Two: TWO' \
	 *  -H 'X-Header-Three: THREE'
	 * </pre>
	 *
	 * @return the line-break strategy that adds line breaks for each header.
	 */
	public static CurlLineBreakStrategy headersOnly() {
		return new HeadersOnlyCurlLineBreakStrategy();
	}

	/**
	 * A factory method that returns a line-breaking strategy that only adds line
	 * breaks for multiparts. Using this strategy, you will see a snippet like:
	 *
	 * <pre>
	 * $ curl 'http://localhost/foo' -i -X POST -H 'X-Header-One: ONE' \
	 *  -F 'field1=Field1Data' \
	 *  -F 'field2=Field2Data'
	 * </pre>
	 *
	 * @return the line-break strategy that adds line breaks for each multipart.
	 */
	public static CurlLineBreakStrategy partsOnly() {
		return new PartsOnlyCurlLineBreakStrategy();
	}

	/**
	 * A factory method that returns a line-breaking strategy that adds line breaks
	 * for both headers and multiparts. Using this strategy, you will see a snippet like:
	 *
	 * <pre>
	 * $ curl 'http://localhost/foo' -i -X POST \
	 *  -H 'X-Header-One: ONE' \
	 *  -H 'X-Header-Two: TWO' \
	 *  -H 'X-Header-Three: THREE' \
	 *  -F 'field1=Field1Data' \
	 *  -F 'field2=Field2Data'
	 * </pre>
	 *
	 * @return the line-break strategy that breaks for headers and parts.
	 */
	public static CurlLineBreakStrategy headersAndParts() {
		return new HeadersAndPartsCurlLineBreakStrategy();
	}

	/**
	 * A factory method that returns a line-breaking strategy that puts only the
	 * content on its own line. Using this strategy, you will see a snippet like:
	 *
	 * <pre>
	 * $ curl 'http://localhost/foo' -i -X POST -H 'X-Header-One: ONE' \
	 *  -d 'a=aplha&amp;b=bravo'
	 * </pre>
	 *
	 * @return the line-break strategy that puts only the content on its own line.
	 */
	public static CurlLineBreakStrategy contentOnly() {
		return new ContentOnlyPartsCurlLineBreakStrategy();
	}

	/**
	 * A factory method that return a line-breaking strategy that adds line breaks
	 * for headers and content. The content will come after the headers. Using this
	 * strategy, you will see a snippet like
	 *
	 * <pre>
	 * $ curl 'http://localhost/foo' -i -X POST \
	 *  -H 'X-Header-One: ONE' \
	 *  -H 'X-Header-Two: TWO' \
	 *  -H 'X-Header-Three: THREE' \
	 *  -d 'Some Content'
	 * </pre>
	 *
	 * @return the line-break strategy that puts headers and content on their own line.
	 */
	public static CurlLineBreakStrategy headersAndContent() {
		return new HeadersAndContentCurlLineBreakStrategy();
	}

	private static final class DefaultCurlLineBreakStrategy
			implements CurlLineBreakStrategy {

		@Override
		public List<CurlLineGroup> getLineGroups() {
			return Arrays.asList(new CurlLineGroup(CurlPart.SHOW_HEADER_OPTION,
					CurlPart.USER_OPTION, CurlPart.HTTP_METHOD,
					CurlPart.HEADERS, CurlPart.MULTIPARTS, CurlPart.CONTENT));
		}

		@Override
		public boolean splitHeaders() {
			return false;
		}

		@Override
		public boolean splitMultiParts() {
			return false;
		}
	}

	private static final class HeadersOnlyCurlLineBreakStrategy
			implements CurlLineBreakStrategy {

		@Override
		public List<CurlLineGroup> getLineGroups() {
			CurlLineGroup allButHeaders = new CurlLineGroup(CurlPart.SHOW_HEADER_OPTION,
					CurlPart.USER_OPTION, CurlPart.HTTP_METHOD,
					CurlPart.MULTIPARTS, CurlPart.CONTENT);
			CurlLineGroup headers = new CurlLineGroup(CurlPart.HEADERS);
			return Arrays.asList(allButHeaders, headers);
		}

		@Override
		public boolean splitHeaders() {
			return true;
		}

		@Override
		public boolean splitMultiParts() {
			return false;
		}
	}

	private static final class PartsOnlyCurlLineBreakStrategy
			implements CurlLineBreakStrategy {

		@Override
		public List<CurlLineGroup> getLineGroups() {
			CurlLineGroup allButParts = new CurlLineGroup(CurlPart.SHOW_HEADER_OPTION,
					CurlPart.USER_OPTION, CurlPart.HTTP_METHOD,
					CurlPart.HEADERS, CurlPart.CONTENT);
			CurlLineGroup multiparts = new CurlLineGroup(CurlPart.MULTIPARTS);
			return Arrays.asList(allButParts, multiparts);
		}

		@Override
		public boolean splitHeaders() {
			return false;
		}

		@Override
		public boolean splitMultiParts() {
			return true;
		}
	}

	private static final class HeadersAndPartsCurlLineBreakStrategy
			implements CurlLineBreakStrategy {

		@Override
		public List<CurlLineGroup> getLineGroups() {
			CurlLineGroup optionsMethodContent = new CurlLineGroup(CurlPart.SHOW_HEADER_OPTION,
					CurlPart.USER_OPTION, CurlPart.HTTP_METHOD,
					CurlPart.CONTENT);
			CurlLineGroup headers = new CurlLineGroup(CurlPart.HEADERS);
			CurlLineGroup multiparts = new CurlLineGroup(CurlPart.MULTIPARTS);
			return Arrays.asList(optionsMethodContent, headers, multiparts);
		}

		@Override
		public boolean splitHeaders() {
			return true;
		}

		@Override
		public boolean splitMultiParts() {
			return true;
		}
	}

	private static final class HeadersAndContentCurlLineBreakStrategy
			implements CurlLineBreakStrategy {

		@Override
		public List<CurlLineGroup> getLineGroups() {
			CurlLineGroup optionsAndMethod = new CurlLineGroup(CurlPart.SHOW_HEADER_OPTION,
					CurlPart.USER_OPTION, CurlPart.HTTP_METHOD,
					CurlPart.MULTIPARTS);
			CurlLineGroup headers = new CurlLineGroup(CurlPart.HEADERS);
			CurlLineGroup content = new CurlLineGroup(CurlPart.CONTENT);
			return Arrays.asList(optionsAndMethod, headers, content);
		}

		@Override
		public boolean splitHeaders() {
			return true;
		}

		@Override
		public boolean splitMultiParts() {
			return false;
		}
	}

	private static final class ContentOnlyPartsCurlLineBreakStrategy
			implements CurlLineBreakStrategy {

		@Override
		public List<CurlLineGroup> getLineGroups() {
			CurlLineGroup allButContent = new CurlLineGroup(CurlPart.SHOW_HEADER_OPTION,
					CurlPart.USER_OPTION, CurlPart.HTTP_METHOD,
					CurlPart.HEADERS, CurlPart.MULTIPARTS);
			CurlLineGroup content = new CurlLineGroup(CurlPart.CONTENT);
			return Arrays.asList(allButContent, content);
		}

		@Override
		public boolean splitHeaders() {
			return false;
		}

		@Override
		public boolean splitMultiParts() {
			return false;
		}
	}
}
