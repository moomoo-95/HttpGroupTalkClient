package com.hgtp.client.protocol.hgtp;

import org.apache.commons.net.ntp.TimeStamp;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hgtp.client.protocol.hgtp.exception.HgtpException;
import com.hgtp.client.protocol.hgtp.message.request.*;
import com.hgtp.client.protocol.hgtp.message.base.HgtpHeader;
import com.hgtp.client.protocol.hgtp.message.base.HgtpMessageType;
import com.hgtp.client.protocol.hgtp.message.response.HgtpCommonResponse;
import com.hgtp.client.protocol.hgtp.message.response.HgtpUnauthorizedResponse;

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

    @Test
    public void hgtpRegisterTest(String userId) {
        try {
            // send first Register
            HgtpRegisterRequest sendFirstHgtpRegisterRequest = new HgtpRegisterRequest(
                    HgtpHeader.MAGIC_COOKIE, HgtpMessageType.REGISTER, userId, 4, TimeStamp.getCurrentTime().getSeconds(),
                    3600L, (short) 5060);
            log.debug("RG1 SEND DATA : {}", sendFirstHgtpRegisterRequest);

            // recv first Register
            byte[] recvFirstRegister = sendFirstHgtpRegisterRequest.getByteData();
            HgtpRegisterRequest recvFirstHgtpRegisterRequest = new HgtpRegisterRequest(recvFirstRegister);
            log.debug("RG1 RECV DATA  : {}", recvFirstHgtpRegisterRequest);

            // send unauthorized
            HgtpHeader recvReg1Header = recvFirstHgtpRegisterRequest.getHgtpHeader();
            HgtpUnauthorizedResponse sendHgtpUnauthorizedResponse = new HgtpUnauthorizedResponse(
                    recvReg1Header.getMagicCookie(), HgtpMessageType.UNAUTHORIZED,
                    recvReg1Header.getRequestType(), recvReg1Header.getUserId(),
                    recvFirstHgtpRegisterRequest.getHgtpHeader().getSeqNumber() + 1, TimeStamp.getCurrentTime().getSeconds(),
                    CLIENT_TEST_REALM);
            log.debug("URE SEND DATA : {}", sendHgtpUnauthorizedResponse);

            // recv unauthorized
            byte[] recvUnauthorized = sendHgtpUnauthorizedResponse.getByteData();
            HgtpUnauthorizedResponse recvHgtpUnauthorizedResponse = new HgtpUnauthorizedResponse(recvUnauthorized);
            log.debug("URE RECV DATA : {}", recvHgtpUnauthorizedResponse);

            // Encoding realm -> nonce
            MessageDigest messageDigestRealm = MessageDigest.getInstance("MD5");
            messageDigestRealm.update(recvHgtpUnauthorizedResponse.getHgtpContext().getRealm().getBytes(StandardCharsets.UTF_8));
            messageDigestRealm.update(TEST_HASH_KEY.getBytes(StandardCharsets.UTF_8));
            byte[] digestRealm = messageDigestRealm.digest();
            messageDigestRealm.reset();
            messageDigestRealm.update(digestRealm);
            String nonce = new String(messageDigestRealm.digest());

            // send second Register
            HgtpHeader recvUnauthHeader = recvHgtpUnauthorizedResponse.getHgtpHeader();
            HgtpRegisterRequest sendSecondHgtpRegisterRequest = new HgtpRegisterRequest(
                    HgtpHeader.MAGIC_COOKIE, HgtpMessageType.REGISTER, recvUnauthHeader.getUserId(),
                    recvUnauthHeader.getSeqNumber() + 1, TimeStamp.getCurrentTime().getSeconds(),
                    3600L, (short) 5060);
            sendSecondHgtpRegisterRequest.getHgtpContext().setNonce(sendSecondHgtpRegisterRequest.getHgtpHeader(), nonce);
            log.debug("RG2 SEND DATA : {}", sendSecondHgtpRegisterRequest);

            // recv second Register
            byte[] recvSecondRegister = sendSecondHgtpRegisterRequest.getByteData();
            HgtpRegisterRequest recvSecondHgtpRegisterRequest = new HgtpRegisterRequest(recvSecondRegister);
            log.debug("RG2 RECV DATA  : {}", recvSecondHgtpRegisterRequest);

            // Decoding nonce -> realm
            MessageDigest messageDigestNonce = MessageDigest.getInstance("MD5");
            messageDigestNonce.update(SERVER_TEST_REALM.getBytes(StandardCharsets.UTF_8));
            messageDigestNonce.update(TEST_HASH_KEY.getBytes(StandardCharsets.UTF_8));
            byte[] digestNonce = messageDigestNonce.digest();
            messageDigestNonce.reset();
            messageDigestNonce.update(digestNonce);

            String curNonce = new String(messageDigestNonce.digest());

            short messageType;
            String msgType = "";
            if (curNonce.equals(recvSecondHgtpRegisterRequest.getHgtpContext().getNonce())) {
                if (AVAILABLE_REGISTER > userInfoMap.size()) {
                    userInfoMap.put(recvSecondHgtpRegisterRequest.getHgtpHeader().getUserId(), userId);
                    messageType = HgtpMessageType.OK;
                    msgType = "OK";
                } else if (userInfoMap.containsKey(recvSecondHgtpRegisterRequest.getHgtpHeader().getUserId())) {
                    messageType = HgtpMessageType.SERVER_UNAVAILABLE;
                    msgType = "SUA";
                } else {
                    messageType = HgtpMessageType.UNKNOWN;
                    msgType = "UNW";
                }
            } else {
                messageType = HgtpMessageType.FORBIDDEN;
                msgType = "FBN";
            }

            // send response
            HgtpHeader recvReg2Header = recvSecondHgtpRegisterRequest.getHgtpHeader();
            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
                    recvSecondHgtpRegisterRequest.getHgtpHeader().getMagicCookie(), messageType,
                    recvReg2Header.getMessageType(), recvReg2Header.getUserId(),
                    recvReg2Header.getSeqNumber() + 1, TimeStamp.getCurrentTime().getSeconds()
                    );
            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
            // recv response
            byte[] recvResponse = sendHgtpResponse.getByteData();
            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
            log.debug("{} RECV DATA  : {}", msgType, recvHgtpResponse);

        } catch (HgtpException | NoSuchAlgorithmException e) {
            log.error("HgtpTest.hgtpRegisterTest ", e);
        }
    }

    @Test
    public void hgtpUnregisterTest(String userId){
        try {
            // send Unregister
            HgtpUnregisterRequest sendHgtpUnregisterRequest = new HgtpUnregisterRequest(
                    HgtpHeader.MAGIC_COOKIE, HgtpMessageType.UNREGISTER, userId, 7, TimeStamp.getCurrentTime().getSeconds());
            log.debug("SEND DATA : {}", sendHgtpUnregisterRequest);
            // recv Unregister
            byte[] recvRequestUnregister = sendHgtpUnregisterRequest.getByteData();
            HgtpUnregisterRequest recvHgtpUnregisterRequest = new HgtpUnregisterRequest(recvRequestUnregister);
            log.debug("RECV DATA  : {}", recvHgtpUnregisterRequest);

            short messageType;
            String msgType = "";
            if (isServerError) {
                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
                msgType = "SUA";
            } else {
                if (recvHgtpUnregisterRequest.getHgtpHeader().getMessageType() != HgtpMessageType.UNREGISTER || !userInfoMap.containsKey(recvHgtpUnregisterRequest.getHgtpHeader().getUserId())) {
                    messageType = HgtpMessageType.BAD_REQUEST;
                    msgType = "BAD";
                } else {
                    userInfoMap.remove(recvHgtpUnregisterRequest.getHgtpHeader().getUserId());
                    messageType = HgtpMessageType.OK;
                    msgType = "OK";
                }
            }
            // send response
            HgtpHeader recvUnregHeader = recvHgtpUnregisterRequest.getHgtpHeader();
            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
                    recvUnregHeader.getMagicCookie(), messageType, recvUnregHeader.getMessageType(), recvUnregHeader.getUserId(),
                    recvUnregHeader.getSeqNumber() + 1, TimeStamp.getCurrentTime().getSeconds());
            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
            // recv response
            byte[] recvResponse = sendHgtpResponse.getByteData();
            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
            log.debug("{} RECV DATA : {}", msgType, recvHgtpResponse);

        } catch (HgtpException e) {
            log.error("HgtpTest.hgtpUnregisterTest ", e);
        }
    }

    @Test
    public void hgtpCreateRoomTest(String userId, String roomId){
        try {
            // send Create room
            HgtpCreateRoomRequest sendHgtpCreateRoomReqeust = new HgtpCreateRoomRequest(
                    HgtpHeader.MAGIC_COOKIE, HgtpMessageType.CREATE_ROOM, userId, 9, TimeStamp.getCurrentTime().getSeconds(), roomId
            );
            log.debug("SEND DATA : {}", sendHgtpCreateRoomReqeust);

            // recv Create room
            byte[] recvRequestCreateRoom = sendHgtpCreateRoomReqeust.getByteData();
            HgtpCreateRoomRequest recvHgtpCreateRoomReqeust = new HgtpCreateRoomRequest(recvRequestCreateRoom);
            log.debug("RECV DATA  : {}", recvHgtpCreateRoomReqeust);

            short messageType;
            String msgType = "";
            if (isServerError || AVAILABLE_ROOM < roomInfoMap.size()) {
                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
                msgType = "SUA";
            } else {
                if (recvHgtpCreateRoomReqeust.getHgtpHeader().getMessageType() != HgtpMessageType.CREATE_ROOM || !userInfoMap.containsKey(recvHgtpCreateRoomReqeust.getHgtpHeader().getUserId())){
                    messageType = HgtpMessageType.BAD_REQUEST;
                    msgType = "BAD";
                } else {
                    roomInfoMap.put(recvHgtpCreateRoomReqeust.getHgtpContext().getRoomId(), roomId);
                    messageType = HgtpMessageType.OK;
                    msgType = "OK";
                }
            }
            // send response
            HgtpHeader recvCreateHeader = recvHgtpCreateRoomReqeust.getHgtpHeader();
            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
                    recvCreateHeader.getMagicCookie(), messageType, recvCreateHeader.getMessageType(), recvCreateHeader.getUserId(),
                    recvCreateHeader.getSeqNumber() + 1, TimeStamp.getCurrentTime().getSeconds());
            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
            // recv response
            byte[] recvResponse = sendHgtpResponse.getByteData();
            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
            log.debug("{} RECV DATA : {}", msgType, recvHgtpResponse);
        } catch (HgtpException e) {
            log.error("HgtpTest.hgtpCreateRoomTest ", e);
        }
    }

    @Test
    public void hgtpDeleteRoomTest(String userId, String roomId){
        try {
            // send Delete room
            HgtpDeleteRoomRequest sendHgtpDeleteRoomReqeust = new HgtpDeleteRoomRequest(
                    HgtpHeader.MAGIC_COOKIE, HgtpMessageType.DELETE_ROOM, userId, 9, TimeStamp.getCurrentTime().getSeconds(), roomId
            );
            log.debug("SEND DATA : {}", sendHgtpDeleteRoomReqeust);

            // recv Delete room
            byte[] recvRequestDeleteRoom = sendHgtpDeleteRoomReqeust.getByteData();
            HgtpDeleteRoomRequest recvHgtpDeleteRoomReqeust = new HgtpDeleteRoomRequest(recvRequestDeleteRoom);
            log.debug("RECV DATA  : {}", recvHgtpDeleteRoomReqeust);

            short messageType;
            String msgType = "";
            if (isServerError) {
                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
                msgType = "SUA";
            } else {
                if (recvHgtpDeleteRoomReqeust.getHgtpHeader().getMessageType() != HgtpMessageType.DELETE_ROOM || !roomInfoMap.containsKey(recvHgtpDeleteRoomReqeust.getHgtpContext().getRoomId())){
                    messageType = HgtpMessageType.BAD_REQUEST;
                    msgType = "BAD";
                } else {
                    messageType = HgtpMessageType.OK;
                    msgType = "OK";
                }
            }
            // send response
            HgtpHeader recvUnregHeader = recvHgtpDeleteRoomReqeust.getHgtpHeader();
            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
                    recvUnregHeader.getMagicCookie(), messageType, recvUnregHeader.getMessageType(), recvUnregHeader.getUserId(),
                    recvUnregHeader.getSeqNumber() + 1, TimeStamp.getCurrentTime().getSeconds());
            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
            // recv response
            byte[] recvResponse = sendHgtpResponse.getByteData();
            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
            log.debug("{} RECV DATA : {}", msgType, recvHgtpResponse);


        } catch (HgtpException e) {
            log.error("HgtpTest.hgtpDeleteRoomTest ", e);
        }
    }

    @Test
    public void hgtpJoinRoomTest(String userId, String roomId){
        try {
            // send Join room
            HgtpJoinRoomRequest sendHgtpJoinRoomReqeust = new HgtpJoinRoomRequest(
                    HgtpHeader.MAGIC_COOKIE, HgtpMessageType.JOIN_ROOM, userId, 9, TimeStamp.getCurrentTime().getSeconds(), roomId
            );
            log.debug("SEND DATA : {}", sendHgtpJoinRoomReqeust);

            // recv Join room
            byte[] recvRequestJoinRoom = sendHgtpJoinRoomReqeust.getByteData();
            HgtpJoinRoomRequest recvHgtpJoinRoomReqeust = new HgtpJoinRoomRequest(recvRequestJoinRoom);
            log.debug("RECV DATA  : {}", recvHgtpJoinRoomReqeust);

            short messageType;
            String msgType = "";
            if (isServerError) {
                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
                msgType = "SUA";
            } else {
                if (recvHgtpJoinRoomReqeust.getHgtpHeader().getMessageType() != HgtpMessageType.JOIN_ROOM || !roomInfoMap.containsKey(recvHgtpJoinRoomReqeust.getHgtpContext().getRoomId())){
                    messageType = HgtpMessageType.BAD_REQUEST;
                    msgType = "BAD";
                } else {
                    roomInfoMap.put(recvHgtpJoinRoomReqeust.getHgtpContext().getRoomId(), roomId);
                    messageType = HgtpMessageType.OK;
                    msgType = "OK";
                }
            }
            // send response
            HgtpHeader recvJoinHeader = recvHgtpJoinRoomReqeust.getHgtpHeader();
            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
                    recvJoinHeader.getMagicCookie(), messageType, recvJoinHeader.getMessageType(), recvJoinHeader.getUserId(),
                    recvJoinHeader.getSeqNumber() + 1, TimeStamp.getCurrentTime().getSeconds());
            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
            // recv response
            byte[] recvResponse = sendHgtpResponse.getByteData();
            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
            log.debug("{} RECV DATA : {}", msgType, recvHgtpResponse);
        } catch (HgtpException e) {
            log.error("HgtpTest.hgtpJoinRoomTest ", e);
        }
    }

    @Test
    public void hgtpExitRoomTest(String userId, String roomId){
        try {
            // send Exit room
            HgtpExitRoomRequest sendHgtpExitRoomReqeust = new HgtpExitRoomRequest(
                    HgtpHeader.MAGIC_COOKIE, HgtpMessageType.EXIT_ROOM, userId, 9, TimeStamp.getCurrentTime().getSeconds(), roomId
            );
            log.debug("SEND DATA : {}", sendHgtpExitRoomReqeust);

            // recv Exit room
            byte[] recvRequestExitRoom = sendHgtpExitRoomReqeust.getByteData();
            HgtpExitRoomRequest recvHgtpExitRoomReqeust = new HgtpExitRoomRequest(recvRequestExitRoom);
            log.debug("RECV DATA  : {}", recvHgtpExitRoomReqeust);

            short messageType;
            String msgType = "";
            if (isServerError) {
                messageType = HgtpMessageType.SERVER_UNAVAILABLE;
                msgType = "SUA";
            } else {
                if (recvHgtpExitRoomReqeust.getHgtpHeader().getMessageType() != HgtpMessageType.EXIT_ROOM || !roomInfoMap.containsKey(recvHgtpExitRoomReqeust.getHgtpContext().getRoomId())){
                    messageType = HgtpMessageType.BAD_REQUEST;
                    msgType = "BAD";
                } else {
                    roomInfoMap.put(recvHgtpExitRoomReqeust.getHgtpContext().getRoomId(), roomId);
                    messageType = HgtpMessageType.OK;
                    msgType = "OK";
                }
            }
            // send response
            HgtpHeader recvExitHeader = recvHgtpExitRoomReqeust.getHgtpHeader();
            HgtpCommonResponse sendHgtpResponse = new HgtpCommonResponse(
                    recvExitHeader.getMagicCookie(), messageType, recvExitHeader.getMessageType(), recvExitHeader.getUserId(),
                    recvExitHeader.getSeqNumber() + 1, TimeStamp.getCurrentTime().getSeconds());
            log.debug("{} SEND DATA : {}", msgType, sendHgtpResponse);
            // recv response
            byte[] recvResponse = sendHgtpResponse.getByteData();
            HgtpCommonResponse recvHgtpResponse = new HgtpCommonResponse(recvResponse);
            log.debug("{} RECV DATA : {}", msgType, recvHgtpResponse);
        } catch (HgtpException e) {
            log.error("HgtpTest.hgtpExitRoomTest ", e);
        }
    }
}
