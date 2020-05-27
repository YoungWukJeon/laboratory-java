package kafka.chatting.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import kafka.chatting.model.Message;
import kafka.chatting.model.User;
import kafka.chatting.ui.EventTarget;

import java.util.HashSet;
import java.util.Set;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 8888;
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;
    private static Client client;
    private User user;
    private final Set<Integer> joinChatRoomNos = new HashSet<> ();
    private final ClientHandler clientHandler = new ClientHandler();

    private Client() {
        bootstrap();
    }

    public static Client getInstance() {
        if (client == null) {
            client = new Client();
        }
        return client;
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
                                .addLast(clientHandler);
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

    public void send(final Message message) {
        try {
            channel.writeAndFlush(message.toJsonString()).sync();
        } catch (Exception exception) {
            exception.printStackTrace();
            group.shutdownGracefully();
        }
    }

    public void setEventTarget(EventTarget<Message> eventTarget) {
        clientHandler.setEventTarget(eventTarget);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void addChatRoomNo(int chatRoomNo) {
        joinChatRoomNos.add(chatRoomNo);
        System.out.println("Current user(" + user + ") room list joined => "  + this.joinChatRoomNos);
    }

    public boolean isJoinedChatRoomNo(int chatRoomNo) {
        return joinChatRoomNos.contains(chatRoomNo);
    }
}