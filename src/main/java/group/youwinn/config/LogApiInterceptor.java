package group.youwinn.config;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.lang.StringBuffer;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogApiInterceptor extends HandlerInterceptorAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(LogApiInterceptor.class);

	public String getRawHeaders(HttpServletRequest request) {
            StringBuffer rawHeaders = new StringBuffer();
            Enumeration headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                    String key = (String) headerNames.nextElement();
                    String value = request.getHeader(key);
                    rawHeaders.append(key).append(":").append(value).append("\n");
            }

            return rawHeaders.toString();
    }

    public void writeRequestPayloadAudit(ResettableStreamHttpServletRequest wrappedRequest) {
	    try {
		    String requestHeaders = getRawHeaders(wrappedRequest);
		    String requestBody = org.apache.commons.io.IOUtils.toString(wrappedRequest.getReader());
		    logger.debug(":::::::::::::::REQUEST starts:::::::::::::::::::::::::");
		    logger.debug("Request Method: "+wrappedRequest.getMethod());
		    logger.debug("Request Content type:::::: "+wrappedRequest.getContentType());
		    logger.debug("Request Headers:");
		    logger.debug(requestHeaders);
		    logger.debug("Request body:");
		    logger.debug(requestBody);
		    logger.debug(":::::::::::::::REQUEST ends:::::::::::::::::::::::::");
	    } catch (Exception e) {
	    	System.out.println(e);
	    }
    }
    
}