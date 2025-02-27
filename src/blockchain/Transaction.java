package blockchain;

import java.security.*;
import java.util.ArrayList;

public class Transaction {
	
	public String transactionId; // this is also the hash of the transaction.
	public PublicKey sender; // senders address/public key.
	public PublicKey recipient; // Recipients address/public key.
	public float value;
	// this is to prevent anybody else from spending funds in our wallet.
	public byte[] signature; 

	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	// a rough count of how many transactions have been generated.
	private static int sequence = 0; 
	
	// Constructor: 
		public Transaction(PublicKey from, PublicKey to, 
				float value,  ArrayList<TransactionInput> inputs) {
			this.sender = from;
			this.recipient = to;
			this.value = value;
			this.inputs = inputs;
		}
		
		// This Calculates the transaction hash (which will be used as its Id)
		public String calculateHash() {
			//increase the sequence to avoid 2 transactions having the same hash
			sequence++; 
			return StringUtil.applySHA256(
					StringUtil.getStringFromKey(sender) +
					StringUtil.getStringFromKey(recipient) +
					Float.toString(value) + sequence
					);
		}	
		
		
		public void generateSignature(PrivateKey privateKey) {
			String data = StringUtil.getStringFromKey(sender) + 
					StringUtil.getStringFromKey(recipient) + Float.toString(value);
			signature = StringUtil.applyECDSASig(privateKey,data);		
		}
		
		//Verifies the data we signed hasn't been tampered with
		public boolean verifySignature() {
			String data = StringUtil.getStringFromKey(sender) + 
					StringUtil.getStringFromKey(recipient) + Float.toString(value);
			return StringUtil.verifyECDSASig(sender, data, signature);
		}
		
		public boolean processTransaction() {
			if(verifySignature() == false) {
				System.out.println("#Transaction Signature failed to verify");
				return false;
			}				
			//Checks if transaction is valid:
			if(getInputsValue() < Chain.minimumTransaction) {
				System.out.println("Transaction Inputs too small: " + getInputsValue());
				System.out.println("Please make sure the transaction amount is greater than " + 
										Chain.minimumTransaction);
				return false;
			}
			// Check that all transaction inputs are set
			for(TransactionInput i : inputs) {
				// if transaction input is not set return false
				if(i.UTXO == null) { 
					System.out.println("Transaction Input is not set!");  
					return false;
				}
			}
			//Remove transaction inputs from UTXO set
			for(TransactionInput i : inputs)
				Chain.UTXOs.remove(i.UTXO.id);
			//Add outputs as new UTXOs
			for(TransactionOutput o : outputs)
				Chain.UTXOs.put(o.id , o);
			return true;
		}
			
		public float getInputsValue() {
			float total = 0;
			for(TransactionInput i : inputs) {
				if(i.UTXO != null) 
					total += i.UTXO.value;
			}
			return total;
		}
			
		public float getOutputsValue() {
			float total = 0;
			for(TransactionOutput o : outputs) {
				total += o.value;
			}
			return total;
		}

}

