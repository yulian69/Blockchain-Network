package org.blockchain.node.factory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.blockchain.crypto.Cryptography;
import org.blockchain.node.model.MinedData;
import org.blockchain.node.model.MinerJob;
import org.blockchain.util.Utils;

/**
 * @author Yulian Yordanov
 * Created: Jun 17, 2018
 */
public class MinerFactory {
	
	public static MinedData mine(MinerJob minerJob) {
		long nonce = 0;
		String blockHash = "";		
		String difficultyZeros = Utils.getDifficultyZeros(minerJob.getDifficulty());		
		String dateCreated = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
		
		while ( true ) {
			blockHash = Cryptography.SHA256(minerJob.getBlockDataHash() + nonce + dateCreated);
			if ( blockHash.startsWith(difficultyZeros) ) {
				break;
			}
			if ( nonce == Long.MAX_VALUE) {
				dateCreated = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
				nonce = 0;
			} else {
				nonce++;
			}
		}
		
		return new MinedData(minerJob.getBlockDataHash(), dateCreated, nonce, blockHash);
	}
}
