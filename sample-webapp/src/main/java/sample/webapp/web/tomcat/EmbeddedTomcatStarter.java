package sample.webapp.web.tomcat;

import java.util.Locale;

class EmbeddedTomcatStarter {
    private static final String CONTEXTS = "classpath:/web-context.xml classpath:/dao-context.xml";

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        new EmbeddedTomcat(CONTEXTS).startServer();
    }
}
