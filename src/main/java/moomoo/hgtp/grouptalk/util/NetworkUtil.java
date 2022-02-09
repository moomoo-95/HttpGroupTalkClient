package moomoo.hgtp.grouptalk.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class NetworkUtil {

    private static final Encoder encoder = Base64.getEncoder();
    private static final Decoder decoder = Base64.getDecoder();

    private NetworkUtil() {
        // nothing
    }

    public static long ipToLong(String ipAddress) {
        long result = 0;
        String[] ipAddressInArray = ipAddress.split("\\.");

        for (int i = 3; i >= 0; i--) {
            long ip = Long.parseLong(ipAddressInArray[3 - i]);
            //left shifting 24,16,8,0 and bitwise OR
            //1. 192 << 24
            //1. 168 << 16
            //1. 1   << 8
            //1. 2   << 0
            result |= ip << (i * 8);
        }

        return result;
    }

    public static String longToIp (long ip) {
        StringBuilder result = new StringBuilder(15);

        for (int i = 0; i < 4; i++) {
            result.insert(0, ip & 0xff);
            if (i < 3) {
                result.insert(0,'.');
            }
            ip = ip >> 8;
        }

        return result.toString();
    }

    public static String messageEncoding(String decodingMessage) {
        return new String( encoder.encode(decodingMessage.getBytes(StandardCharsets.UTF_8)) );

    }

    public static String messageDecoding(String encodingMessage) {
        return new String( decoder.decode(encodingMessage) );
    }

    public static String createNonce(String algorithm, String realm, String hashKey) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(realm.getBytes(StandardCharsets.UTF_8));
            messageDigest.update(hashKey.getBytes(StandardCharsets.UTF_8));
            byte[] digestNonce = messageDigest.digest();
            messageDigest.reset();
            messageDigest.update(digestNonce);

            return new String(messageDigest.digest());
        } catch (Exception e) {
            // ignore
        }
        return "";
    }
}
