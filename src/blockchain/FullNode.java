package blockchain;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class FullNode extends Node {
	public FullNode(String n) {
		super(n);
		mempool = new ConcurrentHashMap<>();
	}

	protected ConcurrentHashMap<String, Transaction> mempool;
	public static int MAX_TX_PER_BLOCK = 10;
	
	@Override
	public void receiveTx(Transaction transaction) {
		
		if (validateTx(transaction)) {
			unminedUTXOs.put(transaction.outputs.get(1).id, transaction.outputs.get(1));
			mempool.put(transaction.transactionId , transaction);
			System.out.println(this.name + " received transaction " + transaction.transactionId);
			
			if (mempool.size() >= MAX_TX_PER_BLOCK) {
				startMining();
			}
		}
	}
	
	@Override
    public boolean validateTx(Transaction tx) {
        if (!super.validateTx(tx)) {
            return false;
        }
        for (TransactionInput txinput : tx.inputs) {
	        for (Transaction transaction : mempool.values()) {
	            for (TransactionInput input : transaction.inputs) {
	                if (input.transactionOutputId.equals(txinput.transactionOutputId) && !transaction.transactionId.equals(tx.transactionId)) {
	                	System.err.println("ERROR! Transaction contains previously used and unprocessed UTXO! " + tx.transactionId +" " + transaction.transactionId);
	                    return false; 
	                }
	            }
	        }
        }
        return true;
	}
	
	public void startMining() {
		Block b = new Block(storedblockchain.get(storedblockchain.size() - 1).hash);
		for (ConcurrentHashMap.Entry<String, Transaction> entry : mempool.entrySet()) {
			Transaction tx = entry.getValue();
			b.transactions.add(tx);
			if (b.transactions.size() == MAX_TX_PER_BLOCK) break;
		}
		b.merkleRoot = StringUtil.getMerkleRoot(b.transactions);
		while (!b.hash.startsWith("0".repeat(network.difficulty))) {
			b.nonce++;
			b.hash = b.calculateHash();
		}
		broadcastBlock(b);
		System.out.println(b.hash);
	}
	
	public void broadcastBlock(Block block) {
		for (Node node : network.network) {
			System.out.println(this.name + " broadcasted block " + block.hash + " to " + node.name);
			node.receiveBlock(block);
		}
	}

}
