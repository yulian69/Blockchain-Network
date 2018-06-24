package org.blockchain.node.model;

/**
 * @author Yulian Yordanov
 * Created: Jun 18, 2018
 */
public class Message {
	private String messageId;
	private String nodeId;
	private String hash;
	
	public Message() {
		
	}
	
	public Message(String messageId, String nodeId, String hash) {
		this.messageId = messageId;
		this.nodeId = nodeId;
		this.hash = hash;
	}
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
}
