package cz.cvut.fel.nss.chatgc.interceptor;

import com.hazelcast.config.CacheDeserializedValues;
import com.hazelcast.config.Config;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

/**
 * Represents interceptor that logging authorization.
 */
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private final static Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);

    /**
     * Writes logs before request was sent to controller.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        LOG.info("loggingInterceptor preHandler");
        LOG.info("request url: " + request.getRequestURL());
        return true;
    }

    /**
     * Writes logs after request was sent.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LOG.info("loggingInterceptor postHandler");
    }

    /**
     * Writes logs after request was completed.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LOG.info("loggingInterceptor afterCompletion");
    }

}
