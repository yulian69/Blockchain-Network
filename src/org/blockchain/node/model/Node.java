package org.blockchain.node.model;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Yulian Yordanov
 * Created: Jun 14, 2018
 */
public class Node {
	private Map<String, String> messages = new TreeMap<>();
	private Map<String, Block> miningJobs = new TreeMap<>();
	private Map<String, Long> balances = new TreeMap<>();
	private Map<String, Transaction> transactions = new TreeMap<>();
	private Map<String, List<String>> addresses = new TreeMap<>();
	private Map<String, Transaction> pendingTransactions = new TreeMap<>();
	private Map<String, Block> blockchain = new TreeMap<>();	
	private Map<Long, String> blockchainIds = new TreeMap<>();
	private Map<String, String> peers = new TreeMap<>();
	private Block lastBlock;
	private String chainId;
	private String nodeId;
	private String selfUrl;
	private int port;
	private String about;
	
	public Map<String, String> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}

	public Map<String, Block> getMiningJobs() {
		return miningJobs;
	}

	public void setMiningJobs(Map<String, Block> miningJobs) {
		this.miningJobs = miningJobs;
	}

	public Map<String, Long> getBalances() {
		return balances;
	}

	public void setBalances(Map<String, Long> balances) {
		this.balances = balances;
	}

	public Map<String, Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Map<String, Transaction> transactions) {
		this.transactions = transactions;
	}

	public Map<String, List<String>> getAddresses() {
		return addresses;
	}

	public void setAddresses(Map<String, List<String>> addresses) {
		this.addresses = addresses;
	}

	public Map<String, Transaction> getPendingTransactions() {
		return pendingTransactions;
	}

	public void setPendingTransactions(Map<String, Transaction> pendingTransactions) {
		this.pendingTransactions = pendingTransactions;
	}

	public Map<String, Block> getBlockchain() {
		return blockchain;
	}

	public void setBlockchain(Map<String, Block> blockchain) {
		this.blockchain = blockchain;
	}

	public Map<Long, String> getBlockchainIds() {
		return blockchainIds;
	}

	public void setBlockchainIds(Map<Long, String> blockchainIds) {
		this.blockchainIds = blockchainIds;
	}

	public Block getLastBlock() {
		return lastBlock;
	}

	public void setLastBlock(Block lastBlock) {
		this.lastBlock = lastBlock;
	}

	public String getChainId() {
		return chainId;
	}

	public void setChainId(String chainId) {
		this.chainId = chainId;
	}

	public Map<String, String> getPeers() {
		return peers;
	}

	public void setPeers(Map<String, String> peers) {
		this.peers = peers;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getSelfUrl() {
		return selfUrl;
	}

	public void setSelfUrl(String selfUrl) {
		this.selfUrl = selfUrl;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}
}
