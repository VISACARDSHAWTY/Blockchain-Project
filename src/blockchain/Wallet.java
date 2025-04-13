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
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
        	TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) {
            	total += UTXO.value ; 
            }
        }  
		return total;
	}
	
	public Transaction sendFunds(PublicKey Newrecipient , float value , HashMap<String,TransactionOutput> utxos) {
//		if(getBalance() < value) {
//			System.out.println("Not Enough funds to send transaction. Transaction Discarded.");
//			return null;
//		}
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		
		float total = 0;
		for (Map.Entry<String, TransactionOutput> entry : utxos.entrySet()) {
		    TransactionOutput item = entry.getValue();
		    total += item.value;
		    inputs.add(new TransactionInput(item.id));
		    if (total > value) break;
		}

		
		Transaction newTransaction = new Transaction(publicKey, Newrecipient , value, inputs);
		newTransaction.generateSignature(privateKey);
		
		// Gathers transaction inputs (Making sure they are unspent):
		for(TransactionInput i : newTransaction.inputs) {
			i.UTXO = utxos.get(i.transactionOutputId);
			if (!i.UTXO.mined) {
				newTransaction.chainTx.add(i.UTXO.parentTransactionId);
			}
		}
		
		// Generate transaction outputs:
		// Get value of inputs and calculate the left over:
		float leftOver = newTransaction.getInputsValue() - value;
		newTransaction.transactionId = newTransaction.calculateHash();
		// Send value to recipient
		newTransaction.outputs.add(new TransactionOutput( newTransaction.recipient, 
										newTransaction.value, newTransaction.transactionId)); 
		// Send the left over 'change' back to sender
		TransactionOutput leftover = new TransactionOutput( newTransaction.sender, leftOver, newTransaction.transactionId);
		newTransaction.outputs.add(leftover);
		
		for(TransactionInput input: inputs){
			UTXOs.remove(input.transactionOutputId);
		}
		return newTransaction;
	}
}
