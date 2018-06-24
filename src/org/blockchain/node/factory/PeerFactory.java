package org.blockchain.node.factory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.blockchain.crypto.Cryptography;
import org.blockchain.node.model.Block;
import org.blockchain.node.model.Message;
import org.blockchain.node.model.Node;
import org.blockchain.node.model.Transaction;
import org.blockchain.util.HttpResponse;
import org.blockchain.util.Utils;

import com.google.gson.Gson;

/**
 * @author Yulian Yordanov
 * Created: Jun 18, 2018
 */
public class PeerFactory {
	public static void populateTransaction(Node node, String hash) {		
		Message message = new Message(Cryptography.SHA256(node.getNodeId() + hash + System.currentTimeMillis()), node.getNodeId(), hash);
		node.getMessages().put(message.getMessageId(), "");
		populateTransaction(node, message);	
	}
	
	public static void populateTransaction(Node node, Message message) {		
		message.setNodeId(node.getNodeId());
		
		Gson gson = new Gson();
		String json = gson.toJson(message);
		
		for (String url : node.getPeers().values()) {
			url += "/peers/notify-new-transaction/";
			try {
				Utils.sendHttpRequest(url, json);
			} catch (IOException e) {}
		}		
	}
	
	public static void populateBlock(Node node, String hash) {		
		Message message = new Message(Cryptography.SHA256(node.getNodeId() + hash + System.currentTimeMillis()), node.getNodeId(), hash);
		node.getMessages().put(message.getMessageId(), "");
		populateBlock(node, message);	
	}
	
	public static void populateBlock(Node node, Message message) {		
		message.setNodeId(node.getNodeId());
		
		Gson gson = new Gson();
		String json = gson.toJson(message);
		
		for (String url : node.getPeers().values()) {
			url += "/peers/notify-new-block/";
			try {
				Utils.sendHttpRequest(url, json);
			} catch (IOException e) {}
		}		
	}
		
	public static Block[] requestChain(Node node, String nodeId) {
		Gson gson = new Gson();
		
		String url = node.getPeers().get(nodeId);
		url += "/blocks/";
		try {
			HttpResponse httpResponse = Utils.sendHttpRequest(url, "");
			return gson.fromJson(httpResponse.getResponse(), Block[].class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Block requestBlock(Node node, String nodeId, String hash) {
		Gson gson = new Gson();
		
		String url = node.getPeers().get(nodeId);
		url += "/blocks/" + hash + "/";
		
		try {
			HttpResponse httpResponse = Utils.sendHttpRequest(url, "");
			return gson.fromJson(httpResponse.getResponse(), Block.class);
		} catch (Exception e) {}
		
		return null;
	}
	
	public static Transaction requestTransaction(Node node, String nodeId, String hash) {
		Gson gson = new Gson();
		
		String url = node.getPeers().get(nodeId);
		url += "/transactions/" + hash + "/";
		
		try {
			HttpResponse httpResponse = Utils.sendHttpRequest(url, "");
			return gson.fromJson(httpResponse.getResponse(), Transaction.class);
		} catch (Exception e) {}
		
		return null;
	}
	
	public static void populateResetChain(Node node) {
		Message message = new Message(Cryptography.SHA256(node.getNodeId() + "reset-chain" + System.currentTimeMillis()), node.getNodeId(), "");
		node.getMessages().put(message.getMessageId(), "");
		populateResetChain(node, message);	
	}
	
	public static void populateResetChain(Node node, Message message) {		
		message.setNodeId(node.getNodeId());
		Gson gson = new Gson();
		String json = gson.toJson(message);
		
		for (String url : node.getPeers().values()) {
			url += "/peers/notify-reset-chain/";
			try {
				Utils.sendHttpRequest(url, json);
			} catch (IOException e) {}
		}		
	}
	
	public static void connectToPeer(String url1, String url2) {	
		Gson gson = new Gson();
		url1 = url1 + "/peers/connect/";
		try {
			Map<String, String> map = new HashMap<>();
			map.put("url", url2);
			HttpResponse httpResponse = Utils.sendHttpRequest(url1, gson.toJson(map));
			if ( httpResponse.getResponseCode()/100 == 2 ) {
				System.out.println(httpResponse.getResponse());
			} else {
				System.out.println(httpResponse.getResponseCode() + " " + httpResponse.getResponseMessage());
			}
		} catch (IOException e) {}
	}
	
	public static boolean connectedToPeer(Node node, String url) {	
		Gson gson = new Gson();
		url = url + "/peers/connected/";
		try {
			Map<String, String> map = new HashMap<>();
			map.put("url", node.getSelfUrl());
			HttpResponse httpResponse = Utils.sendHttpRequest(url, gson.toJson(map));
			if ( httpResponse.getResponseCode()/100 != 2 ) {
				return false;
			}
			return true;
		} catch (IOException e) {}
		
		return false;
	}
}
