package org.blockchain.node.model;

/**
 * @author Yulian Yordanov
 * Created: Jun 19, 2018
 */
public class Crypto {
	private String cipher;
	private String ciphertext;
	private String iv;
	private String kdf;
	private Kdfparams kdfparams;
	private String mac;
	
	public String getCipher() {
		return cipher;
	}
	public void setCipher(String cipher) {
		this.cipher = cipher;
	}
	public String getCiphertext() {
		return ciphertext;
	}
	public void setCiphertext(String ciphertext) {
		this.ciphertext = ciphertext;
	}
	public String getIv() {
		return iv;
	}
	public void setIv(String iv) {
		this.iv = iv;
	}
	public String getKdf() {
		return kdf;
	}
	public void setKdf(String kdf) {
		this.kdf = kdf;
	}
	public Kdfparams getKdfparams() {
		return kdfparams;
	}
	public void setKdfparams(Kdfparams kdfparams) {
		this.kdfparams = kdfparams;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
}
