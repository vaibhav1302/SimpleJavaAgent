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

    public static void premain(String args, Instrumentation instrumentation){
        ClassLogger transformer = new ClassLogger();
        instrumentation.addTransformer(transformer);
    }

    public static void agentmain(final String agentArgs,
                                 final Instrumentation inst) {
        System.out.println("Hey, look: I'm instrumenting a running JVM!:AgentMain");
    }
}