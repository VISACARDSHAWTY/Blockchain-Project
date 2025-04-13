package blockchain;

import java.security.PublicKey;

public class TransactionOutput {
	
	public String id;
	public PublicKey recipient; //also known as the new owner of these coins.
	public float value; //the amount of coins they own
	//the id of the transaction this output was created in
	public String parentTransactionId;
	
	public boolean mined;
	public boolean locked;
	
	//Constructor
	public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
		this.recipient = recipient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySHA256((String) (StringUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId));
		this.locked = false;
		this.mined = false;
	}
	
	//Check if coin belongs to you
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == recipient);
	}
	
	public void lock() {
		this.locked = true;
	}
	
	public void unlock() {
		this.locked = false;
	}
}	
