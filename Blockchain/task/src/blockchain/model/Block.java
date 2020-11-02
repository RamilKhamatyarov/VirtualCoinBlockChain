package blockchain.model;

import blockchain.util.StringUtil;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Block {
    private static final AtomicInteger nextId = new AtomicInteger(0);

    private final Integer id;
    private final String previousHash;
    private final Integer difficulty;
    private final String difficultyMessage;
    private final Long minerId;
    private final BigInteger minerAmount;

    private List<VirtualCoinTransaction> transactionList;
    private Long magicNumber;
    private Timestamp timestamp;
    private String hash;
    private Long proofOfWork = 0L;

    public Block(List<VirtualCoinTransaction> transactionList, String previousHash, Integer difficulty, String difficultyMessage, BigInteger minerAmount) {
        this.id = nextId.incrementAndGet();
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.transactionList = transactionList;
        this.previousHash = (previousHash == null) ? "0" : previousHash;
        this.difficulty = difficulty;
        this.difficultyMessage = difficultyMessage;
        this.minerId = Thread.currentThread().getId();
        this.minerAmount = minerAmount;
    }

    public void mineBlock(List<VirtualCoinTransaction> messageList) {
        Long startTime = System.currentTimeMillis();
        while (true) {
            this.magicNumber = new Random().nextLong() & Long.MAX_VALUE;

            this.timestamp = new Timestamp(System.currentTimeMillis());
            this.hash = StringUtil.applySha256(String.valueOf(this.hashCode()));

            if (this.hash.matches("^0{" + difficulty + "}[0-9a-z]+")) {
                this.transactionList = messageList;
                messageList = new ArrayList<>();
                break;
            }
        }
        Long endTime = System.currentTimeMillis();
        this.proofOfWork = endTime - startTime;
    }

    public String getHash() {
        return hash;
    }


    public String getPreviousHash() {
        return previousHash;
    }

    public String getFormattedVirtualCoinTransactions() {
        if (transactionList == null) {
            return "No transactions";
        }
        return String.join("\n\r", this.transactionList.stream()
                .map(t -> String.format("%s sent %d VC to %s", t.getFromAddress(), t.getAmount(), t.getToAddress()))
                .findAny().orElse("No transactions"));
    }

    public List<VirtualCoinTransaction> getTransactionList() {
        return transactionList;
    }


    public Long getProofOfWork() {
        return proofOfWork;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, magicNumber, timestamp.getTime(), hash);
    }

    @Override
    public String toString() {
        return "Block:" +
                "\r\nCreated by miner # " + minerId +
                "\r\n" + String.format("%s gets %s VC", minerId, minerAmount.toString()) +
                "\r\nId: " + id +
                "\r\nTimestamp: " + timestamp.getTime() +
                "\r\nMagic number: " + magicNumber +
                "\r\nHash of the previous block: \r\n" + previousHash +
                "\r\nHash of the block: \r\n" + hash +
                "\r\nBlock data: \r\n" + getFormattedVirtualCoinTransactions() +
                "\r\nBlock was generating for " + proofOfWork / 1000 + " seconds" +
                "\r\n" + difficultyMessage +
                "\r\n";
    }
}
