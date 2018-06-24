package org.blockchain.node.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Yulian Yordanov
 * Created: Jun 20, 2018
 */
public class ChainInfo {
	private List<Block> blocks;
	private Collection<Transaction> pendingTransactions;
	private short currentDifficulty;
	private Map<String, Block> miningJobs;
	
	public List<Block> getBlocks() {
		return blocks;
	}
	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}
	public Collection<Transaction> getPendingTransactions() {
		return pendingTransactions;
	}
	public void setPendingTransactions(Collection<Transaction> pendingTransactions) {
		this.pendingTransactions = pendingTransactions;
	}
	public short getCurrentDifficulty() {
		return currentDifficulty;
	}
	public void setCurrentDifficulty(short currentDifficulty) {
		this.currentDifficulty = currentDifficulty;
	}
	public Map<String, Block> getMiningJobs() {
		return miningJobs;
	}
	public void setMiningJobs(Map<String, Block> miningJobs) {
		this.miningJobs = miningJobs;
	}
}
