package org.blockchain.node.model;

/**
 * @author Yulian Yordanov
 * Created: Jun 13, 2018
 */
public class BlockData {
	private long index;
	private Transaction[] transactions;
	private short difficulty;
	private String prevBlockHash;
	private String minedBy;
	
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
 }
