package moomoo.hgtp.grouptalk.network;

import instance.BaseEnvironment;
import instance.DebugLevel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.network.handler.HgtpChannelHandler;
import moomoo.hgtp.grouptalk.network.handler.HttpChannelHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import network.definition.NetAddress;
import network.socket.GroupSocket;
import network.socket.SocketManager;
import network.socket.SocketProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ResourceManager;
import service.scheduler.schedule.ScheduleManager;

import java.util.concurrent.ConcurrentHashMap;

public class NetworkManager {

    private static final Logger log = LoggerFactory.getLogger(NetworkManager.class);
    private static final int SOCKET_THREAD_SIZE = 10;

    private static NetworkManager networkManager = null;

    private AppInstance appInstance = AppInstance.getInstance();
    private ConfigManager configManager = appInstance.getConfigManager();

    // NetAddress 생성
    private final BaseEnvironment baseEnvironment;

    // Hgtp / udp
    private final SocketManager udpSocketManager;
    // Http / tcp
    private final SocketManager tcpServerSocketManager;
    private final SocketManager tcpClientSocketManager;

    private final NetAddress hgtpLocalAddress;

    private final ConcurrentHashMap<String, NetAddress> httpAddressMap = new ConcurrentHashMap<>();

    private final ChannelInitializer<NioDatagramChannel> hgtpChannelInitializer;
    private final ChannelInitializer<NioSocketChannel> httpChannelInitializer;

    public NetworkManager() {
        // 인스턴스 생성
        baseEnvironment = new BaseEnvironment( new ScheduleManager(), new ResourceManager(configManager.getHttpMinPort(), configManager.getHttpMaxPort()), DebugLevel.DEBUG );

        // SocketManager 생성
        udpSocketManager = new SocketManager(baseEnvironment, false, false, SOCKET_THREAD_SIZE, configManager.getSendBufSize(), configManager.getRecvBufSize());
        tcpServerSocketManager = new SocketManager(baseEnvironment, true, true, SOCKET_THREAD_SIZE, configManager.getSendBufSize(), configManager.getRecvBufSize());
        tcpClientSocketManager = new SocketManager(baseEnvironment, true, false, SOCKET_THREAD_SIZE, configManager.getSendBufSize(), configManager.getRecvBufSize());

        // HGTP , HTTP local 주소 설정
        hgtpLocalAddress = new NetAddress(configManager.getLocalListenIp(), configManager.getHgtpListenPort(),true, SocketProtocol.UDP);

        hgtpChannelInitializer = new ChannelInitializer<NioDatagramChannel>() {
            @Override
            protected void initChannel(NioDatagramChannel datagramChannel) {
                final ChannelPipeline channelPipeline = datagramChannel.pipeline();
                channelPipeline.addLast(new HgtpChannelHandler());
            }
        };

        httpChannelInitializer = new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel socketChannel) {
                final ChannelPipeline channelPipeline = socketChannel.pipeline();
                channelPipeline.addLast(new HttpChannelHandler());
            }
        };
    }

    public static NetworkManager getInstance() {
        if (networkManager == null) {
            networkManager = new NetworkManager();
        }
        return networkManager;
    }

    public void startSocket(){
        // socketManager에 추가 및 listen
        udpSocketManager.addSocket(hgtpLocalAddress, hgtpChannelInitializer);

        GroupSocket hgtpGroupSocket = udpSocketManager.getSocket(hgtpLocalAddress);
        hgtpGroupSocket.getListenSocket().openListenChannel();
    }

    public void stopSocket() {
        // 소켓 삭제
        if (udpSocketManager != null) {
            if (udpSocketManager.getSocket(hgtpLocalAddress) != null) {
                udpSocketManager.removeSocket(hgtpLocalAddress);
            }
        }
        if (tcpClientSocketManager != null && tcpServerSocketManager != null) {
            if (httpAddressMap.size() > 0) {
                httpAddressMap.forEach( (key, address) -> {
                    if (tcpClientSocketManager.getSocket(address) != null) {
                        tcpClientSocketManager.removeSocket(address);
                    } else if (tcpServerSocketManager.getSocket(address) != null) {
                        tcpServerSocketManager.removeSocket(address);
                    }
                });

                httpAddressMap.clear();
            }
        }

        // 인스턴스 삭제
        if (baseEnvironment != null) {
            baseEnvironment.stop();
        }
    }

    public NetAddress getHttpSocket(String userId) {
        return httpAddressMap.get(userId);
    }

    public void addHttpSocket(String userId, NetAddress httpAddress, boolean isServerSocket) {
        if (isServerSocket) {
            tcpServerSocketManager.addSocket(httpAddress, httpChannelInitializer);
        } else {
            tcpClientSocketManager.addSocket(httpAddress, httpChannelInitializer);
        }

        GroupSocket httpGroupSocket;
        httpGroupSocket = isServerSocket ? tcpServerSocketManager.getSocket(httpAddress) : tcpClientSocketManager.getSocket(httpAddress);
        httpGroupSocket.getListenSocket().openListenChannel();

        synchronized (httpAddressMap) {
            httpAddressMap.put(userId, httpAddress);
        }
    }

    public void removeHttpSocket(String userId, boolean isServerSocket){
        NetAddress httpAddress = httpAddressMap.get(userId);

        if (httpAddress == null) {
            log.debug("({}) () () httpAddress already removed.", userId);
            return;
        }

        if (isServerSocket && tcpServerSocketManager.getSocket(httpAddress) != null) {
            tcpServerSocketManager.removeSocket(httpAddress);
        }

        if (!isServerSocket && tcpClientSocketManager.getSocket(httpAddress) != null) {
            tcpClientSocketManager.removeSocket(httpAddress);
        }

        synchronized (httpAddressMap) {
            httpAddressMap.remove(userId);
        }
    }

    public void addDestinationHgtpSocket(UserInfo userInfo) {
        GroupSocket hgtpGroupSocket = getHgtpGroupSocket();

        hgtpGroupSocket.addDestination(userInfo.getHgtpTargetNetAddress(), null, userInfo.getSessionId(), hgtpChannelInitializer);
        log.debug("({}) () () add Destination ok. [{}] -> [{}]", userInfo.getUserId(), hgtpGroupSocket.getListenSocket().getNetAddress().getPort(), hgtpGroupSocket.getDestination(userInfo.getSessionId()).toString());
    }

    public void addDestinationHttpSocket(UserInfo userInfo) {
        GroupSocket httpGroupSocket = getHttpGroupSocket(userInfo.getUserId(), false);

        httpGroupSocket.addDestination(userInfo.getHttpTargetNetAddress(), null, userInfo.getSessionId(), httpChannelInitializer);
        log.debug("({}) () () add Destination ok. [{}] -> [{}]", userInfo.getUserId(), httpGroupSocket.getListenSocket().getNetAddress().getPort(), httpGroupSocket.getDestination(userInfo.getSessionId()).toString());
    }


    public BaseEnvironment getBaseEnvironment() {return baseEnvironment;}

    public GroupSocket getHgtpGroupSocket() {return udpSocketManager.getSocket(hgtpLocalAddress);}

    public GroupSocket getHttpGroupSocket(String userId, boolean isServerSocket) {
        if (httpAddressMap.get(userId) == null) {
            log.warn("({}) () () httpAddress do not exist.", userId);
            return null;
        }
        return isServerSocket ? tcpServerSocketManager.getSocket(httpAddressMap.get(userId)) : tcpClientSocketManager.getSocket(httpAddressMap.get(userId));
    }
}
