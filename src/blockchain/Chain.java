package blockchain;

import java.util.ArrayList;
import com.google.gson.*;

public class Chain {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	
	public static boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		for(int i=1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("Current Hashes not equal");			
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.prev) ) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
		}
		System.out.println("LETSSGOOOOOOOOOO");
		return true;
	}
	public static void main(String[] args) {
//		Block genesisBlock = new Block("Hi I'm the first block", "0");
//		genesisBlock.printBlock();
//		
//		Block secondBlock = new Block("I'm the second block", genesisBlock.hash);
//		secondBlock.printBlock();
//		
//		Block thirdBlock = new Block("I'm the third block", secondBlock.hash);
//		thirdBlock.printBlock();
//		
//		Block FakefirstBlock = new Block("I'm the fake first block", "0");
//		FakefirstBlock.printBlock();
		
		Block genesisBlock = new Block("Hi I am the first block", "0");
		blockchain.add(genesisBlock);
				
		Block secondBlock = new Block("I am the second block", 
		blockchain.get(blockchain.size()-1).hash);
		blockchain.add(secondBlock);
				
		Block thirdBlock = new Block("I am the third block", 
		blockchain.get(blockchain.size()-1).hash);
		blockchain.add(thirdBlock);
				
		String blockchainJson = 
		new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println(blockchainJson);
		
		isChainValid();
		
		blockchain.get(2).prev = StringUtil.applySHA256("ahlaan");
		isChainValid();
	}
}
