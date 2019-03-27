package com.battlezone.megamachines.networking.secure;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class Encryption {
    private static final String ALGO = "AES";
    private static final byte[] keyValue =
            new byte[] { '5', '3', 'b', 'L', 'o', 'v', '3',
                    'S', '#', 'c', 'r','#', 't', 'K', '*', 'y' };
    private static Key key = new SecretKeySpec(keyValue, ALGO);

    public static byte[] encrypt(byte[] Data) throws Exception {
//        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data);
        return encVal;
    }

    public static byte[] decrypt(byte[] encryptedData) throws Exception {
//        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);

        byte[] decValue = c.doFinal(encryptedData);
        return decValue;
    }

    private static Key generateKey() throws Exception {
        return key;
    }
}
