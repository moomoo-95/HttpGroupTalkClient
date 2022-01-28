package moomoo.hgtp.grouptalk.protocol.hgtp.message.request.handler;

import moomoo.hgtp.grouptalk.gui.GuiManager;
import moomoo.hgtp.grouptalk.network.NetworkManager;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content.HgtpRegisterContent;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.content.HgtpRoomContent;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.*;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.HgtpCommonResponse;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.HgtpUnauthorizedResponse;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.handler.HgtpResponseHandler;
import moomoo.hgtp.grouptalk.protocol.http.handler.HttpRequestMessageHandler;
import moomoo.hgtp.grouptalk.service.AppInstance;
import moomoo.hgtp.grouptalk.session.SessionManager;
import moomoo.hgtp.grouptalk.session.base.RoomInfo;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import network.definition.DestinationRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HgtpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HgtpRequestHandler.class);
    private static final String RECV_LOG = "({}) () () RECV HGTP MSG [{}]";
    private static final String SEND_LOG = "({}) () () [{}] SEND DATA {}";
    private static final String DATA_NULL_LOG = "() () () header or content is null [{}]";
    private static final String DEST_CH_NULL_LOG = "({}) () () DestinationRecord Channel is null.";
    private static final String USER_UNREG_LOG = "{} UserInfo is unregister";
    private static final String ROOM_DEL_LOG = "{} RoomInfo is deleted";

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
        if (appInstance.getMode() == AppInstance.CLIENT_MODE) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    AppInstance.MAGIC_COOKIE, HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

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

                userInfo.setHgtpTargetNetAddress(hgtpRegisterContent.getListenIp(), hgtpRegisterContent.getListenPort());
                short httpPort = (short) userInfo.getHttpServerNetAddress().getPort();

                HgtpUnauthorizedResponse hgtpUnauthorizedResponse = new HgtpUnauthorizedResponse(
                        AppInstance.MAGIC_COOKIE, HgtpMessageType.UNAUTHORIZED, hgtpHeader.getRequestType(),
                        userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp(), httpPort, AppInstance.MD5_REALM);

                hgtpResponseHandler.sendUnauthorizedResponse(hgtpUnauthorizedResponse);
            }
            // userInfo 생성 실패
            else {
                HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                        AppInstance.MAGIC_COOKIE, messageType, hgtpHeader.getRequestType(),
                        userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

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
                    AppInstance.MAGIC_COOKIE, messageType, hgtpHeader.getRequestType(),
                    userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

            hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

            if (messageType == HgtpMessageType.FORBIDDEN) {
                sessionManager.deleteUserInfo(userInfo.getUserId());
            } else {
                //현재 user, room list 전송
                HttpRequestMessageHandler httpRequestMessageHandler = new HttpRequestMessageHandler();
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
        if (appInstance.getMode() == AppInstance.CLIENT_MODE) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    AppInstance.MAGIC_COOKIE, HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

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
                AppInstance.MAGIC_COOKIE, messageType, hgtpHeader.getRequestType(),
                userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

        hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

        // ok 응답시에만 userInfo 제거
        if (messageType == HgtpMessageType.OK) {
            sessionManager.deleteUserInfo(userId);

            HttpRequestMessageHandler httpRequestMessageHandler = new HttpRequestMessageHandler();
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
        if (appInstance.getMode() == AppInstance.CLIENT_MODE) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    AppInstance.MAGIC_COOKIE, HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

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

        short messageType = sessionManager.addRoomInfo(roomId, userId);

        HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                AppInstance.MAGIC_COOKIE, messageType, hgtpHeader.getRequestType(),
                userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

        hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

        if (messageType == HgtpMessageType.OK) {
            //현재 room list 전송
            HttpRequestMessageHandler httpRequestMessageHandler = new HttpRequestMessageHandler();
            sessionManager.getUserInfoHashMap().forEach( (key, value) -> httpRequestMessageHandler.sendRoomListRequest(value));
            httpRequestMessageHandler.sendRoomUserListRequest(userInfo);
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
        if (appInstance.getMode() == AppInstance.CLIENT_MODE) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    AppInstance.MAGIC_COOKIE, HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

            hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);
            return;
        }

        String roomId = hgtpRoomContent.getRoomId();
        String userId = hgtpHeader.getUserId();

        if (sessionManager.getUserInfo(userId) == null) {
            log.debug(USER_UNREG_LOG, userId);
            return;
        }

        short messageType = sessionManager.deleteRoomInfo(roomId, userId);

        HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                AppInstance.MAGIC_COOKIE, messageType, hgtpHeader.getRequestType(),
                userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

        hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

        if (messageType == HgtpMessageType.OK) {
            //현재 room list 전송
            HttpRequestMessageHandler httpRequestMessageHandler = new HttpRequestMessageHandler();
            sessionManager.getUserInfoHashMap().forEach( (key, userInfo) -> httpRequestMessageHandler.sendRoomListRequest(userInfo));
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
        if (appInstance.getMode() == AppInstance.CLIENT_MODE) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    AppInstance.MAGIC_COOKIE, HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

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

        short messageType = userInfo.getRoomId().equals("") ? HgtpMessageType.OK : HgtpMessageType.BAD_REQUEST;

        HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                AppInstance.MAGIC_COOKIE, messageType, hgtpHeader.getRequestType(),
                userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

        hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

        if (messageType == HgtpMessageType.OK) {
            roomInfo.addUserGroupSet(userId);

            HttpRequestMessageHandler httpRequestMessageHandler = new HttpRequestMessageHandler();
            roomInfo.getUserGroupSet().forEach(roomUserId -> {
                UserInfo roomUserInfo = sessionManager.getUserInfo(roomUserId);
                if (roomUserInfo != null) {
                    httpRequestMessageHandler.sendRoomUserListRequest(roomUserInfo);
                }
            });
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
        if (appInstance.getMode() == AppInstance.CLIENT_MODE) {
            HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                    AppInstance.MAGIC_COOKIE, HgtpMessageType.BAD_REQUEST, hgtpHeader.getRequestType(),
                    hgtpHeader.getUserId(), hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

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

        // 방에 입장해 있을 때 && manager 가 아닐 때
        short messageType = ( userInfo.getRoomId().equals("") || roomInfo.getManagerId().equals(userInfo) ) ? HgtpMessageType.BAD_REQUEST : HgtpMessageType.OK;


        HgtpCommonResponse hgtpCommonResponse = new HgtpCommonResponse(
                AppInstance.MAGIC_COOKIE, messageType, hgtpHeader.getRequestType(),
                userId, hgtpHeader.getSeqNumber() + AppInstance.SEQ_INCREMENT, appInstance.getTimeStamp());

        hgtpResponseHandler.sendCommonResponse(hgtpCommonResponse);

        if (messageType == HgtpMessageType.OK) {
            roomInfo.removeUserGroupSet(userId);

            HttpRequestMessageHandler httpRequestMessageHandler = new HttpRequestMessageHandler();
            roomInfo.getUserGroupSet().forEach(roomUserId -> {
                UserInfo roomUserInfo = sessionManager.getUserInfo(roomUserId);
                if (roomUserInfo != null) {
                    httpRequestMessageHandler.sendRoomUserListRequest(roomUserInfo);
                }
            });
        }
    }

    public boolean inviteUserFromRoomRequestProcessing(HgtpInviteUserFromRoomRequest hgtpInviteUserFromRoomRequest) {
        log.debug(RECV_LOG, hgtpInviteUserFromRoomRequest.getHgtpHeader().getUserId(), hgtpInviteUserFromRoomRequest);
        return true;
    }

    public boolean removeUserFromRoomRequestProcessing(HgtpRemoveUserFromRoomRequest hgtpRemoveUserFromRoomRequest) {
        log.debug(RECV_LOG, hgtpRemoveUserFromRoomRequest.getHgtpHeader().getUserId(), hgtpRemoveUserFromRoomRequest);
        return true;
    }

    /**
     * @fn sendRegisterRequest
     * @brief register 요청을 전송하는 메서드 (client, proxy 만 처리)
     * @param hgtpRegisterRequest
     * @param nonce
     */
    public void sendRegisterRequest(HgtpRegisterRequest hgtpRegisterRequest, String nonce) {
        if (hgtpRegisterRequest.getHgtpHeader() == null || hgtpRegisterRequest.getHgtpContent() == null) {
            log.warn(DATA_NULL_LOG, hgtpRegisterRequest);
            return;
        }
        if (appInstance.getMode() == AppInstance.SERVER_MODE) {
            log.warn("({}) () () The server cannot request register.", hgtpRegisterRequest.getHgtpHeader().getUserId());
            return;
        }

        if (nonce != null) {
            hgtpRegisterRequest.getHgtpContent().setNonce(hgtpRegisterRequest.getHgtpHeader(), nonce);
        }

        byte[] data = hgtpRegisterRequest.getByteData();
        sendHgtpRequest(data);
        log.debug(SEND_LOG, appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(hgtpRegisterRequest.getHgtpHeader().getMessageType()), hgtpRegisterRequest);
    }

    /**
     * @fn sendUnregisterRequest
     * @brief unregister 요청을 전송하는 메서드 (client, proxy 만 처리)
     * @param hgtpUnregisterRequest
     */
    public void sendUnregisterRequest(HgtpUnregisterRequest hgtpUnregisterRequest) {
        if (hgtpUnregisterRequest.getHgtpHeader() == null) {
            log.warn(DATA_NULL_LOG, hgtpUnregisterRequest);
            return;
        }
        if (appInstance.getMode() == AppInstance.SERVER_MODE) {
            log.warn("({}) () () The server cannot request unregister.", hgtpUnregisterRequest.getHgtpHeader().getUserId());
            return;
        }

        byte[] data = hgtpUnregisterRequest.getByteData();
        sendHgtpRequest(data);

        GuiManager.getInstance().clientFrameInit();
        log.debug(SEND_LOG, appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(hgtpUnregisterRequest.getHgtpHeader().getMessageType()), hgtpUnregisterRequest);
    }

    /**
     * @fn sendCreateRoomRequest
     * @brief create room 요청을 전송하는 메서드 (client, proxy 만 처리)
     * @param hgtpCreateRoomRequest
     */
    public void sendCreateRoomRequest(HgtpCreateRoomRequest hgtpCreateRoomRequest) {
        if (hgtpCreateRoomRequest.getHgtpHeader() == null || hgtpCreateRoomRequest.getHgtpContent() == null) {
            log.warn(DATA_NULL_LOG, hgtpCreateRoomRequest);
            return;
        }
        if (appInstance.getMode() == AppInstance.SERVER_MODE) {
            log.warn("({}) () () The server cannot request create room.", hgtpCreateRoomRequest.getHgtpHeader().getUserId());
            return;
        }

        sessionManager.getUserInfo(appInstance.getUserId()).setRoomId(hgtpCreateRoomRequest.getHgtpContent().getRoomId());

        byte[] data = hgtpCreateRoomRequest.getByteData();
        sendHgtpRequest(data);
        log.debug(SEND_LOG, appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(hgtpCreateRoomRequest.getHgtpHeader().getMessageType()), hgtpCreateRoomRequest);
    }

    /**
     * @fn sendDeleteRoomRequest
     * @brief delete room 요청을 전송하는 메서드 (client, proxy 만 처리)
     * @param hgtpDeleteRoomRequest
     */
    public void sendDeleteRoomRequest(HgtpDeleteRoomRequest hgtpDeleteRoomRequest) {
        if (hgtpDeleteRoomRequest.getHgtpHeader() == null || hgtpDeleteRoomRequest.getHgtpContent() == null) {
            log.warn(DATA_NULL_LOG, hgtpDeleteRoomRequest);
            return;
        }
        if (appInstance.getMode() == AppInstance.SERVER_MODE) {
            log.warn("({}) () () The server cannot request create room.", hgtpDeleteRoomRequest.getHgtpHeader().getUserId());
            return;
        }

        byte[] data = hgtpDeleteRoomRequest.getByteData();
        sendHgtpRequest(data);

        log.debug(SEND_LOG, appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(hgtpDeleteRoomRequest.getHgtpHeader().getMessageType()), hgtpDeleteRoomRequest);
    }

    /**
     * @fn sendJoinRoomRequest
     * @brief join room 요청을 전송하는 메서드 (client, proxy 만 처리)
     * @param hgtpJoinRoomRequest
     */
    public void sendJoinRoomRequest(HgtpJoinRoomRequest hgtpJoinRoomRequest) {
        if (hgtpJoinRoomRequest.getHgtpHeader() == null || hgtpJoinRoomRequest.getHgtpContent() == null) {
            log.warn(DATA_NULL_LOG, hgtpJoinRoomRequest);
            return;
        }
        if (appInstance.getMode() == AppInstance.SERVER_MODE) {
            log.warn("({}) () () The server cannot request create room.", hgtpJoinRoomRequest.getHgtpHeader().getUserId());
            return;
        }

        sessionManager.getUserInfo(appInstance.getUserId()).setRoomId(hgtpJoinRoomRequest.getHgtpContent().getRoomId());

        byte[] data = hgtpJoinRoomRequest.getByteData();
        sendHgtpRequest(data);
        log.debug(SEND_LOG, appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(hgtpJoinRoomRequest.getHgtpHeader().getMessageType()), hgtpJoinRoomRequest);
    }

    /**
     * @fn sendExitRoomRequest
     * @brief exit room 요청을 전송하는 메서드 (client, proxy 만 처리)
     * @param hgtpExitRoomRequest
     */
    public void sendExitRoomRequest(HgtpExitRoomRequest hgtpExitRoomRequest) {
        if (hgtpExitRoomRequest.getHgtpHeader() == null || hgtpExitRoomRequest.getHgtpContent() == null) {
            log.warn(DATA_NULL_LOG, hgtpExitRoomRequest);
            return;
        }
        if (appInstance.getMode() == AppInstance.SERVER_MODE) {
            log.warn("({}) () () The server cannot request create room.", hgtpExitRoomRequest.getHgtpHeader().getUserId());
            return;
        }

        byte[] data = hgtpExitRoomRequest.getByteData();
        sendHgtpRequest(data);

        GuiManager.getInstance().roomInit();
        log.debug(SEND_LOG, appInstance.getUserId(), HgtpMessageType.REQUEST_HASHMAP.get(hgtpExitRoomRequest.getHgtpHeader().getMessageType()), hgtpExitRoomRequest);
    }

    /**
     * @fn sendHgtpRequest
     * @brief byte array data 를 전송하는 메서드
     * @param data
     */
    private void sendHgtpRequest(byte[] data) {
        UserInfo userInfo = sessionManager.getUserInfo(appInstance.getUserId());
        DestinationRecord destinationRecord = networkManager.getHgtpGroupSocket().getDestination(userInfo.getSessionId());
        if (destinationRecord == null) {
            log.warn(DEST_CH_NULL_LOG, appInstance.getUserId());
        }

        destinationRecord.getNettyChannel().sendData(data, data.length);
    }

}
