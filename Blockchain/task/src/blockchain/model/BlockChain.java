package blockchain.model;

import blockchain.util.BlockDataVerifier;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BlockChain {
    private final List<Block> chain;
    private Integer difficulty;
    private List<VirtualCoinTransaction> pendingTransactionList;

    public BlockChain() {
        this.difficulty = 0;
        List<Block> chain = new ArrayList<>();
        chain.add(createGenesisBlock(difficulty));
        this.chain =  chain;
        this.pendingTransactionList = new ArrayList<>();
    }

    public Block createGenesisBlock(Integer difficulty) {
        Block block = new Block(
                new ArrayList<>(),
                null,
                difficulty,
                "N was increased to 1",
                new BigInteger("100")
        );

        block.mineBlock(pendingTransactionList);
        return block;
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    /**
     * Takes all the pending transactions, puts them in a Block and starts the
     * mining process. It also adds a transaction to send the mining reward to
     * the given address.
     *
     */
    public synchronized void minePendingTransactions() {
        validateBlock();
        String difficultyMessage = manageDifficulty();

        Block block = new Block(
                pendingTransactionList,
                getLatestBlock().getHash(),
                difficulty,
                difficultyMessage,
                new BigInteger("100")
        );

        block.mineBlock(pendingTransactionList);

        chain.add(block);
        pendingTransactionList = new ArrayList<>();
    }

    private String manageDifficulty() {
        Block latestBlock = getLatestBlock();
        String difficultyMessage = "";
        if (latestBlock.getProofOfWork() / 1000 < 10) {
            difficulty++;
            difficultyMessage = "N was increased to " + (difficulty + 1);
        } else if (latestBlock.getProofOfWork() / 1000 > 60) {
            difficulty--;
            difficultyMessage = "N was decreased by 1";
        } else {
            difficultyMessage = "N stays the same";
        }
        return difficultyMessage;
    }

    private void validateBlock() {
        Block latestBlock = getLatestBlock();

        if (latestBlock == null) {
            return;
        }

        validateLatestBlockByHash(latestBlock);
        validateLatestBlockByDifficulty(latestBlock);
        validateLatestBlockByBlockData(latestBlock);
    }

    private void validateLatestBlockByBlockData(Block latestBlock) {
        if (latestBlock.getTransactionList() == null) {
            return;
        }
        if (latestBlock.getTransactionList().stream()
                .filter(BlockDataVerifier::verifySignature)
                .count() != latestBlock.getTransactionList().size()) {
            throw new Error("Block data not valid " + latestBlock.getFormattedVirtualCoinTransactions());
        }
    }

    private void validateLatestBlockByDifficulty(Block latestBlock) {
        if (!IntStream.range(0, difficulty)
                .map(i -> latestBlock.getHash().substring(0, difficulty).toCharArray()[i])
                .allMatch(c -> c == '0')) {
            throw new Error("Zeros not valid.");
        }
    }

    private void validateLatestBlockByHash(Block latestBlock) {
        if (chain.size() > 2 &&
                !latestBlock.getPreviousHash().equals(chain.get(chain.size() - 2).getHash())) {
            throw new Error("Previous hash not valid.");
        }
    }


    /**
     * Add a new transaction to the list of pending transactions (to be added
     * next time the mining process starts). This verifies that the given
     * transaction is properly signed.
     *
     * @param transaction
     */
    public void addTransaction(VirtualCoinTransaction transaction) {
        if (transaction.getFromAddress() == null || transaction.getToAddress() == null) {
            throw new Error("Transaction must include from and to address");
        }

        if (transaction.getAmount().longValue() <= 0) {
            throw new Error("Transaction amount should be higher than 0");
        }

        pendingTransactionList.add(transaction);
    }

    /**
     * Returns the balance of a given wallet address.
     * Making sure that the amount sent is not greater than existing balance
     *      getBalanceOfAddress(transaction.getFromAddress()).compareTo(transaction.getAmount()) < 0
     *
     * @param address
     * @returns The balance of the wallet
     */
    public BigInteger getBalanceOfAddress(String address) {
        BigInteger balance = new BigInteger("0");

        for (Block block: chain) {
            if (block.getTransactionList() == null) {
                continue;
            }
            for (Transaction transaction: block.getTransactionList()) {
                if (transaction.getFromAddress().equals(address)) {
                    balance = balance.subtract(transaction.getAmount());
                }

                if (transaction.getToAddress().equals(address)) {
                    balance = balance.add(transaction.getAmount());
                }
            }
        }

        return balance;
    }


    /**
     * Returns a list of all transactions that happened
     * to and from the given wallet address.
     *
     * @param  address
     * @return validTransactionList
     */
    public List<Transaction> getAllTransactionsForWallet(String address) {
        List<Transaction> validTransactionList = new ArrayList<>();

        for (Block block: chain) {
            for (Transaction transaction: block.getTransactionList()) {
                if (transaction.getFromAddress().equals(address) ||
                        transaction.getToAddress().equals(address)) {
                    validTransactionList.add(transaction);
                }
            }
        }

        return validTransactionList;
    }

    public List<Block> getChain() {
        return chain;
    }

    @Override
    public String toString() {
        StringBuilder stringBuffer = new StringBuilder();
        for (Block block: chain) {
            stringBuffer.append(block).append('\n');
        }
        return stringBuffer.toString();
    }

}
