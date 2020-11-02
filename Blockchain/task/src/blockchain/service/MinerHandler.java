package blockchain.service;

import blockchain.model.BlockChain;

public class MinerHandler extends Thread {
    private final BlockChain blockChain;

    public MinerHandler(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    @Override
    public void run() {
        blockChain.minePendingTransactions();
    }
}