package blockchain;

import java.util.ArrayList;
import com.google.gson.*;

public class Chain {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static int difficulty = 4;
			
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
		
		Block genesisBlock = new Block("Hi I am the first block", "0");
		blockchain.add(genesisBlock);
		System.out.println("Trying to mine the genesis block ... ");
		blockchain.get(0).mineBlock(difficulty);
				
		Block secondBlock = new Block("I am the second block", 
		blockchain.get(blockchain.size()-1).hash);
		blockchain.add(secondBlock);
		System.out.println("Trying to mine block 2 ... ");
		blockchain.get(1).mineBlock(difficulty);
				
		Block thirdBlock = new Block("I am the third block", 
		blockchain.get(blockchain.size()-1).hash);
		blockchain.add(thirdBlock);
		System.out.println("Trying to mine block 3 ... ");
		blockchain.get(2).mineBlock(difficulty);

				
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println(blockchainJson);
		
		isChainValid();
		
		blockchain.get(2).prev = StringUtil.applySHA256("ahlaan");
		isChainValid();
	}
}
