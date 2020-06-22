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

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 8888;
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;

    public Client() {
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
            channel = bootstrap.connect(HOST, PORT).channel();
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