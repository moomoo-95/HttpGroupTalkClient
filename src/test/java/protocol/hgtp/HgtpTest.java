package protocol.hgtp;

import moomoo.hgtp.grouptalk.protocol.hgtp.message.request.*;
import moomoo.hgtp.grouptalk.service.AppInstance;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import moomoo.hgtp.grouptalk.protocol.hgtp.exception.HgtpException;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpHeader;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.base.HgtpMessageType;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.HgtpCommonResponse;
import moomoo.hgtp.grouptalk.protocol.hgtp.message.response.HgtpUnauthorizedResponse;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class HgtpTest {

    private static final Logger log = LoggerFactory.getLogger(HgtpTest.class);
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss.SSS");

    // Common
    private static final String TEST_HASH_KEY = "950817";

    // Server
    private boolean isServerError = false;
    private static final String SERVER_TEST_REALM = "HGTP_SERVICE";
    private static final int AVAILABLE_REGISTER = 3;
    private static final int AVAILABLE_ROOM = 3;
    private static final HashMap<String, String> userInfoMap = new HashMap<>();
    private static final HashMap<String, String> roomInfoMap = new HashMap<>();

    // Client
    private static final String CLIENT_TEST_REALM = "HGTP_SERVICE";

    // hgtpRegisterTest
    // 200 OK 응답                    : CLIENT_TEST_REALM == SERVER_TEST_REALM && AVAILABLE_REGISTER > userInfoMap.size()
    // 403 Forbidden 응답             : CLIENT_TEST_REALM != SERVER_TEST_REALM
    // 503 Service Unavailable 응답   : CLIENT_TEST_REALM == SERVER_TEST_REALM && AVAILABLE_REGISTER <= userInfoMap.size()


    // hgtpUnregisterTest
    // 200 OK 응답                    : isServerError == false
    // 400 Bad Request 응답           : isServerError == false && unknown messageType
    // 503 Service Unavailable 응답   : isServerError == true

    // hgtpCreateRoomTest
    // 200 OK 응답                    : isServerError == false && AVAILABLE_REGISTER > roomInfoMap.size()
    // 400 Bad Request 응답           : isServerError == false && unknown messageType
    // 503 Service Unavailable 응답   : isServerError == true  || AVAILABLE_REGISTER <= roomInfoMap.size()

    // hgtpDeleteRoomTest
    // 200 OK 응답                    : isServerError == false
    // 400 Bad Request 응답           : isServerError == false && unknown messageType
    // 503 Service Unavailable 응답   : isServerError == true

    // hgtpJoinRoomTest
    // 200 OK 응답                    : isServerError == false
    // 400 Bad Request 응답           : isServerError == false && unknown messageType
    // 503 Service Unavailable 응답   : isServerError == true

    // hgtpExitRoomTest
    // 200 OK 응답                    : isServerError == false
    // 400 Bad Request 응답           : isServerError == false && unknown messageType
    // 503 Service Unavailable 응답   : isServerError == true
//
//    @Test
//    public void hgtpRegisterTest(String userId) {
//        try {
//
//
//            // send first Register
//            HgtpRegisterRequest sendFirstHgtpRegisterRequest = new HgtpRegisterRequest(
//                    userId, 4,
//                    3600L, AppInstance.getInstance ().getConfigManager().getLocalListenIp(), (short) 5060);
//            log.debug("RG1 SEND DATA : {}", sendFirstHgtpRegisterRequest);
//
//            // recv first Register
//
//            AppInstance.getInstance().putHgtpMessage(sendFirstHgtpRegisterRequest.getByteData());
//            byte[] recvFirstRegister = sendFirstHgtpRegisterRequest.getByteData();
//            HgtpRegisterRequest recvFirstHgtpRegisterRequest = new HgtpRegisterRequest(recvFirstRegister);
//            log.debug("RG1 RECV DATA  : {}", recvFirstHgtpRegisterRequest);
//
//            // send unauthorized
//            HgtpHeader recvReg1Header = recvFirstHgtpRegisterRequest.getHgtpHeader();
//            HgtpUnauthorizedResponse sendHgtpUnauthorizedResponse = new HgtpUnauthorizedResponse(
//                    recvReg1Header.getRequestType(), recvReg1Header.getUserId(),
//                    recvFirstHgtpRegisterRequest.getHgtpHeader().getSeqNumber() + 1,
//                    (short) 4000, CLIENT_TEST_REALM
//            );
//            log.debug("URE SEND DATA : {}", sendHgtpUnauthorizedResponse);
//
//            // recv unauthorized
//            byte[] recvUnauthorized = sendHgtpUnauthorizedResponse.getByteData();
//            HgtpUnauthorizedResponse recvHgtpUnauthorizedResponse = new HgtpUnauthorizedResponse(recvUnauthorized);
//            log.debug("URE RECV DATA : {}", recvHgtpUnauthorizedResponse);
//
//            // Encoding realm -> nonce
//            MessageDigest messageDigestRealm = MessageDigest.getInstance("MD5");
//            messageDigestRealm.update(recvHgtpUnauthorizedResponse.getHgtpContent().getRealm().getBytes(StandardCharsets.UTF_8));
//            messageDigestRealm.update(TEST_HASH_KEY.getBytes(StandardCharsets.UTF_8));
//            byte[] digestRealm = messageDigestRealm.digest();
//            messageDigestRealm.reset();
//            messageDigestRealm.update(digestRealm);
//            String nonce = new String(messageDigestRealm.digest());
//
//            // send second Register
//            HgtpHeader recvUnauthHeader = recvHgtpUnauthorizedResponse.getHgtpHeader();
//            HgtpRegisterRequest sendSecondHgtpRegisterRequest = new HgtpRegisterRequest(
//                    recvUnauthHeader.getUserId(),
//                    recvUnauthHeader.getSeqNumber() + 1,
//                    3600L, AppInstance.getInstance ().getConfigManager().getLocalListenIp(), (short) 5060);
//            sendSecondHgtpRegisterRequest.getHgtpContent().setNonce(sendSecondHgtpRegisterRequest.getHgtpHeader(), nonce);
//            log.debug("RG2 SEND DATA : {}", sendSecondHgtpRegisterRequest);
//
//            // recv second Register
//            byte[] recvSecondRegister = sendSecondHgtpRegisterRequest.getByteData();
//            HgtpRegisterRequest recvSecondHgtpRegisterRequest = new HgtpRegisterRequest(recvSecondRegister);
//            log.debug("RG2 RECV DATA  : {}", recvSecondHgtpRegisterRequest);
//
//            // Decoding nonce -> realm
//            MessageDigest messageDigestNonce = MessageDigest.getInstance("MD5");
//            messageDigestNonce.update(SERVER_TEST_REALM.getBytes(StandardCharsets.UTF_8));
//            messageDigestNonce.update(TEST_HASH_KEY.getBytes(StandardCharsets.UTF_8));
//            byte[] digestNonce = messageDigestNonce.digest();
//            messageDigestNonce.reset();
//            messageDigestNonce.update(digestNonce);
//
//            String curNonce = new String(messageDigestNonce.digest());
//
//            short messageType;
//            String msgType = "";
//            if (curNonce.equals(recvSecondHgtpRegisterRequest.getHgtpContent().getNonce())) {
//                if (AVAILABLE_REGISTER > userInfoMap.size()) {
//                    userInfoMap.put(recvSecondHgtpRegisterRequest.getHgtpHeader().getUserId(), userId);
//                    messageType = HgtpMessageType.OK;
//                    msgType = "OK";
//                } else if (userInfoMap.containsKey(recvSecondHgtpRegisterRequest.getHgtpHeader().getUserId())) {
//                    messageType = HgtpMessageType.SERVER_UNAVAILABLE;
//                    msgType = "SUA";
//                } else {
//                    messageType = HgtpMessageType.UNKNOWN;
//                    msgType = "UNW";
//                }
//            } else {
//                messageType = HgtpMessageType.FORBIDDEN;
//                msgType = "FBN";
//            }
//
//            // send response
//            HgtpHeader recvReg2Header = recvSecondHgtpRegisterRequest.getHgtpHeader();
//            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
//                    messageType,
//                    recvReg2Header.getMessageType(), recvReg2Header.getUserId(),
//                    recvReg2Header.getSeqNumber() + 1
//                    );
//            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
//            // recv response
//            byte[] recvResponse = sendHgtpResponse.getByteData();
//            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
//            log.debug("{} RECV DATA  : {}", msgType, recvHgtpResponse);
//
//        } catch (HgtpException | NoSuchAlgorithmException e) {
//            log.error("HgtpTest.hgtpRegisterTest ", e);
//        }
//    }
//
//    @Test
//    public void hgtpUnregisterTest(String userId){
//        try {
//            // send Unregister
//            HgtpUnregisterRequest sendHgtpUnregisterRequest = new HgtpUnregisterRequest(
//                    userId, 7);
//            log.debug("SEND DATA : {}", sendHgtpUnregisterRequest);
//            // recv Unregister
//            byte[] recvRequestUnregister = sendHgtpUnregisterRequest.getByteData();
//            HgtpUnregisterRequest recvHgtpUnregisterRequest = new HgtpUnregisterRequest(recvRequestUnregister);
//            log.debug("RECV DATA  : {}", recvHgtpUnregisterRequest);
//
//            short messageType;
//            String msgType = "";
//            if (isServerError) {
//                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
//                msgType = "SUA";
//            } else {
//                if (recvHgtpUnregisterRequest.getHgtpHeader().getMessageType() != HgtpMessageType.UNREGISTER || !userInfoMap.containsKey(recvHgtpUnregisterRequest.getHgtpHeader().getUserId())) {
//                    messageType = HgtpMessageType.BAD_REQUEST;
//                    msgType = "BAD";
//                } else {
//                    userInfoMap.remove(recvHgtpUnregisterRequest.getHgtpHeader().getUserId());
//                    messageType = HgtpMessageType.OK;
//                    msgType = "OK";
//                }
//            }
//            // send response
//            HgtpHeader recvUnregHeader = recvHgtpUnregisterRequest.getHgtpHeader();
//            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
//                    messageType, recvUnregHeader.getMessageType(), recvUnregHeader.getUserId(),
//                    recvUnregHeader.getSeqNumber() + 1);
//            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
//            // recv response
//            byte[] recvResponse = sendHgtpResponse.getByteData();
//            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
//            log.debug("{} RECV DATA : {}", msgType, recvHgtpResponse);
//
//        } catch (HgtpException e) {
//            log.error("HgtpTest.hgtpUnregisterTest ", e);
//        }
//    }
//
//    @Test
//    public void hgtpCreateRoomTest(String userId, String roomId){
//        try {
//            // send Create room
//            HgtpCreateRoomRequest sendHgtpCreateRoomRequest = new HgtpCreateRoomRequest(
//                    userId, 9, roomId
//            );
//            log.debug("SEND DATA : {}", sendHgtpCreateRoomRequest);
//
//            // recv Create room
//            byte[] recvRequestCreateRoom = sendHgtpCreateRoomRequest.getByteData();
//            HgtpCreateRoomRequest recvHgtpCreateRoomReqeust = new HgtpCreateRoomRequest(recvRequestCreateRoom);
//            log.debug("RECV DATA  : {}", recvHgtpCreateRoomReqeust);
//
//            short messageType;
//            String msgType = "";
//            if (isServerError || AVAILABLE_ROOM < roomInfoMap.size()) {
//                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
//                msgType = "SUA";
//            } else {
//                if (recvHgtpCreateRoomReqeust.getHgtpHeader().getMessageType() != HgtpMessageType.CREATE_ROOM
//                        || !userInfoMap.containsKey(recvHgtpCreateRoomReqeust.getHgtpHeader().getUserId())){
//                    messageType = HgtpMessageType.BAD_REQUEST;
//                    msgType = "BAD";
//                } else {
//                    roomInfoMap.put(recvHgtpCreateRoomReqeust.getHgtpContent().getRoomId(), roomId);
//                    messageType = HgtpMessageType.OK;
//                    msgType = "OK";
//                }
//            }
//            // send response
//            HgtpHeader recvCreateHeader = recvHgtpCreateRoomReqeust.getHgtpHeader();
//            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
//                    recvCreateHeader.getMessageType(), recvCreateHeader.getRequestType(), recvCreateHeader.getUserId(),
//                    recvCreateHeader.getSeqNumber() + 1);
//            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
//            // recv response
//            byte[] recvResponse = sendHgtpResponse.getByteData();
//            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
//            log.debug("{} RECV DATA : {}", msgType, recvHgtpResponse);
//        } catch (HgtpException e) {
//            log.error("HgtpTest.hgtpCreateRoomTest ", e);
//        }
//    }
//
//    @Test
//    public void hgtpDeleteRoomTest(String userId, String roomId){
//        try {
//            // send Delete room
//            HgtpDeleteRoomRequest sendHgtpDeleteRoomRequest = new HgtpDeleteRoomRequest(
//                    userId, 9, roomId
//            );
//            log.debug("SEND DATA : {}", sendHgtpDeleteRoomRequest);
//
//            // recv Delete room
//            byte[] recvRequestDeleteRoom = sendHgtpDeleteRoomRequest.getByteData();
//            HgtpDeleteRoomRequest recvHgtpDeleteRoomReqeust = new HgtpDeleteRoomRequest(recvRequestDeleteRoom);
//            log.debug("RECV DATA  : {}", recvHgtpDeleteRoomReqeust);
//
//            short messageType;
//            String msgType = "";
//            if (isServerError) {
//                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
//                msgType = "SUA";
//            } else {
//                if (recvHgtpDeleteRoomReqeust.getHgtpHeader().getMessageType() != HgtpMessageType.DELETE_ROOM || !roomInfoMap.containsKey(recvHgtpDeleteRoomReqeust.getHgtpContent().getRoomId())){
//                    messageType = HgtpMessageType.BAD_REQUEST;
//                    msgType = "BAD";
//                } else {
//                    messageType = HgtpMessageType.OK;
//                    msgType = "OK";
//                }
//            }
//            // send response
//            HgtpHeader recvUnregHeader = recvHgtpDeleteRoomReqeust.getHgtpHeader();
//            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
//                    messageType, recvUnregHeader.getMessageType(), recvUnregHeader.getUserId(),
//                    recvUnregHeader.getSeqNumber() + 1);
//            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
//            // recv response
//            byte[] recvResponse = sendHgtpResponse.getByteData();
//            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
//            log.debug("{} RECV DATA : {}", msgType, recvHgtpResponse);
//
//
//        } catch (HgtpException e) {
//            log.error("HgtpTest.hgtpDeleteRoomTest ", e);
//        }
//    }
//
//    @Test
//    public void hgtpJoinRoomTest(String userId, String roomId){
//        try {
//            // send Join room
//            HgtpJoinRoomRequest sendHgtpJoinRoomReqeust = new HgtpJoinRoomRequest(
//                    userId, 9, roomId
//            );
//            log.debug("SEND DATA : {}", sendHgtpJoinRoomReqeust);
//
//            // recv Join room
//            byte[] recvRequestJoinRoom = sendHgtpJoinRoomReqeust.getByteData();
//            HgtpJoinRoomRequest recvHgtpJoinRoomReqeust = new HgtpJoinRoomRequest(recvRequestJoinRoom);
//            log.debug("RECV DATA  : {}", recvHgtpJoinRoomReqeust);
//
//            short messageType;
//            String msgType = "";
//            if (isServerError) {
//                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
//                msgType = "SUA";
//            } else {
//                if (recvHgtpJoinRoomReqeust.getHgtpHeader().getMessageType() != HgtpMessageType.JOIN_ROOM || !roomInfoMap.containsKey(recvHgtpJoinRoomReqeust.getHgtpContent().getRoomId())){
//                    messageType = HgtpMessageType.BAD_REQUEST;
//                    msgType = "BAD";
//                } else {
//                    roomInfoMap.put(recvHgtpJoinRoomReqeust.getHgtpContent().getRoomId(), roomId);
//                    messageType = HgtpMessageType.OK;
//                    msgType = "OK";
//                }
//            }
//            // send response
//            HgtpHeader recvJoinHeader = recvHgtpJoinRoomReqeust.getHgtpHeader();
//            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
//                    messageType, recvJoinHeader.getMessageType(), recvJoinHeader.getUserId(),
//                    recvJoinHeader.getSeqNumber() + 1);
//            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
//            // recv response
//            byte[] recvResponse = sendHgtpResponse.getByteData();
//            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
//            log.debug("{} RECV DATA : {}", msgType, recvHgtpResponse);
//        } catch (HgtpException e) {
//            log.error("HgtpTest.hgtpJoinRoomTest ", e);
//        }
//    }
//
//    @Test
//    public void hgtpExitRoomTest(String userId, String roomId){
//        try {
//            // send Exit room
//            HgtpExitRoomRequest sendHgtpExitRoomRequest = new HgtpExitRoomRequest(
//                    userId, 9, roomId
//            );
//            log.debug("SEND DATA : {}", sendHgtpExitRoomRequest);
//
//            // recv Exit room
//            byte[] recvRequestExitRoom = sendHgtpExitRoomRequest.getByteData();
//            HgtpExitRoomRequest recvHgtpExitRoomReqeust = new HgtpExitRoomRequest(recvRequestExitRoom);
//            log.debug("RECV DATA  : {}", recvHgtpExitRoomReqeust);
//
//            short messageType;
//            String msgType = "";
//            if (isServerError) {
//                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
//                msgType = "SUA";
//            } else {
//                if (recvHgtpExitRoomReqeust.getHgtpHeader().getMessageType() != HgtpMessageType.EXIT_ROOM || !roomInfoMap.containsKey(recvHgtpExitRoomReqeust.getHgtpContent().getRoomId())){
//                    messageType = HgtpMessageType.BAD_REQUEST;
//                    msgType = "BAD";
//                } else {
//                    roomInfoMap.put(recvHgtpExitRoomReqeust.getHgtpContent().getRoomId(), roomId);
//                    messageType = HgtpMessageType.OK;
//                    msgType = "OK";
//                }
//            }
//            // send response
//            HgtpHeader recvExitHeader = recvHgtpExitRoomReqeust.getHgtpHeader();
//            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
//                    messageType, recvExitHeader.getMessageType(), recvExitHeader.getUserId(),
//                    recvExitHeader.getSeqNumber() + 1);
//            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
//            // recv response
//            byte[] recvResponse = sendHgtpResponse.getByteData();
//            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
//            log.debug("{} RECV DATA : {}", msgType, recvHgtpResponse);
//        } catch (HgtpException e) {
//            log.error("HgtpTest.hgtpExitRoomTest ", e);
//        }
//    }
//
//    @Test
//    public void hgtpInviteUserFromRoomTest(String userId, String roomId, String peerUserId){
//        try {
//            // send Exit room
//            HgtpInviteUserFromRoomRequest sendHgtpInviteUserFromRoomRequest = new HgtpInviteUserFromRoomRequest(
//                    userId, 9, roomId, peerUserId
//            );
//            log.debug("SEND DATA : {}", sendHgtpInviteUserFromRoomRequest);
//
//            // recv Exit room
//            byte[] recvHgtpInviteUserFromRoom = sendHgtpInviteUserFromRoomRequest.getByteData();
//            HgtpInviteUserFromRoomRequest recvHgtpInviteUserFromRoomRequest = new HgtpInviteUserFromRoomRequest(recvHgtpInviteUserFromRoom);
//            log.debug("RECV DATA  : {}", recvHgtpInviteUserFromRoomRequest);
//
//            short messageType;
//            String msgType = "";
//            if (isServerError) {
//                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
//                msgType = "SUA";
//            } else {
//                if (recvHgtpInviteUserFromRoomRequest.getHgtpHeader().getMessageType() != HgtpMessageType.INVITE_USER_FROM_ROOM
//                        || !roomInfoMap.containsKey(recvHgtpInviteUserFromRoomRequest.getHgtpContent().getRoomId())
//                        || !userInfoMap.containsKey(recvHgtpInviteUserFromRoomRequest.getHgtpContent().getPeerHostName())){
//                    messageType = HgtpMessageType.BAD_REQUEST;
//                    msgType = "BAD";
//                } else {
//                    roomInfoMap.put(recvHgtpInviteUserFromRoomRequest.getHgtpContent().getRoomId(), roomId);
//                    messageType = HgtpMessageType.OK;
//                    msgType = "OK";
//                }
//            }
//            // send response
//            HgtpHeader recvInviteUserFromRoomHeader = recvHgtpInviteUserFromRoomRequest.getHgtpHeader();
//            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
//                    messageType, recvInviteUserFromRoomHeader.getMessageType(), recvInviteUserFromRoomHeader.getUserId(),
//                    recvInviteUserFromRoomHeader.getSeqNumber() + 1);
//            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
//            // recv response
//            byte[] recvResponse = sendHgtpResponse.getByteData();
//            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
//            log.debug("{} RECV DATA : {}", msgType, recvHgtpResponse);
//        } catch (HgtpException e) {
//            log.error("HgtpTest.hgtpInviteUserFromRoomTest ", e);
//        }
//    }
//
//    @Test
//    public void hgtpRemoveUserFromRoomTest(String userId, String roomId, String peerUserId){
//        try {
//            // send Exit room
//            HgtpRemoveUserFromRoomRequest sendHgtpRemoveUserFromRoomRequest = new HgtpRemoveUserFromRoomRequest(
//                    userId, 9, roomId, peerUserId
//            );
//            log.debug("SEND DATA : {}", sendHgtpRemoveUserFromRoomRequest);
//
//            // recv Exit room
//            byte[] recvHgtpRemoveUserFromRoom = sendHgtpRemoveUserFromRoomRequest.getByteData();
//            HgtpRemoveUserFromRoomRequest recvHgtpRemoveUserFromRoomRequest = new HgtpRemoveUserFromRoomRequest(recvHgtpRemoveUserFromRoom);
//            log.debug("RECV DATA  : {}", recvHgtpRemoveUserFromRoomRequest);
//
//            short messageType;
//            String msgType = "";
//            if (isServerError) {
//                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
//                msgType = "SUA";
//            } else {
//                if (recvHgtpRemoveUserFromRoomRequest.getHgtpHeader().getMessageType() != HgtpMessageType.REMOVE_USER_FROM_ROOM
//                        || !roomInfoMap.containsKey(recvHgtpRemoveUserFromRoomRequest.getHgtpContent().getRoomId())
//                        || !userInfoMap.containsKey(recvHgtpRemoveUserFromRoomRequest.getHgtpContent().getPeerHostName())){
//                    messageType = HgtpMessageType.BAD_REQUEST;
//                    msgType = "BAD";
//                } else {
//                    roomInfoMap.put(recvHgtpRemoveUserFromRoomRequest.getHgtpContent().getRoomId(), roomId);
//                    messageType = HgtpMessageType.OK;
//                    msgType = "OK";
//                }
//            }
//            // send response
//            HgtpHeader recvRemoveUserFromRoomHeader = recvHgtpRemoveUserFromRoomRequest.getHgtpHeader();
//            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
//                    messageType, recvRemoveUserFromRoomHeader.getMessageType(), recvRemoveUserFromRoomHeader.getUserId(),
//                    recvRemoveUserFromRoomHeader.getSeqNumber() + 1);
//            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
//            // recv response
//            byte[] recvResponse = sendHgtpResponse.getByteData();
//            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
//            log.debug("{} RECV DATA : {}", msgType, recvHgtpResponse);
//        } catch (HgtpException e) {
//            log.error("HgtpTest.hgtpRemoveUserFromRoomTest ", e);
//        }
//    }
}
