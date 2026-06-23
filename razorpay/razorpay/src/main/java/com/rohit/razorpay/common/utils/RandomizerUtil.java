package com.rohit.razorpay.common.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomizerUtil {

        private final static SecureRandom SECURE_RANDOM = new SecureRandom();

        public static String randomBase64(int length){
            byte[] buff = new byte[length];
            SECURE_RANDOM.nextBytes(buff);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(buff);
        }
}
