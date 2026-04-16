package sample.basic.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tomcat:
 * <ul>
 *     <li>maxConnections = number of connections that are being processed by servlets + those that were just accepted</li>
 *     <li>maxConnections + acceptCount (backlog), additional connections will be refused</li>
 * </ul>
 *
 * <p>Timeouts:
 * <ul>
 *     <li>connection timeout (before accept if wasn't accepted during some time)</li>
 *     <li>socket timeout (after accept if was idle during some time)</li>
 * </ul>
 *
 * <p>Example:
 * <ul>
 *     <li>in server.xml: maxThreads = 5, maxConnections = 6, acceptCount = 10</li>
 * </ul>
 */
class TomcatClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatClient.class);

    /**
     * Before running this, run sample.webapp.web.EmbeddedTomcatStarter#main (also, add some delay to the endpoint).
     */
    public static void main(String[] args) {
        int threadCount = 20;
        try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
            for (int threadIdx = 0; threadIdx < threadCount; threadIdx++) {
                int connIdx = threadIdx;
                executorService.execute(() -> {
                    try {
                        URI.create("http://localhost:8080/app/info").toURL().openConnection().getInputStream();
                        LOGGER.info("connection #{} completed", connIdx);
                    } catch (IOException e) {
                        LOGGER.error("connection #{} failed, cause=\"{}\"", connIdx, e.getMessage());
                    }
                });
            }
            executorService.shutdown();
        }
    }
}
