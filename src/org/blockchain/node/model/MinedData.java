package org.blockchain.node.model;

/**
 * @author Yulian Yordanov
 * Created: Jun 17, 2018
 */
public class MinedData {
	private String blockDataHash;
	private String dateCreated;
	private long nonce;
	private String blockHash;
	
	public MinedData() {
		
	}
	
	public MinedData(String blockDataHash, String dateCreated, long nonce, String blockHash) {
		this.blockDataHash = blockDataHash;
		this.dateCreated = dateCreated;
		this.nonce = nonce;
		this.blockHash = blockHash;
	}

	public String getBlockDataHash() {
		return blockDataHash;
	}

	public void setBlockDataHash(String blockDataHash) {
		this.blockDataHash = blockDataHash;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public String getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}
}
