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

import java.util.Map;

import org.springframework.restdocs.snippet.Snippet;

/**
 * Static factory methods for documenting a RESTful API as if it were being driven using a
 * command-line utility such as curl or HTTPie.
 *
 * @author Andy Wilkinson
 * @author Paul-Christian Volkmer
 * @author Raman Gupta
 * @author Tomasz Kopczynski
 * @since 1.1.0
 */
public abstract class CliDocumentation {

	static final CommandFormatter DEFAULT_COMMAND_FORMATTER = multiLineFormat();

	private CliDocumentation() {

	}

	/**
	 * Returns a new {@code Snippet} that will document the curl request for the API
	 * operation.
	 *
	 * @return the snippet that will document the curl request
	 */
	public static Snippet curlRequest() {
		return curlRequest(DEFAULT_COMMAND_FORMATTER);
	}

	/**
	 * Returns a new {@code Snippet} that will document the curl request for the API
	 * operation. The given {@code attributes} will be available during snippet
	 * generation.
	 *
	 * @param attributes the attributes
	 * @return the snippet that will document the curl request
	 */
	public static Snippet curlRequest(Map<String, Object> attributes) {
		return curlRequest(attributes, DEFAULT_COMMAND_FORMATTER);
	}

	/**
	 * Returns a new {@code Snippet} that will document the curl request for the API
	 * operation. The given {@code commandFormatter} will be used to format the curl
	 * command in the snippet.
	 *
	 * @param commandFormatter the command formatter
	 * @return the snippet that will document the curl request
	 * @since 1.2.0
	 */
	public static Snippet curlRequest(CommandFormatter commandFormatter) {
		return curlRequest(null, commandFormatter);
	}

	/**
	 * Returns a new {@code Snippet} that will document the curl request for the API
	 * operation. The given {@code attributes} will be available during snippet
	 * generation. The given {@code commandFormatter} will be used to format the curl
	 * command in the snippet.
	 *
	 * @param attributes the attributes
	 * @param commandFormatter the command formatter
	 * @return the snippet that will document the curl request
	 * @since 1.2.0
	 */
	public static Snippet curlRequest(Map<String, Object> attributes,
			CommandFormatter commandFormatter) {
		return new CurlRequestSnippet(attributes, commandFormatter);
	}

	/**
	 * Returns a new {@code Snippet} that will document the HTTPie request for the API
	 * operation.
	 *
	 * @return the snippet that will document the HTTPie request
	 */
	public static Snippet httpieRequest() {
		return httpieRequest(DEFAULT_COMMAND_FORMATTER);
	}

	/**
	 * Returns a new {@code Snippet} that will document the HTTPie request for the API
	 * operation. The given {@code attributes} will be available during snippet
	 * generation.
	 *
	 * @param attributes the attributes
	 * @return the snippet that will document the HTTPie request
	 */
	public static Snippet httpieRequest(Map<String, Object> attributes) {
		return httpieRequest(attributes, DEFAULT_COMMAND_FORMATTER);
	}

	/**
	 * Returns a new {@code Snippet} that will document the HTTPie request for the API
	 * operation. The given {@code commandFormatter} will be used to format the HTTPie
	 * command in the snippet.
	 *
	 * @param commandFormatter the command formatter
	 * @return the snippet that will document the HTTPie request
	 * @since 1.2.0
	 */
	public static Snippet httpieRequest(CommandFormatter commandFormatter) {
		return httpieRequest(null, commandFormatter);
	}

	/**
	 * Returns a new {@code Snippet} that will document the HTTPie request for the API
	 * operation. The given {@code attributes} will be available during snippet
	 * generation. The given {@code commandFormatter} will be used to format the HTTPie
	 * command in the snippet snippet.
	 *
	 * @param attributes the attributes
	 * @param commandFormatter the command formatter
	 * @return the snippet that will document the HTTPie request
	 * @since 1.2.0
	 */
	public static Snippet httpieRequest(Map<String, Object> attributes,
			CommandFormatter commandFormatter) {
		return new HttpieRequestSnippet(attributes, commandFormatter);
	}

	/**
	 * Creates a new {@code CommandFormatter} that produces multi-line output.
	 *
	 * @return A multi-line {@code CommandFormatter}
	 */
	public static CommandFormatter multiLineFormat() {
		return new ConcatenatingCommandFormatter(" \\%n    ");
	}

	/**
	 * Creates a new {@code CommandFormatter} that produces single-line output.
	 *
	 * @return A single-line {@code CommandFormatter}
	 */
	public static CommandFormatter singleLineFormat() {
		return new ConcatenatingCommandFormatter(" ");
	}

}
