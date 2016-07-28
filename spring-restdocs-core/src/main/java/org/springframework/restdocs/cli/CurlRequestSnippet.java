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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpMethod;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.util.StringUtils;

/**
 * A {@link Snippet} that documents the curl command for a request.
 *
 * @author Andy Wilkinson
 * @author Paul-Christian Volkmer
 * @author Paul Samsotha
 * @since 1.1.0
 * @see CliDocumentation#curlRequest()
 * @see CliDocumentation#curlRequest(Map)
 * @see CurlLineBreakStrategy
 */
public class CurlRequestSnippet extends TemplatedSnippet {

	/**
	 * Creates a new {@code CurlRequestSnippet} with no additional attributes.
	 */
	protected CurlRequestSnippet() {
		this(null);
	}

	/**
	 * Creates a new {@code CurlRequestSnippet} with the given additional
	 * {@code attributes} that will be included in the model during template rendering.
	 *
	 * @param attributes The additional attributes
	 */
	protected CurlRequestSnippet(Map<String, Object> attributes) {
		super("curl-request", attributes);
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		Map<String, Object> model = new HashMap<>();
		model.put("url", getUrl(operation));
		model.put("options", getOptions(operation));
		return model;
	}

	private String getUrl(Operation operation) {
		OperationRequest request = operation.getRequest();
		if (!request.getParameters().isEmpty() && includeParametersInUri(request)) {
			return String.format("'%s?%s'", request.getUri(),
					request.getParameters().toQueryString());
		}
		return String.format("'%s'", request.getUri());
	}

	private boolean includeParametersInUri(OperationRequest request) {
		return request.getMethod() == HttpMethod.GET || request.getContent().length > 0;
	}

	private String getOptions(Operation operation) {
		StringWriter command = new StringWriter();
		PrintWriter printer = new PrintWriter(command);

		CurlLineBreakStrategy lineBreak;
		Object attribute = operation.getAttributes().get(CurlLineBreakStrategy.class.getName());
		if (attribute == null || !(attribute instanceof CurlLineBreakStrategy)) {
			lineBreak = CurlLineBreakStrategies.none();
		}
		else {
			lineBreak = (CurlLineBreakStrategy) operation.getAttributes()
					.get(CurlLineBreakStrategy.class.getName());
		}
		CliOperationRequest request = new CliOperationRequest(operation.getRequest());

		List<WriteAction> actions = resolveWriteActions(lineBreak, request);
		for (WriteAction action : actions) {
			action.write(request, printer);
		}

		return command.toString();
	}

	/**
	 * Resolves a list of write actions, based on the line break strategy.
	 *
	 * @param lineBreaks the line break strategy.
	 * @return the list of write actions.
	 */
	private List<WriteAction> resolveWriteActions(CurlLineBreakStrategy lineBreaks,
			CliOperationRequest request) {
		List<WriteAction> writeActions = new ArrayList<>();
		Map<CurlPart, WriteAction> map = getWriteActionMap();

		Iterator<CurlLineGroup> it = lineBreaks.getLineGroups().iterator();
		while (it.hasNext()) {
			CurlLineGroup lineGroup = it.next();
			boolean groupHasContent = false;
			for (CurlPart curlPart : lineGroup.getParts()) {
				WriteAction action = map.get(curlPart);
				if (action.hasContent(request)) {
					groupHasContent = true;
				}
				setWriteActionState(action, lineBreaks);
				writeActions.add(action);
			}
			if (it.hasNext() && groupHasContent) {
				writeActions.add(new LineBreakWriteAction());
			}
		}

		return writeActions;
	}

	private void setWriteActionState(WriteAction action, CurlLineBreakStrategy lineBreaks) {
		if (action instanceof HeadersWriteAction) {
			((HeadersWriteAction) action).setSplitHeader(lineBreaks.splitHeaders());
		}
		else if (action instanceof PartsWriteAction) {
			((PartsWriteAction) action).setSplitParts(lineBreaks.splitMultiParts());
		}
	}

	private Map<CurlPart, WriteAction> getWriteActionMap() {
		Map<CurlPart, WriteAction> map = new HashMap<>();
		map.put(CurlPart.SHOW_HEADER_OPTION, new ShowHeadersOptionWriteAction());
		map.put(CurlPart.USER_OPTION, new UserOptionWriteAction());
		map.put(CurlPart.HTTP_METHOD, new HttpMethodWriteAction());
		map.put(CurlPart.HEADERS, new HeadersWriteAction());
		map.put(CurlPart.MULTIPARTS, new PartsWriteAction());
		map.put(CurlPart.CONTENT, new ContentWriteAction());
		return map;
	}

	/**
	 * A write action that should performed.
	 */
	private interface WriteAction {

		/**
		 * Writes the content to the writer.
		 *
		 * @param request the operation request.
		 * @param writer the writer to write the data to.
		 */
		void write(CliOperationRequest request, PrintWriter writer);

		/**
		 * Check if the action produces any content. This is used to check whether
		 * a new line should be appended. If there is content, a new line should
		 * be appended after its group.
		 *
		 * @return true if the action produces any content, otherwise false.
		 */
		boolean hasContent(CliOperationRequest request);
	}

	/**
	 * Abstract write action that defaults to returning true for whether the
	 * writer produces any content.
	 */
	private abstract class AbstractWriteAction implements WriteAction {

		@Override
		public boolean hasContent(CliOperationRequest request) {
			return true;
		}
	}

	/**
	 * A write action to write a line break with a {@code /} character.
	 */
	private final class LineBreakWriteAction extends AbstractWriteAction {

		@Override
		public void write(CliOperationRequest request, PrintWriter writer) {
			writer.println(" \\");
		}
	}

	/**
	 * A write action to write the {@code -i} switch to show headers.
	 */
	private final class ShowHeadersOptionWriteAction extends AbstractWriteAction {

		@Override
		public void write(CliOperationRequest request, PrintWriter writer) {
			writer.print("-i");
		}
	}

	/**
	 * A write action to write the user authentication.
	 */
	private final class UserOptionWriteAction extends AbstractWriteAction {

		@Override
		public void write(CliOperationRequest request, PrintWriter writer) {
			String credentials = request.getBasicAuthCredentials();
			if (credentials != null) {
				writer.print(String.format(" -u '%s'", credentials));
			}
		}

		@Override
		public boolean hasContent(CliOperationRequest request) {
			return request.getBasicAuthCredentials() != null;
		}
	}

	/**
	 * A write action that writes the HTTP method.
	 */
	private final class HttpMethodWriteAction extends AbstractWriteAction {

		@Override
		public void write(CliOperationRequest request, PrintWriter writer) {
			if (!HttpMethod.GET.equals(request.getMethod())) {
				writer.print(String.format(" -X %s", request.getMethod()));
			}
		}

		@Override
		public boolean hasContent(CliOperationRequest request) {
			return !HttpMethod.GET.equals(request.getMethod());
		}
	}

	/**
	 * A write action that writes the request headers.
	 */
	private final class HeadersWriteAction extends AbstractWriteAction {

		private boolean splitHeaders;

		void setSplitHeader(boolean splitHeaders) {
			this.splitHeaders = splitHeaders;
		}

		@Override
		public void write(CliOperationRequest request, PrintWriter writer) {
			Iterator<Entry<String, List<String>>> it = request.getHeaders().entrySet().iterator();
			Entry<String, List<String>> entry;
			while (it.hasNext()) {
				entry = it.next();
				Iterator<String> headerIt = entry.getValue().iterator();
				String header;
				while (headerIt.hasNext()) {
					header = headerIt.next();
					writer.print(String.format(" -H '%s: %s'", entry.getKey(), header));
					if (this.splitHeaders && (it.hasNext() || headerIt.hasNext())) {
						writer.println(" \\");
					}
				}
			}
		}

		@Override
		public boolean hasContent(CliOperationRequest request) {
			return !request.getHeaders().isEmpty();
		}
	}

	/**
	 * A write action that writes the multipart parts.
	 */
	private final class PartsWriteAction extends AbstractWriteAction {

		private boolean splitParts;

		void setSplitParts(boolean splitParts) {
			this.splitParts = splitParts;
		}

		@Override
		public void write(CliOperationRequest request, PrintWriter writer) {
			Iterator<OperationRequestPart> it = request.getParts().iterator();
			while (it.hasNext()) {
				OperationRequestPart part = it.next();
				writer.printf(" -F '%s=", part.getName());
				if (!StringUtils.hasText(part.getSubmittedFileName())) {
					writer.append(part.getContentAsString());
				}
				else {
					writer.printf("@%s", part.getSubmittedFileName());
				}
				if (part.getHeaders().getContentType() != null) {
					writer.append(";type=")
							.append(part.getHeaders().getContentType().toString());
				}

				writer.append("'");
				if (this.splitParts && it.hasNext()) {
					writer.println(" \\");
				}
			}
		}

		@Override
		public boolean hasContent(CliOperationRequest request) {
			return !request.getParts().isEmpty();
		}
	}

	/**
	 * A write action that writes the request entity content.
	 */
	private final class ContentWriteAction extends AbstractWriteAction {

		@Override
		public void write(CliOperationRequest request, PrintWriter writer) {
			String content = request.getContentAsString();
			if (StringUtils.hasText(content)) {
				writer.print(String.format(" -d '%s'", content));
			}
			else if (!request.getParts().isEmpty()) {
				for (Entry<String, List<String>> entry : request.getParameters().entrySet()) {
					for (String value : entry.getValue()) {
						writer.print(String.format(" -F '%s=%s'", entry.getKey(), value));
					}
				}
			}
			else if (request.isPutOrPost()) {
				writeContentUsingParameters(request, writer);
			}
		}

		@Override
		public boolean hasContent(CliOperationRequest request) {
			String content = request.getContentAsString();
			return StringUtils.hasText(content)
					|| !request.getParts().isEmpty()
					|| request.isPutOrPost();
		}

		private void writeContentUsingParameters(CliOperationRequest request, PrintWriter writer) {
			Parameters uniqueParameters = request.getUniqueParameters();
			String queryString = uniqueParameters.toQueryString();
			if (StringUtils.hasText(queryString)) {
				writer.print(String.format(" -d '%s'", queryString));
			}
		}
	}
}
