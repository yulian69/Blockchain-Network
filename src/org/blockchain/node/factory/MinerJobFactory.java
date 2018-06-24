package org.blockchain.node.factory;

import org.blockchain.node.model.Block;
import org.blockchain.node.model.MinerJob;
import org.blockchain.node.model.Node;

/**
 * @author Yulian Yordanov
 * Created: Jun 13, 2018
 */
public class MinerJobFactory {
	public static MinerJob requestMiningJob(Node node, String address) {
		MinerJob minerJob = new MinerJob();
		
		if ( node.getPendingTransactions().size() == 0 ) {
			return minerJob;
		}
		
		Block block = BlockFactory.getMinerBlock(node, address);
		node.getMiningJobs().put(block.getBlockDataHash(), block);
		
		long expectedReward = BlockFactory.getReward(block);
				
		minerJob.setIndex(block.getIndex());
		minerJob.setTransactionsIncluded(block.getTransactions().length);
		minerJob.setDifficulty(block.getDifficulty());
		minerJob.setExpectedReward(expectedReward);
		minerJob.setRewardAddress(address);
		minerJob.setBlockDataHash(block.getBlockDataHash());
		
		minerJob.setBlockDataHash(block.getBlockDataHash());
		
		return minerJob;
	}
	
}
