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

package org.springframework.restdocs.operation.preprocess;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
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

import org.springframework.http.MediaType;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * A {@link ContentModifier} that modifies the content by pretty printing it.
 *
 * @author Andy Wilkinson
 */
public class PrettyPrintingContentModifier implements ContentModifier {

	private static final List<PrettyPrinter> PRETTY_PRINTERS = Collections
			.unmodifiableList(Arrays.asList(new JsonPrettyPrinter(),
					new XmlPrettyPrinter()));

	@Override
	public byte[] modifyContent(byte[] originalContent, MediaType contentType) {
		if (originalContent.length > 0) {
			for (PrettyPrinter prettyPrinter : PRETTY_PRINTERS) {
				try {
					return prettyPrinter.prettyPrint(originalContent).getBytes();
				}
				catch (Exception ex) {
					// Continue
				}
			}
		}
		return originalContent;
	}

	private interface PrettyPrinter {

		String prettyPrint(byte[] content) throws Exception;

	}

	private static final class XmlPrettyPrinter implements PrettyPrinter {

		@Override
		public String prettyPrint(byte[] original) throws Exception {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
					"4");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
			StringWriter transformed = new StringWriter();
			transformer.setErrorListener(new SilentErrorListener());
			transformer.transform(createSaxSource(original),
					new StreamResult(transformed));
			return transformed.toString();
		}

		private SAXSource createSaxSource(byte[] original)
				throws ParserConfigurationException, SAXException {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			xmlReader.setErrorHandler(new SilentErrorHandler());
			SAXSource xmlInput = new SAXSource(xmlReader, new InputSource(
					new ByteArrayInputStream(original)));
			return xmlInput;
		}

		private static final class SilentErrorListener implements ErrorListener {

			@Override
			public void warning(TransformerException exception)
					throws TransformerException {

			}

			@Override
			public void error(TransformerException exception) throws TransformerException {

			}

			@Override
			public void fatalError(TransformerException exception)
					throws TransformerException {

			}

		}

		private static final class SilentErrorHandler implements ErrorHandler {

			@Override
			public void warning(SAXParseException exception) throws SAXException {

			}

			@Override
			public void error(SAXParseException exception) throws SAXException {

			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {

			}
		}
	}

	private static final class JsonPrettyPrinter implements PrettyPrinter {

		@Override
		public String prettyPrint(byte[] original) throws IOException {
			ObjectMapper objectMapper = new ObjectMapper().configure(
					SerializationFeature.INDENT_OUTPUT, true);
			return objectMapper.writeValueAsString(objectMapper.readTree(original));
		}
	}

}
