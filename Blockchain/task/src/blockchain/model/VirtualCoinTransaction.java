package blockchain.model;

import java.math.BigInteger;

public class VirtualCoinTransaction extends Transaction {
    public VirtualCoinTransaction(String fromAddress, String toAddress, String amount) {
        super(fromAddress, toAddress, new BigInteger(amount));
    }

}
