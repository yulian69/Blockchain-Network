package org.blockchain.webserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author Yulian Yordanov
 * Created: Jun 9, 2018
 */
public class ProcessRequest extends Thread {
	private Socket socket;
	private RequestListener requestListener;
	
    public ProcessRequest(Socket socket, RequestListener requestListener) throws IOException {
    	this.socket = socket;    	
    	this.requestListener = requestListener;
	}
    
	@Override
	public void run() {
		try {
			Parameters headers = processRequestData(socket);
			String postData = headers.getValueNotNull("Post-Data");		
			headers.getMap().remove("Post-Data");
			
			HttpRequest httpRequest = new HttpRequest(socket, headers, postData);
			requestListener.onRequest(httpRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Parameters processRequestData(Socket socket) throws IOException {
		InputStream inputStream = socket.getInputStream();
		
		int buffSize = 1024;
		byte[] bytes = new byte[buffSize];
		StringBuffer buffer = new StringBuffer();
		int read;
		while ( (read = inputStream.read(bytes)) == buffSize ) {
			buffer.append(new String(bytes, 0, read, StandardCharsets.UTF_8));
		}
		
		if ( read >= 0 ) {
			buffer.append(new String(bytes, 0, read, StandardCharsets.UTF_8));
		}
		
		String request = buffer.toString().replaceAll("\r", "");
		
		Parameters headers = new Parameters();
		String postData = "";
		String[] items = request.split("\n\n");
		if ( items.length > 1 ) {
			postData = items[1];
		}
		
		String[] lines = items[0].split("\n");
		for (String line : lines) {
			String[] parts = line.split(": ");
			if ( parts.length > 1 ) {				
				headers.addValue(parts[0], parts[1]);
			} else {
				headers.addValue("", line);
			}
		}
		
		int contentLength = headers.getValueAsInt("Content-Length");
		if ( postData.length() == 0 && contentLength > 0 ) {
			buffer = new StringBuffer();
			while ( (read = inputStream.read(bytes)) == buffSize ) {
				buffer.append(new String(bytes, 0, read, StandardCharsets.UTF_8));
			}
			
			if ( read >= 0 ) {
				buffer.append(new String(bytes, 0, read, StandardCharsets.UTF_8));
			}
			
			postData = buffer.toString();
		}
		
		headers.addValue("Post-Data", postData);
		
		return headers;
	}
	
	
}
