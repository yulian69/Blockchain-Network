package org.blockchain.util;

/**
 * @author Yulian Yordanov
 * Created: Jun 15, 2018
 */
public class HttpResponse {
	private int responseCode;
	private String responseMessage;
	private String response;
	
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
}
