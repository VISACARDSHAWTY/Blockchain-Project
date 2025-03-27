//package blockchain;
//
//import java.util.ArrayList;
//import com.google.gson.*;
//import java.security.Security;
//import java.util.HashMap;
//
//public class Chain {
//	
//	public static ArrayList<Block> blockchain = new ArrayList<Block>();
//	public static int difficulty = 4;
//	
//	public static Wallet walletA;
//	public static Wallet walletB;
//	public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
//	public static float minimumTransaction = 0.1f;
//
//			
//	public static boolean isChainValid() {
//		Block currentBlock; 
//		Block previousBlock;
//		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
//		for(int i=1; i < blockchain.size(); i++) {
//			currentBlock = blockchain.get(i);
//			previousBlock = blockchain.get(i-1);
//			//check if hash is correctly solved
//			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
//				System.out.println("This block hasn't been mined");
//				return false;
//			}
//			//compare registered hash and calculated hash:
//			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
//				System.out.println("Current Hashes not equal");			
//				return false;
//			}
//			//compare previous hash and registered previous hash
//			if(!previousBlock.hash.equals(currentBlock.prev) ) {
//				System.out.println("Previous Hashes not equal");
//				return false;
//			}
//			
//
//			
//		}
//		System.out.println("LETSSGOOOOOOOOOO");
//		return true;
//	}
//	
//	public static void main(String[] args) {
//		
//		Block genesisBlock = new Block("Hi I am the first block", "0");
//		blockchain.add(genesisBlock);
//		System.out.println("Trying to mine the genesis block ... ");
//		blockchain.get(0).mineBlock(difficulty);
//				
//		Block secondBlock = new Block("I am the second block", blockchain.get(blockchain.size()-1).hash);
//		blockchain.add(secondBlock);
//		System.out.println("Trying to mine block 2 ... ");
//		blockchain.get(1).mineBlock(difficulty);
//				
//		Block thirdBlock = new Block("I am the third block", blockchain.get(blockchain.size()-1).hash);
//		blockchain.add(thirdBlock);
//		System.out.println("Trying to mine block 3 ... ");
//		blockchain.get(2).mineBlock(difficulty);
//
//				
//		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
//		System.out.println(blockchainJson);
//		
////		TEMPERING ATTACK 
////		blockchain.get(2).prev = StringUtil.applySHA256("ahlaan");
////		isChainValid();
//		
//		System.out.println("\nBlockchain is Valid: " + isChainValid());
//		
//		
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
//		//Create the new wallets
//		walletA = new Wallet();
//		walletB = new Wallet();
//		//Test public and private keys
//		System.out.println("Private and public keys:");
//		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
//		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
//		//Create a test transaction from WalletA to walletB 
//		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
//		transaction.generateSignature(walletA.privateKey);
//		//Verify the signature works and verify it from the public key
//		System.out.println("Is signature verified");
//		System.out.println(transaction.verifySignature());
//	}
//}

package blockchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.security.Security;

public class Chain {
	
	public static int difficulty = 5;
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	// list of all unspent transactions outputs in the blockchain
	public static HashMap<String,TransactionOutput> UTXOs = 
								new HashMap<String,TransactionOutput>(); 
	public static float minimumTransaction = 0.1f;
	
	public static Wallet walletA;
	public static Wallet walletB;
	
	public static Transaction genesisTransaction;
	
public static void main(String[] args) {
	
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
	
	//Create the new wallets
	walletA = new Wallet();
	walletB = new Wallet();
	
	Wallet coinbase = new Wallet();
    
	genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
	// manually sign the Genesis transaction 
	genesisTransaction.generateSignature(coinbase.privateKey);
	// manually set the transaction id
	genesisTransaction.transactionId = "0"; 
	// manually add the Transactions Output
	genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, 
								genesisTransaction.value, genesisTransaction.transactionId)); 
	// it's important to store our first transaction in the UTXOs list
	UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); 
	
	System.out.println("Creating and Mining Genesis block... ");
	Block genesis = new Block("0");
	genesis.addTransaction(genesisTransaction);
	addBlock(genesis);
	
	//testing
	Block block1 = new Block(genesis.hash);
	System.out.println("\nWalletA's balance is: " + walletA.getBalance());
	System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
	block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
	addBlock(block1);
	System.out.println("\nWalletA's balance is: " + walletA.getBalance());
	System.out.println("WalletB's balance is: " + walletB.getBalance());
			
	Block block2 = new Block(block1.hash);
	System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
	block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
	addBlock(block2);
	System.out.println("\nWalletA's balance is: " + walletA.getBalance());
	System.out.println("WalletB's balance is: " + walletB.getBalance());
			
	Block block3 = new Block(block2.hash);
	System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
	block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
	addBlock(block3);
	block3.printBlock();
	System.out.println("\nWalletA's balance is: " + walletA.getBalance());
	System.out.println("WalletB's balance is: " + walletB.getBalance());
			
	System.out.println("\nBlockchain is valid: " + isChainValid()) ;
}

public static void addBlock(Block newBlock) {
	newBlock.mineBlock(difficulty);
	blockchain.add(newBlock);
} 

public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		// A temporary list of unspent transactions at a given block state.
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); 
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		//loop through blockchain to check hashes:
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
			//check if hash is correctly solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}
			
			//loop through the transaction of currentBlock:
			TransactionOutput tempOutput;
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);
				
				if(!currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs in Transaction(" + t + ")");
					return false; 
				}
				
				for(TransactionInput input: currentTransaction.inputs) {	
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					if(tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}
					
					if(input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}
					
					tempUTXOs.remove(input.transactionOutputId);
				}
				
				for(TransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}
				
				if( currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if( currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
				
			}
		}
		return true;
	}
}