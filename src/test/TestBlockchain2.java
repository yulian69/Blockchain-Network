package test;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.blockchain.node.factory.TransactionFactory;
import org.blockchain.node.model.Transaction;
import org.blockchain.util.HttpResponse;
import org.blockchain.util.Utils;

import com.google.gson.Gson;

/**
 * @author Yulian Yordanov
 * Created: Jun 14, 2018
 */
public class TestBlockchain2 {

	public static void main(String[] args) {
		String from = "8c18502007646197095099cd78e087017857d7e2";
		String to = "72d1c23991ccf47b3beeae6f3de995f567348bf0";
		long value = 5*Utils.MICRO_COINS_RATE;
		int fee = Utils.TRANSACTION_FEE;
		String dateCreated = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
		String data = "Test";
		String senderPrivateKey = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";
		
		Gson gson = new Gson();
		
		try {
			
			Transaction transaction = TransactionFactory.generateTransaction(from, to, value, fee, dateCreated, data, senderPrivateKey);
			HttpResponse httpResponse = Utils.sendHttpRequest("http://localhost:5555/transactions/send/", gson.toJson(transaction));
			
			System.out.println("ReponseCode: " + httpResponse.getResponseCode());
			System.out.println("ReponseMessage: " + httpResponse.getResponseMessage());
			System.out.println("Reponse: " + httpResponse.getResponse());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
