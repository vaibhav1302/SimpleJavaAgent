import com.sun.net.httpserver.HttpServer;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import net.bytebuddy.agent.builder.AgentBuilder;

import java.io.StringWriter;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;
import java.util.Collections;

import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class DemoAgent {

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        new AgentBuilder.Default()
                .type(hasSuperType(named("Controller")))
                .transform(new AgentBuilder.Transformer.ForAdvice()
                        .include(DemoAgent.class.getClassLoader())
                        .advice(named("get"), DemoAdvice.class.getName()))
                .installOn(inst);
        runHttpServer();

    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        //TODO: currently replacing the agent does not really work as all Agent versions share the same namespace in the same classpath
        try{
            premain(agentArgs, inst);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    static void runHttpServer() throws Exception {
        InetSocketAddress address = new InetSocketAddress(9300);
        HttpServer httpServer = HttpServer.create(address, 10);
        httpServer.createContext("/metrics", httpExchange -> {
            StringWriter respBodyWriter = new StringWriter();
            TextFormat.write004(respBodyWriter, CollectorRegistry.defaultRegistry.metricFamilySamples());
            byte[] respBody = respBodyWriter.toString().getBytes("UTF-8");
            httpExchange.getResponseHeaders().put("Context-Type", Collections.singletonList("text/plain; charset=UTF-8"));
            httpExchange.sendResponseHeaders(200, respBody.length);
            httpExchange.getResponseBody().write(respBody);
            httpExchange.getResponseBody().close();
        });
        httpServer.start();
    }
}