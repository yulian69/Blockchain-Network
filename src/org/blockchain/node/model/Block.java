package org.blockchain.node.model;

import java.math.BigInteger;

/**
 * @author Yulian Yordanov
 * Created: Jun 13, 2018
 */
public class Block {
	private long index;
	private Transaction[] transactions;
	private short difficulty;
	private String prevBlockHash;
	private String minedBy;
	private String blockDataHash;
	private long nonce;
	private String dateCreated;
	private String blockHash;
	private BigInteger comulativeDifficulty;
	
	
	public long getIndex() {
		return index;
	}
	public void setIndex(long index) {
		this.index = index;
	}
	public Transaction[] getTransactions() {
		return transactions;
	}
	public void setTransactions(Transaction[] transactions) {
		this.transactions = transactions;
	}
	public short getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(short difficulty) {
		this.difficulty = difficulty;
	}
	public String getPrevBlockHash() {
		return prevBlockHash;
	}
	public void setPrevBlockHash(String prevBlockHash) {
		this.prevBlockHash = prevBlockHash;
	}
	public String getMinedBy() {
		return minedBy;
	}
	public void setMinedBy(String minedBy) {
		this.minedBy = minedBy;
	}
	public String getBlockDataHash() {
		return blockDataHash;
	}
	public void setBlockDataHash(String blockDataHash) {
		this.blockDataHash = blockDataHash;
	}
	public long getNonce() {
		return nonce;
	}
	public void setNonce(long nonce) {
		this.nonce = nonce;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getBlockHash() {
		return blockHash;
	}
	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}
	public BigInteger getComulativeDifficulty() {
		return comulativeDifficulty;
	}
	public void setComulativeDifficulty(BigInteger comulativeDifficulty) {
		this.comulativeDifficulty = comulativeDifficulty;
	}
 }
