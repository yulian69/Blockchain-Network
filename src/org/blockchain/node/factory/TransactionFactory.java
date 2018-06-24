package org.blockchain.node.factory;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.blockchain.crypto.Cryptography;
import org.blockchain.node.exception.TransactionValidationException;
import org.blockchain.node.model.Node;
import org.blockchain.node.model.Transaction;
import org.blockchain.node.model.TransactionData;
import org.blockchain.util.Utils;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import com.google.gson.Gson;

/**
 * @author Yulian Yordanov
 * Created: Jun 12, 2018
 */
public class TransactionFactory {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
	
	public static Transaction generateGenesisTransaction() {
		Transaction transaction = generateTransaction(Utils.ADDRESS_ZERO, Utils.FAUCET_ADDRESS, Utils.INITIAL_COINS, 0, Utils.GENESIS_DATE, Utils.GENESIS_DATA, "");		
		transaction.setMinedInBlock(0);		
		return transaction;
	}
	
	public static Transaction generateFaucetTransaction(String address) {
		String dateCreated = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
		Transaction transaction = generateTransaction(Utils.FAUCET_ADDRESS, address, Utils.FAUCET_COINS, Utils.TRANSACTION_FEE, dateCreated, "Faucet", Utils.FAUCET_PRIVATE_KEY);		
		transaction.setMinedInBlock(-1);		
		return transaction;
	}
	
	public static Transaction generateTransaction(String from, String to, long value, int fee, String dateCreated, String data, String senderPrivateKey) {
		Transaction transaction = new Transaction();
				
		String senderPubKey = Utils.PUBLIC_KEY_ZERO;
		if ( senderPrivateKey.length() > 0 ) {
			senderPubKey = Cryptography.getPublicKeyCompressed(senderPrivateKey);
		}
			
		transaction.setFrom(from);
		transaction.setTo(to);
		transaction.setValue(value);
		transaction.setFee(fee);
		transaction.setDateCreated(dateCreated);
		transaction.setData(data);
		transaction.setSenderPubKey(senderPubKey);
		transaction.setMinedInBlock(-1);
		
		TransactionData transactionData = TransactionFactory.getTransactionData(transaction);
		Gson gson = new Gson();
		String dataHash = Cryptography.SHA256(gson.toJson(transactionData));
		
		transaction.setTransactionDataHash(dataHash);
		
		String[] signature = new String[] {Utils.PUBLIC_KEY_ZERO, Utils.PUBLIC_KEY_ZERO};
		if ( senderPrivateKey.length() > 0 ) {
			signature = Cryptography.signData(senderPrivateKey, dataHash);
		}
		transaction.setSenderSignature(signature);
		
		return transaction;
	}
	
	public static Transaction generateCoinbaseTransaction(String address) {
		Transaction transaction = new Transaction();
		
		String senderPubKey = Utils.PUBLIC_KEY_ZERO;
		String[] signature = new String[] {Utils.SIGNATURE_ZERO, Utils.SIGNATURE_ZERO};
		
		transaction.setFrom(Utils.ADDRESS_ZERO);
		transaction.setTo(address);
		transaction.setValue(Utils.MINER_REWARD);
		transaction.setFee(0);
		transaction.setDateCreated(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
		transaction.setData("Coinbase TX");
		transaction.setSenderPubKey(senderPubKey);
		transaction.setSenderSignature(signature);
		transaction.setMinedInBlock(-1);
		
		TransactionData transactionData = TransactionFactory.getTransactionData(transaction);
		Gson gson = new Gson();
		String dataHash = Cryptography.SHA256(gson.toJson(transactionData));
		transaction.setTransactionDataHash(dataHash);
				
		return transaction;
	}
	
	public static TransactionData getTransactionData(Transaction transaction){
		TransactionData transactionData = new TransactionData();
		
		transactionData.setFrom(transaction.getFrom());
		transactionData.setTo(transaction.getTo());
		transactionData.setValue(transaction.getValue());
		transactionData.setFee(transaction.getFee());
		transactionData.setDateCreated(transaction.getDateCreated());
		transactionData.setData(transaction.getData());
		transactionData.setSenderPubKey(transaction.getSenderPubKey());
		
		return transactionData;
	}
	
	public static void signTransaction(Transaction transaction, String privateKey) {
		String publicKey = Cryptography.getPublicKeyCompressed(privateKey);
		transaction.setSenderPubKey(publicKey);
		
		TransactionData transactionData = TransactionFactory.getTransactionData(transaction);
		
		Gson gson = new Gson();
		String dataHash = Cryptography.SHA256(gson.toJson(transactionData));
		String[] signature = Cryptography.signData(privateKey, dataHash);
		
		transaction.setTransactionDataHash(dataHash);
		transaction.setSenderSignature(signature);
	}
	
	public static void validateTransaction(Node node, Transaction transaction, long blockPendingValue, boolean isNew) throws TransactionValidationException {
		if ( !validateDuplicate(node, transaction, isNew) ) {
			throw new TransactionValidationException("Duplicate transaction");
		}
		if ( !validateAddressFrom(transaction) ) {
			throw new TransactionValidationException("Invalid From property");
		}
		if ( !validateAddressTo(transaction) ) {
			throw new TransactionValidationException("Invalid To property");
		}
		if ( !validateValue(transaction) ) {
			throw new TransactionValidationException("Invalid Value property");
		}		
		if ( !validateFee(transaction) ) {
			throw new TransactionValidationException("Invalid Fee property");
		}
		if ( !validateDateCreated(transaction) ) {
			throw new TransactionValidationException("Invalid DateCreated property");
		}
		if ( !validateDataHash(transaction) ) {
			throw new TransactionValidationException("Invalid TransactionDataHash property");
		}
		if ( !validateSignature(transaction) ) {
			throw new TransactionValidationException("Invalid Signature");
		}
		if ( !validateAddressByPubKey(transaction) ) {
			throw new TransactionValidationException("Signature and address do not match");
		}
		if ( !validateBalance(node, transaction, blockPendingValue) ) {
			throw new TransactionValidationException("Balance insufficient");
		}
	}
	
	private static boolean validateAddressFrom(Transaction transaction) {
		return validateAddress(transaction.getFrom());
	}
	
	private static boolean validateAddressTo(Transaction transaction) {
		return validateAddress(transaction.getTo());
	}
	
	public static boolean validateAddress(String address) {
		try {
			if ( address.length() != 40 ) {
				return false;
			}
			new BigInteger(address,16);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static boolean validateValue(Transaction transaction) {
		if ( transaction.getValue() <= 0 ) {
			return false;
		}
		return true;
	}
	
	private static boolean validateFee(Transaction transaction) {
		if ( transaction.getFee() < 10 ) {
			return false;
		}
		return true;
	}
	
	public static boolean validateBalance(Node node, Transaction transaction, long blockPendingValue) {
		if ( transaction.getMinedInBlock() == 0 && (new BigInteger(transaction.getFrom(),16)).compareTo(new BigInteger("0")) == 0 ) {
			return true;
		}
		
		long pending = 0;
		
		for (Transaction trans : node.getPendingTransactions().values()) {
			if ( trans.getFrom().equals(transaction.getFrom()) ) {
				pending += trans.getValue() + trans.getFee();
			}
		}
	
		Long balance = node.getBalances().get(transaction.getFrom());
		if ( balance == null ) {
			balance = 0L;
		}
		
		if ( transaction.getValue() + transaction.getFee() + pending + blockPendingValue > balance ) {
			return false;
		}
		return true;
	}
	
	private static boolean validateDateCreated(Transaction transaction) {
		try {
			dateFormat.parse(transaction.getDateCreated());
			return true;
		} catch (Exception e) {}
		return false;
	}
	
	private static boolean validateDataHash(Transaction transaction) {
		try {
			TransactionData transactionData = TransactionFactory.getTransactionData(transaction);
			Gson gson = new Gson();
			if ( Cryptography.SHA256(gson.toJson(transactionData)).equals(transaction.getTransactionDataHash()) ) {
				return true;
			}
		} catch (Exception e) {}
		return false;
	}
	
	private static boolean validateDuplicate(Node node, Transaction transaction, boolean isNew) {
		if ( isNew && node.getPendingTransactions().get(transaction.getTransactionDataHash()) != null ) {
			return false;
		}
		if ( node.getTransactions().get(transaction.getTransactionDataHash()) != null ) {
			return false;
		}
		return true;
	}
	
	private static boolean validateSignature(Transaction transaction) {
		try {
			String[] signature = new String[] {transaction.getSenderSignature()[0], transaction.getSenderSignature()[1]};
			ECPublicKeyParameters pubKey = Cryptography.getPublicKeyParams(transaction.getSenderPubKey());
			return Cryptography.verifySignature(pubKey, signature, transaction.getTransactionDataHash());
		} catch (Exception e) {}
		return false;
	}
	
	private static boolean validateAddressByPubKey(Transaction transaction) {
		try {
			if (transaction.getFrom().equals(Cryptography.getAddress(transaction.getSenderPubKey())) ) {
				return true;
			}
		} catch (Exception e) {}
		return false;
	}
	
	public static void updateTransactions(Node node, Transaction[] transactions) {
		for (Transaction transaction : transactions) {
			addTransaction(node, transaction);
		}
	}
		
	public static Transaction getTransaction(Node node, String hash) {
		return node.getTransactions().get(hash);
	}
	
	public static void addTransaction(Node node, Transaction transaction) {
		node.getTransactions().put(transaction.getTransactionDataHash(), transaction);
		
		List<String> transFrom = node.getAddresses().get(transaction.getFrom());
		if ( transFrom == null ) {
			transFrom = new ArrayList<>();
		}
		transFrom.add(transaction.getTransactionDataHash());
		node.getAddresses().put(transaction.getFrom(), transFrom);
		
		List<String> transTo = node.getAddresses().get(transaction.getTo());
		if ( transTo == null ) {
			transTo = new ArrayList<>();
		}
		transTo.add(transaction.getTransactionDataHash());
		node.getAddresses().put(transaction.getTo(), transTo);
	}	
	
	public static void addPendingTransaction(Node node, Transaction transaction) {
		node.getPendingTransactions().put(transaction.getTransactionDataHash(), transaction);
		
	}
	public static void updatePendingTransactions(Node node, Transaction[] transactions) {
		for (Transaction transaction : transactions) {
			node.getPendingTransactions().remove(transaction.getTransactionDataHash());
		}
	}
	
	public static Transaction[] getPendingTransactions(Node node, Transaction coinBaseTransaction, long minedInBlock) {
		coinBaseTransaction.setMinedInBlock(minedInBlock);
		Transaction[] pendingTransactions = new Transaction[node.getPendingTransactions().size()+1];
		node.getPendingTransactions().values().toArray(pendingTransactions);
		for (int i = 0; i < pendingTransactions.length-1; i++) {
			pendingTransactions[i].setMinedInBlock(minedInBlock);
			coinBaseTransaction.setValue(coinBaseTransaction.getValue()+pendingTransactions[i].getFee());
		}
		pendingTransactions[pendingTransactions.length-1] = coinBaseTransaction;
		return pendingTransactions;
	}
	
	public static List<Transaction> getTransactions(Node node, String address) {
		List<Transaction> transactions = new ArrayList<>();
				
		for (String hash : node.getAddresses().get(address)) {
			Transaction transaction = node.getTransactions().get(hash);
			if ( transaction == null ) {
				continue;
			}
			
			transactions.add(transaction);
		}
		
		return transactions;
	}
}
