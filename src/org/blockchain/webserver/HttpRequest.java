package org.blockchain.webserver;

import java.net.Socket;

/**
 * @author Yulian Yordanov
 * Created: Jun 9, 2018
 */
public class HttpRequest {
	Socket socket;
	private String postData;
	private boolean methodPost;
	private boolean methodGet;
	private boolean file;
	private String fileName;
	private String path;
	
	public HttpRequest(Socket socket, Parameters headers, String postData) {
		this.socket = socket;
		this.postData = postData;
		init(headers);
	}

	private void init(Parameters headers) {
		String method = headers.getValueNotNull("") ;
		String[] parts = method.split(" ");
		
		if (parts.length >= 2 ) {
			path = parts[1];
			
			if ( parts[0].equals("GET") ) {
				methodGet = true;
				if ( !path.endsWith("/") ) {
					file = true;
					fileName = path;
				}
			}
			
			if ( parts[0].equals("POST") ) {
				methodPost = true;
			}
		}
	}
	
	public Socket getSocket() {
		return socket;
	}
	public String getHeadersData() {
		return getHeadersData();
	}
	public String getPostData() {
		return postData;
	}
	public boolean isMethodPost() {
		return methodPost;
	}
	public boolean isMethodGet() {
		return methodGet;
	}	
	public String getFileName() {
		return fileName;
	}
	public boolean isFile() {
		return file;
	}
	public String getPath() {
		return path;
	}
}
