package github.tonyenergy.utils;

import javax.servlet.http.HttpServletRequest;

public class IpUtils {
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }

    public static String getProxyIpChain(HttpServletRequest request) {
        String ipChain = request.getHeader("X-Forwarded-For");
        if (ipChain != null && !ipChain.isEmpty() && !"unknown".equalsIgnoreCase(ipChain)) {
            return ipChain.trim();
        }
        return request.getRemoteAddr();
    }
}

