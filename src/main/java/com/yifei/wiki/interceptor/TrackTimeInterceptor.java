package com.yifei.wiki.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring框架特有的拦截器，常用于d登录/权限校验，日志打印
 */
@Component
public class TrackTimeInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(TrackTimeInterceptor.class);

    /**
     * 目标方法执行之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.info("----Interceptor: Track Time starts----");
        LOG.info("Request URL: {} {}", request.getRequestURL().toString(), request.getMethod());
        LOG.info("Remote Address: {}", request.getRemoteAddr());
        long startTime = System.currentTimeMillis();
        request.setAttribute("requestStartTime", startTime);
        return true;
    }

    /**
     * 目标方法执行之后
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("requestStartTime");
        LOG.info("Time spent: {} ms", System.currentTimeMillis() - startTime);
        LOG.info("----Interceptor: Track Time ends----");
    }

    /**
     * 页面渲染完成之后，一般用来做一些清理工作
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
