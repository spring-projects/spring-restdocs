/*
 * Copyright 2014 the original author or authors.
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

package org.springframework.restdocs.core;

import static org.springframework.restdocs.core.RestDocumentationResultHandlers.documentCurlRequest;
import static org.springframework.restdocs.core.RestDocumentationResultHandlers.documentCurlRequestAndResponse;
import static org.springframework.restdocs.core.RestDocumentationResultHandlers.documentCurlResponse;

import java.util.Arrays;

import org.springframework.restdocs.core.RestDocumentationResultHandlers.LinkDocumentingResultHandler;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

public class RestDocumentationResultHandler implements ResultHandler {

    private final String outputDir;
    private ResultHandler linkDocumentingResultHandler;

    public RestDocumentationResultHandler(String outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public void handle(MvcResult result) throws Exception {
        documentCurlRequest(outputDir).includeResponseHeaders().handle(result);
        documentCurlResponse(outputDir).includeResponseHeaders().handle(result);
        documentCurlRequestAndResponse(outputDir).includeResponseHeaders().handle(result);
        if(linkDocumentingResultHandler != null) {
            linkDocumentingResultHandler.handle(result);
        }
    }

    public RestDocumentationResultHandler withLinks(LinkExtractor linkExtractor, LinkDescriptor... descriptors) {
        linkDocumentingResultHandler = new LinkDocumentingResultHandler(outputDir, linkExtractor, Arrays.asList(descriptors));
        return this;
    }
}
