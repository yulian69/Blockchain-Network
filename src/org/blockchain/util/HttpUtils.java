package org.blockchain.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.blockchain.webserver.HttpRequest;

/**
 * @author Yulian Yordanov
 * Created: Jun 10, 2018
 */
public class HttpUtils {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss 'GMT'");
    
	public static void printFile(HttpRequest httpRequest) throws IOException {
		try {
			String html = loadHTML("./"+httpRequest.getFileName());
			String responseHeader = getResponseHeaders(200, "OK", html.length(), "text/html");
			printResponse(httpRequest.getSocket(), responseHeader, html);
		} catch (Exception e) {
			//e.printStackTrace();
			String message = "File not found!";
			String responseHeader = getResponseHeaders(401, message, message.length(), "text/html");
			printResponse(httpRequest.getSocket(), responseHeader, message);			
		}
	}
	
	public static String loadHTML(String fileName) throws IOException {
		char[] cbuf = new char[1024]; 
		StringBuffer buffer = new StringBuffer();
		FileReader fileReader = new FileReader(fileName);
		int read;
		while ( (read = fileReader.read(cbuf)) > 0 ) {
			buffer.append(cbuf, 0, read);
		}	
		fileReader.close();
		return buffer.toString();
	}
	
	public static String getResponseHeaders(int responseCode, String responseMessage, int contentLength, String contentType) {
		String responseHeader = "HTTP/1.1 " + responseCode + " " + responseMessage + "\n"
				+ "Date:" + dateFormat.format(new Date()) + "\n"
				+ "Server: Test Web Server\n"
				+ "Last-Modified: Sat, 09 Jun 2018 08:23:00 GMT\n"
				+ "Content-Length: " +  contentLength + "\n"
				+ "Content-Type: " +  contentType + "\n"
				+ "Connection: Closed\n\n";
		return responseHeader;
	}

	public static void printResponse(Socket socket, String header, String message) throws IOException {
		OutputStream outputStream = socket.getOutputStream();
		outputStream.write(header.getBytes());
		outputStream.write(message.getBytes());
		outputStream.flush();
        socket.close();
    }
}
