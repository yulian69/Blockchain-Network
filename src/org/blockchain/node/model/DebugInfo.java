package org.blockchain.node.model;

import java.util.Collection;
import java.util.Map;

/**
 * @author Yulian Yordanov
 * Created: Jun 20, 2018
 */
public class DebugInfo {
	private String selfUrl;
	private Collection<String> peers;
	private ChainInfo chain;
	private Map<String, Long> confirmedBalances;
	
	public String getSelfUrl() {
		return selfUrl;
	}
	public void setSelfUrl(String selfUrl) {
		this.selfUrl = selfUrl;
	}
	public Collection<String> getPeers() {
		return peers;
	}
	public void setPeers(Collection<String> peers) {
		this.peers = peers;
	}
	public ChainInfo getChain() {
		return chain;
	}
	public void setChain(ChainInfo chain) {
		this.chain = chain;
	}
	public Map<String, Long> getConfirmedBalances() {
		return confirmedBalances;
	}
	public void setConfirmedBalances(Map<String, Long> confirmedBalances) {
		this.confirmedBalances = confirmedBalances;
	}
}
