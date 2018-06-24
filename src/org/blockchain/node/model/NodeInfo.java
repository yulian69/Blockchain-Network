package org.blockchain.node.model;

import java.math.BigInteger;

/**
 * @author Yulian Yordanov
 * Created: Jun 20, 2018
 */
public class NodeInfo {
	private String about;
	private String nodeId;
	private String chainId;
	private String nodeUrl;
	private int peers;
	private short currentDifficulty;
	private long blockCount;
	private BigInteger comulativeDifficulty;
	private long confirmedTransactions;
	private int pendingTransactions;
	
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getChainId() {
		return chainId;
	}
	public void setChainId(String chainId) {
		this.chainId = chainId;
	}
	public String getNodeUrl() {
		return nodeUrl;
	}
	public void setNodeUrl(String nodeUrl) {
		this.nodeUrl = nodeUrl;
	}
	public int getPeers() {
		return peers;
	}
	public void setPeers(int peers) {
		this.peers = peers;
	}
	public short getCurrentDifficulty() {
		return currentDifficulty;
	}
	public void setCurrentDifficulty(short currentDifficulty) {
		this.currentDifficulty = currentDifficulty;
	}
	public long getBlockCount() {
		return blockCount;
	}
	public void setBlockCount(long blockCount) {
		this.blockCount = blockCount;
	}
	public BigInteger getComulativeDifficulty() {
		return comulativeDifficulty;
	}
	public void setComulativeDifficulty(BigInteger comulativeDifficulty) {
		this.comulativeDifficulty = comulativeDifficulty;
	}
	public long getConfirmedTransactions() {
		return confirmedTransactions;
	}
	public void setConfirmedTransactions(long confirmedTransactions) {
		this.confirmedTransactions = confirmedTransactions;
	}
	public int getPendingTransactions() {
		return pendingTransactions;
	}
	public void setPendingTransactions(int pendingTransactions) {
		this.pendingTransactions = pendingTransactions;
	}
}
