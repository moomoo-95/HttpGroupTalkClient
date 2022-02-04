package moomoo.hgtp.grouptalk.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import moomoo.hgtp.grouptalk.service.AppInstance;

public class HgtpChannelHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
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

        AppInstance.getInstance().putHgtpMessage(data);
    }
}
