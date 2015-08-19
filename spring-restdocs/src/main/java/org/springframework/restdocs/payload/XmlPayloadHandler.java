package org.springframework.restdocs.payload;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * A {@link PayloadHandler} for XML payloads
 * 
 * @author Andy Wilkinson
 */
class XmlPayloadHandler implements PayloadHandler {

	private final DocumentBuilder documentBuilder;

	private final String rawPayload;

	XmlPayloadHandler(String rawPayload) {
		try {
			this.documentBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
		}
		catch (ParserConfigurationException ex) {
			throw new IllegalStateException("Failed to create document builder", ex);
		}
		this.rawPayload = rawPayload;
	}

	@Override
	public List<FieldDescriptor> findMissingFields(List<FieldDescriptor> fieldDescriptors) {
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

	private NodeList findMatchingNodes(FieldDescriptor fieldDescriptor, Document payload) {
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
			return this.documentBuilder.parse(new InputSource(new StringReader(
					this.rawPayload)));
		}
		catch (Exception ex) {
			throw new PayloadHandlingException(ex);
		}
	}

	private XPathExpression createXPath(String fieldPath) throws XPathExpressionException {
		return XPathFactory.newInstance().newXPath().compile(fieldPath);
	}

	@Override
	public String getUndocumentedPayload(List<FieldDescriptor> fieldDescriptors) {
		Document payload = readPayload();
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
				node.getParentNode().removeChild(node);
			}
		}
		if (payload.getChildNodes().getLength() > 0) {
			return prettyPrint(payload);
		}
		return null;
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
	public Object determineFieldType(String path) {
		try {
			return new JsonFieldTypeResolver().resolveFieldType(path, readPayload());
		}
		catch (FieldDoesNotExistException ex) {
			String message = "Cannot determine the type of the field '" + path + "' as"
					+ " it is not present in the payload. Please provide a type using"
					+ " FieldDescriptor.type(Object type).";
			throw new FieldTypeRequiredException(message);
		}
	}

}
