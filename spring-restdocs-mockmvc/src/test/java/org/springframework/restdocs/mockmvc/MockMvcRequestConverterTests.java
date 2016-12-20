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

package org.springframework.restdocs.mockmvc;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Arrays;

import javax.servlet.http.Part;

import org.junit.Test;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockServletContext;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link MockMvcRequestConverter}.
 *
 * @author Andy Wilkinson
 */
public class MockMvcRequestConverterTests {

	private final MockMvcRequestConverter factory = new MockMvcRequestConverter();

	@Test
	public void httpRequest() throws Exception {
		OperationRequest request = createOperationRequest(
				MockMvcRequestBuilders.get("/foo"));
		assertThat(request.getUri(), is(URI.create("http://localhost/foo")));
		assertThat(request.getMethod(), is(HttpMethod.GET));
	}

	@Test
	public void httpRequestWithCustomPort() throws Exception {
		MockHttpServletRequest mockRequest = MockMvcRequestBuilders.get("/foo")
				.buildRequest(new MockServletContext());
		mockRequest.setServerPort(8080);
		OperationRequest request = this.factory.convert(mockRequest);
		assertThat(request.getUri(), is(URI.create("http://localhost:8080/foo")));
		assertThat(request.getMethod(), is(HttpMethod.GET));
	}

	@Test
	public void requestWithContextPath() throws Exception {
		OperationRequest request = createOperationRequest(
				MockMvcRequestBuilders.get("/foo/bar").contextPath("/foo"));
		assertThat(request.getUri(), is(URI.create("http://localhost/foo/bar")));
		assertThat(request.getMethod(), is(HttpMethod.GET));
	}

	@Test
	public void requestWithHeaders() throws Exception {
		OperationRequest request = createOperationRequest(MockMvcRequestBuilders
				.get("/foo").header("a", "alpha", "apple").header("b", "bravo"));
		assertThat(request.getUri(), is(URI.create("http://localhost/foo")));
		assertThat(request.getMethod(), is(HttpMethod.GET));
		assertThat(request.getHeaders(), hasEntry("a", Arrays.asList("alpha", "apple")));
		assertThat(request.getHeaders(), hasEntry("b", Arrays.asList("bravo")));
	}

	@Test
	public void httpsRequest() throws Exception {
		MockHttpServletRequest mockRequest = MockMvcRequestBuilders.get("/foo")
				.buildRequest(new MockServletContext());
		mockRequest.setScheme("https");
		mockRequest.setServerPort(443);
		OperationRequest request = this.factory.convert(mockRequest);
		assertThat(request.getUri(), is(URI.create("https://localhost/foo")));
		assertThat(request.getMethod(), is(HttpMethod.GET));
	}

	@Test
	public void httpsRequestWithCustomPort() throws Exception {
		MockHttpServletRequest mockRequest = MockMvcRequestBuilders.get("/foo")
				.buildRequest(new MockServletContext());
		mockRequest.setScheme("https");
		mockRequest.setServerPort(8443);
		OperationRequest request = this.factory.convert(mockRequest);
		assertThat(request.getUri(), is(URI.create("https://localhost:8443/foo")));
		assertThat(request.getMethod(), is(HttpMethod.GET));
	}

	@Test
	public void getRequestWithParametersProducesUriWithQueryString() throws Exception {
		OperationRequest request = createOperationRequest(MockMvcRequestBuilders
				.get("/foo").param("a", "alpha", "apple").param("b", "br&vo"));
		assertThat(request.getUri(),
				is(URI.create("http://localhost/foo?a=alpha&a=apple&b=br%26vo")));
		assertThat(request.getParameters().size(), is(2));
		assertThat(request.getParameters(),
				hasEntry("a", Arrays.asList("alpha", "apple")));
		assertThat(request.getParameters(), hasEntry("b", Arrays.asList("br&vo")));
		assertThat(request.getMethod(), is(HttpMethod.GET));
	}

	@Test
	public void getRequestWithQueryStringPopulatesParameters() throws Exception {
		OperationRequest request = createOperationRequest(
				MockMvcRequestBuilders.get("/foo?a=alpha&b=bravo"));
		assertThat(request.getUri(),
				is(URI.create("http://localhost/foo?a=alpha&b=bravo")));
		assertThat(request.getParameters().size(), is(2));
		assertThat(request.getParameters(), hasEntry("a", Arrays.asList("alpha")));
		assertThat(request.getParameters(), hasEntry("b", Arrays.asList("bravo")));
		assertThat(request.getMethod(), is(HttpMethod.GET));
	}

	@Test
	public void postRequestWithParameters() throws Exception {
		OperationRequest request = createOperationRequest(MockMvcRequestBuilders
				.post("/foo").param("a", "alpha", "apple").param("b", "br&vo"));
		assertThat(request.getUri(), is(URI.create("http://localhost/foo")));
		assertThat(request.getMethod(), is(HttpMethod.POST));
		assertThat(request.getParameters().size(), is(2));
		assertThat(request.getParameters(),
				hasEntry("a", Arrays.asList("alpha", "apple")));
		assertThat(request.getParameters(), hasEntry("b", Arrays.asList("br&vo")));
	}

	@Test
	public void mockMultipartFileUpload() throws Exception {
		OperationRequest request = createOperationRequest(
				MockMvcRequestBuilders.fileUpload("/foo")
						.file(new MockMultipartFile("file", new byte[] { 1, 2, 3, 4 })));
		assertThat(request.getUri(), is(URI.create("http://localhost/foo")));
		assertThat(request.getMethod(), is(HttpMethod.POST));
		assertThat(request.getParts().size(), is(1));
		OperationRequestPart part = request.getParts().iterator().next();
		assertThat(part.getName(), is(equalTo("file")));
		assertThat(part.getSubmittedFileName(), is(nullValue()));
		assertThat(part.getHeaders().size(), is(1));
		assertThat(part.getHeaders().getContentLength(), is(4L));
		assertThat(part.getContent(), is(equalTo(new byte[] { 1, 2, 3, 4 })));
	}

	@Test
	public void mockMultipartFileUploadWithContentType() throws Exception {
		OperationRequest request = createOperationRequest(
				MockMvcRequestBuilders.fileUpload("/foo").file(new MockMultipartFile(
						"file", "original", "image/png", new byte[] { 1, 2, 3, 4 })));
		assertThat(request.getUri(), is(URI.create("http://localhost/foo")));
		assertThat(request.getMethod(), is(HttpMethod.POST));
		assertThat(request.getParts().size(), is(1));
		OperationRequestPart part = request.getParts().iterator().next();
		assertThat(part.getName(), is(equalTo("file")));
		assertThat(part.getSubmittedFileName(), is(equalTo("original")));
		assertThat(part.getHeaders().getContentType(), is(MediaType.IMAGE_PNG));
		assertThat(part.getContent(), is(equalTo(new byte[] { 1, 2, 3, 4 })));
	}

	@Test
	public void requestWithPart() throws Exception {
		MockHttpServletRequest mockRequest = MockMvcRequestBuilders.get("/foo")
				.buildRequest(new MockServletContext());
		Part mockPart = mock(Part.class);
		given(mockPart.getHeaderNames()).willReturn(Arrays.asList("a", "b"));
		given(mockPart.getHeaders("a")).willReturn(Arrays.asList("alpha"));
		given(mockPart.getHeaders("b")).willReturn(Arrays.asList("bravo", "banana"));
		given(mockPart.getInputStream())
				.willReturn(new ByteArrayInputStream(new byte[] { 1, 2, 3, 4 }));
		given(mockPart.getName()).willReturn("part-name");
		given(mockPart.getSubmittedFileName()).willReturn("submitted.txt");
		mockRequest.addPart(mockPart);
		OperationRequest request = this.factory.convert(mockRequest);
		assertThat(request.getParts().size(), is(1));
		OperationRequestPart part = request.getParts().iterator().next();
		assertThat(part.getName(), is(equalTo("part-name")));
		assertThat(part.getSubmittedFileName(), is(equalTo("submitted.txt")));
		assertThat(part.getHeaders().getContentType(), is(nullValue()));
		assertThat(part.getHeaders().get("a"), contains("alpha"));
		assertThat(part.getHeaders().get("b"), contains("bravo", "banana"));
		assertThat(part.getContent(), is(equalTo(new byte[] { 1, 2, 3, 4 })));
	}

	@Test
	public void requestWithPartWithContentType() throws Exception {
		MockHttpServletRequest mockRequest = MockMvcRequestBuilders.get("/foo")
				.buildRequest(new MockServletContext());
		Part mockPart = mock(Part.class);
		given(mockPart.getHeaderNames()).willReturn(Arrays.asList("a", "b"));
		given(mockPart.getHeaders("a")).willReturn(Arrays.asList("alpha"));
		given(mockPart.getHeaders("b")).willReturn(Arrays.asList("bravo", "banana"));
		given(mockPart.getInputStream())
				.willReturn(new ByteArrayInputStream(new byte[] { 1, 2, 3, 4 }));
		given(mockPart.getName()).willReturn("part-name");
		given(mockPart.getSubmittedFileName()).willReturn("submitted.png");
		given(mockPart.getContentType()).willReturn("image/png");
		mockRequest.addPart(mockPart);
		OperationRequest request = this.factory.convert(mockRequest);
		assertThat(request.getParts().size(), is(1));
		OperationRequestPart part = request.getParts().iterator().next();
		assertThat(part.getName(), is(equalTo("part-name")));
		assertThat(part.getSubmittedFileName(), is(equalTo("submitted.png")));
		assertThat(part.getHeaders().getContentType(), is(MediaType.IMAGE_PNG));
		assertThat(part.getHeaders().get("a"), contains("alpha"));
		assertThat(part.getHeaders().get("b"), contains("bravo", "banana"));
		assertThat(part.getContent(), is(equalTo(new byte[] { 1, 2, 3, 4 })));
	}

	private OperationRequest createOperationRequest(MockHttpServletRequestBuilder builder)
			throws Exception {
		return this.factory.convert(builder.buildRequest(new MockServletContext()));
	}

}
