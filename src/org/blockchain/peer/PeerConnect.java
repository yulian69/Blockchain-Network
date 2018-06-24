package org.blockchain.peer;

import org.blockchain.node.factory.PeerFactory;

/**
 * @author Yulian Yordanov
 * Created: Jun 21, 2018
 */
public class PeerConnect {

	public static void main(String[] args) {
		if ( args.length < 2 ) {
			System.out.println("Please enter peers url");
			return;
		}
		String url1 = "";
		String url2 = "";		
		if (args.length > 1) {
			url1 = args[0];
			url2 = args[1];
		}
		
		PeerFactory.connectToPeer(url1, url2);
	}
}
