package org.blockchain.node.factory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.blockchain.node.model.Block;
import org.blockchain.node.model.Message;
import org.blockchain.node.model.Node;
import org.blockchain.node.model.Transaction;

/**
 * @author Yulian Yordanov
 * Created: Jun 13, 2018
 */
public class BlockchainFactory {
	
	public static List<Block> getBlocks(Node node) {
		List<Block> blocks = new ArrayList<>();
		
		Block block = node.getLastBlock();
		while (block != null) {
			blocks.add(block);
			block = node.getBlockchain().get(block.getPrevBlockHash());
		}		
		Collections.reverse(blocks);
		
		return blocks;
	}
	
	public static boolean addBlock(Node node, Block block, Message message) {
		if ( block.getComulativeDifficulty().compareTo(node.getLastBlock().getComulativeDifficulty()) <= 0 ) {
			return false;
		}
		
		if ( !block.getPrevBlockHash().equals(node.getLastBlock().getBlockHash()) ) {
			if ( message != null ) {
				Block[] blocks = PeerFactory.requestChain(node, message.getNodeId());				
				return importBlockchain(node, blocks);
			}
			return false;
		}
		
		if ( !BlockFactory.validateBlock(node, block, (message == null)) ) {
			return false;
		}
		
		addBlock(node, block);
		BalanceFactory.updateBalance(node, block.getTransactions());
		TransactionFactory.updatePendingTransactions(node, block.getTransactions());
		TransactionFactory.updateTransactions(node, block.getTransactions());
		
		return true;
	}
	
	public static boolean importBlockchain(Node node, Block[] blocks) {
		System.out.println("Import chain");
		
		if ( blocks.length == 0 ) {
			return false;
		}
		
		if ( !blocks[0].getBlockHash().equals(node.getChainId()) ) {
			return false;
		}
		
		Node candidateNode = new Node();
		candidateNode.setChainId(node.getChainId());
		
		Block lastBlock = null;
		
		long index = 0;
		for (Block block : blocks) {
			if (block.getIndex() != index) {
				return false;
			}
			if ( block.getIndex() > 0 ) {
				if ( !lastBlock.getBlockHash().equals(block.getPrevBlockHash()) ) {
					return false;
				}
			}
		
			if ( !BlockFactory.validateBlock(candidateNode, block, false) ) {
				return false;
			}
			
			for (Transaction transaction : block.getTransactions()) {
				String from = transaction.getFrom();
				String to = transaction.getTo();
				
				long balanceFrom = 0;
				if ( candidateNode.getBalances().get(from) != null ) {
					balanceFrom = candidateNode.getBalances().get(from);
				}
				balanceFrom -= (transaction.getValue() + transaction.getFee());
				
				long balanceTo = 0;
				if ( candidateNode.getBalances().get(to) != null ) {
					balanceTo = candidateNode.getBalances().get(to);
				}
				balanceTo += transaction.getValue();
				
				candidateNode.getBalances().put(from, balanceFrom);
				candidateNode.getBalances().put(to, balanceTo);
				
				candidateNode.getTransactions().put(transaction.getTransactionDataHash(), transaction);
				
				List<String> transFrom = candidateNode.getAddresses().get(transaction.getFrom());
				if ( transFrom == null ) {
					transFrom = new ArrayList<>();
				}
				transFrom.add(transaction.getTransactionDataHash());
				candidateNode.getAddresses().put(transaction.getFrom(), transFrom);
				
				List<String> transTo = candidateNode.getAddresses().get(transaction.getTo());
				if ( transTo == null ) {
					transTo = new ArrayList<>();
				}
				transTo.add(transaction.getTransactionDataHash());
				candidateNode.getAddresses().put(transaction.getTo(), transTo);
			}
			
			candidateNode.getBlockchain().put(block.getBlockHash(), block);
			candidateNode.getBlockchainIds().put(block.getIndex(), block.getBlockHash());
			lastBlock = block;
			index++;
		}
		
		if ( lastBlock.getComulativeDifficulty().compareTo(node.getLastBlock().getComulativeDifficulty()) <= 0 ) {
			return false;
		}
		
		Map<String, Transaction> candidatePendingTransactions = node.getTransactions();
		for (Transaction pendingTransaction : node.getPendingTransactions().values()) {
			candidatePendingTransactions.put(pendingTransaction.getTransactionDataHash(), pendingTransaction);
		}
		
		node.getMiningJobs().clear();
		node.getPendingTransactions().clear();
		node.setTransactions(candidateNode.getTransactions());
		node.setAddresses(candidateNode.getAddresses());
		node.setBalances(candidateNode.getBalances());
		node.setBlockchain(candidateNode.getBlockchain());
		node.setBlockchainIds(candidateNode.getBlockchainIds());
		node.setLastBlock(lastBlock);
		
		for (Transaction candidatePendingTransaction : candidatePendingTransactions.values()) {			
			BigInteger bigInteger = new BigInteger(candidatePendingTransaction.getFrom(),16);			
			if ( bigInteger.compareTo(new BigInteger("0")) == 0 ) {
				continue;
			}
			
			if ( node.getTransactions().get(candidatePendingTransaction.getTransactionDataHash()) != null ) {
				continue;
			}
			
			if ( TransactionFactory.validateBalance(node, candidatePendingTransaction, 0) ) {
				node.getPendingTransactions().put(candidatePendingTransaction.getTransactionDataHash(), candidatePendingTransaction);
			}			
		}
		
		for (Transaction pendingTransaction : node.getPendingTransactions().values()) {
			PeerFactory.populateTransaction(node, pendingTransaction.getTransactionDataHash());
		}	
		
		System.out.println("Chain imported");
		
		return true;		
	}
	
	public static void addBlock(Node node, Block block) {
		node.getBlockchain().put(block.getBlockHash(), block);
		node.getBlockchainIds().put(block.getIndex(), block.getBlockHash());
		node.setLastBlock(block);
	}
	
	public static Block getBlock(Node node, long index) {
		String hash = node.getBlockchainIds().get(index);
		if ( hash == null ) {
			return null;
		}
		return node.getBlockchain().get(hash);
	}
}
