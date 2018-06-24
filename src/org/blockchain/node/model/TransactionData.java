package org.blockchain.node.model;

/**
 * @author Yulian Yordanov
 * Created: Jun 12, 2018
 */
public class TransactionData {
	private String from;
	private String to;
	private long value;
	private int fee;
	private String dateCreated;
	private String data;
	private String senderPubKey;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	public int getFee() {
		return fee;
	}
	public void setFee(int fee) {
		this.fee = fee;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getSenderPubKey() {
		return senderPubKey;
	}
	public void setSenderPubKey(String senderPubKey) {
		this.senderPubKey = senderPubKey;
	}
}
