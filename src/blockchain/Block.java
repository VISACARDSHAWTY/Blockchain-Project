package blockchain;

import java.util.Date;

public class Block {
	public String hash;
	public String prev;
	private String data;
	private long timestamp;
	private int nonce;
	
	public Block(String data , String prev) {
		this.data = data;
		this.prev = prev;
		this.timestamp = new Date().getTime();
		this.hash = calculateHash();
	}
	
	public String calculateHash() {
		String hash = StringUtil.applySHA256(prev + Long.toString(timestamp) + Integer.toString(nonce) + data);
		return hash;
	}
	
	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0' , '0');
		while (!hash.substring(0 , difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Block Mined: " + hash);
	}
	
	public void printBlock() {
		System.out.println();	
		System.out.println("Block Timestamp: " + this.timestamp);
		System.out.println("Block Hash: " + this.hash);
		System.out.println("Block Data: " + this.data);
		System.out.println("Block Previous Hash: " + this.prev);
		System.out.println();
	}
}
