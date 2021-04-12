package com.vinylteam.vinyl.web.handler;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class DefaultErrorHandler extends ErrorPageErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected void generateAcceptableResponse(Request baseRequest, HttpServletRequest request, HttpServletResponse response,
                                              int code, String message, String mimeType) {
        logger.debug("Start of function DefaultErrorHandler" +
                ".generateAcceptableResponse(Request baseRequest, HttpServletRequest request, HttpServletResponse response, " +
                        "int code, String message, String mimeType) with " +
                        "{'baseRequest':{},\n 'request':{},\n 'response':{},\n 'code':{},\n 'message':{},\n 'mimeType':{}}",
                baseRequest, request, response, code, message, mimeType);
        Map<String, Object> parameterMap = new HashMap<>();
        logger.debug("Created and initialized Map<String, Object> object for storing error code and message " +
                "{'parameterMap':{}}", parameterMap);
        String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");

        logger.error("Error in servlet " +
                "{'servlet':{},\n 'code':{},\n 'message':{}}", servletName, code, message, exception);

        baseRequest.setHandled(true);
        logger.debug("Set baseRequest._handled true");

        parameterMap.put("code", code);
        parameterMap.put("message", message);
        logger.debug("Filled parameterMap " +
                "{'parameterMap':{}}", parameterMap);
        response.setContentType("text/html;charset=utf-8");
        logger.debug("Set response content type to text/html;charset=utf-8");
        response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        logger.debug("Set response status to " +
                "{'status':{}}", javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}
