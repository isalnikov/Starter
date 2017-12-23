package ru.isalnikov.fastechoserver.jetty;

import ru.isalnikov.fastechoserver.EchoServer;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.HttpConnectionFactory;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 * https://www.eclipse.org/jetty/documentation/9.4.x/embedded-examples.html
 * http://www.baeldung.com/jetty-embedded
 *
 * @author Igor Salnikov 
 */
public class JettyServer implements EchoServer{

    public static final int scJettyMaxThreads = 5_000;
    public static final int scJettyMinThreads = 1_000;
    public static final int scJettyIdleTimeout = 60_000;
    public static final int scJettyQueueSize = 5_000;
    public static final int scJettyPort = 8080;
    
    
    public static final  AtomicLong longAdder = new AtomicLong(0);

    public static void main(String[] args) throws Exception {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(scJettyQueueSize);

        QueuedThreadPool threadPool = new QueuedThreadPool(scJettyMaxThreads, scJettyMinThreads, scJettyIdleTimeout, queue);
        Server server = new Server(threadPool);

        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory());
        connector.setPort(scJettyPort);
        server.addConnector(connector);

        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        handler.addServletWithMapping(EchoServlet.class, "/*");

        server.start();

        server.join();
    }

    @SuppressWarnings("serial")
    public static class EchoServlet extends HttpServlet {

        @Override
        protected void doGet(
                HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {

            try {
                response.setContentType("text/plain");
                response.setStatus(HttpServletResponse.SC_OK);
                
                response.getWriter().println("REQUEST_SIGN=" + longAdder.incrementAndGet());
                response.getWriter().flush();
            } finally {
                
                response.getWriter().close();
            }

        }

        @Override
        protected void doPost(
                HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {
            doGet(request, response);

        }
    }
}
