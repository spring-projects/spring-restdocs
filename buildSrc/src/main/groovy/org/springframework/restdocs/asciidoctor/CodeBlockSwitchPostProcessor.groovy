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

package org.springframework.restdocs.asciidoctor

import org.asciidoctor.ast.Document
import org.asciidoctor.extension.Postprocessor

class CodeBlockSwitchPostProcessor extends Postprocessor {

	String process(Document document, String output) {
		def css = getClass().getResource("/codeBlockSwitch.css").text
		def javascript = getClass().getResource("/codeBlockSwitch.js").text
		def replacement = """<style>
$css
</style>
<script src=\"http://cdnjs.cloudflare.com/ajax/libs/zepto/1.1.6/zepto.min.js\"></script>
<script type="text/javascript">
$javascript
</script>
</head>
"""
		return output.replace("</head>", replacement);
	}

}