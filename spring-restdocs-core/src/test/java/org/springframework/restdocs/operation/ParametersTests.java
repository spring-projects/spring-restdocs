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

package org.springframework.restdocs.operation;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link Parameters}.
 *
 * @author Andy Wilkinson
 */
public class ParametersTests {

	private final Parameters parameters = new Parameters();

	@Test
	public void queryStringForNoParameters() {
		assertThat(this.parameters.toQueryString(), is(equalTo("")));
	}

	@Test
	public void queryStringForSingleParameter() {
		this.parameters.add("a", "b");
		assertThat(this.parameters.toQueryString(), is(equalTo("a=b")));
	}

	@Test
	public void queryStringForSingleParameterWithMultipleValues() {
		this.parameters.add("a", "b");
		this.parameters.add("a", "c");
		assertThat(this.parameters.toQueryString(), is(equalTo("a=b&a=c")));
	}

	@Test
	public void queryStringForMutipleParameters() {
		this.parameters.add("a", "alpha");
		this.parameters.add("b", "bravo");
		assertThat(this.parameters.toQueryString(), is(equalTo("a=alpha&b=bravo")));
	}

	@Test
	public void queryStringForParameterWithEmptyValue() {
		this.parameters.add("a", "");
		assertThat(this.parameters.toQueryString(), is(equalTo("a=")));
	}

	@Test
	public void queryStringForParameterWithNullValue() {
		this.parameters.add("a", null);
		assertThat(this.parameters.toQueryString(), is(equalTo("a=")));
	}

	@Test
	public void queryStringForParameterThatRequiresEncoding() {
		this.parameters.add("a", "alpha&bravo");
		assertThat(this.parameters.toQueryString(), is(equalTo("a=alpha%26bravo")));
	}

}
