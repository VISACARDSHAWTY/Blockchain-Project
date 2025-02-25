package blockchain;

import java.security.MessageDigest;
import java.security.Key;
import java.util.Base64;



public class StringUtil {
	
	public static String applySHA256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes("UTF-8")); // convert string to array of bytes
			StringBuffer hexString = new StringBuffer();
			for (int i = 0 ; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]); // the input should be 8 bits so we AND it with 11111111 so if it is less than 8 it fills the missing bits at the start with 0s
				if (hex.length() == 1) hexString.append('0'); // the previous function returns for example "4" for 00000100 and not "04" to represent the first 4 zeros, so if its of length 1 add a zero
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
}
