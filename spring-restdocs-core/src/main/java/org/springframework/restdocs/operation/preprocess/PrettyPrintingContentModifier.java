/*
 * Copyright 2014-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.operation.preprocess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.jspecify.annotations.Nullable;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.http.MediaType;

/**
 * A {@link ContentModifier} that modifies the content by pretty printing it.
 *
 * @author Andy Wilkinson
 */
public class PrettyPrintingContentModifier implements ContentModifier {

	private static final List<PrettyPrinter> PRETTY_PRINTERS = Collections
		.unmodifiableList(Arrays.asList(new JsonPrettyPrinter(), new XmlPrettyPrinter()));

	@Override
	public byte[] modifyContent(byte[] originalContent, @Nullable MediaType contentType) {
		if (originalContent.length > 0) {
			for (PrettyPrinter prettyPrinter : PRETTY_PRINTERS) {
				try {
					return prettyPrinter.prettyPrint(originalContent);
				}
				catch (Exception ex) {
					// Continue
				}
			}
		}
		return originalContent;
	}

	private interface PrettyPrinter {

		byte[] prettyPrint(byte[] content) throws Exception;

	}

	private static final class XmlPrettyPrinter implements PrettyPrinter {

		@Override
		public byte[] prettyPrint(byte[] original) throws Exception {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");

			try {
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			}
			catch (IllegalArgumentException ex) {
				// safely ignore if indent-amount is not supported
			}

			ByteArrayOutputStream transformed = new ByteArrayOutputStream();
			transformer.setErrorListener(new SilentErrorListener());
			transformer.transform(createSaxSource(original), new StreamResult(transformed));

			if (transformerFactory.getClass().getName().contains("saxon")) {
				byte[] rawResult = transformed.toByteArray();
				String resultString = new String(rawResult, StandardCharsets.UTF_8);
				resultString = normalizeToStandardFormat(resultString);
				return resultString.getBytes(StandardCharsets.UTF_8);
			}
			else {
				return transformed.toByteArray();
			}
		}

		/**
		 * Normalizes XML output to use 4-space indentation and CRLF line endings.
		 * Converts Saxon's 3-space format to match Xalan's 4-space format.
		 *
		 * @param xmlOutput the XML string to normalize
		 * @return normalized XML string
		 */
		private String normalizeToStandardFormat(String xmlOutput) {
			String normalized = xmlOutput.replace("\r\n", "\n").replace("\n", "\r\n");

			StringBuilder result = new StringBuilder();
			String[] lines = normalized.split("\r\n");

			for (String line : lines) {
				if (line.trim().isEmpty()) {
					result.append("\r\n");
					continue;
				}

				int spaces = 0;
				for (char c : line.toCharArray()) {
					if (c == ' ') {
						spaces++;
					}
					else {
						break;
					}
				}
				String content = line.trim();

				if (spaces > 0) {
					int level = (spaces + 1) / 3;
					String newIndent = "    ".repeat(level);
					result.append(newIndent).append(content).append("\r\n");
				}
				else {
					result.append(content).append("\r\n");
				}
			}

			return result.toString();
		}


		private SAXSource createSaxSource(byte[] original) throws ParserConfigurationException, SAXException {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			xmlReader.setErrorHandler(new SilentErrorHandler());
			return new SAXSource(xmlReader, new InputSource(new ByteArrayInputStream(original)));
		}

		private static final class SilentErrorListener implements ErrorListener {

			@Override
			public void warning(TransformerException exception) throws TransformerException {
				// Suppress
			}

			@Override
			public void error(TransformerException exception) throws TransformerException {
				// Suppress
			}

			@Override
			public void fatalError(TransformerException exception) throws TransformerException {
				// Suppress
			}

		}

		private static final class SilentErrorHandler implements ErrorHandler {

			@Override
			public void warning(SAXParseException exception) throws SAXException {
				// Suppress
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				// Suppress
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				// Suppress
			}

		}

	}

	private static final class JsonPrettyPrinter implements PrettyPrinter {

		private final ObjectMapper objectMapper = JsonMapper.builder()
			.enable(SerializationFeature.INDENT_OUTPUT)
			.build();

		@Override
		public byte[] prettyPrint(byte[] original) throws IOException {
			return this.objectMapper.writeValueAsBytes(this.objectMapper.readTree(original));
		}

	}

}
