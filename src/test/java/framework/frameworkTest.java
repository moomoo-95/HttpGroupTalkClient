package framework;

import instance.BaseEnvironment;
import instance.DebugLevel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import moomoo.hgtp.grouptalk.network.handler.HttpChannelHandler;
import moomoo.hgtp.grouptalk.protocol.http.handler.HttpRequestMessageHandler;
import network.definition.NetAddress;
import network.socket.GroupSocket;
import network.socket.SocketManager;
import network.socket.SocketProtocol;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ResourceManager;
import service.scheduler.schedule.ScheduleManager;

public class frameworkTest {

    private static final Logger log = LoggerFactory.getLogger(frameworkTest.class);
    private static final String LOCAL_IP = "192.168.2.163";
    private static final String TARGET_IP = "192.168.2.163";
    private static final int LOCAL_PORT = 5100;
    private static final int TARGET_PORT = 5200;

    // NetAddress 생성
    private BaseEnvironment baseEnvironment;
    private SocketManager socketManager;

    private NetAddress httpLocalAddress;
    private NetAddress httpTargetAddress;

    @Test
    public void frameworkTest() {

        // 인스턴스 생성
        baseEnvironment = new BaseEnvironment( new ScheduleManager(), new ResourceManager(5000, 5100), DebugLevel.DEBUG );

        // SocketManager 생성
        socketManager = new SocketManager( baseEnvironment, true, false, 10, 1048576, 1048576 );

        httpLocalAddress = new NetAddress(LOCAL_IP, LOCAL_PORT,true, SocketProtocol.TCP);
        httpTargetAddress = new NetAddress(TARGET_IP, TARGET_PORT,true, SocketProtocol.TCP);

        ChannelInitializer httpChannelInitializer = new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel socketChannel) {
                final ChannelPipeline channelPipeline = socketChannel.pipeline();
                channelPipeline.addLast(new HttpChannelHandler());
            }
        };

        socketManager.addSocket(httpLocalAddress, httpChannelInitializer);

        GroupSocket localSocket = socketManager.getSocket(httpLocalAddress);
        localSocket.getListenSocket().openListenChannel();

        EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();
        ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossLoopGroup, workerLoopGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(httpChannelInitializer);
        try {
            ChannelFuture channelFuture = bootstrap.bind(TARGET_PORT).sync();
            channelGroup.add(channelFuture.channel());
        } catch (Exception e) {
            channelGroup.close();
            bossLoopGroup.shutdownGracefully();
            workerLoopGroup.shutdownGracefully();
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }

        localSocket.addDestination(httpTargetAddress, null, 111L, httpChannelInitializer);

        log.debug("{}", localSocket.getDestination(111L).toString());
        HttpRequestMessageHandler httpRequestMessageHandler = new HttpRequestMessageHandler();
//        UserInfo userInfo = new UserInfo("dlagustjd", LOCAL_IP, (short)LOCAL_PORT, (short) 5070, 3600L);
//        httpMessageHandler.sendRoomListRequest(userInfo, "192.168.2.163:5040");

    }
}
