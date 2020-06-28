package kafka.chatting.client.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import kafka.chatting.model.Message;

import java.util.Objects;

public class Client {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8888;
    private final String host;
    private final int port;
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;

    public Client(String host, String port) {
        this.host = Objects.requireNonNullElse(host, DEFAULT_HOST);
        this.port = Integer.parseInt(Objects.requireNonNullElse(port, Integer.toString(DEFAULT_PORT)));
        bootstrap();
    }

    private void bootstrap() {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel> () {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new StringDecoder(CharsetUtil.UTF_8), new StringEncoder(CharsetUtil.UTF_8))
                                .addLast(new ClientHandler());
                    }
                });
    }

    public void run() {
        try {
            channel = bootstrap.connect(host, port).channel();
            channel.closeFuture().sync();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void send(Message message) {
        try {
            System.out.println("Send > " + message);
            channel.writeAndFlush(message.toJsonString()).sync();
        } catch (Exception exception) {
            exception.printStackTrace();
            group.shutdownGracefully();
        }
    }
}