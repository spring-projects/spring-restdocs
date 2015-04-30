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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

class PrettyPrintingResponsePostProcessor extends ContentModifyingReponsePostProcessor {

	private static final List<PrettyPrinter> prettyPrinters = Arrays.asList(
			new JsonPrettyPrinter(), new XmlPrettyPrinter());

	@Override
	protected String modifyContent(String originalContent) {
		if (StringUtils.hasText(originalContent)) {
			for (PrettyPrinter prettyPrinter : prettyPrinters) {
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

		String prettyPrint(String string) throws Exception;

	}

	private static final class XmlPrettyPrinter implements PrettyPrinter {

		@Override
		public String prettyPrint(String original) throws Exception {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
					"4");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
			StringWriter transformed = new StringWriter();
			transformer.transform(new StreamSource(new StringReader(original)),
					new StreamResult(transformed));
			return transformed.toString();
		}
	}

	private static final class JsonPrettyPrinter implements PrettyPrinter {

		@Override
		public String prettyPrint(String original) throws IOException {
			ObjectMapper objectMapper = new ObjectMapper().configure(
					SerializationFeature.INDENT_OUTPUT, true);
			return objectMapper.writeValueAsString(objectMapper.readTree(original));
		}
	}

}
