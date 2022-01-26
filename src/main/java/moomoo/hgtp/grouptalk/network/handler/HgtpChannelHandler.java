package moomoo.hgtp.grouptalk.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import moomoo.hgtp.grouptalk.protocol.hgtp.HgtpManager;

public class HgtpChannelHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) {
        ByteBuf buf = datagramPacket.content();
        if (buf == null) {
            return;
        }

        int readBytes = buf.readableBytes();
        if (buf.readableBytes() <= 0) {
            return;
        }

        byte[] data = new byte[readBytes];
        buf.getBytes(0, data);

        HgtpManager.getInstance().putMessage(data);
    }
}
