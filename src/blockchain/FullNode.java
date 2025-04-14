package blockchain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FullNode extends Node {
	public FullNode(String n) {
		super(n);
		mempool = new ConcurrentHashMap<>();
		mining = false;
	}

	protected ConcurrentHashMap<String, Transaction> mempool;
	public static int MAX_TX_PER_BLOCK = 10;
	public boolean mining;
	private Thread miningThread;
	
	@Override
	public void receiveTx(Transaction transaction) {
		
		if (validateTx(transaction)) {
//			for (TransactionOutput utxo : transaction.outputs) {
//				unminedUTXOs.put(utxo.id, utxo);
//			}
			unminedUTXOs.put(transaction.outputs.get(1).id, transaction.outputs.get(1));
			mempool.put(transaction.transactionId , transaction);
			System.out.println(this.name + " received transaction " + transaction.transactionId);
			
			if (mempool.size() >= MAX_TX_PER_BLOCK - 1) {
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
	                	System.err.println("ERROR! Transaction contains previously used and unprocessed UTXO! ");
	                    return false; 
	                }
	            }
	        }
        }
        return true;
	}
	
	public void startMining() {
if (mining) return;
        
        mining = true;
        
        miningThread = new Thread(() -> {
            try {
                Block b = new Block(storedblockchain.get(storedblockchain.size() - 1).hash);
                Transaction coinbase = coinbaseTx();
                b.transactions.add(coinbase);
                Set<String> addedTx = new HashSet<>();
                
                ArrayList<Transaction> mempoolCopy = new ArrayList<>(mempool.values());
                
                for (Transaction tx : mempoolCopy) {
                    if (b.transactions.size() == MAX_TX_PER_BLOCK) break;
                    addTx(tx, b, addedTx);
                }
                
                b.merkleRoot = StringUtil.getMerkleRoot(b.transactions);
                
                
                while (!b.hash.startsWith("0".repeat(network.difficulty))) {
                    // Check if the blockchain has changed since we started mining
                    if (!storedblockchain.get(storedblockchain.size() - 1).hash.equals(b.prev)) {
                        mining = false;
                        return;
                    }
                    
                    b.nonce++;
                    b.hash = b.calculateHash();
                }
                broadcastBlock(b);
            } finally {
                mining = false;
            }
        });
        
        miningThread.start();
	}
	
	@Override
	public boolean receiveBlock(Block block) {
		if (validateBlock(block)) {
			if (mining && miningThread != null) {
	            miningThread.interrupt();
	            mining = false;
	        }
			storedblockchain.add(block);
			for (Transaction tx : block.transactions) {
				for (TransactionOutput utxo : tx.outputs) {
					utxo.unlock();
					utxo.mined = true;
					UTXOs.put(utxo.id , utxo);
				}
				if (tx.inputs != null) {
					for (TransactionInput utxo : tx.inputs) {
						UTXOs.remove(utxo.transactionOutputId);
						unminedUTXOs.remove(utxo.transactionOutputId);
					}
				}
				mempool.remove(tx.transactionId);
			}
			System.out.println(this.name + " received block " + block.hash + " and added to its copy of the BC.");
			
			if (!mining && mempool.size() >= MAX_TX_PER_BLOCK - 1) {
                startMining();
            }
			return true;
		}
		return false;
		
	}
	private Transaction coinbaseTx() {
		int height = storedblockchain.size();
		int halvings = (height / 10) + 1; // every 10 blocks in the blockchain we half and the first mined block gets half of the reward of the genesis tx;
		float reward = network.genesis_reward;
		for (int i = 0 ; i < halvings ; i++) {
			reward = reward / 2;
		}
		if (reward < 0.00008f) {
			reward = 0; // stops giving coinbase reward after 200 blocks are added to the blockchain.
		}
		Transaction coinbase = new Transaction(network.coinbase.publicKey , getPublicKey() , reward , null);
		coinbase.generateSignature(network.coinbase.privateKey);
		coinbase.transactionId = coinbase.calculateHash();
		coinbase.outputs.add(new TransactionOutput(coinbase.recipient , coinbase.value , coinbase.transactionId));
		return coinbase;
	}
	
	private void addTx(Transaction tx, Block b, Set<String> addedTx) {
	    if (addedTx.contains(tx.transactionId) || b.transactions.size() >= MAX_TX_PER_BLOCK) {
	        return;
	    }

	    for (String depId : tx.chainTx) {
	        if (b.transactions.size() >= MAX_TX_PER_BLOCK) break;
	        if (mempool.containsKey(depId)) {
	            Transaction depTx = mempool.get(depId);
	            addTx(depTx, b, addedTx);
	        }
	    }

	    if (!addedTx.contains(tx.transactionId) && b.transactions.size() < MAX_TX_PER_BLOCK) {
	        b.transactions.add(tx);
	        addedTx.add(tx.transactionId);
	    }
	}
	
	public void broadcastBlock(Block block) {
		for (Node node : network.network) {
			System.out.println(this.name + " broadcasted block " + block.hash + " to " + node.name);
			node.receiveBlock(block);
		}
	}

}
