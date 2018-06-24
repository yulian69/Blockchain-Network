package org.blockchain.miner;

import java.io.IOException;

import org.blockchain.node.factory.MinerFactory;
import org.blockchain.node.model.MinedData;
import org.blockchain.node.model.MinerJob;
import org.blockchain.util.HttpResponse;
import org.blockchain.util.Utils;

import com.google.gson.Gson;

/**
 * @author Yulian Yordanov
 * Created: Jun 17, 2018
 */
public class MinerMain {
	public static void main(String[] args) {
		if ( args.length < 2 ) {
			System.out.println("Please enter url and miner address");
			return;
		}
		
		System.out.println("Miner started");
		
		String url = args[0];
		String address = args[1];	
		String requestJobUrl = url + "/mining/get-mining-job/" + address + "/";
		String responseJobUrl = url + "/mining/submit-mined-block/";
		
		Gson gson = new Gson();
		
		while (true) {
			try {
				HttpResponse httpResponse = Utils.sendHttpRequest(requestJobUrl, gson.toJson(address));
				if ( httpResponse.getResponseCode()/100 == 2 ) {
					MinerJob minerJob = gson.fromJson(httpResponse.getResponse(), MinerJob.class);
					if ( minerJob.getBlockDataHash() != null ) {
						System.out.println("Index: " + minerJob.getIndex());
						System.out.println("BlockDataHash: " + minerJob.getBlockDataHash());
						System.out.println("Difficulty: " + minerJob.getDifficulty());
						System.out.println("ExpectedReward: " + minerJob.getExpectedReward());						
						System.out.println("RewardAddress: " + minerJob.getRewardAddress());
						System.out.println("TransactionsIncluded: " + minerJob.getTransactionsIncluded());
						
						MinedData minedData = MinerFactory.mine(minerJob);
						
						System.out.println();
						System.out.println("BlockHash: " + minedData.getBlockHash());
						System.out.println("BlockDataHash: " + minedData.getBlockDataHash());
						System.out.println("DateCreated: " + minedData.getDateCreated());
						System.out.println("Nonce: " + minedData.getNonce());
						System.out.println();
						
						HttpResponse httpResponse2 = Utils.sendHttpRequest(responseJobUrl, gson.toJson(minedData));
						System.out.println(httpResponse2.getResponse());
						System.out.println();
						
					}
				}
			} catch (IOException e) {}
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
		}
	}
}
