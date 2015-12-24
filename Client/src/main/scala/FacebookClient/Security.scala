package Client
import Client._
import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto._
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import javax.xml.bind.DatatypeConverter
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;

import java.security.spec.RSAPublicKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.X509EncodedKeySpec
//import java.util.Base64
import javax.crypto.spec.IvParameterSpec
object encryption  {
    def generateAESkey():String={
    	var generator = KeyGenerator.getInstance("AES");
		generator.init(128);
		var key = generator.generateKey();
		//Base64.getEncoder().encodeToString(key.getEncoded())
		Base64.encodeBase64String(key.getEncoded());
    }
	
	def encryptWithAESKey(data:String,key:String,Iv:Array[Byte]):String  = {


 var AesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    //var iv = Array[Byte](1, 2, 3, 4, 5,6,7,8,9,10,11,12,13,14,15,16)
     //println("key="+iv.length)
     var secKey = new SecretKeySpec(Base64.decodeBase64(key),"AES")
    AesCipher.init(Cipher.ENCRYPT_MODE, secKey,new IvParameterSpec(Iv))

    val byteText = data.getBytes()
    //println("byteText="+byteText)
    val byteCipherText = AesCipher.doFinal(byteText)
    Base64.encodeBase64String(byteCipherText);

	/*	var secKey = new SecretKeySpec(Base64.decodeBase64(key),"AES");
		var cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secKey);
		var newData = cipher.doFinal(data.getBytes());
		Base64.encodeBase64String(newData);*/
	}

	def decryptWithAESKey(inputData: String, key:String,Iv:Array[Byte]) = {
	 var AesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
	 var secKey = new SecretKeySpec(Base64.decodeBase64(key), "AES");
     AesCipher.init(Cipher.DECRYPT_MODE, secKey, new IvParameterSpec(Iv))
    val newData = AesCipher.doFinal(Base64.decodeBase64(inputData.getBytes()));
      new String(newData);

		/*var cipher = Cipher.getInstance("AES");
		var secKey = new SecretKeySpec(Base64.decodeBase64(key), "AES");
		cipher.init(Cipher.DECRYPT_MODE, secKey);
		var newData = cipher.doFinal(Base64.decodeBase64(inputData.getBytes()));
		new String(newData);*/
	}

	def generateSymKey: KeyPair = {
		var keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		var keypair = keyGen.genKeyPair()
		keypair
	}

	def encryptRSA(text:String,key:PublicKey):String = {
		var cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		var cipherText = cipher.doFinal(text.getBytes());
		var temp = Base64.encodeBase64String(cipherText);
	//	println("encryptRSA"+" text "+text+" key "+key+" res "+temp)
		temp
	}
	def encryptRSA(text:String,key:PrivateKey):String = {
		var cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		var cipherText = cipher.doFinal(text.getBytes());
		var temp = Base64.encodeBase64String(cipherText);
		temp
	}
	  	
	def decryptRSA(text :String,  key:PrivateKey):String  ={
		var dectyptedText = Base64.decodeBase64(text)
		var cipher = Cipher.getInstance("RSA");

		cipher.init(Cipher.DECRYPT_MODE, key);
		dectyptedText = cipher.doFinal(dectyptedText);

		new String(dectyptedText);
	}
	def decryptRSA(text :String,  key:PublicKey):String  ={
		var dectyptedText = Base64.decodeBase64(text)
		var cipher = Cipher.getInstance("RSA");

		cipher.init(Cipher.DECRYPT_MODE, key);
		dectyptedText = cipher.doFinal(dectyptedText);

		new String(dectyptedText);
	}
	def generatepublickeystring(key:PublicKey):String ={
		Base64.encodeBase64String(key.getEncoded())
	}
	def getpubkeyfromstring(key:String):PublicKey ={
		val publicBytes = Base64.decodeBase64(key);
	//	println("getpubkeyfromstring",key)
		val keySpec = new X509EncodedKeySpec(publicBytes);
		 val keyFactory = KeyFactory.getInstance("RSA");
		 keyFactory.generatePublic(keySpec);
	}
	
}
