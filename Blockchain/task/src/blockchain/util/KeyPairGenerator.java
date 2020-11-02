package blockchain.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.security.*;

public class KeyPairGenerator {

    private java.security.KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public KeyPairGenerator(int keylength) throws NoSuchAlgorithmException, NoSuchProviderException {
        this.keyGen = java.security.KeyPairGenerator.getInstance("RSA");
        this.keyGen.initialize(keylength);
    }

    public void createKeys() {
        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }

    public static void main(String[] args) {
        KeyPairGenerator gk;
        try {
            gk = new KeyPairGenerator(1024);
            gk.createKeys();
            gk.writeToFile(getCurrentDirectoryPath() + "/BlockChain/task/KeyPair/publicKey", gk.getPublicKey().getEncoded());
            gk.writeToFile(getCurrentDirectoryPath() + "/BlockChain/task/KeyPair/privateKey", gk.getPrivateKey().getEncoded());
        } catch (NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public static String getCurrentDirectoryPath() {
        return FileSystems.getDefault()
                .getPath("")
                .toAbsolutePath()
                .toString();
    }

}
