package kafka.chatting.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import kafka.chatting.model.User;

public class Server {
    public final static AttributeKey<User> USER_NAME = AttributeKey.newInstance("user");
    private static final int PORT = 8888;
    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;
    private ServerBootstrap serverBootstrap;

    public Server() {
        bootstrap();
    }

    private void bootstrap() {
        parentGroup = new NioEventLoopGroup(1);
        childGroup = new NioEventLoopGroup();

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .childHandler(new ChannelInitializer<SocketChannel> () {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new StringDecoder(CharsetUtil.UTF_8), new StringEncoder(CharsetUtil.UTF_8))
                                .addLast(new ServerHandler());
                    }
                });
    }

    public void run() {
        try {
            serverBootstrap.bind(PORT)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}