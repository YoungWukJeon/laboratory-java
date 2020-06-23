package kafka.chatting.server.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import kafka.chatting.model.Message;
import kafka.chatting.model.User;

import java.util.Set;

public class Server {
    public static final AttributeKey<User> USER = AttributeKey.newInstance("user");
    public static final AttributeKey<Set<Integer>> CHAT_ROOM_NO = AttributeKey.newInstance("chat_room_no");
    private static final int PORT = 8888;
    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;
    private ServerBootstrap serverBootstrap;
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

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
                                .addLast(new ServerHandler(channelGroup));
                    }
                });
        System.out.println("Server is ready to connect clients.");
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

    public void send(Channel channel, Message message) {
        writeMessage(channel, message);
    }

    public void broadcast(Message message) {
        System.out.println("Server broadcast > " + message);
        channelGroup.stream()
//                .filter(Predicate.not(
//                        channel -> message.getCommandType() == Message.CommandType.JOIN
//                                && channel.attr(Server.USER).get().equals(message.getUser())))
                .filter(channel -> channel.attr(Server.CHAT_ROOM_NO).get() != null
                        && channel.attr(Server.CHAT_ROOM_NO).get().contains(message.getChatRoomNo()))
                .forEach(channel -> writeMessage(channel, message));
    }

    private void writeMessage(Channel channel, Message message) {
        channel.writeAndFlush(message.toJsonString());
    }
}