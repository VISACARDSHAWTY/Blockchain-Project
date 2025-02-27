package blockchain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
	public PrivateKey privateKey;
	public PublicKey publicKey;
	public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	
	public Wallet(){
		generateKeyPair();	
	}
	
	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a KeyPair
			// 256 bytes provides an acceptable security level
			keyGen.initialize(ecSpec, random);   
	        	KeyPair keyPair = keyGen.generateKeyPair();
	        	// Set the public and private keys from the keyPair
	        	privateKey = keyPair.getPrivate();
	        	publicKey = keyPair.getPublic();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	public float getBalance() {
		float total = 0;	
        for (Map.Entry<String, TransactionOutput> item: MyChain.UTXOs.entrySet()){
        	TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
            	UTXOs.put(UTXO.id,UTXO); //add it to our list of unspent transactions.
            	total += UTXO.value ; 
            }
        }  
		return total;
	}
	
	public Transaction sendFunds(PublicKey Newrecipient,float value ) {
		if(getBalance() < value) {
			System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
			return null;
		}
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if(total > value) break;
		}
		
		Transaction newTransaction = new Transaction(publicKey, Newrecipient , value, inputs);
		newTransaction.generateSignature(privateKey);
		
		// Gathers transaction inputs (Making sure they are unspent):
		for(TransactionInput i : newTransaction.inputs) {
			i.UTXO = MyChain.UTXOs.get(i.transactionOutputId);
		}
		
		// Generate transaction outputs:
		// Get value of inputs and calculate the left over:
		float leftOver = newTransaction.getInputsValue() - value; 
		newTransaction.transactionId = newTransaction.calculateHash();
		// Send value to recipient
		newTransaction.outputs.add(new TransactionOutput( newTransaction.recipient, 
										newTransaction.value, newTransaction.transactionId)); 
		// Send the left over 'change' back to sender
		newTransaction.outputs.add(new TransactionOutput( newTransaction.sender, 
										leftOver, newTransaction.transactionId)); 		
		
		for(TransactionInput input: inputs){
			UTXOs.remove(input.transactionOutputId);
		}
		
		return newTransaction;
	}
}
