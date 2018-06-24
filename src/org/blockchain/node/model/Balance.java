package org.blockchain.node.model;

/**
 * @author Yulian Yordanov
 * Created: Jun 16, 2018
 */
public class Balance {
	private long safeBalance;
	private long confirmedBalance;
	private long pendingBalance;
	
	public long getSafeBalance() {
		return safeBalance;
	}
	public void setSafeBalance(long safeBalance) {
		this.safeBalance = safeBalance;
	}
	public long getConfirmedBalance() {
		return confirmedBalance;
	}
	public void setConfirmedBalance(long confirmedBalance) {
		this.confirmedBalance = confirmedBalance;
	}
	public long getPendingBalance() {
		return pendingBalance;
	}
	public void setPendingBalance(long pendingBalance) {
		this.pendingBalance = pendingBalance;
	}
}
