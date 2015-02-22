/*
 * Copyright 2014-2015 the original author or authors.
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

package org.springframework.restdocs.state;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.style.ToStringCreator;

/**
 * Representation of a path for a field. In case the field is a nested field, there will
 * be multiple steps. Each step is the name of a field, albeit parents of the field in
 * question. In case the field is not nested, there is only one step, which is the name of the field.
 *
 * @author Andreas Evers
 */
public class Path implements Comparable<Path> {

	private List<String> steps = new ArrayList<>();

	public Path(Path path) {
		this.steps = new ArrayList<String>(path.getSteps());
	}

	public Path(List<String> steps) {
		this.steps = steps;
	}

	public Path(Path previousSteps, String newStep) {
		this.steps.addAll(previousSteps.getSteps());
		this.steps.add(newStep);
	}

	public Path(String... steps) {
		this.steps = Arrays.asList(steps);
	}

	public static Path path(List<String> steps) {
		return new Path(steps);
	}

	public static Path path(String... steps) {
		return new Path(steps);
	}

	public static Path path(Path previousSteps, String newStep) {
		return new Path(previousSteps, newStep);
	}

	public List<String> getSteps() {
		return this.steps;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.steps == null) ? 0 : this.steps.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Path other = (Path) obj;
		if (this.steps == null) {
			if (other.steps != null) {
				return false;
			}
		}
		else if (!this.steps.equals(other.steps)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("steps", this.steps).toString();
	}

	@Override
	public int compareTo(Path o) {
		int comparison = 0;
		int size = min(this.steps.size(), o.getSteps().size());
		for (int i = 0; i < size; i++) {
			String thisStep = this.steps.get(i);
			String thatStep = o.getSteps().get(i);
			comparison = thisStep.compareTo(thatStep);
			if (comparison != 0) {
				break;
			}
		}
		if (comparison == 0) {
			comparison = ((Integer) this.steps.size()).compareTo(o.getSteps().size());
		}
		return comparison;
	}
}
