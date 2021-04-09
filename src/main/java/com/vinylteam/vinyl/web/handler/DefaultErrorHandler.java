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
        Map<String, Object> parameterMap = new HashMap<>();

        String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");

        logger.error("Error in servlet: {},\n code: {},\n message: {}", servletName, code, message, exception);

        baseRequest.setHandled(true);

        parameterMap.put("code", code);
        parameterMap.put("message", message);

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}
