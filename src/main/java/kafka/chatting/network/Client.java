package kafka.chatting.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

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
                        ChannelPipeline pipeline = socketChannel.pipeline();

                        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());

                        pipeline.addLast(new ClientHandler());
                    }
                });

        try {
            channel = bootstrap.connect(HOST, PORT).sync().channel();

//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            Scanner in = new Scanner(System.in);

            System.out.println("gg");

            ChannelFuture lastWriteFuture = null;

            while (true) {
                System.out.print("input > ");
                System.out.flush();;
                String message = in.nextLine();

                lastWriteFuture = channel.writeAndFlush(message + "\r\n");

                if ("bye".equals(message.toLowerCase())) {
//                    sync(channel.closeFuture());
                    channel.closeFuture().sync();
                    break;
                }
            }

            if (lastWriteFuture != null) {
//                    sync(lastWriteFuture);
                lastWriteFuture.sync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }


    }

    public void run() {
        try {
            channel = bootstrap.connect(HOST, PORT)
                    .sync()
                    .channel();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void send(String msg) {
        if ("bye".equals(msg.toLowerCase())) {
            sync(channel.closeFuture());
            return;
        }

        ChannelFuture lastWriteFuture = channel.writeAndFlush(msg + "\r\n");

        if (lastWriteFuture != null) {
            sync(lastWriteFuture);
        }
    }

    private void sync(ChannelFuture channelFuture) {
        try {
            channelFuture.sync();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
//        client.run();


    }
}