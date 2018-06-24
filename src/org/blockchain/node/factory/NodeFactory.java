package org.blockchain.node.factory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.blockchain.crypto.Cryptography;
import org.blockchain.node.model.Block;
import org.blockchain.node.model.ChainInfo;
import org.blockchain.node.model.DebugInfo;
import org.blockchain.node.model.Node;
import org.blockchain.node.model.NodeInfo;
import org.blockchain.util.Utils;

/**
 * @author Yulian Yordanov
 * Created: Jun 14, 2018
 */
public class NodeFactory {
	public static void initialize(Node node, int port) {
		node.setAbout("YulianChain/1.0-Java");
		node.setSelfUrl("http://localhost:" + port);
		node.setNodeId(Cryptography.RipeMD160(node.getSelfUrl()));
		node.setPort(port);
		setPeers(node);
		
		Block block = BlockFactory.getGenesisBlock();
		BalanceFactory.updateBalance(node, block.getTransactions());
		TransactionFactory.updateTransactions(node, block.getTransactions());
		
		BlockchainFactory.addBlock(node, block);
		node.setLastBlock(block);
		node.setChainId(block.getBlockHash());
	}
		
	public static void setPeers(Node node) {
		Map<String, String> peers = new TreeMap<>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("./" + node.getPort() + ".conf"));
			String line;
			while ( (line = bufferedReader.readLine()) != null ) {
				try {
					int port = Integer.parseInt(line.trim());
					String url = "http://localhost:" + port;
					peers.put(Cryptography.RipeMD160(url), url);
				} catch (Exception e) {}
			}
			bufferedReader.close();
		} catch (IOException e) {}
		node.setPeers(peers);
	}
	
	public static NodeInfo getNodeInfo(Node node) {
		NodeInfo nodeInfo = new NodeInfo();
		
		nodeInfo.setAbout(node.getAbout());
		nodeInfo.setNodeId(node.getNodeId());
		nodeInfo.setChainId(node.getChainId());
		nodeInfo.setNodeUrl(node.getSelfUrl());
		nodeInfo.setPeers(node.getPeers().size());
		nodeInfo.setCurrentDifficulty(Utils.DIFFICULTY);
		nodeInfo.setBlockCount(node.getBlockchain().size());
		nodeInfo.setComulativeDifficulty(node.getLastBlock().getComulativeDifficulty());
		nodeInfo.setConfirmedTransactions(node.getTransactions().size());
		nodeInfo.setPendingTransactions(node.getPendingTransactions().size());
		
		return nodeInfo;
	}
	
	private static ChainInfo getChainInfo(Node node) {
		ChainInfo chainInfo = new ChainInfo();
		
		chainInfo.setBlocks(BlockchainFactory.getBlocks(node));
		chainInfo.setPendingTransactions(node.getPendingTransactions().values());
		chainInfo.setCurrentDifficulty(Utils.DIFFICULTY);
		chainInfo.setMiningJobs(node.getMiningJobs());
		
		return chainInfo;
	}
	
	public static DebugInfo getDebugInfo(Node node) {
		DebugInfo debugInfo = new DebugInfo();
		
		debugInfo.setSelfUrl(node.getSelfUrl());
		debugInfo.setPeers(node.getPeers().values());
		debugInfo.setChain(getChainInfo(node));
		debugInfo.setConfirmedBalances(node.getBalances());
		
		return debugInfo;
	}
}
