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
}

