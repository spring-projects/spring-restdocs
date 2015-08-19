package org.springframework.restdocs.payload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * A {@link PayloadHandler} for JSON payloads
 * 
 * @author Andy Wilkinson
 */
class JsonPayloadHandler implements PayloadHandler {

	private final JsonFieldProcessor fieldProcessor = new JsonFieldProcessor();

	private final ObjectMapper objectMapper = new ObjectMapper()
			.enable(SerializationFeature.INDENT_OUTPUT);

	private final String rawPayload;

	JsonPayloadHandler(String payload) throws IOException {
		this.rawPayload = payload;
	}

	@Override
	public List<FieldDescriptor> findMissingFields(List<FieldDescriptor> fieldDescriptors) {
		List<FieldDescriptor> missingFields = new ArrayList<FieldDescriptor>();
		Object payload = readPayload();
		for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
			if (!fieldDescriptor.isOptional()
					&& !this.fieldProcessor.hasField(
							JsonFieldPath.compile(fieldDescriptor.getPath()), payload)) {
				missingFields.add(fieldDescriptor);
			}
		}

		return missingFields;
	}

	@Override
	public String getUndocumentedPayload(List<FieldDescriptor> fieldDescriptors) {
		Object payload = readPayload();
		for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
			JsonFieldPath path = JsonFieldPath.compile(fieldDescriptor.getPath());
			this.fieldProcessor.remove(path, payload);
		}
		if (!isEmpty(payload)) {
			try {
				return this.objectMapper.writeValueAsString(payload);
			}
			catch (JsonProcessingException ex) {
				throw new PayloadHandlingException(ex);
			}
		}
		return null;
	}

	private Object readPayload() {
		try {
			return new ObjectMapper().readValue(this.rawPayload, Object.class);
		}
		catch (IOException ex) {
			throw new PayloadHandlingException(ex);
		}
	}

	private boolean isEmpty(Object object) {
		if (object instanceof Map) {
			return ((Map<?, ?>) object).isEmpty();
		}
		return ((List<?>) object).isEmpty();
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
