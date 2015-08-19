package org.springframework.restdocs.payload;

import java.util.List;

/**
 * A handler for a request or response payload
 * 
 * @author Andy Wilkinson
 */
interface PayloadHandler {

	/**
	 * Finds the fields that are missing from the handler's payload. A field is missing if
	 * it is described by one of the {@code fieldDescriptors} but is not present in the
	 * payload.
	 * 
	 * @param fieldDescriptors the descriptors
	 * @return descriptors for the fields that are missing from the payload
	 * @throws PayloadHandlingException if a failure occurs
	 */
	List<FieldDescriptor> findMissingFields(List<FieldDescriptor> fieldDescriptors);

	/**
	 * Returns a modified payload, formatted as a String, that only contains the fields
	 * that are undocumented. A field is undocumented if it is present in the handler's
	 * payload but is not described by the given {@code fieldDescriptors}. If the payload
	 * is completely documented, {@code null} is returned
	 * 
	 * @param fieldDescriptors the descriptors
	 * @return the undocumented payload, or {@code null} if all of the payload is
	 * documented
	 * @throws PayloadHandlingException if a failure occurs
	 */
	String getUndocumentedPayload(List<FieldDescriptor> fieldDescriptors);

	/**
	 * Returns the type of the field with the given {@code path} based on the content of
	 * the payload.
	 * 
	 * @param path the field path
	 * @return the type of the field
	 */
	Object determineFieldType(String path);

}