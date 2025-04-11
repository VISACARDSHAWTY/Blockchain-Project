package blockchain;

import java.util.ArrayList;
import java.util.Date;

public class Block {

	public String hash;
	public String prev;
	public String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	private long timestamp; 
	public int nonce;
	
	//Block Constructor.
	public Block(String previousHash ) {
		this.prev = previousHash;
		this.timestamp = new Date().getTime();
		this.hash = calculateHash(); 
	}
	
	//Calculate new hash based on block content
	public String calculateHash() {
		String calculatedhash = StringUtil.applySHA256( 
				prev +
				Long.toString(timestamp) +
				Integer.toString(nonce) + merkleRoot);
		return calculatedhash;
	}
	
	//Increases nonce value until hash target is reached.
	public void mineBlock(int difficulty) {
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		String target = new String(new char[difficulty]).replace('\0', '0'); 
		while(!hash.substring( 0, difficulty).equals(target)) {
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("Block Mined!!! : " + hash);
	}
	
	// Add transaction to this block
	public boolean addTransaction(Transaction transaction) {
		// Unless block is Genesis block, process transaction and check if valid
		if(transaction == null) return false;		
		if((!"0".equals(prev))) {
			if((transaction.processTransaction() != true)) {
				System.out.println("Transaction failed to process. Discarded.");
				return false; 
			}
		}
		transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}
	public void printBlock() {
		System.out.println();	
		System.out.println("Block Timestamp: " + this.timestamp);
		System.out.println("Block Hash: " + this.hash);
		System.out.println("Transactions: ");
		for (Transaction transaction : transactions) {
		    System.out.println("Transaction: " + transaction.transactionId);
		    System.out.println("Value: " + transaction.value + "\n");
		}

		System.out.println("Block Previous Hash: " + this.prev);
		System.out.println();
	}
}

