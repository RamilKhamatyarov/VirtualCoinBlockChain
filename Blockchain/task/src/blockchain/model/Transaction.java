package blockchain.model;

import blockchain.util.BlockDataSignGenerator;
import blockchain.util.StringUtil;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Objects;

class Transaction {
    private final String fromAddress;
    private final String toAddress;
    private final BigInteger amount;
    private final Timestamp timestamp;
    private final byte[] sign;

    public Transaction(String fromAddress, String toAddress, BigInteger amount) {
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
        this.timestamp = new Timestamp(System.currentTimeMillis());

        this.sign = BlockDataSignGenerator.sign(toString());
    }

    public String calculateHash() {
        return StringUtil.applySha256(String.valueOf(this.hashCode()));
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public byte[] getSign() {
        return sign;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromAddress, toAddress, amount, timestamp, sign);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}