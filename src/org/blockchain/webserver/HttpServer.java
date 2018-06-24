package org.blockchain.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Yulian Yordanov
 * Created: Jun 9, 2018
 */
public class HttpServer extends Thread{
	private int port;
	private ServerSocket serverSocket;
	private boolean isRunning = true;
	private RequestListener requestListener;
	
	public HttpServer(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			
			System.out.println("Running web server on port " + port);
			
			while (isRunning) { 
			    try {
			        Socket socket = serverSocket.accept();             
			        ProcessRequest processRequest = new ProcessRequest(socket, requestListener);
			        processRequest.start();
			    } catch (Exception e) {
			    	e.printStackTrace();
			    }
			}   
			
			System.out.println("Web server on port " + port + " stopped.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startWebServer() {
		this.setDaemon(true);
		this.start();
	}
	
	public void stopWebServer() {
		isRunning = false;
	}
	
	public void addRequestListener(RequestListener requestListener) {
		this.requestListener = requestListener;
	}
}
