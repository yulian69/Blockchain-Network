package org.blockchain.node.factory;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

import org.blockchain.crypto.Cryptography;
import org.blockchain.node.exception.TransactionValidationException;
import org.blockchain.node.model.Block;
import org.blockchain.node.model.BlockData;
import org.blockchain.node.model.Node;
import org.blockchain.node.model.Transaction;
import org.blockchain.util.Utils;

import com.google.gson.Gson;

/**
 * @author Yulian Yordanov
 * Created: Jun 13, 2018
 */
public class BlockFactory {
	public static Block getGenesisBlock() {
		Block block = new Block();
		
		block.setIndex(0);
		block.setTransactions(new Transaction[] {TransactionFactory.generateGenesisTransaction()});
		block.setDifficulty((short)0);
		block.setPrevBlockHash(Utils.HASH_ZERO);
		block.setMinedBy(Utils.ADDRESS_ZERO);
		block.setNonce(0);
		block.setDateCreated(Utils.GENESIS_DATE);		
		block.setBlockDataHash(getBlockDataHash(block));		
		block.setBlockHash(getBlockHash(block));
		block.setComulativeDifficulty(new BigInteger("0"));
		
		return block;
	}
	
	public static String getBlockHash(Block block) {
		return Cryptography.SHA256(block.getBlockDataHash() + block.getNonce() + block.getDateCreated());
	}
	
	public static Block getMinerBlock(Node node, String address) {
		Block block = new Block();
		
		block.setIndex(node.getLastBlock().getIndex()+1);
		block.setTransactions(TransactionFactory.getPendingTransactions(node, TransactionFactory.generateCoinbaseTransaction(address), block.getIndex()));
		block.setDifficulty(Utils.DIFFICULTY);
		block.setPrevBlockHash(node.getLastBlock().getBlockHash());
		block.setMinedBy(address);
		block.setBlockDataHash(getBlockDataHash(block));
		block.setComulativeDifficulty(node.getLastBlock().getComulativeDifficulty().add(new BigInteger("2").pow(Utils.DIFFICULTY)));
		
		return block;
	}
	
	public static BlockData getBlockData(Block block) {
		BlockData blockData = new BlockData();
		
		blockData.setIndex(block.getIndex());
		blockData.setTransactions(block.getTransactions());
		blockData.setDifficulty(block.getDifficulty());
		blockData.setPrevBlockHash(block.getPrevBlockHash());
		blockData.setMinedBy(block.getMinedBy());
		
		return blockData;
	}
	
	public static String getBlockDataHash(Block block) {
		BlockData blockData = getBlockData(block);
		
		Gson gson = new Gson();
		return Cryptography.SHA256(gson.toJson(blockData));		
	}
	
	public static boolean validateBlock(Node node, Block block, boolean fromMiner) {
		if ( block.getIndex() == 0 && block.getBlockHash().equals(node.getChainId()) ) {
			return true;
		}
		
		String blockHash = getBlockHash(block);
		if ( !blockHash.equals(block.getBlockHash()) ) {
			return false;
		}
		
		String difficultyZeros = Utils.getDifficultyZeros(block.getDifficulty());		
		if ( !blockHash.startsWith(difficultyZeros) ) {
			return false;
		}
		
		Block prevBlock = node.getBlockchain().get(block.getPrevBlockHash());
		if ( prevBlock != null ) {
			if ( prevBlock.getComulativeDifficulty().add(new BigInteger("2").pow(block.getDifficulty())).compareTo(block.getComulativeDifficulty()) != 0 ) {
				return false;
			}
		}
		
		if ( !fromMiner ) {
			BlockData blockData = getBlockData(block);
			
			Gson gson = new Gson();
			String blockDataHash = Cryptography.SHA256(gson.toJson(blockData));
			
			if ( !blockDataHash.equals(block.getBlockDataHash()) ) {
				return false;
			}
			
			Map<String, Long> blockPendinValues = new TreeMap<>();
			
			Transaction coinbaseTransaction = null;
			Transaction[] transactions = block.getTransactions();
			for (Transaction transaction : transactions) {
				try {
					BigInteger bigInteger = new BigInteger(transaction.getFrom(),16);
					if ( bigInteger.compareTo(new BigInteger("0")) == 0 ) {
						if (coinbaseTransaction != null) {
							return false;
						}
						coinbaseTransaction = transaction;
						continue;
					}
				} catch (Exception e) {
					return false;
				}
				
				long blockPendinValue = 0;
				if ( blockPendinValues.get(transaction.getFrom()) != null ) {
					blockPendinValue = blockPendinValues.get(transaction.getFrom());
				}
				
				try {
					TransactionFactory.validateTransaction(node, transaction, blockPendinValue, false);
					blockPendinValue += transaction.getValue() + transaction.getFee();
					blockPendinValues.put(transaction.getFrom(), blockPendinValue);
				} catch (TransactionValidationException e) {
					//e.printStackTrace();
					return false;
				}
				
				if ( transaction.getMinedInBlock() != block.getIndex() ) {
					return false;
				}
			}
			
			if ( coinbaseTransaction == null ) {
				return false;
			}
			
			long amount = 0;			
			for (Transaction transaction : transactions) {
				if ( !transaction.getTransactionDataHash().equals(coinbaseTransaction.getTransactionDataHash()) ) {
					amount += transaction.getFee();
				}
			}
			
			if ( amount + Utils.MINER_REWARD != coinbaseTransaction.getValue() ) {
				return false;
			}			
		}
		
		return true;
	}

	public static long getReward(Block block) {
		long expectedReward = 0;
		Transaction[] transactions = block.getTransactions();
		for (Transaction transaction : transactions) {
			BigInteger bigInteger = new BigInteger(transaction.getFrom(),16);
			if ( bigInteger.compareTo(new BigInteger("0")) == 0 ) {
				expectedReward = transaction.getValue();
				break;
			}
		}
		
		return expectedReward;
	}
}
