package com.vinylteam.vinyl.web.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DefaultErrorHandler extends ErrorPageErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected void generateAcceptableResponse(Request baseRequest, HttpServletRequest request, HttpServletResponse response,
                                              int code, String message, String mimeType) {
        Map<String, Object> parameterMap = new HashMap<>();
        String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        logger.error("Error in {'servlet':{},\n 'code':{},\n 'message':{}}", servletName, code, message, exception);
        baseRequest.setHandled(true);
        parameterMap.put("code", code);
        parameterMap.put("message", message);
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        logger.debug("Set response status to " +
                "{'status':{}}", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}
