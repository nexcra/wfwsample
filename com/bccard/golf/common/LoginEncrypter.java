package com.bccard.golf.common;


/*******************************************************************************
*   클래스명  : StringEncrypter
*   작성자    : 황병식
*   내용      : 문자열 암호화
*   적용범위  : 
*   작성일자  : 2009.06.01
************************** 수정이력 ********************************************
*    일자      버전   작성자   변경사항
*                                                                               
*******************************************************************************/

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class converts a UTF-8 string into a cipher string, and vice versa.
 * It uses 128-bit AES Algorithm in Cipher Block Chaining (CBC) mode with a UTF-8 key
 * string and a UTF-8 initial vector string which are hashed by MD5. PKCS5Padding is used
 * as a padding mode and binary output is encoded by Base64.
 * 
 * @author 황병식
 */
public class LoginEncrypter {
	private Cipher rijndael;
	private SecretKeySpec key;
	private IvParameterSpec initalVector;
	private String resKey;

	/**
	 * Creates a StringEncrypter instance.
	 * 
	 * @param key A key string which is converted into UTF-8 and hashed by MD5.
	 *            Null or an empty string is not allowed.
	 * @param initialVector An initial vector string which is converted into UTF-8
	 *                      and hashed by MD5. Null or an empty string is not allowed.
	 * @throws Exception
	 */
	public LoginEncrypter(String key, String initialVector) throws Exception {
		if (key == null || key.equals("")) {
			throw new Exception("The key can not be null or an empty string.");
		}
        

		if (initialVector == null || initialVector.equals("")) {
			throw new Exception("The initial vector can not be null or an empty string.");
		}

		Provider p = Security.getProvider("SunJCE");
        
        Security.addProvider(new com.sun.crypto.provider.SunJCE());

		// Create a AES algorithm.
		this.rijndael = Cipher.getInstance("AES/CBC/PKCS5Padding","SunJCE");

		// Initialize an encryption key and an initial vector.
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		this.key = new SecretKeySpec(md5.digest(key.getBytes("UTF8")), "AES");
		this.initalVector = new IvParameterSpec(md5.digest(initialVector.getBytes("UTF8")));

	}

	/**
	 * Creates a StringEncrypter instance.
	 * 
	 * @param key A key string which is converted into UTF-8 and hashed by MD5.
	 *            Null or an empty string is not allowed.
	 * @param initialVector An initial vector string which is converted into UTF-8
	 *                      and hashed by MD5. Null or an empty string is not allowed.
	 * @throws Exception
	 */
	public LoginEncrypter(String value) throws Exception {
	  if (value == null || value.equals(""))
	   throw new Exception("The cipher string can not be null or an empty string.");

        Provider p = Security.getProvider("SunJCE");
        Security.addProvider(new com.sun.crypto.provider.SunJCE());


		 KeyGenerator kgen = KeyGenerator.getInstance("AES", "SunJCE");
		 kgen.init(128); // 192 and 256 bits may not be available
		 
		 // Generate the secret key specs.
		 SecretKey skey = kgen.generateKey();
		 
		 //   Create a AES algorithm.
		this.rijndael = Cipher.getInstance("AES/CBC/PKCS5Padding","SunJCE");
	 
		// Initialize an encryption key and an initial vector.
		MessageDigest md5 = MessageDigest.getInstance("MD5");  
		this.key = new SecretKeySpec(md5.digest(skey.getEncoded()), "AES");
		this.initalVector = new IvParameterSpec(md5.digest(value.getBytes("UTF8")));
		this.resKey = Base64Encoder.encode(skey.getEncoded());
	   
	 }
	 
	 public String getResKey(){
		return this.resKey;
	 }
	/**
	 * Encrypts a string.
	 * 
	 * @param value A string to encrypt. It is converted into UTF-8 before being encrypted.
	 *              Null is regarded as an empty string.
	 * @return An encrypted string.
	 * @throws Exception
	 */
	public String encrypt(String value) throws Exception { 
		if (value == null)
			value = "";

		// Initialize the cryptography algorithm.
		this.rijndael.init(Cipher.ENCRYPT_MODE, this.key, this.initalVector);

		// Get a UTF-8 byte array from a unicode string.
		byte[] utf8Value = value.getBytes("UTF8");

		// Encrypt the UTF-8 byte array.
		byte[] encryptedValue = this.rijndael.doFinal(utf8Value);

		// Return a base64 encoded string of the encrypted byte array.
		return Base64Encoder.encode(encryptedValue);
	}

	/**
	 * Decrypts a string which is encrypted with the same key and initial vector. 
	 * 
	 * @param value A string to decrypt. It must be a string encrypted with the same key and initial vector.
	 *              Null or an empty string is not allowed.
	 * @return A decrypted string
	 * @throws Exception
	 */
	public String decrypt(String value) throws Exception {
		if (value == null || value.equals(""))
			throw new Exception("The cipher string can not be null or an empty string.");

		// Initialize the cryptography algorithm.
		this.rijndael.init(Cipher.DECRYPT_MODE, this.key, this.initalVector);

		// Get an encrypted byte array from a base64 encoded string.
		byte[] encryptedValue = Base64Encoder.decode(value);

		// Decrypt the byte array.
		byte[] decryptedValue = this.rijndael.doFinal(encryptedValue);

		// Return a string converted from the UTF-8 byte array.
		return new String(decryptedValue, "UTF8");
	}

}

