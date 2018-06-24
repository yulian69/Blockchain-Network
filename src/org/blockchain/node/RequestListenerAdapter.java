package org.blockchain.node;


import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.blockchain.crypto.Cryptography;
import org.blockchain.node.exception.TransactionValidationException;
import org.blockchain.node.factory.BalanceFactory;
import org.blockchain.node.factory.BlockFactory;
import org.blockchain.node.factory.BlockchainFactory;
import org.blockchain.node.factory.MinerJobFactory;
import org.blockchain.node.factory.NodeFactory;
import org.blockchain.node.factory.PeerFactory;
import org.blockchain.node.factory.TransactionFactory;
import org.blockchain.node.model.Balance;
import org.blockchain.node.model.Block;
import org.blockchain.node.model.Message;
import org.blockchain.node.model.MinedData;
import org.blockchain.node.model.MinerJob;
import org.blockchain.node.model.Node;
import org.blockchain.node.model.Transaction;
import org.blockchain.util.HttpUtils;
import org.blockchain.util.Utils;
import org.blockchain.webserver.HttpRequest;
import org.blockchain.webserver.RequestListener;

import com.google.gson.Gson;

/**
 * @author Yulian Yordanov
 * Created: Jun 10, 2018
 */
public class RequestListenerAdapter implements RequestListener {
	private Node node;
	
	public RequestListenerAdapter(Node node) {
		this.node = node;
	}
	
	@Override
	public void onRequest(HttpRequest httpRequest) {
		Gson gson = new Gson();
		
		try {			
			if ( httpRequest.isFile() ) {
				HttpUtils.printFile(httpRequest);
				return;
			}
			
			if ( httpRequest.getPath().equals("/balances/") ) {
				printJson(httpRequest, gson.toJson(node.getBalances()));
				return;
			}
			
			if ( httpRequest.getPath().startsWith("/faucet/") ) {
				processFaucet(httpRequest, gson);
				return;
			}
			
			if ( httpRequest.getPath().startsWith("/blocks/") ) {
				processBlocks(httpRequest, gson);
				return;
			}
			
			if ( httpRequest.getPath().startsWith("/transactions/") ) {
				processTransactions(httpRequest, gson);
				return;
			}
			
			if ( httpRequest.getPath().startsWith("/address/") ) {
				processAddresses(httpRequest, gson);
				return;
			}
			
			if ( httpRequest.getPath().startsWith("/mining/") ) {
				processMining(httpRequest, gson);
				return;
			}
			
			if ( httpRequest.getPath().startsWith("/peers/") ) {
				processPeers(httpRequest, gson);
				return;
			}
			
			if ( httpRequest.getPath().startsWith("/debug/") ) {
				processDebug(httpRequest, gson);
				return;
			}
			
			if ( httpRequest.getPath().startsWith("/info/") ) {
				processInfo(httpRequest, gson);
				return;
			}
			
			printNotFound(httpRequest, "Requested resource not found");
		} catch (Exception e) {
			Map<String, String> map = new HashMap<>();
			map.put("errorMsg", e.getMessage());
			try {
				printError(httpRequest, gson.toJson(map));
			} catch (IOException e1) {}
		}
	}
	
	private void processInfo(HttpRequest httpRequest, Gson gson) throws IOException {
		if ( httpRequest.getPath().equals("/info/") ) {
			printJson(httpRequest, gson.toJson(NodeFactory.getNodeInfo(node)));
			return;
		}
		
		printNotFound(httpRequest, "Requested resource not found");
	}
	
	private void processDebug(HttpRequest httpRequest, Gson gson) throws IOException {
		if ( httpRequest.getPath().equals("/debug/reset-chain/") ) {
			Map<String, String> peers = node.getPeers();
			int port = node.getPort();
			node = new Node();
			NodeFactory.initialize(node, port);
			node.setPeers(peers);
			
			System.out.println("The chain was reset to its genesis block");
			
			PeerFactory.populateResetChain(node);
			
			Map<String, String> map = new HashMap<>();
			map.put("message", "The chain was reset to its genesis block");
			printJson(httpRequest, gson.toJson(map));
			return;
		}
		if ( httpRequest.getPath().equals("/debug/") ) {
			printJson(httpRequest, gson.toJson(NodeFactory.getDebugInfo(node)));
			return;
		}
		
		printNotFound(httpRequest, "Requested resource not found");
	}
	
	private void processPeers(HttpRequest httpRequest, Gson gson) throws IOException {
		if ( httpRequest.getPath().equals("/peers/notify-reset-chain/") ) {
			Message message = gson.fromJson(httpRequest.getPostData(), Message.class);
			if ( node.getMessages().get(message.getMessageId()) == null ) {
				Map<String, String> peers = node.getPeers();
				int port = node.getPort();
				node = new Node();
				NodeFactory.initialize(node, port);
				node.setPeers(peers);
				node.getMessages().put(message.getMessageId(), "");
				
				System.out.println("The chain was reset to its genesis block");
				
				PeerFactory.populateResetChain(node, message);
			}			
			printJson(httpRequest, "");
			return;
		}
		
		if ( httpRequest.getPath().equals("/peers/notify-new-block/") ) {
			Message message = gson.fromJson(httpRequest.getPostData(), Message.class);
			if ( node.getMessages().get(message.getMessageId()) == null ) {
				Block block = PeerFactory.requestBlock(node, message.getNodeId(), message.getHash());
				if ( block != null ) {
					if ( BlockchainFactory.addBlock(node, block, message) ) {
						PeerFactory.populateBlock(node, message);
					}					
				}
			}			
			printJson(httpRequest, "");
			return;
		}
		
		if ( httpRequest.getPath().equals("/peers/notify-new-transaction/") ) {
			Message message = gson.fromJson(httpRequest.getPostData(), Message.class);
			if ( node.getMessages().get(message.getMessageId()) == null) {
				Transaction transaction = PeerFactory.requestTransaction(node, message.getNodeId(), message.getHash());
				if ( transaction != null ) {
					try {
						TransactionFactory.validateTransaction(node, transaction, 0, true);
						TransactionFactory.addPendingTransaction(node, transaction);
						PeerFactory.populateTransaction(node, message);
					} catch (TransactionValidationException e) {}
				}
			}			
			printJson(httpRequest, "");
			return;
		}
		
		if ( httpRequest.getPath().equals("/peers/connect/") ) {
			Map<String, String> response = new HashMap<>();
			try {
				Map<String, String> request = gson.fromJson(httpRequest.getPostData(), HashMap.class);
				String url = request.get("url");
				if ( PeerFactory.connectedToPeer(node, url) ) {
					node.getPeers().put(Cryptography.RipeMD160(url), url);
					response.put("message", "Connected to peer " + url);
					printJson(httpRequest, gson.toJson(response));
					return;
				} else {
					response.put("errorMsg", "Unable to connet to peer " + url);
					printJson(httpRequest, gson.toJson(response));
				}
				return;
			} catch (Exception e) {
				response.put("errorMsg", e.getMessage());
				printJson(httpRequest, gson.toJson(response));
				return;
			}
		}
		
		if ( httpRequest.getPath().startsWith("/peers/connected/") ) {
			Map<String, String> response = new HashMap<>();
			try {
				Map<String, String> request = gson.fromJson(httpRequest.getPostData(), HashMap.class);
				String url = request.get("url");
				node.getPeers().put(Cryptography.RipeMD160(url), url);
				response.put("message", "Connected to peer " + url);
				printJson(httpRequest, gson.toJson(response));
				return;				
			} catch (Exception e) {
				response.put("errorMsg", e.getMessage());
				printJson(httpRequest, gson.toJson(response));
				return;
			}
		}
		
		if ( httpRequest.getPath().equals("/peers/") ) {
			printJson(httpRequest, gson.toJson(node.getPeers()));
			return;
		}
		
		printNotFound(httpRequest, "Requested resource not found");
	}
	
	private void processFaucet(HttpRequest httpRequest, Gson gson) throws IOException {
		if ( httpRequest.getPath().startsWith("/faucet/") ) {
			try {
				String[] items = httpRequest.getPath().split("/");
				String address = items[items.length-1];
				
				Map<String, String> map = new HashMap<>();
				
				if ( !TransactionFactory.validateAddress(address) ) {
					map.put("errorMsg", "Invalid address");
					printJson(httpRequest, gson.toJson(map));
					return;
				}
				
				BigInteger bigInteger = new BigInteger(address,16);
				if ( bigInteger.equals(new BigInteger("0")) ) {
					map.put("errorMsg", "Invalid address");
					printJson(httpRequest, gson.toJson(map));
					return;
				}				
				
				Balance balance = BalanceFactory.getBalanceInfo(node, address);
				if (balance.getConfirmedBalance() + balance.getPendingBalance() >= 5*Utils.MICRO_COINS_RATE) {
					map.put("errorMsg", "Your balance is more than 5 coins");
					printJson(httpRequest, gson.toJson(map));
					return;
				}
				Transaction transaction = TransactionFactory.generateFaucetTransaction(address);
				try {
					TransactionFactory.validateTransaction(node, transaction, 0, true);
					TransactionFactory.addPendingTransaction(node, transaction);
					
					PeerFactory.populateTransaction(node, transaction.getTransactionDataHash());
					
					map.put("message", "1 coin is transferred to your address");
					printJson(httpRequest, gson.toJson(map));
				} catch (TransactionValidationException e) {
					map.put("errorMsg", e.getMessage());
					printJson(httpRequest, gson.toJson(map));
				}
				
				//printJson(httpRequest, gson.toJson(block));				
			} catch (Exception e) {
				printNotFound(httpRequest, "Block not found");
			}
			return;
		}
		
		printNotFound(httpRequest, "Requested resource not found");
	}
	
	private void processBlocks(HttpRequest httpRequest, Gson gson) throws IOException {
		if ( httpRequest.getPath().equals("/blocks/") ) {
			printJson(httpRequest, gson.toJson(BlockchainFactory.getBlocks(node)));
			return;
		}
		
		if ( httpRequest.getPath().startsWith("/blocks/") ) {
			try {
				String[] items = httpRequest.getPath().split("/");
				long index = -1;
				try {
					index = Long.parseLong(items[items.length-1]);
				} catch (Exception e) {}
				
				String blockHash = items[items.length-1];
				if ( index >= 0 ) {
					blockHash = node.getBlockchainIds().get(index);
				}
				Block block = node.getBlockchain().get(blockHash);
				if (block != null) {
					printJson(httpRequest, gson.toJson(block));
				} else {
					printNotFound(httpRequest, "Block not found");
				}
			} catch (Exception e) {
				printNotFound(httpRequest, "Block not found");
			}
			return;
		}
		
		printNotFound(httpRequest, "Requested resource not found");
	}
	
	private void processMining(HttpRequest httpRequest, Gson gson) throws IOException {
		if ( httpRequest.getPath().startsWith("/mining/get-mining-job/") ) {
			String[] items = httpRequest.getPath().split("/");
			String address = items[items.length-1];
			if ( !TransactionFactory.validateAddress(address) ) {
				printNotFound(httpRequest, "Invalid address");
				return;
			}
			MinerJob minerJob = MinerJobFactory.requestMiningJob(node, address);
			printJson(httpRequest, gson.toJson(minerJob));
			return;
		}
		
		if ( httpRequest.getPath().equals("/mining/submit-mined-block/") ) {
			MinedData minedData = gson.fromJson(httpRequest.getPostData(), MinedData.class);
			Map<String, String> map = new HashMap<>();
			
			Block block = node.getMiningJobs().get(minedData.getBlockDataHash());
			if ( block != null ) {
				if ( block.getIndex() <= node.getLastBlock().getIndex() ) {
					node.getMiningJobs().remove(minedData.getBlockDataHash());
					map.put("errorMsg", "Block not found or already mined");				
					printJson(httpRequest, gson.toJson(map));
					return;
				}
				block.setBlockHash(minedData.getBlockHash());
				block.setNonce(minedData.getNonce());
				block.setDateCreated(minedData.getDateCreated());
								
				if ( BlockchainFactory.addBlock(node, block, null) ) {
					node.getMiningJobs().remove(minedData.getBlockDataHash());
					PeerFactory.populateBlock(node, block.getBlockHash());
					
					map.put("message", "Block accepted, reward paid: " + BlockFactory.getReward(block) + " microcoins");				
					printJson(httpRequest, gson.toJson(map));
				} else {
					map.put("errorMsg", "Block not validated");				
					printJson(httpRequest, gson.toJson(map));
				}				
			} else {
				map.put("errorMsg", "Block not found or already mined");				
				printJson(httpRequest, gson.toJson(map));
			}
			return;
		}
		printNotFound(httpRequest, "Requested resource not found");
	}

	private void processTransactions(HttpRequest httpRequest, Gson gson) throws IOException {
		if ( httpRequest.getPath().equals("/transactions/confirmed/") ) {
			printJson(httpRequest, gson.toJson(node.getTransactions().values()));
			return;
		}
		
		if ( httpRequest.getPath().equals("/transactions/pending/") ) {
			printJson(httpRequest, gson.toJson(node.getPendingTransactions().values()));
			return;
		}
		
		if ( httpRequest.getPath().equals("/transactions/send/") ) {
			Map<String, String> map = new HashMap<>();
			
			try {
				Transaction transaction = gson.fromJson(httpRequest.getPostData(), Transaction.class);
				TransactionFactory.validateTransaction(node, transaction, 0, true);
				TransactionFactory.addPendingTransaction(node, transaction);
				
				PeerFactory.populateTransaction(node, transaction.getTransactionDataHash());
				
				map.put("transactionDataHash", transaction.getTransactionDataHash());				
				printCreateJson(httpRequest, gson.toJson(map));
			} catch (Exception e) {
				map.put("errorMsg", e.getMessage());
				printJson(httpRequest, gson.toJson(map));
			} catch (TransactionValidationException e) {				
				map.put("errorMsg", e.getErrorMessage());
				printJson(httpRequest, gson.toJson(map));
			}
			return;
		}
		
		if ( httpRequest.getPath().startsWith("/transactions/") ) {
			String[] items = httpRequest.getPath().split("/");
			Transaction transaction = node.getPendingTransactions().get(items[items.length-1]);
			if ( transaction == null ) {
				transaction = node.getTransactions().get(items[items.length-1]);
			}
			if ( transaction != null ) {
				printJson(httpRequest, gson.toJson(transaction));
			} else {
				printNotFound(httpRequest, "Transaction not found");
			}
			return;
		}
		
		printNotFound(httpRequest, "Requested resource not found");
	}
	
	private void processAddresses(HttpRequest httpRequest, Gson gson) throws IOException {
		String[] items =  httpRequest.getPath().split("/");
		
		if (items.length < 4) {
			printNotFound(httpRequest, "Invalid parameters");
			return;
		}
		
		String address = items[2];
		String action = items[3];
		
		if ( action.equals("transactions") ) {
			printJson(httpRequest, gson.toJson(TransactionFactory.getTransactions(node, address)));
			return;
		}
		
		if ( action.equals("balance") ) {
			printJson(httpRequest, gson.toJson(BalanceFactory.getBalanceInfo(node, address)));
			return;
		}
		
		printNotFound(httpRequest, "Invalid parameters");
	}

	private void printJson(HttpRequest httpRequest, String json) throws IOException {
		String responseHeader = HttpUtils.getResponseHeaders(200, "OK", json.length(), "text/json");
		HttpUtils.printResponse(httpRequest.getSocket(), responseHeader, json);
	}
	
	private void printCreateJson(HttpRequest httpRequest, String json) throws IOException {
		String responseHeader = HttpUtils.getResponseHeaders(201, "Created", json.length(), "text/json");
		HttpUtils.printResponse(httpRequest.getSocket(), responseHeader, json);
	}

	private void printNotFound(HttpRequest httpRequest, String message) throws IOException {
		String responseHeader = HttpUtils.getResponseHeaders(400, "Not Found", message.length(), "text/json");
		HttpUtils.printResponse(httpRequest.getSocket(), responseHeader, message);
	}

	public void printError(HttpRequest httpRequest, String json) throws IOException {
		String responseHeader = HttpUtils.getResponseHeaders(400, "Bad Request", json.length(), "text/json");
		HttpUtils.printResponse(httpRequest.getSocket(), responseHeader, json);
	}
}
