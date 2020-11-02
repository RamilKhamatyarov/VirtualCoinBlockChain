package blockchain.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class BlockDataSignGenerator {
    private static final String DEFAULT_KEY_FILE = "KeyPair/privateKey";
    private BlockDataSignGenerator() {}

    public static byte[] sign(String data) {
        return sign(data, DEFAULT_KEY_FILE);
    }
    /**
     * The method that signs the data using the private key that is stored in keyFile path
     *
     * @param data
     * @param keyFile
     * @return
     * @throws InvalidKeyException
     * @throws Exception
     */
    public static byte[] sign(String data, String keyFile) {
        Signature rsa = null;
        byte[] signedMessage = new byte[0];
        try {
            rsa = Signature.getInstance("SHA1withRSA");
            rsa.initSign(getPrivate(keyFile));
            rsa.update(data.getBytes());
            signedMessage = rsa.sign();
        } catch (SignatureException | InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            System.err.println(e.getMessage());
        }

        return signedMessage;
    }

    /**
     * Method to retrieve the Private Key from a file
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivate(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
