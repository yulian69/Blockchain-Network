package org.blockchain.node.model;

/**
 * @author Yulian Yordanov
 * Created: Jun 19, 2018
 */
public class WalletData {
	private String address;
	private String id;
	private String version;
	private Crypto crypto;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Crypto getCrypto() {
		return crypto;
	}
	public void setCrypto(Crypto crypto) {
		this.crypto = crypto;
	}
}
