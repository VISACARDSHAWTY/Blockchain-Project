package blockchain;

import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
// client
public class Node {
	public String name;
	// each node has a copy of the blockchain and the utxo set
	public ArrayList<Block> storedblockchain;
	public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); 
	public HashMap<String,TransactionOutput> unminedUTXOs = new HashMap<String,TransactionOutput>(); 
	public Wallet wallet; // each node has its own wallet, full node extends this class so it can act as both a miner and a client
	
	public Chain network; // pointer to the network a node is going to broadcast a transaction/block to
	
	
	public Node(String n) {
		name = n;
		wallet = new Wallet();
		storedblockchain = new ArrayList<Block>();
		UTXOs = new HashMap<String,TransactionOutput>();
	}
	
	public PublicKey getPublicKey() {
		return wallet.publicKey;
	}
	
	public PrivateKey getPrivateKey() {
		return wallet.privateKey;
	}
	
	public void connectToNetwork(Chain chain) {
		chain.registerNode(this);
		network = chain;
	}
	
	public void createTx(PublicKey receiver , float value) {
		HashMap<String, TransactionOutput> utxos = getUTXOs();
		Transaction transaction = wallet.sendFunds(receiver , value , utxos);
		if (transaction == null) {
			System.err.println("ERROR! Transaction was not created!");
			return;
		}
		for (TransactionInput txinput : transaction.inputs) {
			try {
				UTXOs.get(txinput.transactionOutputId).lock();
			} catch (NullPointerException e) {
				unminedUTXOs.get(txinput.transactionOutputId).lock();
			}
		}
		broadcastTx(transaction);
	}
	
	private void broadcastTx(Transaction transaction) {
		for (Node node : network.network) {
			System.out.println(this.name + " broadcasted transaction " + transaction.transactionId + " to " + node.name);
			node.receiveTx(transaction);
		}
	}
	
	public void receiveTx(Transaction transaction) {
		if (validateTx(transaction)) {
			unminedUTXOs.put(transaction.outputs.get(1).id, transaction.outputs.get(1));
		}
	}
	
	public void initializeBlockchain() {
		if (!storedblockchain.isEmpty()) {
			System.err.println("ERROR! Blockchain is already initialized!");
			return;
		}
		Transaction genesis_tx = new Transaction(this.getPublicKey() , this.getPublicKey() , 100f , null);
		genesis_tx.generateSignature(this.getPrivateKey());
		genesis_tx.transactionId = "0";
		genesis_tx.outputs.add(new TransactionOutput(genesis_tx.recipient , genesis_tx.value , "0"));
		
		Block genesis_b = new Block("0");
		genesis_b.transactions.add(genesis_tx);
		genesis_b.merkleRoot = StringUtil.getMerkleRoot(genesis_b.transactions);
		genesis_b.hash = genesis_b.calculateHash();
		
		// broadcast to network
		boolean broadcasted = false;
		for (Node node : network.network) {
			broadcasted = node.receiveBlock(genesis_b);
		}
		
	}
	
	public boolean receiveBlock(Block block) {
		if (validateBlock(block)) {
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
			}
			System.out.println(this.name + " received block " + block.hash + " and added to its copy of the BC.");
			return true;
		}
		return false;
		
	}
	
	private boolean validateBlock(Block block) {
		if (block.prev.equals("0")) {
			boolean valid = (block.hash.equals(block.calculateHash()) && (StringUtil.getMerkleRoot(block.transactions).equals(block.merkleRoot)));
			return valid;
		}
		else {
			boolean valid = true;
			
			HashSet<String> seenIds = new HashSet<>();
	        boolean duplicates = false;
	        boolean txvalid = true;
	        
	        for (Transaction transaction : block.transactions) {
	            for (TransactionInput input : transaction.inputs) {
	                if (!seenIds.add(input.transactionOutputId)) {
	                    duplicates = true;
	                    break;
	                }
	            }
	            if (!validateTx(transaction)) {
	            	txvalid = false;
	            	break;
	            }
	        }
			if (!block.hash.equals(block.calculateHash())) {
				System.err.println("ERROR! Hash is not of the block's content!");
				valid = false;
			}
			else if (!(StringUtil.getMerkleRoot(block.transactions).equals(block.merkleRoot))) {
				System.err.println("ERROR! Merkle root does not represent the transactions!");
				valid = false;
			}
			else if (!block.prev.equals(storedblockchain.get(storedblockchain.size() - 1).hash)) {
				System.err.println("ERROR! Previous hash is not of the hash of the last block of the chain!");
				valid = false;
			}
			else if (!block.hash.startsWith("0".repeat(network.difficulty))) {
				System.err.println("ERROR! Block has not been mined!");
				valid = false;
			}
			else if (duplicates) {
				System.err.println("ERROR! Block has two transactions using the same UTXO twice!");
				valid = false;
			}
			else if (!txvalid) {
				valid = false;
			}
			return valid;
		}
	}
	
	public boolean validateTx(Transaction tx) { // new tx validation fct, to fight against doublespending by checking if utxo are not previously used 
		if(tx.verifySignature() == false) {
			System.err.println("ERROR! Transaction's signature failed to verify!");
			return false;
		}
		for (TransactionInput utxo : tx.inputs) {
			if (!UTXOs.containsKey(utxo.transactionOutputId) && !unminedUTXOs.containsKey(utxo.transactionOutputId)) {
				System.err.println("ERROR! Transaction contains previously used or non-existent UTXO!");
				return false;
			}
		}
		return true;
	}
	
	public HashMap<String,TransactionOutput> getUTXOs() {
		HashMap<String,TransactionOutput> utxos = new HashMap<String,TransactionOutput>();
		for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
        	TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(getPublicKey()) && !UTXO.locked) {
            	utxos.put(UTXO.id , UTXO);
            }
        }
		for (Map.Entry<String, TransactionOutput> item: unminedUTXOs.entrySet()){
        	TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(getPublicKey()) && !UTXO.locked) {
            	utxos.put(UTXO.id , UTXO);
            }
        }
		return utxos;
	}
	
	
	public float getBalance() {
		float total = 0;	
		for (TransactionOutput utxo : getUTXOs().values()) {
		    if (utxo.isMine(getPublicKey())) {
		        total += utxo.value;
		    }
		}

		return total;
	}
	
}
