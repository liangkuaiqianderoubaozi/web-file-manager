package com.file.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 拦截器 验证是否登陆
 *
 * @author Administrator
 */
public class CertificationInterceptor implements HandlerInterceptor {

    public static final List<String> RESOURCE_PATH = List.of("/css/", "/img/","/js/","/font/");

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) {

        String requestURL = httpServletRequest.getRequestURL().toString();
        if (RESOURCE_PATH.stream().anyMatch(requestURL::contains)) {
            return;
        }


        httpServletRequest.getSession().getAttribute("certification");
        Boolean flag = (Boolean) httpServletRequest.getSession().getAttribute("certification");

        if (flag == null || !flag) {
            modelAndView.setViewName("/login.html");
        }
    }
}
