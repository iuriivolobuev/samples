package sample.webapp.web;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.ProtocolHandler;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyMap;

class EmbeddedTomcat {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedTomcat.class);
    private static final int PORT = 8080;
    private static final boolean DAEMON = true;
    private static final int THREAD_PRIORITY = Thread.NORM_PRIORITY;

    private final Tomcat tomcat;
    private final Context context;

    public EmbeddedTomcat(String springConfigLocation) {
        this.tomcat = new Tomcat();
        this.tomcat.setBaseDir(System.getProperty("java.io.tmpdir"));
        this.context = this.tomcat.addContext("", null);
        this.context.setSessionTimeout(30);
        this.context.addServletContainerInitializer(new org.apache.tomcat.websocket.server.WsSci(), null);
        this.tomcat.setConnector(createConnector());
        addSpringContext(springConfigLocation);

        addFilter(CharacterEncodingFilter.class, "encodingFilter", Map.of("encoding", "UTF-8"));
        addFilter(ForwardedHeaderFilter.class, "forwardedHeaderFilter", emptyMap());
        //addFilter(DelegatingFilterProxy.class, "springSecurityFilterChain"/*the name is important*/, emptyMap());
    }

    public void startServer() {
        try {
            this.tomcat.start();
            this.tomcat.getServer().await();
        } catch (LifecycleException ex) {
            LOGGER.error("Can't start Tomcat.", ex);
            throw new RuntimeException(ex);
        }
    }

    private static Connector createConnector() {
        Connector connector = new Connector();
        connector.setPort(PORT);
        ProtocolHandler protocolHandler = connector.getProtocolHandler();
        protocolHandler.setExecutor(createExecutor());
        connector.setParseBodyMethods("POST,PUT,PATCH,DELETE");
        return connector;
    }

    private static Executor createExecutor() {
        int minThreads = Runtime.getRuntime().availableProcessors();
        int maxThreads = minThreads * 3;
        TaskThreadFactory tf = new TaskThreadFactory(String.format("http-%s-", PORT), DAEMON, THREAD_PRIORITY);
        return new ThreadPoolExecutor(minThreads, maxThreads, 60, TimeUnit.SECONDS, new TaskQueue(), tf);
    }

    private void addSpringContext(String contextConfigLocation) {
        Context tomcatContext = this.context;
        Container springContext = tomcatContext.findChild("spring-dispatcher");
        if (springContext == null) {
            Wrapper spring = tomcatContext.createWrapper();
            spring.setName("spring-dispatcher");
            spring.setServletClass("org.springframework.web.servlet.DispatcherServlet");
            spring.addInitParameter("contextConfigLocation", contextConfigLocation);
            spring.setLoadOnStartup(1);
            tomcatContext.addChild(spring);
            tomcatContext.addServletMappingDecoded("/", "spring-dispatcher");
        }
    }

    private void addFilter(Class<?> filterClass, String filterName, Map<String, String> params) {
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterClass(filterClass.getName());
        filterDef.setFilterName(filterName);
        for (Map.Entry<String, String> param : params.entrySet())
            filterDef.addInitParameter(param.getKey(), param.getValue());
        this.context.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(filterName);
        filterMap.addURLPattern("/*");
        this.context.addFilterMap(filterMap);
    }
}
