package moomoo.hgtp.client.network;

import instance.BaseEnvironment;
import instance.DebugLevel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import moomoo.hgtp.client.config.ConfigManager;
import moomoo.hgtp.client.network.handler.HgtpChannelHandler;
import moomoo.hgtp.client.network.handler.HttpChannelHandler;
import moomoo.hgtp.client.service.AppInstance;
import network.definition.NetAddress;
import network.socket.GroupSocket;
import network.socket.SocketManager;
import network.socket.SocketProtocol;
import service.ResourceManager;
import service.scheduler.schedule.ScheduleManager;

public class NetworkManager {

    private static NetworkManager networkManager = null;
    private static final int MIN_PORT = 5000;
    private static final int MAX_PORT = 7000;
    private static final int SEND_BUF = 1048576;
    private static final int RECV_BUF = 1048576;

    // NetAddress 생성
    private BaseEnvironment baseEnvironment = null;
    private SocketManager socketManager = null;

    NetAddress hgtpAddress = null;
    NetAddress httpAddress = null;

    public NetworkManager() {
        // nothing
    }

    public static NetworkManager getInstance() {
        if (networkManager == null) {
            networkManager = new NetworkManager();
        }
        return networkManager;
    }

    public void startNetwork(){
        ConfigManager configManager = AppInstance.getInstance().getConfigManager();

        // 인스턴스 생성
        baseEnvironment = new BaseEnvironment( new ScheduleManager(), new ResourceManager(MIN_PORT, MAX_PORT), DebugLevel.DEBUG );

        // SocketManager 생성
        socketManager = new SocketManager( baseEnvironment, true, 10, SEND_BUF, RECV_BUF );

        hgtpAddress = new NetAddress(configManager.getLocalListenIp(), configManager.getHgtpListenPort(),true, SocketProtocol.UDP);
        httpAddress = new NetAddress(configManager.getLocalListenIp(), configManager.getHttpListenPort(), true, SocketProtocol.TCP);

        ChannelInitializer<NioDatagramChannel> hgtpChannelInitializer = new ChannelInitializer<NioDatagramChannel>() {
            @Override
            protected void initChannel(NioDatagramChannel datagramChannel) {
                final ChannelPipeline channelPipeline = datagramChannel.pipeline();
                channelPipeline.addLast(new HgtpChannelHandler());
            }
        };

        ChannelInitializer<NioSocketChannel> httpChannelInitializer = new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel socketChannel) {
                final ChannelPipeline channelPipeline = socketChannel.pipeline();
                channelPipeline.addLast(new HttpChannelHandler());
            }
        };

        socketManager.addSocket(hgtpAddress, hgtpChannelInitializer);
        socketManager.addSocket(httpAddress, httpChannelInitializer);

        GroupSocket hgtpGroupSocket = socketManager.getSocket(hgtpAddress);
        hgtpGroupSocket.getListenSocket().openListenChannel();

        GroupSocket httpGroupSocket = socketManager.getSocket(httpAddress);
        httpGroupSocket.getListenSocket().openListenChannel();
    }

    public void stopNetwork() {
        // 소켓 삭제
        if (socketManager != null) {
            if (socketManager.getSocket(hgtpAddress) != null) {
                socketManager.removeSocket(hgtpAddress);
            }

            if (socketManager.getSocket(httpAddress) != null) {
                socketManager.removeSocket(httpAddress);
            }
        }

        // 인스턴스 삭제
        if (baseEnvironment != null) {
            baseEnvironment.stop();
        }
    }
}
