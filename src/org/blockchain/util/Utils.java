package org.blockchain.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * @author Yulian Yordanov
 * Created: Jun 13, 2018
 */
public class Utils {
	public final static String ADDRESS_ZERO = "0000000000000000000000000000000000000000";
	public final static String HASH_ZERO = "0000000000000000000000000000000000000000000000000000000000000000";
	public final static String PUBLIC_KEY_ZERO = "00000000000000000000000000000000000000000000000000000000000000000";
	public final static String SIGNATURE_ZERO = "0000000000000000000000000000000000000000000000000000000000000000";
	public final static String FAUCET_ADDRESS = "8c18502007646197095099cd78e087017857d7e2";
	public final static String FAUCET_PRIVATE_KEY = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";
	public final static long MICRO_COINS_RATE = 1000000L; // 1 coin
	public final static long INITIAL_COINS = 1000000L*MICRO_COINS_RATE; // 1 000 000 coins
	public final static String GENESIS_DATE = "2018-06-13T14:00:22.479Z";
	public final static String GENESIS_DATA = "Genesis TX";
	public final static int TRANSACTION_FEE = 10;
	public final static short DIFFICULTY = 5;	
	public final static long MINER_REWARD = 5*MICRO_COINS_RATE; // 5 coins
	public final static long FAUCET_COINS = 1*MICRO_COINS_RATE; // 1 coin
	
	public static HttpResponse sendRequest(String sUrl, String json) throws KeyManagementException, NoSuchAlgorithmException, IOException {
    	if ( sUrl.toLowerCase().startsWith("https") ) {
    		return sendHttpsRequest(sUrl, json);
    	}
    	return sendHttpRequest(sUrl, json);
    }

    public static HttpResponse sendHttpRequest(String sUrl, String json) throws IOException {    	
		URL url = new URL(sUrl);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setConnectTimeout(5000);
		connection.setRequestProperty("Content-Type", "text/json");
											
		connection.setRequestProperty("Content-length", ""+json.length());
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(json);
		writer.flush();
		writer.close();
		
		int responseCode = connection.getResponseCode();
		String responseMessage = connection.getResponseMessage();
		
		if ( responseCode / 100 != 2 ) {
			HttpResponse httpResponse = new HttpResponse();
			httpResponse.setResponseCode(responseCode);
			httpResponse.setResponseMessage(responseMessage);
			httpResponse.setResponse("");
			
			return httpResponse;
		}
		
		String response = "";
		String line = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while ( (line = reader.readLine()) != null ) {
			response += line;
		}	
		reader.close();		
		connection.disconnect();
		
		HttpResponse httpResponse = new HttpResponse();
		httpResponse.setResponseCode(responseCode);
		httpResponse.setResponseMessage(responseMessage);
		httpResponse.setResponse(response);
		
		return httpResponse;
	}
    
    public static HttpResponse sendHttpsRequest(String sUrl, String json) throws NoSuchAlgorithmException, IOException, KeyManagementException {
		URL url = new URL(sUrl);

		SSLContext sc = SSLContext.getInstance("TLSv1.2");
		sc.init(null, null, new java.security.SecureRandom());
		
		HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
		httpsURLConnection.setSSLSocketFactory(sc.getSocketFactory());
		httpsURLConnection.setRequestMethod("POST");
		httpsURLConnection.setUseCaches(false);
		httpsURLConnection.setDoInput(true);
		httpsURLConnection.setDoOutput(true);
		httpsURLConnection.setConnectTimeout(5000);
		httpsURLConnection.setRequestProperty("Content-Type", "text/json");
		
		httpsURLConnection.setRequestProperty("Content-length", ""+json.length());
		OutputStreamWriter writer = new OutputStreamWriter(httpsURLConnection.getOutputStream());
		writer.write(json);
		writer.flush();
		writer.close();
			
		int responseCode = httpsURLConnection.getResponseCode();
		String responseMessage = httpsURLConnection.getResponseMessage();
		
		if ( responseCode / 100 != 2 ) {
			HttpResponse httpResponse = new HttpResponse();
			httpResponse.setResponseCode(responseCode);
			httpResponse.setResponseMessage(responseMessage);
			httpResponse.setResponse("");
			
			return httpResponse;
		}
		
		String response = "";
		String line = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
		while ( (line = reader.readLine()) != null ) {
			response += line;
		}	
		reader.close();		
		httpsURLConnection.disconnect();
			
		HttpResponse httpResponse = new HttpResponse();
		httpResponse.setResponseCode(responseCode);
		httpResponse.setResponseMessage(responseMessage);
		httpResponse.setResponse(response);
		
		return httpResponse;
	}
    
    public static String getDifficultyZeros(short difficulty) {
		String diffZeros = "";
		for (int i = 0; i < difficulty; i++) {
			diffZeros += "0";
		}
		return diffZeros;
	}
}
