package com.file.iInterceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器 验证是否登陆
 *
 * @author Administrator
 */
public class CertificationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        httpServletRequest.getSession().getAttribute("certification");

        Boolean flag = (Boolean) httpServletRequest.getSession().getAttribute("certification");
        if (flag == null || !flag) {
            modelAndView.setViewName("/login.html");
        }
    }
}
