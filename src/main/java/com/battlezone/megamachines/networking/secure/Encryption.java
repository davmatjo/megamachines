package com.battlezone.megamachines.networking.secure;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class Encryption {
    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[]{'5', '3', 'b', 'L', 'o', 'v', '3',
                    'S', '#', 'c', 'r', '#', 't', 'K', '*', 'y'};
    private static Key key = new SecretKeySpec(keyValue, ALGO);
    private static Cipher enc, dec;

    public static void setUp() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        enc = Cipher.getInstance(ALGO);
        enc.init(Cipher.ENCRYPT_MODE, key);
        dec = Cipher.getInstance(ALGO);
        dec.init(Cipher.DECRYPT_MODE, key);
    }

    public static byte[] encrypt(byte[] Data) throws Exception {
        byte[] encVal = enc.doFinal(Data);
        return encVal;
    }

    public static byte[] decrypt(byte[] encryptedData) throws Exception {
        byte[] decValue = dec.doFinal(encryptedData);
        return decValue;
    }

    private static Key generateKey() {
        return key;
    }
}
