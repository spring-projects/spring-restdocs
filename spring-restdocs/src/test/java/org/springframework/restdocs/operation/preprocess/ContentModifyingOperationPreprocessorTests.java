package org.springframework.restdocs.operation.preprocess;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.Collections;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.operation.StandardOperationRequest;
import org.springframework.restdocs.operation.StandardOperationResponse;

/**
 * Tests for {@link ContentModifyingOperationPreprocessor}
 * 
 * @author Andy Wilkinson
 *
 */
public class ContentModifyingOperationPreprocessorTests {

	private final ContentModifyingOperationPreprocessor preprocessor = new ContentModifyingOperationPreprocessor(
			new ContentModifier() {

				@Override
				public byte[] modifyContent(byte[] originalContent) {
					return "modified".getBytes();
				}

			});

	@Test
	public void modifyRequestContent() {
		StandardOperationRequest request = new StandardOperationRequest(
				URI.create("http://localhost"), HttpMethod.GET, "content".getBytes(),
				new HttpHeaders(), new Parameters(),
				Collections.<OperationRequestPart> emptyList());
		OperationRequest preprocessed = this.preprocessor.preprocess(request);
		assertThat(preprocessed.getContent(), is(equalTo("modified".getBytes())));
	}

	@Test
	public void modifyResponseContent() {
		StandardOperationResponse response = new StandardOperationResponse(HttpStatus.OK,
				new HttpHeaders(), "content".getBytes());
		OperationResponse preprocessed = this.preprocessor.preprocess(response);
		assertThat(preprocessed.getContent(), is(equalTo("modified".getBytes())));
	}

	@Test
	public void unknownContentLengthIsUnchanged() {
		StandardOperationRequest request = new StandardOperationRequest(
				URI.create("http://localhost"), HttpMethod.GET, "content".getBytes(),
				new HttpHeaders(), new Parameters(),
				Collections.<OperationRequestPart> emptyList());
		OperationRequest preprocessed = this.preprocessor.preprocess(request);
		assertThat(preprocessed.getHeaders().getContentLength(), is(equalTo(-1L)));
	}

	@Test
	public void contentLengthIsUpdated() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentLength(7);
		StandardOperationRequest request = new StandardOperationRequest(
				URI.create("http://localhost"), HttpMethod.GET, "content".getBytes(),
				httpHeaders, new Parameters(),
				Collections.<OperationRequestPart> emptyList());
		OperationRequest preprocessed = this.preprocessor.preprocess(request);
		assertThat(preprocessed.getHeaders().getContentLength(), is(equalTo(8L)));
	}

}
