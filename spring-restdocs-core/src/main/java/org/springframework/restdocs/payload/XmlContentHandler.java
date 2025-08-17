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

package org.springframework.restdocs.payload;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jspecify.annotations.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * A {@link ContentHandler} for XML content.
 *
 * @author Andy Wilkinson
 */
class XmlContentHandler implements ContentHandler {

	private final DocumentBuilder documentBuilder;

	private final byte[] rawContent;

	private final List<FieldDescriptor> fieldDescriptors;

	XmlContentHandler(byte[] rawContent, List<FieldDescriptor> fieldDescriptors) {
		try {
			this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (ParserConfigurationException ex) {
			throw new IllegalStateException("Failed to create document builder", ex);
		}
		this.rawContent = rawContent;
		this.fieldDescriptors = fieldDescriptors;
		readPayload();
	}

	@Override
	public List<FieldDescriptor> findMissingFields() {
		List<FieldDescriptor> missingFields = new ArrayList<>();
		Document payload = readPayload();
		for (FieldDescriptor fieldDescriptor : this.fieldDescriptors) {
			if (!fieldDescriptor.isOptional()) {
				NodeList matchingNodes = findMatchingNodes(fieldDescriptor, payload);
				if (matchingNodes.getLength() == 0) {
					missingFields.add(fieldDescriptor);
				}
			}
		}

		return missingFields;
	}

	private NodeList findMatchingNodes(FieldDescriptor fieldDescriptor, Document payload) {
		try {
			return (NodeList) createXPath(fieldDescriptor.getPath()).evaluate(payload, XPathConstants.NODESET);
		}
		catch (XPathExpressionException ex) {
			throw new PayloadHandlingException(ex);
		}
	}

	private Document readPayload() {
		try {
			return this.documentBuilder.parse(new InputSource(new ByteArrayInputStream(this.rawContent)));
		}
		catch (Exception ex) {
			throw new PayloadHandlingException(ex);
		}
	}

	private XPathExpression createXPath(String fieldPath) throws XPathExpressionException {
		return XPathFactory.newInstance().newXPath().compile(fieldPath);
	}

	@Override
	public @Nullable String getUndocumentedContent() {
		Document payload = readPayload();
		List<Node> matchedButNotRemoved = new ArrayList<>();
		for (FieldDescriptor fieldDescriptor : this.fieldDescriptors) {
			NodeList matchingNodes;
			try {
				matchingNodes = (NodeList) createXPath(fieldDescriptor.getPath()).evaluate(payload,
						XPathConstants.NODESET);
			}
			catch (XPathExpressionException ex) {
				throw new PayloadHandlingException(ex);
			}
			for (int i = 0; i < matchingNodes.getLength(); i++) {
				Node node = matchingNodes.item(i);
				if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
					Attr attr = (Attr) node;
					attr.getOwnerElement().removeAttributeNode(attr);
				}
				else {
					if (fieldDescriptor instanceof SubsectionDescriptor || isLeafNode(node)) {
						node.getParentNode().removeChild(node);
					}
					else {
						matchedButNotRemoved.add(node);
					}
				}

			}
		}
		removeLeafNodes(matchedButNotRemoved);
		if (payload.getChildNodes().getLength() > 0) {
			return prettyPrint(payload);
		}
		return null;
	}

	private void removeLeafNodes(List<Node> candidates) {
		boolean changed = true;
		while (changed) {
			changed = false;
			Iterator<Node> iterator = candidates.iterator();
			while (iterator.hasNext()) {
				Node node = iterator.next();
				if (isLeafNode(node)) {
					node.getParentNode().removeChild(node);
					iterator.remove();
					changed = true;
				}
			}
		}
	}

	private boolean isLeafNode(Node node) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i) instanceof Element) {
				return false;
			}
		}
		return true;
	}

	private String prettyPrint(Document document) {
		try {
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			try {
				transformerFactory.setAttribute("indent-number", 4);
			}
			catch (IllegalArgumentException ex) {
				// safely ignore if indent-amount is not supported
			}

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			transformer.transform(new DOMSource(document), xmlOutput);
			String result = xmlOutput.getWriter().toString();

			if (transformerFactory.getClass().getName().contains("saxon")) {
				result = normalizeToStandardFormat(result);
			}

			return result;
		} catch (Exception ex) {
			throw new PayloadHandlingException(ex);
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

	@Override
	public Object resolveFieldType(FieldDescriptor fieldDescriptor) {
		if (fieldDescriptor.getType() != null) {
			return fieldDescriptor.getType();
		}
		else {
			throw new FieldTypeRequiredException("The type of a field in an XML payload "
					+ "cannot be determined automatically. Please provide a type using "
					+ "FieldDescriptor.type(Object type)");
		}
	}

}
