package kafka.chatting.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import kafka.chatting.model.CommandType;
import kafka.chatting.model.Message;
import kafka.chatting.model.MessageType;
import kafka.chatting.model.User;
import kafka.chatting.ui.EventTarget;

public class Client implements Runnable {
    private static final String HOST = "localhost";
    private static final int PORT = 8888;
    private EventLoopGroup group;
    private Bootstrap bootstrap;
    private Channel channel;
    private EventTarget<Message> eventTarget;
    private static Client client;
    private User user;

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
                                .addLast(new ClientHandler(eventTarget));
                    }
                });
    }

    @Override
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

    public void send(String text) {
        try {
            final Message message;

            if ("!quit".equals(text)) {
                message = makeMessage(CommandType.LEAVE, text);
            } else {
                message = makeMessage(CommandType.NORMAL, text);
            }
            ChannelFuture lastWriteFuture = channel.writeAndFlush(message.toJsonString());

            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            group.shutdownGracefully();
        }
    }

    public void setEventTarget(EventTarget eventTarget) {
        this.eventTarget = eventTarget;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }

    private Message makeMessage(CommandType type, String message) {
        return Message.builder()
                .messageType(MessageType.CLIENT)
                .commandType(type)
                .user(user)
                .message(message)
                .build();
    }
}