package org.springframework.restdocs.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.restdocs.snippet.SnippetWritingResultHandler;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

public abstract class AbstractParametersSnippetResultHandler extends
		SnippetWritingResultHandler {

	private final Map<String, ParameterDescriptor> descriptorsByName = new LinkedHashMap<>();

	protected AbstractParametersSnippetResultHandler(String identifier,
			String snippetName, Map<String, Object> attributes,
			ParameterDescriptor... descriptors) {
		super(identifier, snippetName, attributes);
		for (ParameterDescriptor descriptor : descriptors) {
			Assert.hasText(descriptor.getName());
			Assert.hasText(descriptor.getDescription());
			this.descriptorsByName.put(descriptor.getName(), descriptor);
		}
	}

	@Override
	protected Map<String, Object> doHandle(MvcResult result) throws IOException {
		verifyParameterDescriptors(result);

		Map<String, Object> model = new HashMap<>();
		List<Map<String, Object>> parameters = new ArrayList<>();
		for (Entry<String, ParameterDescriptor> entry : this.descriptorsByName.entrySet()) {
			parameters.add(entry.getValue().toModel());
		}
		model.put("parameters", parameters);
		return model;
	}

	protected void verifyParameterDescriptors(MvcResult result) {
		Set<String> actualParameters = extractActualParameters(result);
		Set<String> expectedParameters = this.descriptorsByName.keySet();
		Set<String> undocumentedParameters = new HashSet<String>(actualParameters);
		undocumentedParameters.removeAll(expectedParameters);
		Set<String> missingParameters = new HashSet<String>(expectedParameters);
		missingParameters.removeAll(actualParameters);

		if (!undocumentedParameters.isEmpty() || !missingParameters.isEmpty()) {
			verificationFailed(undocumentedParameters, missingParameters);
		}
		else {
			Assert.isTrue(actualParameters.equals(expectedParameters));
		}
	}

	protected abstract Set<String> extractActualParameters(MvcResult result);

	protected abstract void verificationFailed(Set<String> undocumentedParameters,
			Set<String> missingParameters);

}
