package moomoo.hgtp.grouptalk.network;

import instance.BaseEnvironment;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.http.*;
import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.network.handler.DashHttpChannelHandler;
import moomoo.hgtp.grouptalk.network.handler.HgtpChannelHandler;
import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessageRoute;
import moomoo.hgtp.grouptalk.protocol.http.base.HttpMessageRouteTable;
import moomoo.hgtp.grouptalk.protocol.http.handler.HttpMessageHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import network.definition.NetAddress;
import network.socket.GroupSocket;
import network.socket.SocketManager;
import network.socket.SocketProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

import static moomoo.hgtp.grouptalk.protocol.http.base.HttpMessageType.*;

public class NetworkManager {

    private static final Logger log = LoggerFactory.getLogger(NetworkManager.class);
    private static final int SOCKET_THREAD_SIZE = 10;

    private static NetworkManager networkManager = null;

    private AppInstance appInstance = AppInstance.getInstance();

    // Hgtp / udp
    private final SocketManager udpSocketManager;
    // Http / tcp
    private final SocketManager tcpServerSocketManager;
    private final SocketManager tcpClientSocketManager;

    private final NetAddress hgtpLocalAddress;

    private final ConcurrentHashMap<String, NetAddress> httpServerAddressMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, NetAddress> httpClientAddressMap = new ConcurrentHashMap<>();

    private final ChannelInitializer<NioDatagramChannel> hgtpChannelInitializer;

    private final HttpMessageRouteTable routeTable;
    private final ChannelInitializer<SocketChannel> httpMessageServerInitializer;
    private final ChannelInitializer<SocketChannel> httpMessageClientInitializer;

    public NetworkManager() {
        ConfigManager configManager = appInstance.getConfigManager();

        BaseEnvironment baseEnvironment = appInstance.getBaseEnvironment();

        // SocketManager 생성
        udpSocketManager = new SocketManager(baseEnvironment, false, false, SOCKET_THREAD_SIZE, configManager.getSendBufSize(), configManager.getRecvBufSize());
        tcpServerSocketManager = new SocketManager(baseEnvironment, true, true, SOCKET_THREAD_SIZE, configManager.getSendBufSize(), configManager.getRecvBufSize());
        tcpClientSocketManager = new SocketManager(baseEnvironment, true, false, SOCKET_THREAD_SIZE, configManager.getSendBufSize(), configManager.getRecvBufSize());

        // http message route table 설정
        routeTable = new HttpMessageRouteTable();

        routeTable.addRoute(new HttpMessageRoute(HttpMethod.POST, ROOM_LIST, new HttpMessageHandler()));
        routeTable.addRoute(new HttpMessageRoute(HttpMethod.POST, USER_LIST, new HttpMessageHandler()));
        routeTable.addRoute(new HttpMessageRoute(HttpMethod.POST, ROOM_USER_LIST, new HttpMessageHandler()));
        routeTable.addRoute(new HttpMessageRoute(HttpMethod.POST, MESSAGE, new HttpMessageHandler()));
        routeTable.addRoute(new HttpMessageRoute(HttpMethod.POST, NOTICE, new HttpMessageHandler()));
        routeTable.addRoute(new HttpMessageRoute(HttpMethod.POST, REFRESH, new HttpMessageHandler()));

        // HGTP , HTTP local 주소 설정
        hgtpLocalAddress = new NetAddress(configManager.getLocalListenIp(), configManager.getHgtpListenPort(),true, SocketProtocol.UDP);

        hgtpChannelInitializer = new ChannelInitializer<NioDatagramChannel>() {
            @Override
            protected void initChannel(NioDatagramChannel datagramChannel) {
                final ChannelPipeline channelPipeline = datagramChannel.pipeline();
                channelPipeline.addLast(new HgtpChannelHandler());
            }
        };

        httpMessageServerInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                final ChannelPipeline p = socketChannel.pipeline();
                p.addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192, false));
                p.addLast("aggregator", new HttpObjectAggregator(100 * 1024 * 1024));
                p.addLast("encoder", new HttpResponseEncoder());
                p.addLast("handler", new DashHttpChannelHandler(routeTable));
            }
        };

        httpMessageClientInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                final ChannelPipeline p = socketChannel.pipeline();
                p.addLast("decoder", new HttpResponseDecoder(4096, 8192, 8192, false));
                p.addLast("aggregator", new HttpObjectAggregator(100 * 1024 * 1024));
                p.addLast("encoder", new HttpRequestEncoder());
                p.addLast("handler", new DashHttpChannelHandler(routeTable));
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
        if ( !udpSocketManager.addSocket(hgtpLocalAddress, hgtpChannelInitializer) ) {
            log.error("{} port is unavailable.", hgtpLocalAddress.getPort());
            System.exit(1);
        }

        GroupSocket hgtpGroupSocket = udpSocketManager.getSocket(hgtpLocalAddress);
        hgtpGroupSocket.getListenSocket().openListenChannel();
    }

    public void stopSocket() {
        // 소켓 삭제
        if (udpSocketManager != null && udpSocketManager.getSocket(hgtpLocalAddress) != null) {
            udpSocketManager.removeSocket(hgtpLocalAddress);
        }
        if (tcpServerSocketManager != null && httpServerAddressMap.size() > 0) {
            httpServerAddressMap.forEach( (key, address) -> {
                if (tcpServerSocketManager.getSocket(address) != null) {
                    tcpServerSocketManager.removeSocket(address);
                }
            });

            httpServerAddressMap.clear();
        }
        if (tcpClientSocketManager != null && httpClientAddressMap.size() > 0) {
            httpClientAddressMap.forEach( (key, address) -> {
                if (tcpClientSocketManager.getSocket(address) != null) {
                    tcpClientSocketManager.removeSocket(address);
                }
            });

            httpClientAddressMap.clear();
        }
    }

    public NetAddress getHttpSocket(String userId, boolean isServerSocket) {
        return isServerSocket ? httpServerAddressMap.get(userId) : httpClientAddressMap.get(userId);
    }

    public boolean addHttpSocket(String userId, NetAddress httpAddress, boolean isServerSocket) {
        GroupSocket httpGroupSocket;
        if (isServerSocket) {
            if (tcpServerSocketManager.addSocket(httpAddress, httpMessageServerInitializer)) {
                httpGroupSocket = tcpServerSocketManager.getSocket(httpAddress);
                if (httpGroupSocket.getListenSocket().openListenChannel()) {
                    synchronized (httpServerAddressMap) {
                        httpServerAddressMap.put(userId, httpAddress);
                    }
                    return true;
                }
            }
        } else {
            if (tcpClientSocketManager.addSocket(httpAddress, httpMessageClientInitializer)){
                httpGroupSocket = tcpClientSocketManager.getSocket(httpAddress);
                if (httpGroupSocket.getListenSocket().openListenChannel()) {
                    synchronized (httpClientAddressMap) {
                        httpClientAddressMap.put(userId, httpAddress);
                    }
                    return true;
                }
            }
        }
        removeHttpSocket(userId, isServerSocket);
        return false;
    }

    public void removeHttpSocket(String userId, boolean isServerSocket){
        NetAddress httpAddress = isServerSocket ? httpServerAddressMap.get(userId) : httpClientAddressMap.get(userId);

        if (httpAddress == null) {
            log.debug("({}) () () httpAddress already removed.", userId);
            return;
        }
        appInstance.getResourceManager().restorePort(httpAddress.getPort());

        if (isServerSocket && tcpServerSocketManager.getSocket(httpAddress) != null) {
            tcpServerSocketManager.removeSocket(httpAddress);
        }

        if (!isServerSocket && tcpClientSocketManager.getSocket(httpAddress) != null) {
            tcpClientSocketManager.removeSocket(httpAddress);
        }

        synchronized (httpServerAddressMap) {
            httpServerAddressMap.remove(userId);
        }
    }

    public void addDestinationHgtpSocket(UserInfo userInfo) {
        GroupSocket hgtpGroupSocket = getHgtpGroupSocket();

        hgtpGroupSocket.addDestination(userInfo.getHgtpTargetNetAddress(), null, userInfo.getSessionId(), hgtpChannelInitializer);
        log.debug("({}) () () add Destination ok. [{}] -> [{}]", userInfo.getUserId(), hgtpGroupSocket.getListenSocket().getNetAddress().getPort(), hgtpGroupSocket.getDestination(userInfo.getSessionId()).getGroupEndpointId().getGroupAddress().getPort());
    }

    public void addDestinationHttpSocket(UserInfo userInfo) {
        GroupSocket httpGroupSocket = getHttpGroupSocket(userInfo.getUserId(), false);

        httpGroupSocket.addDestination(userInfo.getHttpTargetNetAddress(), null, userInfo.getSessionId(), httpMessageClientInitializer);
        log.debug("({}) () () add Destination ok. [{}] -> [{}]", userInfo.getUserId(), httpGroupSocket.getListenSocket().getNetAddress().getPort(), httpGroupSocket.getDestination(userInfo.getSessionId()).getGroupEndpointId().getGroupAddress().getPort());
    }

    public GroupSocket getHgtpGroupSocket() {return udpSocketManager.getSocket(hgtpLocalAddress);}

    public GroupSocket getHttpGroupSocket(String userId, boolean isServerSocket) {
        if (httpServerAddressMap.get(userId) == null) {
            log.warn("({}) () () httpAddress do not exist.", userId);
            return null;
        }
        return isServerSocket ? tcpServerSocketManager.getSocket(httpServerAddressMap.get(userId)) : tcpClientSocketManager.getSocket(httpClientAddressMap.get(userId));
    }
}
