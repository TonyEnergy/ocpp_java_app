package github.tonyenergy.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import github.tonyenergy.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class IpLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String realIp = IpUtils.getClientIp(request);
        String proxyChain = IpUtils.getProxyIpChain(request);
        String path = request.getRequestURI();
        log.info("🌐 Request Path: {} | Real IP: {} | Agent Chain: {}", path, realIp, proxyChain);
        return true;
    }
}
