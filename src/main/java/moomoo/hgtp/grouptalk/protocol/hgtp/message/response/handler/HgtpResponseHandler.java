package moomoo.hgtp.grouptalk.protocol.hgtp.message.response.handler;

import moomoo.hgtp.grouptalk.config.ConfigManager;
import moomoo.hgtp.grouptalk.fsm.HgtpEvent;
import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.gui.component.panel.ControlPanel;
import moomoo.hgtp.grouptalk.network.NetworkManager;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessage;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content.HgtpUnauthorizedContent;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.HgtpRegisterRequest;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler.HgtpRequestHandler;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.*;
import moomoo.hgtp.grouptalk.protocol.http.handler.HttpMessageHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.service.base.ProcessMode;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.RoomInfo;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import moomoo.hgtp.grouptalk.util.NetworkUtil;
import network.definition.DestinationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class HgtpResponseHandler {

    private static final Logger log = LoggerFactory.getLogger(HgtpResponseHandler.class);
    private static final String RECV_LOG = "({}) () () RECV HGTP MSG [{}]";
    private static final String HEADER_LOG = "() () () header is null [{}]";

    private static AppInstance appInstance = AppInstance.getInstance();
    private static SessionManager sessionManager = SessionManager.getInstance();
    private static NetworkManager networkManager = NetworkManager.getInstance();

    public HgtpResponseHandler() {
        // nothing
    }

    /**
     * @fn okResponseProcessing
     * @brief ok 수신시 처리하는 메서드
     * @param hgtpOkResponse
     */
    public void okResponseProcessing(HgtpCommonResponse hgtpOkResponse) {
        HgtpHeader hgtpHeader = hgtpOkResponse.getHgtpHeader();
        if (hgtpHeader == null) {
            log.debug(HEADER_LOG, hgtpOkResponse);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpOkResponse);

        if (appInstance.getMode() == ProcessMode.SERVER) {
            UserInfo userInfo = sessionManager.getUserInfo(hgtpHeader.getUserId());
            short messageType = HgtpMessageType.OK;
            if (userInfo == null) {
                return;
            }
            RoomInfo roomInfo = sessionManager.getRoomInfo(userInfo.getRoomId());
            if (roomInfo == null) {
                return;
            }
            final String processResult;
            switch (hgtpHeader.getRequestType()) {
                case HgtpMessageType.INVITE_USER_FROM_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.INVITE_USER_ROOM_SUC, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    roomInfo.addUserGroupSet(userInfo.getUserId());
                    processResult = "초대 되었";
                    break;
                case HgtpMessageType.REMOVE_USER_FROM_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.REMOVE_USER_ROOM_SUC, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    roomInfo.removeUserGroupSet(userInfo.getUserId());
                    processResult = "퇴장 당하였";
                    break;
                default:
                    return;
            }

            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    messageType, hgtpHeader.getRequestType(),
                    roomInfo.getManagerId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

            sendCommonResponse(hgtpCommonResponse);

            HttpMessageHandler httpRequestMessageHandler = new HttpMessageHandler();
            roomInfo.getUserGroupSet().forEach(roomUserId -> {
                UserInfo roomUserInfo = sessionManager.getUserInfo(roomUserId);
                if (roomUserInfo != null) {
                    httpRequestMessageHandler.sendRoomUserListRequest(roomUserInfo);
                    httpRequestMessageHandler.sendNoticeRequest("[" + userInfo.getHostName() + "]님이 " + processResult + "습니다.", roomUserInfo);
                }
            });
        } else if (appInstance.getMode() == ProcessMode.CLIENT) {
            GuiManager guiManager = GuiManager.getInstance();
            ControlPanel controlPanel = guiManager.getControlPanel();

            UserInfo userInfo = sessionManager.getUserInfo(hgtpHeader.getUserId());
            switch (hgtpHeader.getRequestType()) {
                case HgtpMessageType.REGISTER:
                    appInstance.getStateHandler().fire(HgtpEvent.REGISTER_SUC, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    guiManager.setClientTitle(userInfo.getHostName());
                    controlPanel.setRegisterButtonStatus();
                    break;
                case HgtpMessageType.UNREGISTER:
                    break;
                case HgtpMessageType.CREATE_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.CREATE_ROOM_SUC, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    appInstance.setManager(true);

                    controlPanel.setCreateRoomButtonStatus();
                    break;
                case HgtpMessageType.DELETE_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.DELETE_ROOM_SUC, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    appInstance.setManager(false);
                    controlPanel.setRegisterButtonStatus();
                    RoomInfo roomInfo = sessionManager.getRoomInfo(userInfo.getRoomId());
                    sessionManager.deleteRoomInfo(roomInfo.getRoomId(), userInfo.getUserId());
                    break;
                case HgtpMessageType.JOIN_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.JOIN_ROOM_SUC, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    controlPanel.setJoinRoomButtonStatus();
                    break;
                case HgtpMessageType.EXIT_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.EXIT_ROOM_SUC, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    controlPanel.setRegisterButtonStatus();
                    sessionManager.deleteRoomInfo(hgtpHeader.getUserId(), hgtpHeader.getUserId());
                    break;
                default:
            }
        }
    }

    /**
     * @fn badRequestResponseProcessing
     * @brief badRequest 수신시 처리하는 메서드
     * @param hgtpBadRequestResponse
     */
    public void badRequestResponseProcessing(HgtpCommonResponse hgtpBadRequestResponse) {
        HgtpHeader hgtpHeader = hgtpBadRequestResponse.getHgtpHeader();
        if (hgtpHeader == null) {
            log.debug(HEADER_LOG, hgtpBadRequestResponse);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpBadRequestResponse);

        if (appInstance.getMode() == ProcessMode.CLIENT) {

            UserInfo userInfo = sessionManager.getUserInfo(hgtpHeader.getUserId());
            switch (hgtpHeader.getRequestType()) {
                case HgtpMessageType.REGISTER:
                    appInstance.getStateHandler().fire(HgtpEvent.REGISTER_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    sessionManager.getUserInfo(appInstance.getUserId()).initHostName();
                    break;
                case HgtpMessageType.UNREGISTER:
                    break;
                case HgtpMessageType.CREATE_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.CREATE_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    RoomInfo roomInfo = sessionManager.getRoomInfo(userInfo.getRoomId());
                    sessionManager.deleteRoomInfo(roomInfo.getRoomId(), userInfo.getUserId());
                    break;
                case HgtpMessageType.DELETE_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.DELETE_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    break;
                case HgtpMessageType.JOIN_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.JOIN_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    sessionManager.deleteRoomInfo(hgtpHeader.getUserId(), hgtpHeader.getUserId());
                    break;
                case HgtpMessageType.EXIT_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.EXIT_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    break;
                default:
            }
        }

    }

    /**
     * @fn unauthorizedResponseProcessing
     * @brief unauthorized 수신시 처리하는 메서드
     * @param hgtpUnauthorizedResponse
     */
    public void unauthorizedResponseProcessing(HgtpUnauthorizedResponse hgtpUnauthorizedResponse) {
        ConfigManager configManager = appInstance.getConfigManager();

        HgtpHeader hgtpHeader = hgtpUnauthorizedResponse.getHgtpHeader();
        HgtpUnauthorizedContent hgtpRegisterContent = hgtpUnauthorizedResponse.getHgtpContent();

        if (hgtpHeader == null || hgtpRegisterContent == null) {
            log.debug("() () () header or content is null [{}]", hgtpUnauthorizedResponse);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpUnauthorizedResponse);

        // server 일 경우 bad request 전송
        if (appInstance.getMode() == ProcessMode.SERVER) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

            sendCommonResponse(hgtpCommonResponse);
            return;
        }

        String nonce = NetworkUtil.createNonce(AppInstance.ALGORITHM, hgtpRegisterContent.getRealm(), AppInstance.MD5_HASH_KEY);

        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());

        // http socket 설정 및 target address 설정
        userInfo.setHttpTargetNetAddress(configManager.getTargetListenIp(), hgtpRegisterContent.getListenPort());
        appInstance.getStateHandler().fire(HgtpEvent.REGISTER, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));

        // Send Register
        HgtpRegisterRequest hgtpRegisterRequest = new HgtpRegisterRequest(
                appInstance.getUserId(),
                hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT,
                configManager.getHgtpExpireTime(), configManager.getLocalListenIp(), (short) userInfo.getHttpServerNetAddress().getPort()
        );
        HgtpRequestHandler hgtpRequestHandler = new HgtpRequestHandler();
        hgtpRequestHandler.sendRegisterRequest(hgtpRegisterRequest, nonce);
    }

    public boolean forbiddenResponseProcessing(HgtpCommonResponse hgtpForbiddenResponse) {
        log.debug(RECV_LOG, hgtpForbiddenResponse.getHgtpHeader().getUserId(), hgtpForbiddenResponse);
        return true;
    }

    public void serverUnavailableResponseProcessing(HgtpCommonResponse hgtpServerUnavailableResponse) {
        HgtpHeader hgtpHeader = hgtpServerUnavailableResponse.getHgtpHeader();
        if (hgtpHeader == null) {
            log.debug(HEADER_LOG, hgtpServerUnavailableResponse);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpServerUnavailableResponse);

        if (appInstance.getMode() == ProcessMode.CLIENT) {
            ControlPanel controlPanel = GuiManager.getInstance().getControlPanel();

            UserInfo userInfo = sessionManager.getUserInfo(hgtpHeader.getUserId());
            switch (hgtpHeader.getRequestType()) {
                case HgtpMessageType.REGISTER:
                    appInstance.getStateHandler().fire(HgtpEvent.REGISTER_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    break;
                case HgtpMessageType.UNREGISTER:
                    appInstance.getStateHandler().fire(HgtpEvent.CREATE_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    controlPanel.setInitButtonStatus();
                    break;
                case HgtpMessageType.CREATE_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.CREATE_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    sessionManager.getUserInfo(appInstance.getUserId()).initRoomId();
                    break;
                case HgtpMessageType.DELETE_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.DELETE_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    break;
                case HgtpMessageType.JOIN_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.JOIN_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    sessionManager.getUserInfo(appInstance.getUserId()).initRoomId();
                    break;
                case HgtpMessageType.EXIT_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.EXIT_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    break;
                default:
            }
        }
    }

    /**
     * @fn declineResponseProcessing
     * @brief decline 수신시 처리하는 메서드 (server, proxy 는 relay, client 는 처리)
     * @param hgtpDeclineResponse
     */
    public void declineResponseProcessing(HgtpCommonResponse hgtpDeclineResponse) {
        HgtpHeader hgtpHeader = hgtpDeclineResponse.getHgtpHeader();
        if (hgtpHeader == null) {
            log.debug(HEADER_LOG, hgtpDeclineResponse);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpDeclineResponse);

        UserInfo userInfo = sessionManager.getUserInfo(hgtpHeader.getUserId());
        if (appInstance.getMode() == ProcessMode.SERVER) {
            short messageType = HgtpMessageType.DECLINE;
            if (userInfo == null) {
                return;
            }
            RoomInfo roomInfo = sessionManager.getRoomInfo(userInfo.getRoomId());
            if (roomInfo != null) {
                HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                        messageType, hgtpHeader.getRequestType(),
                        roomInfo.getManagerId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

                sendCommonResponse(hgtpCommonResponse);
            }
            switch (hgtpHeader.getRequestType()) {
                case HgtpMessageType.INVITE_USER_FROM_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.INVITE_USER_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    userInfo.initRoomId();
                    break;
                case HgtpMessageType.REMOVE_USER_FROM_ROOM:
                    appInstance.getStateHandler().fire(HgtpEvent.REMOVE_USER_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                    break;
                default:
                    return;
            }
        } else if (appInstance.getMode() == ProcessMode.CLIENT) {
            switch (hgtpHeader.getRequestType()) {
                case HgtpMessageType.REGISTER:
                    JOptionPane.showConfirmDialog(
                            null,
                            "Your name [" + userInfo.getHostName() + "] is duplicated. Please register again.",
                            "NAME DUPLICATION",
                            JOptionPane.YES_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null
                    );

                    userInfo.initHostName();
                    GuiManager.getInstance().clientFrameInit();
                    break;
                case HgtpMessageType.CREATE_ROOM:
                    RoomInfo roomInfo = sessionManager.getRoomInfo(userInfo.getRoomId());
                    if (roomInfo == null) return;
                    JOptionPane.showConfirmDialog(
                            null,
                            "Room name [" + roomInfo.getRoomName() + "] is duplicated. Please register again.",
                            "NAME DUPLICATION",
                            JOptionPane.YES_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null
                    );

                    sessionManager.deleteRoomInfo(userInfo.getRoomId(), userInfo.getUserId());
                    GuiManager.getInstance().getControlPanel().setRegisterButtonStatus();
                    break;
                default:
                    return;
            }
        }
    }

    /**
     * @fn sendCommonResponse
     * @brief UnauthorizedResponse 를 제외한 나머지 응답을 전송하는 메서드
     * @param hgtpCommonResponse
     */
    public void sendCommonResponse(HgtpCommonResponse hgtpCommonResponse) {
        HgtpHeader hgtpHeader = hgtpCommonResponse.getHgtpHeader();
        if (hgtpHeader == null) {
            log.warn("() () () hgtpHeader is null.");
            return;
        }

        String userId = hgtpHeader.getUserId();
        UserInfo userInfo = sessionManager.getUserInfo(userId);
        if (userInfo == null) {
            log.warn("() () () UserInfo is null.");
            return;
        }

        DestinationRecord destinationRecord = networkManager.getHgtpGroupSocket().getDestination(userInfo.getSessionId());
        if (destinationRecord == null) {
            log.warn("({}) () () DestinationRecord Channel is null.", userId);
            return;
        }

        byte[] data = hgtpCommonResponse.getByteData();
        destinationRecord.getNettyChannel().sendData(data, data.length);
        log.debug("({}) () () [{}] SEND DATA {}", userId, HgtpMessageType.RESPONSE_HASHMAP.get(hgtpCommonResponse.getHgtpHeader().getMessageType()), hgtpCommonResponse);

    }

    private HgtpMessage createHgtpResponse(short messageType, HgtpHeader requestHeader, short listenPort) {
        int seqNumber = requestHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT;
        switch (messageType) {
            case HgtpMessageType.OK:
                return new HgtpOkResponse(requestHeader.getRequestType(), requestHeader.getUserId(), seqNumber);
            case HgtpMessageType.BAD_REQUEST:
                return new HgtpBadRequestResponse(requestHeader.getRequestType(), requestHeader.getUserId(), seqNumber);
            case HgtpMessageType.SERVER_UNAVAILABLE:
                return new HgtpServerUnavailableResponse(requestHeader.getRequestType(), requestHeader.getUserId(), seqNumber);
            case HgtpMessageType.FORBIDDEN:
                return new HgtpForbiddenResponse(requestHeader.getRequestType(), requestHeader.getUserId(), seqNumber);
            case HgtpMessageType.UNAUTHORIZED:
                if (listenPort <= 0) {
                    return null;
                } else {
                    return new HgtpUnauthorizedResponse(requestHeader.getRequestType(), requestHeader.getUserId(), seqNumber, listenPort, AppInstance.MD5_REALM);
                }
            default:
                return null;
        }
    }

    public void sendHgtpResponse(short messageType, HgtpHeader requestHeader, short listenPort) {
        if (requestHeader == null) {
            log.warn("() () () hgtpHeader is null.");
            return;
        }
        HgtpMessage hgtpMessage = createHgtpResponse(messageType, requestHeader, listenPort);

        String userId = requestHeader.getUserId();
        UserInfo userInfo = sessionManager.getUserInfo(userId);
        if (userInfo == null) {
            log.warn("() () () UserInfo is null.");
            return;
        }

        DestinationRecord destinationRecord = networkManager.getHgtpGroupSocket().getDestination(userInfo.getSessionId());
        if (destinationRecord == null) {
            log.warn("({}) () () DestinationRecord Channel is null.", userId);
            return;
        }

        byte[] data = hgtpMessage.getByteData();
        destinationRecord.getNettyChannel().sendData(data, data.length);
    }
}
