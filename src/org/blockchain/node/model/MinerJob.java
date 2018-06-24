package org.blockchain.node.model;

/**
 * @author Yulian Yordanov
 * Created: Jun 17, 2018
 */
public class MinerJob {
	long index;
	int transactionsIncluded;
	private short difficulty;
	long expectedReward;
	String rewardAddress;
	private String blockDataHash;
	
	public long getIndex() {
		return index;
	}
	public void setIndex(long index) {
		this.index = index;
	}
	public int getTransactionsIncluded() {
		return transactionsIncluded;
	}
	public void setTransactionsIncluded(int transactionsIncluded) {
		this.transactionsIncluded = transactionsIncluded;
	}
	public short getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(short difficulty) {
		this.difficulty = difficulty;
	}
	public long getExpectedReward() {
		return expectedReward;
	}
	public void setExpectedReward(long expectedReward) {
		this.expectedReward = expectedReward;
	}
	public String getRewardAddress() {
		return rewardAddress;
	}
	public void setRewardAddress(String rewardAddress) {
		this.rewardAddress = rewardAddress;
	}
	public String getBlockDataHash() {
		return blockDataHash;
	}
	public void setBlockDataHash(String blockDataHash) {
		this.blockDataHash = blockDataHash;
	}
}
