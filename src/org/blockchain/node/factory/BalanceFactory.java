package org.blockchain.node.factory;

import java.util.List;

import org.blockchain.node.model.Balance;
import org.blockchain.node.model.Node;
import org.blockchain.node.model.Transaction;

/**
 * @author Yulian Yordanov
 * Created: Jun 15, 2018
 */
public class BalanceFactory {
	public static long getBalance(Node node, String address) {
		Long balance = node.getBalances().get(address);
		if ( balance == null ) {
			return 0;
		}
		return balance;
	}
	
	public static void addBalance(Node node, String address, long amount) {
		Long balance = node.getBalances().get(address);
		if ( balance == null ) {
			node.getBalances().put(address, amount);
		} else {
			node.getBalances().put(address, balance + amount);
		}
	}
	
	public static void updateBalance(Node node, Transaction[] transactions) {
		for (Transaction transaction : transactions) {
			BalanceFactory.addBalance(node, transaction.getFrom(), -transaction.getValue() - transaction.getFee());
			BalanceFactory.addBalance(node, transaction.getTo(), transaction.getValue());
		}
	}
	
	public static long getPendingBalance(Node node, String address) {
		long balance = 0;
		for (Transaction transaction : node.getPendingTransactions().values()) {
			if ( transaction.getFrom().equals(address) ) {
				balance -= transaction.getValue();
			}
			if ( transaction.getTo().equals(address) ) {
				balance += transaction.getValue();
			}
		}
		return balance;
	}
	
	public static Balance getBalanceInfo(Node node, String address) {
		Balance balance = new Balance();
		
		long currentIndex = node.getLastBlock().getIndex();		
		long balanceSafe = 0;
		long balanceConfirmed = 0;
		
		List<String> hashes = node.getAddresses().get(address);
		if ( hashes != null ) {		
			for (String hash : hashes) {
				Transaction transaction = node.getTransactions().get(hash);
				if ( transaction == null ) {
					continue;
				}
				
				if ( transaction.getFrom().equals(address) ) {
					if (transaction.getMinedInBlock() <= currentIndex-5 ) {
						balanceSafe -= transaction.getValue();
					}
					balanceConfirmed -= transaction.getValue();
				}
				if ( transaction.getTo().equals(address) ) {
					if (transaction.getMinedInBlock() <= currentIndex-5 ) {
						balanceSafe += transaction.getValue();
					}
					balanceConfirmed += transaction.getValue();
				}
			}
		}
		
		balance.setSafeBalance(balanceSafe);
		balance.setConfirmedBalance(balanceConfirmed);
		balance.setPendingBalance(balanceConfirmed+getPendingBalance(node, address));
		
		return balance;
	}
}
