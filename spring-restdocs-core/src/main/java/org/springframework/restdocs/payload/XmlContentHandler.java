/*
 * Copyright 2014-2017 the original author or authors.
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

	XmlContentHandler(byte[] rawContent) {
		try {
			this.documentBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
		}
		catch (ParserConfigurationException ex) {
			throw new IllegalStateException("Failed to create document builder", ex);
		}
		this.rawContent = rawContent;
		readPayload();
	}

	@Override
	public List<FieldDescriptor> findMissingFields(
			List<FieldDescriptor> fieldDescriptors) {
		List<FieldDescriptor> missingFields = new ArrayList<>();
		Document payload = readPayload();
		for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
			if (!fieldDescriptor.isOptional()) {
				NodeList matchingNodes = findMatchingNodes(fieldDescriptor, payload);
				if (matchingNodes.getLength() == 0) {
					missingFields.add(fieldDescriptor);
				}
			}
		}

		return missingFields;
	}

	private NodeList findMatchingNodes(FieldDescriptor fieldDescriptor,
			Document payload) {
		try {
			return (NodeList) createXPath(fieldDescriptor.getPath()).evaluate(payload,
					XPathConstants.NODESET);
		}
		catch (XPathExpressionException ex) {
			throw new PayloadHandlingException(ex);
		}
	}

	private Document readPayload() {
		try {
			return this.documentBuilder
					.parse(new InputSource(new ByteArrayInputStream(this.rawContent)));
		}
		catch (Exception ex) {
			throw new PayloadHandlingException(ex);
		}
	}

	private XPathExpression createXPath(String fieldPath)
			throws XPathExpressionException {
		return XPathFactory.newInstance().newXPath().compile(fieldPath);
	}

	@Override
	public String getUndocumentedContent(List<FieldDescriptor> fieldDescriptors) {
		Document payload = readPayload();
		List<Node> matchedButNotRemoved = new ArrayList<>();
		for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
			NodeList matchingNodes;
			try {
				matchingNodes = (NodeList) createXPath(fieldDescriptor.getPath())
						.evaluate(payload, XPathConstants.NODESET);
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
					if (fieldDescriptor instanceof SubsectionDescriptor
							|| isLeafNode(node)) {
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
			transformerFactory.setAttribute("indent-number", 4);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(new DOMSource(document), xmlOutput);
			return xmlOutput.getWriter().toString();
		}
		catch (Exception ex) {
			throw new PayloadHandlingException(ex);
		}
	}

	@Override
	public Object determineFieldType(FieldDescriptor fieldDescriptor) {
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
