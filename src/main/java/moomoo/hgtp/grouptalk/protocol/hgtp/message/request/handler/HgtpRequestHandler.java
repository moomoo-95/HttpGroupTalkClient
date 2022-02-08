package moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler;

import moomoo.hgtp.grouptalk.fsm.HgtpEvent;
import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.gui.component.panel.ControlPanel;
import moomoo.hgtp.grouptalk.network.NetworkManager;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content.HgtpRegisterContent;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content.HgtpRoomContent;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content.HgtpRoomManagerContent;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.*;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.HgtpCommonResponse;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.HgtpUnauthorizedResponse;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.handler.HgtpResponseHandler;
import moomoo.hgtp.grouptalk.protocol.http.handler.HttpMessageHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.service.base.ProcessMode;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.RoomInfo;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import network.definition.DestinationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class HgtpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HgtpRequestHandler.class);
    private static final String RECV_LOG = "({}) () () RECV HGTP MSG [{}]";
    private static final String SEND_LOG = "({}) () () [{}] SEND DATA {}";
    private static final String DATA_NULL_LOG = "() () () header or content is null [{}]";
    private static final String DEST_CH_NULL_LOG = "({}) () () DestinationRecord Channel is null.";
    private static final String USER_UNREG_LOG = "{} UserInfo is unregister";
    private static final String ROOM_DEL_LOG = "{} RoomInfo is deleted";
    private static final String SERVER_UNAVAIL_LOG = "({}) () () The server cannot request create room.";

    private static AppInstance appInstance = AppInstance.getInstance();
    private static SessionManager sessionManager = SessionManager.getInstance();
    private static NetworkManager networkManager = NetworkManager.getInstance();

    private HgtpResponseHandler hgtpResponseHandler = new HgtpResponseHandler();

    public HgtpRequestHandler() {
        // nothing
    }

    /**
     * @fn registerRequestProcessing
     * @brief register 수신시 처리하는 메서드 (server, proxy는 처리, client 는 오류메시지 전송)
     * @param hgtpRegisterRequest
     */
    public void registerRequestProcessing(HgtpRegisterRequest hgtpRegisterRequest) {
        HgtpHeader hgtpHeader = hgtpRegisterRequest.getHgtpHeader();
        HgtpRegisterContent hgtpRegisterContent = hgtpRegisterRequest.getHgtpContent();

        if (hgtpHeader == null || hgtpRegisterContent == null) {
            log.debug(DATA_NULL_LOG, hgtpRegisterRequest);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpRegisterRequest);

        // client 일 경우 bad request 전송
        if (appInstance.getMode() == ProcessMode.CLIENT) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

            hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
            return;
        }

        String userId = hgtpHeader.getUserId();

        if (userId.equals("")) {
            log.warn("({}) () () UserId is null", userId);
            return;
        }

        // 첫 번째 Register Request
        short messageType;
        if (hgtpRegisterContent.getNonce().equals("")) {
            // userInfo 생성
            messageType = sessionManager.addUserInfo( userId, hgtpRegisterContent.getExpires() );

            // userInfo 생성 성공 시 UNAUTHORIZED 응답 (server의 http socket port 전송)
            if (messageType == HgtpMessageType.OK) {
                UserInfo userInfo = sessionManager.getUserInfo(userId);
                appInstance.getStateHandler().fire(HgtpEvent.REGISTER, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));

                userInfo.setHgtpTargetNetAddress(hgtpRegisterContent.getListenIp(), hgtpRegisterContent.getListenPort());
                short httpPort = (short) userInfo.getHttpServerNetAddress().getPort();

                HgtpUnauthorizedResponse hgtpUnauthorizedResponse = new HgtpUnauthorizedResponse(
                        hgtpHeader.getRequestType(), userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT,
                        httpPort, AppInstance.MD5_REALM);

                hgtpResponseHandler.sendUnauthorizedResponse(hgtpUnauthorizedResponse);
            }
            // userInfo 생성 실패
            else {
                HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                        messageType, hgtpHeader.getRequestType(),
                        userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

                hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
                sessionManager.deleteUserInfo(userId);
            }
        }
        // 두 번째 Register Request
        else {
            UserInfo userInfo = sessionManager.getUserInfo(userId);
            if (userInfo == null) {
                log.debug("({}) () () userInfo is null", userId);
            }

            // nonce 일치하면 userInfo 유지
            if (hgtpRegisterContent.getNonce().equals(appInstance.getServerNonce())) {
                userInfo.setHttpTargetNetAddress(hgtpRegisterContent.getListenIp(), hgtpRegisterContent.getListenPort());
                messageType = HgtpMessageType.OK;
            }
            // 불일치 시 userInfo 삭제
            else {
                messageType = HgtpMessageType.FORBIDDEN;
            }

            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    messageType, hgtpHeader.getRequestType(),
                    userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

            hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

            if (messageType == HgtpMessageType.FORBIDDEN) {
                appInstance.getStateHandler().fire(HgtpEvent.REGISTER_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
                sessionManager.deleteUserInfo(userInfo.getUserId());
            } else {
                userInfo.setRegister();
                appInstance.getStateHandler().fire(HgtpEvent.REGISTER_SUC, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));

                //현재 user, room list 전송
                HttpMessageHandler httpRequestMessageHandler = new HttpMessageHandler();
                httpRequestMessageHandler.sendRoomListRequest(userInfo);
                sessionManager.getUserInfoHashMap().forEach( (key, value) -> httpRequestMessageHandler.sendUserListRequest(value));
            }
        }
    }

    /**
     * @fn unregisterRequestProcessing
     * @brief unregister 수신시 처리하는 메서드 (server, proxy는 처리, client 는 오류메시지 전송)
     * @param hgtpUnregisterRequest
     */
    public void unregisterRequestProcessing(HgtpUnregisterRequest hgtpUnregisterRequest) {
        HgtpHeader hgtpHeader = hgtpUnregisterRequest.getHgtpHeader();

        if (hgtpHeader == null) {
            log.debug("() () () header is null [{}]", hgtpUnregisterRequest);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpUnregisterRequest);

        // client 일 경우 bad request 전송
        if (appInstance.getMode() == ProcessMode.CLIENT) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

            hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
            return;
        }

        String userId = hgtpHeader.getUserId();

        if (userId.equals("")) {
            log.warn("({}) () () UserId is null", userId);
            return;
        }

        UserInfo userInfo = sessionManager.getUserInfo(userId);
        if (userInfo == null) {
            log.debug(USER_UNREG_LOG, userId);
            return;
        }

        short messageType;
        if (sessionManager.getRoomInfo(userInfo.getRoomId()) != null) {
            // userInfo 가 아직 roomInfo 에 존재
            messageType = HgtpMessageType.BAD_REQUEST;
            log.debug("({}) ({}) () RoomInfo still exists.", userId, userInfo.getRoomId());
        } else {
            messageType = HgtpMessageType.OK;
        }

        HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                messageType, hgtpHeader.getRequestType(),
                userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

        hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

        // ok 응답시에만 userInfo 제거
        if (messageType == HgtpMessageType.OK) {
            appInstance.getStateHandler().fire(HgtpEvent.UNREGISTER, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
            sessionManager.deleteUserInfo(userId);

            HttpMessageHandler httpRequestMessageHandler = new HttpMessageHandler();
            sessionManager.getUserInfoHashMap().forEach( (key, value) -> httpRequestMessageHandler.sendUserListRequest(value));
        }
    }

    /**
     * @fn createRoomRequestProcessing
     * @brief createRoom 수신시 처리하는 메서드 (server, proxy는 처리, client 는 오류메시지 전송)
     * @param hgtpCreateRoomRequest
     */
    public void createRoomRequestProcessing(HgtpCreateRoomRequest hgtpCreateRoomRequest) {
        HgtpHeader hgtpHeader = hgtpCreateRoomRequest.getHgtpHeader();
        HgtpRoomContent hgtpRoomContent = hgtpCreateRoomRequest.getHgtpContent();

        if (hgtpHeader == null || hgtpRoomContent == null) {
            log.debug(DATA_NULL_LOG, hgtpCreateRoomRequest);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpCreateRoomRequest);

        // client 일 경우 bad request 전송
        if (appInstance.getMode() == ProcessMode.CLIENT) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

            hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
            return;
        }

        String roomId = hgtpRoomContent.getRoomId();
        String userId = hgtpHeader.getUserId();

        UserInfo userInfo = sessionManager.getUserInfo(userId);
        if (userInfo == null) {
            log.debug(USER_UNREG_LOG, userId);
            return;
        }
        appInstance.getStateHandler().fire(HgtpEvent.CREATE_ROOM, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));

        short messageType = sessionManager.addRoomInfo(roomId, userId);

        HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                messageType, hgtpHeader.getRequestType(),
                userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

        hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

        if (messageType == HgtpMessageType.OK) {
            appInstance.getStateHandler().fire(HgtpEvent.CREATE_ROOM_SUC, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
            //현재 room list 전송
            HttpMessageHandler httpRequestMessageHandler = new HttpMessageHandler();
            sessionManager.getUserInfoHashMap().forEach( (key, value) -> httpRequestMessageHandler.sendRoomListRequest(value));
            httpRequestMessageHandler.sendRoomUserListRequest(userInfo);

            httpRequestMessageHandler.sendNoticeRequest("[" + userId + "] 님이 [" + roomId + "]방을 생성했습니다.", userInfo);
        } else{
            appInstance.getStateHandler().fire(HgtpEvent.CREATE_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
        }
    }

    /**
     * @fn deleteRoomRequestProcessing
     * @brief deleteRoom 수신시 처리하는 메서드 (server, proxy는 처리, client 는 오류메시지 전송)
     * @param hgtpDeleteRoomRequest
     */
    public void deleteRoomRequestProcessing(HgtpDeleteRoomRequest hgtpDeleteRoomRequest) {
        HgtpHeader hgtpHeader = hgtpDeleteRoomRequest.getHgtpHeader();
        HgtpRoomContent hgtpRoomContent = hgtpDeleteRoomRequest.getHgtpContent();

        if (hgtpHeader == null || hgtpRoomContent == null) {
            log.debug(DATA_NULL_LOG, hgtpDeleteRoomRequest);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpDeleteRoomRequest);

        // client 일 경우 bad request 전송
        if (appInstance.getMode() == ProcessMode.CLIENT) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

            hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
            return;
        }

        String roomId = hgtpRoomContent.getRoomId();
        String userId = hgtpHeader.getUserId();

        UserInfo userInfo = sessionManager.getUserInfo(userId);
        if (userInfo == null) {
            log.debug(USER_UNREG_LOG, userId);
            return;
        }
        appInstance.getStateHandler().fire(HgtpEvent.DELETE_ROOM, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));

        short messageType = sessionManager.deleteRoomInfo(roomId, userId);

        HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                messageType, hgtpHeader.getRequestType(),
                userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

        hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

        if (messageType == HgtpMessageType.OK) {
            appInstance.getStateHandler().fire(HgtpEvent.DELETE_ROOM_SUC, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
            //현재 room list 전송
            HttpMessageHandler httpRequestMessageHandler = new HttpMessageHandler();
            sessionManager.getUserInfoHashMap().forEach( (key, roomUserInfo) -> httpRequestMessageHandler.sendRoomListRequest(roomUserInfo));
        } else {
            appInstance.getStateHandler().fire(HgtpEvent.DELETE_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
        }
    }

    /**
     * @fn joinRoomRequestProcessing
     * @brief join room 수신시 처리하는 메서드 (server, proxy는 처리, client 는 오류메시지 전송)
     * @param hgtpJoinRoomRequest
     */
    public void joinRoomRequestProcessing(HgtpJoinRoomRequest hgtpJoinRoomRequest) {
        HgtpHeader hgtpHeader = hgtpJoinRoomRequest.getHgtpHeader();
        HgtpRoomContent hgtpRoomContent = hgtpJoinRoomRequest.getHgtpContent();

        if (hgtpHeader == null || hgtpRoomContent == null) {
            log.debug(DATA_NULL_LOG, hgtpJoinRoomRequest);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpJoinRoomRequest);

        // client 일 경우 bad request 전송
        if (appInstance.getMode() == ProcessMode.CLIENT) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

            hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
            return;
        }

        String roomId = hgtpRoomContent.getRoomId();
        String userId = hgtpHeader.getUserId();

        UserInfo userInfo = sessionManager.getUserInfo(userId);
        if (userInfo == null) {
            log.debug(USER_UNREG_LOG, userId);
            return;
        }
        appInstance.getStateHandler().fire(HgtpEvent.JOIN_ROOM, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));

        RoomInfo roomInfo = sessionManager.getRoomInfo(roomId);
        if (roomInfo == null) {
            log.debug(ROOM_DEL_LOG, roomId);
            return;
        }

        short messageType = userInfo.getRoomId().equals("") ? HgtpMessageType.OK : HgtpMessageType.BAD_REQUEST;

        HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                messageType, hgtpHeader.getRequestType(),
                userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

        hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

        if (messageType == HgtpMessageType.OK) {
            appInstance.getStateHandler().fire(HgtpEvent.JOIN_ROOM_SUC, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
            roomInfo.addUserGroupSet(userId);

            HttpMessageHandler httpRequestMessageHandler = new HttpMessageHandler();
            roomInfo.getUserGroupSet().forEach(roomUserId -> {
                UserInfo roomUserInfo = sessionManager.getUserInfo(roomUserId);
                if (roomUserInfo != null) {
                    httpRequestMessageHandler.sendRoomUserListRequest(roomUserInfo);
                    httpRequestMessageHandler.sendNoticeRequest("[" + userId+ "]님이 입장 했습니다.", roomUserInfo);
                }
            });
        } else {
            appInstance.getStateHandler().fire(HgtpEvent.JOIN_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
        }
    }

    /**
     * @fn exitRoomRequestProcessing
     * @brief exit room 수신시 처리하는 메서드 (server, proxy는 처리, client 는 오류메시지 전송)
     * @param hgtpExitRoomRequest
     */
    public void exitRoomRequestProcessing(HgtpExitRoomRequest hgtpExitRoomRequest) {
        HgtpHeader hgtpHeader = hgtpExitRoomRequest.getHgtpHeader();
        HgtpRoomContent hgtpRoomContent = hgtpExitRoomRequest.getHgtpContent();

        if (hgtpHeader == null || hgtpRoomContent == null) {
            log.debug(DATA_NULL_LOG, hgtpExitRoomRequest);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpExitRoomRequest);

        // client 일 경우 bad request 전송
        if (appInstance.getMode() == ProcessMode.CLIENT) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

            hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
            return;
        }

        String roomId = hgtpRoomContent.getRoomId();
        String userId = hgtpHeader.getUserId();

        UserInfo userInfo = sessionManager.getUserInfo(userId);
        if (userInfo == null) {
            log.debug(USER_UNREG_LOG, userId);
            return;
        }

        RoomInfo roomInfo = sessionManager.getRoomInfo(roomId);
        if (roomInfo == null) {
            log.debug(ROOM_DEL_LOG, roomId);
            return;
        }
        appInstance.getStateHandler().fire(HgtpEvent.EXIT_ROOM, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));

        // 방에 입장해 있을 때 && manager 가 아닐 때
        short messageType = ( userInfo.getRoomId().equals("") || roomInfo.getManagerId().equals(userInfo.getUserId()) ) ? HgtpMessageType.BAD_REQUEST : HgtpMessageType.OK;

        HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                messageType, hgtpHeader.getRequestType(),
                userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

        hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

        if (messageType == HgtpMessageType.OK) {
            appInstance.getStateHandler().fire(HgtpEvent.EXIT_ROOM_SUC, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
            roomInfo.removeUserGroupSet(userId);

            HttpMessageHandler httpRequestMessageHandler = new HttpMessageHandler();
            roomInfo.getUserGroupSet().forEach(roomUserId -> {
                UserInfo roomUserInfo = sessionManager.getUserInfo(roomUserId);
                if (roomUserInfo != null) {
                    httpRequestMessageHandler.sendRoomUserListRequest(roomUserInfo);
                    httpRequestMessageHandler.sendNoticeRequest("[" + userId+ "]님이 퇴장 했습니다.", roomUserInfo);
                }
            });
        } else {
            appInstance.getStateHandler().fire(HgtpEvent.EXIT_ROOM_FAIL, appInstance.getStateManager().getStateUnit(userInfo.getHgtpStateUnitId()));
        }
    }

    /**
     * @fn inviteUserFromRoomRequestProcessing
     * @brief invite User From Room 수신시 처리하는 메서드 (server, proxy 는 처리, client 응답, server 는 응답 또는 relay, proxy 는 relay)
     * @param hgtpInviteUserFromRoomRequest
     */
    public void inviteUserFromRoomRequestProcessing(HgtpInviteUserFromRoomRequest hgtpInviteUserFromRoomRequest) {
        HgtpHeader hgtpHeader = hgtpInviteUserFromRoomRequest.getHgtpHeader();
        HgtpRoomManagerContent hgtpContent = hgtpInviteUserFromRoomRequest.getHgtpContent();

        if (hgtpHeader == null || hgtpContent == null) {
            log.debug(DATA_NULL_LOG, hgtpInviteUserFromRoomRequest);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpInviteUserFromRoomRequest);

        String userId = hgtpHeader.getUserId();
        String roomId = hgtpContent.getRoomId();
        String peerUserId = hgtpContent.getPeerUserId();

        UserInfo userInfo = sessionManager.getUserInfo(userId);
        if (userInfo == null) {
            log.debug(USER_UNREG_LOG, userId);
            return;
        }

        UserInfo peerUserInfo = sessionManager.getUserInfo(peerUserId);
        short messageType = HgtpMessageType.OK;
        switch (appInstance.getMode()) {
            case SERVER:
                RoomInfo roomInfo = sessionManager.getRoomInfo(roomId);
                if (roomInfo == null) {
                    log.debug(ROOM_DEL_LOG, roomId);
                    return;
                }

                if (userInfo.getRoomId().equals("") || peerUserInfo == null) {
                    messageType = HgtpMessageType.BAD_REQUEST;
                } else if (roomInfo.getUserGroupSet().contains(peerUserId)) {
                    messageType = HgtpMessageType.DECLINE;
                }
                appInstance.getStateHandler().fire(HgtpEvent.INVITE_USER_ROOM, appInstance.getStateManager().getStateUnit(peerUserInfo.getHgtpStateUnitId()));

                if (messageType != HgtpMessageType.OK) {
                    appInstance.getStateHandler().fire(HgtpEvent.INVITE_USER_ROOM_FAIL, appInstance.getStateManager().getStateUnit(peerUserInfo.getHgtpStateUnitId()));
                    HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                            messageType, hgtpHeader.getRequestType(),
                            userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

                    hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
                } else {
                    peerUserInfo.setRoomId(roomId);

                    HgtpInviteUserFromRoomRequest hgtpInviteRequest = new HgtpInviteUserFromRoomRequest(
                            peerUserId,
                            hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT,
                            peerUserInfo.getRoomId(), peerUserId
                    );
                    sendInviteUserFromRoomRequest(hgtpInviteRequest);
                }
                break;
            case CLIENT:
                appInstance.getStateHandler().fire(HgtpEvent.INVITE_USER_ROOM, appInstance.getStateManager().getStateUnit(peerUserInfo.getHgtpStateUnitId()));
                if (!userInfo.getRoomId().equals("")) {
                    appInstance.getStateHandler().fire(HgtpEvent.INVITE_USER_ROOM_FAIL, appInstance.getStateManager().getStateUnit(peerUserInfo.getHgtpStateUnitId()));
                    messageType = HgtpMessageType.DECLINE;
                } else {
                    appInstance.getStateHandler().fire(HgtpEvent.INVITE_USER_ROOM_SUC, appInstance.getStateManager().getStateUnit(peerUserInfo.getHgtpStateUnitId()));
                    GuiManager guiManager = GuiManager.getInstance();
                    ControlPanel controlPanel = guiManager.getControlPanel();

                    controlPanel.setJoinRoomButtonStatus();

                    userInfo.setRoomId(roomId);
                }

                HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                        messageType, hgtpHeader.getRequestType(),
                        userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

                hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
                break;
            case PROXY:
                break;
            default:
        }
    }

    /**
     * @fn removeUserFromRoomRequestProcessing
     * @brief remove User From Room 수신시 처리하는 메서드 (server, proxy 는 처리, client 응답, server 는 응답 또는 relay, proxy 는 relay)
     * @param hgtpRemoveUserFromRoomRequest
     */
    public void removeUserFromRoomRequestProcessing(HgtpRemoveUserFromRoomRequest hgtpRemoveUserFromRoomRequest) {
        HgtpHeader hgtpHeader = hgtpRemoveUserFromRoomRequest.getHgtpHeader();
        HgtpRoomManagerContent hgtpContent = hgtpRemoveUserFromRoomRequest.getHgtpContent();

        if (hgtpHeader == null || hgtpContent == null) {
            log.debug(DATA_NULL_LOG, hgtpRemoveUserFromRoomRequest);
            return;
        }
        log.debug(RECV_LOG, hgtpHeader.getUserId(), hgtpRemoveUserFromRoomRequest);

        String userId = hgtpHeader.getUserId();
        String roomId = hgtpContent.getRoomId();
        String peerUserId = hgtpContent.getPeerUserId();

        UserInfo userInfo = sessionManager.getUserInfo(userId);
        if (userInfo == null) {
            log.debug(USER_UNREG_LOG, userId);
            return;
        }

        UserInfo peerUserInfo = sessionManager.getUserInfo(peerUserId);
        short messageType = HgtpMessageType.OK;
        switch (appInstance.getMode()) {
            case SERVER:
                RoomInfo roomInfo = sessionManager.getRoomInfo(roomId);
                if (roomInfo == null) {
                    log.debug(ROOM_DEL_LOG, roomId);
                    return;
                }

                if (userInfo.getRoomId().equals("") || peerUserInfo == null) {
                    messageType = HgtpMessageType.BAD_REQUEST;
                } else if (!roomInfo.getUserGroupSet().contains(peerUserId)) {
                    messageType = HgtpMessageType.DECLINE;
                }
                appInstance.getStateHandler().fire(HgtpEvent.REMOVE_USER_ROOM, appInstance.getStateManager().getStateUnit(peerUserInfo.getHgtpStateUnitId()));

                if (messageType != HgtpMessageType.OK) {
                    appInstance.getStateHandler().fire(HgtpEvent.REMOVE_USER_ROOM_FAIL, appInstance.getStateManager().getStateUnit(peerUserInfo.getHgtpStateUnitId()));
                    HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                            messageType, hgtpHeader.getRequestType(),
                            userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

                    hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
                } else {
                    HgtpRemoveUserFromRoomRequest hgtpRemoveRequest = new HgtpRemoveUserFromRoomRequest(
                            peerUserId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, peerUserInfo.getRoomId(), peerUserId
                    );

                    sendRemoveUserFromRoomRequest(hgtpRemoveRequest);
                }
                break;
            case CLIENT:
                appInstance.getStateHandler().fire(HgtpEvent.REMOVE_USER_ROOM, appInstance.getStateManager().getStateUnit(peerUserInfo.getHgtpStateUnitId()));
                if (userInfo.getRoomId().equals("")) {
                    appInstance.getStateHandler().fire(HgtpEvent.REMOVE_USER_ROOM_FAIL, appInstance.getStateManager().getStateUnit(peerUserInfo.getHgtpStateUnitId()));
                    messageType = HgtpMessageType.DECLINE;
                } else {
                    appInstance.getStateHandler().fire(HgtpEvent.REMOVE_USER_ROOM_SUC, appInstance.getStateManager().getStateUnit(peerUserInfo.getHgtpStateUnitId()));
                    GuiManager guiManager = GuiManager.getInstance();
                    ControlPanel controlPanel = guiManager.getControlPanel();

                    guiManager.roomInit();
                    controlPanel.setRegisterButtonStatus();

                    userInfo.initRoomId();
                }

                HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                        messageType, hgtpHeader.getRequestType(),
                        userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT);

                hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

                JOptionPane.showConfirmDialog(
                        null,
                        "Kicked out of the room by room manager.",
                        "Kicked out of the room",
                        JOptionPane.YES_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null
                );
                break;
            case PROXY:
                break;
            default:
        }
    }

    /**
     * @fn sendRegisterRequest
     * @brief register 요청을 전송하는 메서드 (client, proxy 만 처리)
     * @param hgtpRegisterRequest
     * @param nonce
     */
    public void sendRegisterRequest(HgtpRegisterRequest hgtpRegisterRequest, String nonce) {
        HgtpHeader hgtpHeader = hgtpRegisterRequest.getHgtpHeader();
        if (hgtpHeader == null || hgtpRegisterRequest.getHgtpContent() == null) {
            log.warn(DATA_NULL_LOG, hgtpRegisterRequest);
            return;
        }
        if (appInstance.getMode() == ProcessMode.SERVER) {
            log.warn("({}) () () The server cannot request register.", hgtpHeader.getUserId());
            return;
        }

        if (nonce != null) {
            hgtpRegisterRequest.getHgtpContent().setNonce(hgtpHeader, nonce);
        }

        byte[] data = hgtpRegisterRequest.getByteData();
        sendHgtpRequest(hgtpHeader.getUserId(), data);
        log.debug(SEND_LOG, appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(hgtpHeader.getMessageType()), hgtpRegisterRequest);
    }

    /**
     * @fn sendUnregisterRequest
     * @brief unregister 요청을 전송하는 메서드 (client, proxy 만 처리)
     * @param hgtpUnregisterRequest
     */
    public void sendUnregisterRequest(HgtpUnregisterRequest hgtpUnregisterRequest) {
        HgtpHeader hgtpHeader = hgtpUnregisterRequest.getHgtpHeader();
        if (hgtpHeader == null) {
            log.warn(DATA_NULL_LOG, hgtpUnregisterRequest);
            return;
        }
        if (appInstance.getMode() == ProcessMode.SERVER) {
            log.warn("({}) () () The server cannot request unregister.", hgtpHeader.getUserId());
            return;
        }

        byte[] data = hgtpUnregisterRequest.getByteData();
        sendHgtpRequest(hgtpHeader.getUserId(), data);

        GuiManager.getInstance().clientFrameInit();
        log.debug(SEND_LOG, appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(hgtpHeader.getMessageType()), hgtpUnregisterRequest);
    }

    /**
     * @fn sendCreateRoomRequest
     * @brief create room 요청을 전송하는 메서드 (client, proxy 만 처리)
     * @param hgtpCreateRoomRequest
     */
    public void sendCreateRoomRequest(HgtpCreateRoomRequest hgtpCreateRoomRequest) {
        HgtpHeader hgtpHeader = hgtpCreateRoomRequest.getHgtpHeader();
        HgtpRoomContent hgtpRoomContent = hgtpCreateRoomRequest.getHgtpContent();
        if (hgtpHeader == null || hgtpRoomContent == null) {
            log.warn(DATA_NULL_LOG, hgtpCreateRoomRequest);
            return;
        }
        if (appInstance.getMode() == ProcessMode.SERVER) {
            log.warn(SERVER_UNAVAIL_LOG, hgtpHeader.getUserId());
            return;
        }

        sessionManager.getUserInfo(hgtpHeader.getUserId()).setRoomId(hgtpRoomContent.getRoomId());

        byte[] data = hgtpCreateRoomRequest.getByteData();
        sendHgtpRequest(hgtpHeader.getUserId(), data);
        log.debug(SEND_LOG, appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(hgtpHeader.getMessageType()), hgtpCreateRoomRequest);
    }

    /**
     * @fn sendDeleteRoomRequest
     * @brief delete room 요청을 전송하는 메서드 (client, proxy 만 처리)
     * @param hgtpDeleteRoomRequest
     */
    public void sendDeleteRoomRequest(HgtpDeleteRoomRequest hgtpDeleteRoomRequest) {
        HgtpHeader hgtpHeader = hgtpDeleteRoomRequest.getHgtpHeader();
        HgtpRoomContent hgtpRoomContent = hgtpDeleteRoomRequest.getHgtpContent();
        if (hgtpHeader == null || hgtpRoomContent == null) {
            log.warn(DATA_NULL_LOG, hgtpDeleteRoomRequest);
            return;
        }
        if (appInstance.getMode() == ProcessMode.SERVER) {
            log.warn(SERVER_UNAVAIL_LOG, hgtpHeader.getUserId());
            return;
        }

        byte[] data = hgtpDeleteRoomRequest.getByteData();
        sendHgtpRequest(hgtpHeader.getUserId(), data);

        log.debug(SEND_LOG, appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(hgtpHeader.getMessageType()), hgtpDeleteRoomRequest);
    }

    /**
     * @fn sendJoinRoomRequest
     * @brief join room 요청을 전송하는 메서드 (client, proxy 만 처리)
     * @param hgtpJoinRoomRequest
     */
    public void sendJoinRoomRequest(HgtpJoinRoomRequest hgtpJoinRoomRequest) {
        HgtpHeader hgtpHeader = hgtpJoinRoomRequest.getHgtpHeader();
        HgtpRoomContent hgtpRoomContent = hgtpJoinRoomRequest.getHgtpContent();
        if (hgtpHeader == null || hgtpJoinRoomRequest.getHgtpContent() == null) {
            log.warn(DATA_NULL_LOG, hgtpJoinRoomRequest);
            return;
        }
        if (appInstance.getMode() == ProcessMode.SERVER) {
            log.warn(SERVER_UNAVAIL_LOG, hgtpHeader.getUserId());
            return;
        }

        sessionManager.getUserInfo(hgtpHeader.getUserId()).setRoomId(hgtpRoomContent.getRoomId());

        byte[] data = hgtpJoinRoomRequest.getByteData();
        sendHgtpRequest(hgtpHeader.getUserId(), data);
        log.debug(SEND_LOG, appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(hgtpHeader.getMessageType()), hgtpJoinRoomRequest);
    }

    /**
     * @fn sendExitRoomRequest
     * @brief exit room 요청을 전송하는 메서드 (client, proxy 만 처리)
     * @param hgtpExitRoomRequest
     */
    public void sendExitRoomRequest(HgtpExitRoomRequest hgtpExitRoomRequest) {
        HgtpHeader hgtpHeader = hgtpExitRoomRequest.getHgtpHeader();
        HgtpRoomContent hgtpRoomContent = hgtpExitRoomRequest.getHgtpContent();
        if (hgtpHeader == null || hgtpRoomContent == null) {
            log.warn(DATA_NULL_LOG, hgtpExitRoomRequest);
            return;
        }
        if (appInstance.getMode() == ProcessMode.SERVER) {
            log.warn(SERVER_UNAVAIL_LOG, hgtpHeader.getUserId());
            return;
        }

        byte[] data = hgtpExitRoomRequest.getByteData();
        sendHgtpRequest(hgtpHeader.getUserId(), data);

        GuiManager.getInstance().roomInit();
        log.debug(SEND_LOG, appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(hgtpHeader.getMessageType()), hgtpExitRoomRequest);
    }

    /**
     * @fn sendInviteUserFromRoomRequest
     * @brief manager 가 다른 user에게 invite 요청을 전송하는 메서드
     * @param hgtpInviteUserFromRoomRequest
     */
    public void sendInviteUserFromRoomRequest(HgtpInviteUserFromRoomRequest hgtpInviteUserFromRoomRequest) {
        HgtpHeader hgtpHeader = hgtpInviteUserFromRoomRequest.getHgtpHeader();
        HgtpRoomManagerContent hgtpRoomContent = hgtpInviteUserFromRoomRequest.getHgtpContent();
        if (hgtpHeader == null || hgtpRoomContent == null) {
            log.warn(DATA_NULL_LOG, hgtpInviteUserFromRoomRequest);
            return;
        }

        byte[] data = hgtpInviteUserFromRoomRequest.getByteData();
        String userId = hgtpHeader.getUserId();

        sendHgtpRequest(userId, data);
        log.debug(SEND_LOG, userId, HgtpMessageType.REQUEST_HASHMAP.get(hgtpInviteUserFromRoomRequest.getHgtpHeader().getMessageType()), hgtpInviteUserFromRoomRequest);
    }

    /**
     * @fn sendInviteUserFromRoomRequest
     * @brief manager 가 다른 user 에게 remove 요청을 전송하는 메서드
     * @param hgtpRemoveUserFromRoomRequest
     */
    public void sendRemoveUserFromRoomRequest(HgtpRemoveUserFromRoomRequest hgtpRemoveUserFromRoomRequest) {
        HgtpHeader hgtpHeader = hgtpRemoveUserFromRoomRequest.getHgtpHeader();
        HgtpRoomManagerContent hgtpRoomContent = hgtpRemoveUserFromRoomRequest.getHgtpContent();
        if (hgtpHeader == null || hgtpRoomContent == null) {
            log.warn(DATA_NULL_LOG, hgtpRemoveUserFromRoomRequest);
            return;
        }

        byte[] data = hgtpRemoveUserFromRoomRequest.getByteData();
        String userId = hgtpHeader.getUserId();

        sendHgtpRequest(userId, data);
        log.debug(SEND_LOG, userId, HgtpMessageType.REQUEST_HASHMAP.get(hgtpRemoveUserFromRoomRequest.getHgtpHeader().getMessageType()), hgtpRemoveUserFromRoomRequest);
    }

    /**
     * @fn sendHgtpRequest
     * @brief byte array data 를 전송하는 메서드
     * @param data
     */
    private void sendHgtpRequest(String userId, byte[] data) {
        UserInfo userInfo = sessionManager.getUserInfo(userId);
        if (userInfo == null) {
            return;
        }
        DestinationRecord destinationRecord = networkManager.getHgtpGroupSocket().getDestination(userInfo.getSessionId());
        if (destinationRecord == null) {
            log.warn(DEST_CH_NULL_LOG, appInstance.getUserId());
        }

        destinationRecord.getNettyChannel().sendData(data, data.length);
    }

}
