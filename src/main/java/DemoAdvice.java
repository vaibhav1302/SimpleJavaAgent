import io.prometheus.client.Counter;
import net.bytebuddy.asm.Advice;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class DemoAdvice {

    public static final Counter httpRequestsTotal = Counter
            .build("http_requests_total", "Total number of HTTP requests")
            .labelNames("path")
            .register();

    @Advice.OnMethodEnter
    public static void before(ServletRequest request, ServletResponse response) {
        // TODO: Check if request instanceof HttpServletRequest and if getPathInfo() returns null
        httpRequestsTotal.labels(((HttpServletRequest) request).getPathInfo()).inc();
        System.err.println("before serving the request...");
    }

    @Advice.OnMethodExit
    public static void after(ServletRequest request, ServletResponse response) {
        System.err.println("after serving the request...");
    }
}