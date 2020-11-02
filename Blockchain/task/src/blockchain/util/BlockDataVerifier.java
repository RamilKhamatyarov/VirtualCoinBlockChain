package blockchain.util;

import blockchain.model.VirtualCoinTransaction;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class BlockDataVerifier {
    private static final String DEFAULT_PUB_KEY_FILE = "KeyPair/publicKey";

    private BlockDataVerifier() { }

    public static Boolean verifySignature(VirtualCoinTransaction transaction) {
        return verifySignature(transaction, DEFAULT_PUB_KEY_FILE);
    }

    public static Boolean verifySignature(VirtualCoinTransaction transaction, String keyFile) {
        return verifySignature(transaction.toString().getBytes(), transaction.getSign(), keyFile);
    }

    /**
     * Method for signature verification that initializes with the Public Key,
     * updates the data to be verified and then verifies them using the signature
     *
     * @param data
     * @param signature
     * @param keyFile
     * @return
     * @throws Exception
     */
    private static Boolean verifySignature(byte[] data, byte[] signature, String keyFile) {
        Boolean verifiedResult = Boolean.FALSE;
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(getPublic(keyFile));
            sig.update(data);
            verifiedResult = sig.verify(signature);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return verifiedResult;
    }

    /**
     * Method to retrieve the Public Key from a file
     *
     * @param filename
     * @return
     * @throws Exception
     */
    private static PublicKey getPublic(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
