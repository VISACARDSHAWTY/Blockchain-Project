package blockchain;

import java.util.Date;

public class Block {
	public String hash;
	public String prev;
	private String data;
	private long timestamp;
	
	public Block(String data , String prev) {
		this.data = data;
		this.prev = prev;
		this.timestamp = new Date().getTime();
		this.hash = calculateHash();
	}
	
	public String calculateHash() {
		String hash = StringUtil.applySHA256(prev + Long.toString(timestamp) + data);
		return hash;
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
