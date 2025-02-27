package blockchain;

import java.util.ArrayList;
import com.google.gson.*;
import java.security.Security;
import java.util.HashMap;

public class Chain {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static int difficulty = 4;
	
	public static Wallet walletA;
	public static Wallet walletB;
	public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	public static float minimumTransaction = 0.1f;

			
	public static boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		for(int i=1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//check if hash is correctly solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}
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
				
		Block secondBlock = new Block("I am the second block", blockchain.get(blockchain.size()-1).hash);
		blockchain.add(secondBlock);
		System.out.println("Trying to mine block 2 ... ");
		blockchain.get(1).mineBlock(difficulty);
				
		Block thirdBlock = new Block("I am the third block", blockchain.get(blockchain.size()-1).hash);
		blockchain.add(thirdBlock);
		System.out.println("Trying to mine block 3 ... ");
		blockchain.get(2).mineBlock(difficulty);

				
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println(blockchainJson);
		
//		TEMPERING ATTACK 
//		blockchain.get(2).prev = StringUtil.applySHA256("ahlaan");
//		isChainValid();
		
		System.out.println("\nBlockchain is Valid: " + isChainValid());
		
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
		//Create the new wallets
		walletA = new Wallet();
		walletB = new Wallet();
		//Test public and private keys
		System.out.println("Private and public keys:");
		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
		//Create a test transaction from WalletA to walletB 
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		transaction.generateSignature(walletA.privateKey);
		//Verify the signature works and verify it from the public key
		System.out.println("Is signature verified");
		System.out.println(transaction.verifySignature());
	}
}
