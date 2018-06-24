package org.blockchain.node;

import java.io.IOException;

import org.blockchain.node.factory.NodeFactory;
import org.blockchain.node.model.Node;
import org.blockchain.webserver.HttpServer;

/**
 * @author Yulian Yordanov
 * Created: Jun 9, 2018
 */
public class HttpServerMain {
	
	public static void main(String[] args) throws IOException {
		if ( args.length == 0 ) {
			System.out.println("Please enter port number");
			return;
		}
		
		int port = 0;		
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("Please enter valid port number");
			return;
		}
		
		Node node = new Node();
		NodeFactory.initialize(node, port);
				
		HttpServer httpServer = new HttpServer(port);
		httpServer.addRequestListener(new RequestListenerAdapter(node));
		httpServer.startWebServer();
		
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
	}
}
