package org.blockchain.crypto;


/**
 * @author Yulian Yordanov
 * Created: Jun 12, 2018
 */


import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.blockchain.node.model.Kdfparams;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jcajce.provider.digest.SHA384;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jcajce.provider.digest.SHA512;
import org.bouncycastle.jcajce.provider.digest.Whirlpool;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import java.math.BigInteger;

public class Cryptography {	
	private final static X9ECParameters curve = SECNamedCurves.getByName("secp256k1");
	private final static ECDomainParameters Domain = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH());
	private static final ECCurve.Fp eccurve = (ECCurve.Fp) Domain.getCurve();
	
    public static String SHA256(String message) {
    	SHA256.Digest digest = new SHA256.Digest();
        return Hex.toHexString(digest.digest(message.getBytes(StandardCharsets.UTF_8)));
    }
       
    public static byte[] SHA512(byte[] bytes) {
    	SHA512.Digest digest = new SHA512.Digest();
        return digest.digest(bytes);
    }
          
    public static byte[] SHA384(byte[] bytes) {
    	SHA384.Digest digest = new SHA384.Digest();
    	return digest.digest(bytes);
    }
    
    public static byte[] SHA3_512(byte[] bytes) {
    	SHA3.DigestSHA3 digest = new SHA3.Digest512();
        return digest.digest(bytes);
    }
    
    public static byte[] Keccak256(byte[] bytes) {
    	Keccak.Digest256 digest = new Keccak.Digest256();
        return digest.digest(bytes);
    }
    
    public static byte[] Keccak512(byte[] bytes) {
    	Keccak.Digest512 digest = new Keccak.Digest512();
        return digest.digest(bytes);
    }
    
    public static byte[] Whirlpool512(byte[] bytes) {
    	Whirlpool.Digest digest = new Whirlpool.Digest();
        return digest.digest(bytes);
    }
    
    public static String RipeMD160(String message) {
    	RIPEMD160.Digest digest = new RIPEMD160.Digest();
    	return Hex.toHexString(digest.digest(message.getBytes(StandardCharsets.UTF_8)));
    }
        
    public static byte[] HMAC_SHA256(byte[] message, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {    
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(message);		        		
    }
    
    public static byte[] HMAC_SHA512(byte[] message, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
    	Mac mac = Mac.getInstance("HmacSHA512");
		mac.init(new SecretKeySpec(key, "HmacSHA512"));
        return mac.doFinal(message);        		
    }
    
    public static byte[] SCrypt(String password, Kdfparams kdfparams) {
    	return SCrypt.generate(password.getBytes(StandardCharsets.UTF_8), kdfparams.getSalt().getBytes(), kdfparams.getN(), kdfparams.getR(), kdfparams.getP(), kdfparams.getDklen());
    }
    
    public static byte[] encryptTwofish(byte[] text, String password, byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {              	
    	Cipher cipher = Cipher.getInstance("Twofish/CBC/PKCS7Padding", new BouncyCastleProvider());
    	        	
    	SecretKeySpec secretKey = new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), "Twofish/CBC/PKCS7Padding");
    	
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

		return cipher.doFinal(text);		        		
    }
    
    public static byte[] decryptTwofish(byte[] encrypted, String password, byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("Twofish/CBC/PKCS7Padding", new BouncyCastleProvider());
    	
    	SecretKeySpec secretKey = new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), "Twofish/CBC/PKCS7Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

		return cipher.doFinal(encrypted);
    }
        
    public static AsymmetricCipherKeyPair generateRandomKeys() {
    	ECKeyPairGenerator generator = new ECKeyPairGenerator();
    	SecureRandom secureRandom = new SecureRandom();
    	KeyGenerationParameters keyGenerationParameters = new ECKeyGenerationParameters(Domain, secureRandom);
    	generator.init(keyGenerationParameters);
    	return generator.generateKeyPair();
    }
    
    public static String getPublicKeyCompressed(String privateKey) {
    	ECPoint pubKey = getPublicKeyFromPrivateKey(privateKey);
    	return pubKey.getXCoord().toBigInteger().toString(16) + (pubKey.getYCoord().toBigInteger().testBit(0) ? 1: 0);
    }
    
    public static String getAddress(String pubKeyCompressed) {    	
    	return RipeMD160(pubKeyCompressed);
    }
   /* 
    public static String getAddress(String x, String y) {    	
    	String pubKeyCompressed = getPublicKeyCompressed(getPublicKeyParams(x, y).getQ());
    	return RipeMD160(pubKeyCompressed);
    }*/
    
    public static ECPoint getPublicKeyFromPrivateKey(String privateKey) {    	
    	return curve.getG().multiply(new BigInteger(privateKey,16)).normalize();
    }
    
    public static String[] signData(String privateKey, String data) {    	
    	ECPrivateKeyParameters keyParameters = new ECPrivateKeyParameters(new BigInteger(privateKey,16), Domain);
    	DSAKCalculator calculator = new HMacDSAKCalculator(new SHA256Digest());
    	ECDSASigner signer = new ECDSASigner(calculator);
    	signer.init(true, keyParameters);
    	BigInteger[] signature = signer.generateSignature(data.getBytes(StandardCharsets.UTF_8));
    	return new String[] {signature[0].toString(16),signature[1].toString(16)};
    }
    
    public static ECPublicKeyParameters toPublicKey(String privateKey) {
    	BigInteger d = new BigInteger(privateKey,16);
    	ECPoint q = Domain.getG().multiply(d);
    	return new ECPublicKeyParameters(q,Domain);
    }
    
    public static boolean verifySignature(ECPublicKeyParameters pubKey, String[] signature, String data) {
    	DSAKCalculator calculator = new HMacDSAKCalculator(new SHA256Digest());
    	ECDSASigner signer = new ECDSASigner(calculator);
    	signer.init(false, pubKey);    	
    	return signer.verifySignature(data.getBytes(StandardCharsets.UTF_8), new BigInteger(signature[0],16), new BigInteger(signature[1],16));
    }
    
   /* public static ECPublicKeyParameters getPublicKeyParams(String x, String y) {
    	return new ECPublicKeyParameters(eccurve.createPoint(new BigInteger(x,16), new BigInteger(y,16)), Domain);
    }*/
    
    public static ECPublicKeyParameters getPublicKeyParams(String compressedPibKey) {
    	ECPoint ecPoint = decompressKey(compressedPibKey);
    	return new ECPublicKeyParameters(eccurve.createPoint(ecPoint.getXCoord().toBigInteger(), ecPoint.getYCoord().toBigInteger()), Domain);
    }
    
    public static ECPoint decompressKey(String compressedPibKey) {
    	boolean yBit = compressedPibKey.charAt(compressedPibKey.length()-1) == '1';
    	compressedPibKey = compressedPibKey.substring(0, compressedPibKey.length()-1);
        X9IntegerConverter x9 = new X9IntegerConverter();
        byte[] compEnc = x9.integerToBytes(new BigInteger(compressedPibKey,16), 1 + x9.getByteLength(curve.getCurve()));
        compEnc[0] = (byte)(yBit ? 0x03 : 0x02);
        return curve.getCurve().decodePoint(compEnc);
    }
}