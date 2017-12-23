package ru.isalnikov.fastechoserver.netty;

/**
 *
 * @author Igor Salnikov igor.salnikov@stoloto.ru
 */
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import ru.isalnikov.fastechoserver.EchoServer;

/**
 * http://netty.io/4.1/xref/io/netty/example/http/snoop/HttpSnoopServerHandler.htmls
 *
 * An HTTP server that sends back the content of the received HTTP request in a
 * pretty plaintext form.
 */
public final class HttpSnoopServer implements EchoServer{

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        // Configure the server.
        EventLoopGroup bossGroup = new EpollEventLoopGroup(1);//NioEventLoopGroup
        EventLoopGroup workerGroup = new EpollEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(EpollServerSocketChannel.class) //  import io.netty.channel.socket.nio.NioServerSocketChannel;
                    //.handler(new LoggingHandler(LogLevel.ERROR)) //logger
                    .childHandler(new HttpSnoopServerInitializer(sslCtx));

            Channel ch = b.bind(PORT).sync().channel();

            System.err.println("Open your web browser and navigate to "
                    + (SSL ? "https" : "http") + "://127.0.0.1:" + PORT + '/');
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
