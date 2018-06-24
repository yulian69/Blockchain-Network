package org.blockchain.node.model;

/**
 * @author Yulian Yordanov
 * Created: Jun 19, 2018
 */
public class Wallet {
	private String address;
	private String privateKey;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}		
}
