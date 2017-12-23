package ru.isalnikov.fastechoserver;

import lombok.extern.log4j.Log4j2;
import ru.isalnikov.fastechoserver.jetty.JettyServer;
import ru.isalnikov.fastechoserver.netty.HttpSnoopServer;

/**
 *
 * @author Igor Salnikov igor.salnikov@stoloto.ru
 */
@Log4j2
public class Runner {

    private static final String JETTY = "jetty";
    private static final String NETTY = "netty";
    public static EchoServer echoServer;

    public static void main(String[] args) {
        try {
            if (!(args == null || args.length == 0 || (args[0] != null && JETTY.equals(args[0])))) {
                log.info("Run Jetty");
                JettyServer.main(args);
            } else {
                log.info("Run Netty");
                HttpSnoopServer.main(args);
            }

        } catch (Exception ex) {
            log.error(ex);
            System.exit(0);
        }
    }
}
