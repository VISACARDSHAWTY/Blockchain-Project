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
	
	public static Transaction genesisTransaction;
	
	public ArrayList<Node> network;
	public Wallet coinbase;
	public float genesis_reward;
	
public static void main(String[] args) {
	
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
	Chain chain = new Chain(100f);
	Node n = new Node("Satoshi Nakamoto");
	n.connectToNetwork(chain);;
	Node m = new Node("Mark");
	m.connectToNetwork(chain);;
	FullNode o = new FullNode("Miner - A");
	o.connectToNetwork(chain);
	FullNode p = new FullNode("Miner - B");
	p.connectToNetwork(chain);
	
	n.initializeBlockchain();
	n.createTx(m.getPublicKey() , 5);
	n.createTx(m.getPublicKey() , 5);
	n.createTx(m.getPublicKey() , 5);
	n.createTx(m.getPublicKey() , 5);
	n.createTx(m.getPublicKey() , 5);
	n.createTx(m.getPublicKey() , 5);
	n.createTx(m.getPublicKey() , 5);
	n.createTx(m.getPublicKey() , 5);
	n.createTx(m.getPublicKey() , 5);
//	for (int i = 0 ; i < 2020 ; i++) {
//		if (i % 2 == 0) {
//			n.createTx(m.getPublicKey() , 5);
//		}
//		else {
//			m.createTx(n.getPublicKey() , 5);
//		}
//		System.err.println(i);
//	}
	
	
	try {
        Thread.sleep(10000); // Wait 10 seconds
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
//	Block bk = new Block("z");
//	ArrayList<TransactionInput> txinput = new ArrayList<TransactionInput>();
//	TransactionOutput utxo = m.storedblockchain.get(1).transactions.get(4).outputs.get(1);
//	txinput.add(new TransactionInput(utxo.id));
//	Transaction transaction = new Transaction(m.getPublicKey() , n.getPublicKey() , 10f , txinput);
//	transaction.generateSignature(m.getPrivateKey());
//	transaction.transactionId = transaction.calculateHash();
//	bk.transactions.add(transaction);
//	bk.merkleRoot = StringUtil.getMerkleRoot(bk.transactions);
//	bk.prev = o.storedblockchain.get(o.storedblockchain.size() - 1).hash;
//	bk.hash = bk.calculateHash();
//	while (!bk.hash.startsWith("0".repeat(difficulty))) {
//		bk.nonce++;
//		bk.hash = bk.calculateHash();
//	}
//	o.broadcastBlock(bk);
	
	
	System.out.println("\n\n\n" + n.name + "'s stored blockchain: ");
	int counter = 1;
	for (Block b : n.storedblockchain) {
		System.out.println("Block #" + counter + ": " + b.hash);
		System.out.println("Transactions in that block: ");
		int counteragain = 1;
		for (Transaction tx : b.transactions) {
			System.out.println("Transaction #" + counter +"." + counteragain + ": " + tx.transactionId);
			counteragain++;
		}
		counter++;
	}
	System.out.println("\n\n\n" + m.name + "'s stored blockchain: ");
	counter = 1;
	for (Block b : m.storedblockchain) {
		System.out.println("Block #" + counter + ": " + b.hash);
		System.out.println("Transactions in that block: ");
		int counteragain = 1;
		for (Transaction tx : b.transactions) {
			System.out.println("Transaction #" + counter +"." + counteragain + ": " + tx.transactionId);
			counteragain++;
		}
		counter++;
	}
	System.out.println("\n\n\n" + o.name + "'s stored blockchain: ");
	counter = 1;
	for (Block b : o.storedblockchain) {
		System.out.println("Block #" + counter + ": " + b.hash);
		System.out.println("Transactions in that block: ");
		int counteragain = 1;
		System.err.println("REWARD = " + b.transactions.get(0).value);
		for (Transaction tx : b.transactions) {
			System.out.println("Transaction #" + counter +"." + counteragain + ": " + tx.transactionId);
			counteragain++;
		}
		counter++;
	}
	
	System.out.println("\n\n\n" + p.name + "'s stored blockchain: ");
	counter = 1;
	for (Block b : p.storedblockchain) {
		System.out.println("Block #" + counter + ": " + b.hash);
		System.out.println("Transactions in that block: ");
		int counteragain = 1;
		System.err.println("REWARD = " + b.transactions.get(0).value);
		for (Transaction tx : b.transactions) {
			System.out.println("Transaction #" + counter +"." + counteragain + ": " + tx.transactionId);
			counteragain++;
		}
		counter++;
	}
	
	System.out.println("\n\n" + n.name + "'s balance: " + n.getBalance());
	System.out.println(m.name + "'s balance: " + m.getBalance());
	System.out.println(o.name + "'s balance: " + o.getBalance());
	System.out.println(p.name + "'s balance: " + p.getBalance());
	
	
//	walletA = new Wallet();
//	walletB = new Wallet();
//	
//	Wallet coinbase = new Wallet();
//    
//	genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
//	// manually sign the Genesis transaction 
//	genesisTransaction.generateSignature(coinbase.privateKey);
//	// manually set the transaction id
//	genesisTransaction.transactionId = "0"; 
//	// manually add the Transactions Output
//	genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, 
//								genesisTransaction.value, genesisTransaction.transactionId)); 
//	// it's important to store our first transaction in the UTXOs list
//	UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); 
//	
//	System.out.println("Creating and Mining Genesis block... ");
//	Block genesis = new Block("0");
//	genesis.addTransaction(genesisTransaction);
//	addBlock(genesis);
//	
//	//testing
//	Block block1 = new Block(genesis.hash);
//	System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//	System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
//	block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
//	addBlock(block1);
//	System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//	System.out.println("WalletB's balance is: " + walletB.getBalance());
//			
//	Block block2 = new Block(block1.hash);
//	System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
//	block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
//	addBlock(block2);
//	System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//	System.out.println("WalletB's balance is: " + walletB.getBalance());
//			
//	Block block3 = new Block(block2.hash);
//	System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
//	block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
//	addBlock(block3);
//	block3.printBlock();
//	System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//	System.out.println("WalletB's balance is: " + walletB.getBalance());
//			
//	System.out.println("\nBlockchain is valid: " + isChainValid()) ;
//	System.out.println("\n\n\nWalletA:\n");
//	for (HashMap.Entry<String, TransactionOutput> entry : walletA.UTXOs.entrySet()) {
//	    System.out.println("Key: " + entry.getKey());
//	    System.out.println("Value: " + entry.getValue());
//	}
//	System.out.println("\n\n\nWalletB:\n");
//	for (HashMap.Entry<String, TransactionOutput> entry : walletB.UTXOs.entrySet()) {
//	    System.out.println("Key: " + entry.getKey());
//	    System.out.println("Value: " + entry.getValue());
//	}
//	System.out.println("\n\n\nUTXOS:\n");
//	for (HashMap.Entry<String, TransactionOutput> entry : UTXOs.entrySet()) {
//	    System.out.println("Key: " + entry.getKey());
//	    System.out.println("Value: " + entry.getValue());
//	}
}

public Chain(float r) {
	network = new ArrayList<Node>();
	coinbase = new Wallet();
	genesis_reward = r;
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

public void registerNode(Node n) {
	if (!network.contains(n)) {
		network.add(n);
	}
}
}
