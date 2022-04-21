package group.youwinn.config;

import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Component
public class RequestFilter implements Filter {
    @Autowired
    LogApiInterceptor logApiInterceptor;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
            // LOG REQUEST
            ResettableStreamHttpServletRequest wrappedRequest = null;
            try {
                    wrappedRequest = new ResettableStreamHttpServletRequest((HttpServletRequest) request);
                    logApiInterceptor.writeRequestPayloadAudit(wrappedRequest);
            } catch (Exception e) {
            }
            chain.doFilter(wrappedRequest, response);
    }
}