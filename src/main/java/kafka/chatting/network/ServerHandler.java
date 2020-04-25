package kafka.chatting.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded of [SERVER]");
        Channel incoming = ctx.channel();
        // 사용자가 추가되었을 때 기존 사용자에게 알림
        channelGroup.forEach(channel -> channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " has joined!\r\n"));
        channelGroup.add(incoming);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 사용자가 접속했을 때 서버에 표시
        System.out.println("User Access! > " + ctx.channel().remoteAddress());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved of [SERVER]");
        Channel incoming = ctx.channel();
        // 사용자가 나갔을 때 기존 사용자에게 알림
        channelGroup.forEach(channel -> channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " has left!\r\n"));
        channelGroup.remove(incoming);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        System.out.println("channelRead of [SERVER] " + message);
        Channel incoming = ctx.channel();
        channelGroup.stream()
                .filter(channel -> !channel.equals(incoming))
                .forEach(channel -> channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + message + "\r\n"));

        if ("bye".equals(message.toLowerCase())) {
            incoming.close();
//            ctx.close();
        }
    }
}