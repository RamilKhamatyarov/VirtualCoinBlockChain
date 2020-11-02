package blockchain;

import blockchain.model.BlockChain;
import blockchain.model.VirtualCoinTransaction;
import blockchain.service.MinerHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        BlockChain blockChain = new BlockChain();

        ExecutorService executor = Executors.newFixedThreadPool(15);
        executor.execute(new MinerHandler(blockChain));

        blockChain.addTransaction(
                new VirtualCoinTransaction("miner9", "miner1", "10"));

        for (int i = 0; i < 14; i++) {
            executor.execute(new MinerHandler(blockChain));
        }

        if (executor.awaitTermination(1, TimeUnit.SECONDS) || blockChain.getChain().size() > 15) {
            executor.shutdownNow();
        }

        Thread.currentThread().join(10000L);

        System.out.println(blockChain);
    }
}



