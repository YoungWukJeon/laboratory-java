package chatting.server.network;

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
import chatting.model.Message;
import chatting.model.User;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class Server {
    public static final AttributeKey<User> USER = AttributeKey.newInstance("user");
    public static final AttributeKey<Set<Integer>> CHAT_ROOM_NO = AttributeKey.newInstance("chat_room_no");
    public static final int DEFAULT_PORT = 8888;
    private final int port;
    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;
    private ServerBootstrap serverBootstrap;
    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public Server(String port) {
        this.port = Integer.parseInt(Objects.requireNonNullElse(port, Integer.toString(DEFAULT_PORT)));
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

    public int getPort() {
        return port;
    }

    public void run() {
        try {
            serverBootstrap.bind(port)
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
                .filter(Predicate.not(channel -> channel.attr(Server.CHAT_ROOM_NO).get() == null))
                .filter(channel -> channel.attr(Server.CHAT_ROOM_NO).get().contains(message.getChatRoomNo()))
                .forEach(channel -> writeMessage(channel, message));
    }

    public void addUserInChatRoomNo(User user, int chatRoomNo) {
        channelGroup.stream()
                .filter(channel -> channel.attr(Server.USER).get().equals(user))
                .forEach(channel -> {
                    Set<Integer> chatRoomNoes = channel.attr(Server.CHAT_ROOM_NO).get();
                    if (chatRoomNoes == null) {
                        chatRoomNoes = new HashSet<>();
                        channel.attr(Server.CHAT_ROOM_NO).set(chatRoomNoes);
                    }
                    channel.attr(Server.CHAT_ROOM_NO).get().add(chatRoomNo);
                });
    }

    public void removeUserInChatRoomNo(User user, int chatRoomNo) {
        channelGroup.stream()
                .filter(channel -> channel.attr(Server.USER).get().equals(user))
                .forEach(channel -> channel.attr(Server.CHAT_ROOM_NO).get().remove(chatRoomNo));
    }

    private void writeMessage(Channel channel, Message message) {
        channel.writeAndFlush(message.toJsonString());
    }
}