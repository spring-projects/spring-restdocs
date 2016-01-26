/*
 * Copyright 2012-2016 the original author or authors.
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

package org.springframework.restdocs.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertThat;

/**
 * JUnit {@code @Rule} to capture output from System.out and System.err.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class OutputCapture implements TestRule {

	private CaptureOutputStream captureOut;

	private CaptureOutputStream captureErr;

	private ByteArrayOutputStream capturedOutput;

	private List<Matcher<? super String>> matchers = new ArrayList<>();

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				captureOutput();
				try {
					base.evaluate();
				}
				finally {
					try {
						if (!OutputCapture.this.matchers.isEmpty()) {
							assertThat(getOutputAsString(),
									allOf(OutputCapture.this.matchers));
						}
					}
					finally {
						releaseOutput();
					}
				}
			}
		};
	}

	private void captureOutput() {
		this.capturedOutput = new ByteArrayOutputStream();
		this.captureOut = new CaptureOutputStream(System.out, this.capturedOutput);
		this.captureErr = new CaptureOutputStream(System.err, this.capturedOutput);
		System.setOut(new PrintStream(this.captureOut));
		System.setErr(new PrintStream(this.captureErr));
	}

	private String getOutputAsString() {
		flush();
		return this.capturedOutput.toString();
	}

	private void releaseOutput() {
		System.setOut(this.captureOut.getOriginal());
		System.setErr(this.captureErr.getOriginal());
		this.capturedOutput = null;
	}

	private void flush() {
		try {
			this.captureOut.flush();
			this.captureErr.flush();
		}
		catch (IOException ex) {
			// ignore
		}
	}

	/**
	 * Verify that the output is matched by the supplied {@code matcher}. Verification is
	 * performed after the test method has executed.
	 *
	 * @param matcher the matcher
	 */
	public final void expect(Matcher<? super String> matcher) {
		this.matchers.add(matcher);
	}

	private static final class CaptureOutputStream extends OutputStream {

		private final PrintStream original;

		private final OutputStream copy;

		private CaptureOutputStream(PrintStream original, OutputStream copy) {
			this.original = original;
			this.copy = copy;
		}

		@Override
		public void write(int b) throws IOException {
			this.copy.write(b);
			this.original.write(b);
			this.original.flush();
		}

		@Override
		public void write(byte[] b) throws IOException {
			write(b, 0, b.length);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			this.copy.write(b, off, len);
			this.original.write(b, off, len);
		}

		private PrintStream getOriginal() {
			return this.original;
		}

		@Override
		public void flush() throws IOException {
			this.copy.flush();
			this.original.flush();
		}

	}

}
