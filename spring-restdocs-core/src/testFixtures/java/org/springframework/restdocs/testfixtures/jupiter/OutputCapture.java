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

package org.springframework.restdocs.testfixtures.jupiter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.springframework.util.Assert;

/**
 * Provides support for capturing {@link System#out System.out} and {@link System#err
 * System.err}.
 *
 * @author Madhura Bhave
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
class OutputCapture implements CapturedOutput {

	private final Deque<SystemCapture> systemCaptures = new ArrayDeque<>();

	/**
	 * Push a new system capture session onto the stack.
	 */
	final void push() {
		this.systemCaptures.addLast(new SystemCapture());
	}

	/**
	 * Pop the last system capture session from the stack.
	 */
	final void pop() {
		this.systemCaptures.removeLast().release();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof CharSequence) {
			return getAll().equals(obj.toString());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return getAll();
	}

	/**
	 * Return all content (both {@link System#out System.out} and {@link System#err
	 * System.err}) in the order that it was captured.
	 * @return all captured output
	 */
	@Override
	public String getAll() {
		return get((type) -> true);
	}

	/**
	 * Return {@link System#out System.out} content in the order that it was captured.
	 * @return {@link System#out System.out} captured output
	 */
	@Override
	public String getOut() {
		return get(Type.OUT::equals);
	}

	/**
	 * Return {@link System#err System.err} content in the order that it was captured.
	 * @return {@link System#err System.err} captured output
	 */
	@Override
	public String getErr() {
		return get(Type.ERR::equals);
	}

	/**
	 * Resets the current capture session, clearing its captured output.
	 */
	void reset() {
		this.systemCaptures.peek().reset();
	}

	private String get(Predicate<Type> filter) {
		Assert.state(!this.systemCaptures.isEmpty(),
				"No system captures found. Please check your output capture registration.");
		StringBuilder builder = new StringBuilder();
		for (SystemCapture systemCapture : this.systemCaptures) {
			systemCapture.append(builder, filter);
		}
		return builder.toString();
	}

	/**
	 * A capture session that captures {@link System#out System.out} and {@link System#err
	 * System.err}.
	 */
	private static class SystemCapture {

		private final PrintStreamCapture out;

		private final PrintStreamCapture err;

		private final Object monitor = new Object();

		private final List<CapturedString> capturedStrings = new ArrayList<>();

		SystemCapture() {
			this.out = new PrintStreamCapture(System.out, this::captureOut);
			this.err = new PrintStreamCapture(System.err, this::captureErr);
			System.setOut(this.out);
			System.setErr(this.err);
		}

		void release() {
			System.setOut(this.out.getParent());
			System.setErr(this.err.getParent());
		}

		private void captureOut(String string) {
			synchronized (this.monitor) {
				this.capturedStrings.add(new CapturedString(Type.OUT, string));
			}
		}

		private void captureErr(String string) {
			synchronized (this.monitor) {
				this.capturedStrings.add(new CapturedString(Type.ERR, string));
			}
		}

		void append(StringBuilder builder, Predicate<Type> filter) {
			synchronized (this.monitor) {
				for (CapturedString stringCapture : this.capturedStrings) {
					if (filter.test(stringCapture.getType())) {
						builder.append(stringCapture);
					}
				}
			}
		}

		void reset() {
			synchronized (this.monitor) {
				this.capturedStrings.clear();
			}
		}

	}

	/**
	 * A {@link PrintStream} implementation that captures written strings.
	 */
	private static class PrintStreamCapture extends PrintStream {

		private final PrintStream parent;

		PrintStreamCapture(PrintStream parent, Consumer<String> copy) {
			super(new OutputStreamCapture(getSystemStream(parent), copy));
			this.parent = parent;
		}

		PrintStream getParent() {
			return this.parent;
		}

		private static PrintStream getSystemStream(PrintStream printStream) {
			while (printStream instanceof PrintStreamCapture printStreamCapture) {
				printStream = printStreamCapture.getParent();
			}
			return printStream;
		}

	}

	/**
	 * An {@link OutputStream} implementation that captures written strings.
	 */
	private static class OutputStreamCapture extends OutputStream {

		private final PrintStream systemStream;

		private final Consumer<String> copy;

		OutputStreamCapture(PrintStream systemStream, Consumer<String> copy) {
			this.systemStream = systemStream;
			this.copy = copy;
		}

		@Override
		public void write(int b) throws IOException {
			write(new byte[] { (byte) (b & 0xFF) });
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			this.copy.accept(new String(b, off, len));
			this.systemStream.write(b, off, len);
		}

		@Override
		public void flush() throws IOException {
			this.systemStream.flush();
		}

	}

	/**
	 * A captured string that forms part of the full output.
	 */
	private static class CapturedString {

		private final Type type;

		private final String string;

		CapturedString(Type type, String string) {
			this.type = type;
			this.string = string;
		}

		Type getType() {
			return this.type;
		}

		@Override
		public String toString() {
			return this.string;
		}

	}

	/**
	 * Types of content that can be captured.
	 */
	private enum Type {

		OUT, ERR

	}

}
