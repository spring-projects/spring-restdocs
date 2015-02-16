package org.springframework.restdocs.core;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CurlRequestDocumentationActionTest {

    @Test
    public void testQueryParamsToStringReturnsEmptyStringIfThereIsNoParams(){

        RestDocumentationResultHandlers.CurlRequestDocumentationAction curlRequestDocumentationAction =
                new RestDocumentationResultHandlers.CurlRequestDocumentationAction(null, null, null);

        String queryParamsToString = curlRequestDocumentationAction.queryParamsToString(new MockHttpServletRequest());
        assertTrue(queryParamsToString.isEmpty());

    }

    @Test
    public void testQueryParamsToStringSingleParameter(){

        RestDocumentationResultHandlers.CurlRequestDocumentationAction curlRequestDocumentationAction =
                new RestDocumentationResultHandlers.CurlRequestDocumentationAction(null, null, null);

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addParameter("someParam", "someValue");

        String queryParamsToString = curlRequestDocumentationAction.queryParamsToString(request);
        assertEquals("?someParam=someValue", queryParamsToString);

    }

    @Test
    public void testQueryParamsToStringMultipleParameters(){

        RestDocumentationResultHandlers.CurlRequestDocumentationAction curlRequestDocumentationAction =
                new RestDocumentationResultHandlers.CurlRequestDocumentationAction(null, null, null);

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addParameter("firstParam", "firstValue");
        request.addParameter("secondParam", "secondValue");
        request.addParameter("thirdParam", "thirdValue");

        String queryParamsToString = curlRequestDocumentationAction.queryParamsToString(request);
        assertEquals("?firstParam=firstValue&secondParam=secondValue&thirdParam=thirdValue", queryParamsToString);

    }

    @Test
    public void testQueryParamsToStringParameterWithoutValue(){

        RestDocumentationResultHandlers.CurlRequestDocumentationAction curlRequestDocumentationAction =
                new RestDocumentationResultHandlers.CurlRequestDocumentationAction(null, null, null);

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addParameter("firstParam", "firstValue");
        request.addParameter("secondParam", new String[0]);
        request.addParameter("thirdParam", "thirdValue");

        String queryParamsToString = curlRequestDocumentationAction.queryParamsToString(request);
        assertEquals("?firstParam=firstValue&secondParam&thirdParam=thirdValue", queryParamsToString);

    }

}