package test;

import java.math.BigInteger;
import java.text.ParseException;

import org.blockchain.crypto.Cryptography;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

/**
 * @author Yulian Yordanov
 * Created: Mar 5, 2018
 */
public class TestBlockchain {

	public static void main(String[] args) throws ParseException {
		String privateKey = (new BigInteger(Cryptography.SHA256("test5"),16)).toString(16);
		ECPoint ecPoint = Cryptography.getPublicKeyFromPrivateKey(privateKey);
		String compressedPibKey = Cryptography.getPublicKeyCompressed(privateKey);
		System.out.println("Private Key: " + privateKey);
		System.out.println("Public Key: " + Hex.toHexString(ecPoint.getXCoord().getEncoded()) + " " + Hex.toHexString(ecPoint.getYCoord().getEncoded()));
		System.out.println("Public Key Compressed: " + compressedPibKey);
		System.out.println("Address: " + Cryptography.getAddress(compressedPibKey));
		
		ecPoint = Cryptography.decompressKey(compressedPibKey);
		System.out.println("Public Key: " + Hex.toHexString(ecPoint.getXCoord().getEncoded()) + " " + Hex.toHexString(ecPoint.getYCoord().getEncoded()));
		
		//ecPoint = Crypto.decompressKey(compressedPibKey, yBit)
		
		/*int port = 5555;
		for (int i = 0; i < 4; i++) {
			String url = "http://localhost:" + port++;
			System.out.println(Cryptography.RipeMD160(url) + " " + url);
		}*/
				
		/*String message = "message";
		BigInteger[] signature = Crypto.signData(privateKey, message.getBytes(StandardCharsets.UTF_8));
		for (BigInteger bigInteger : signature) {
			System.out.println(bigInteger);
		}
		
		ECPublicKeyParameters pubKey = Crypto.getPublicKeyParams(ecPoint.getXCoord().toBigInteger(), ecPoint.getYCoord().toBigInteger());
		boolean verified = Crypto.verifySignature(pubKey, signature, message.getBytes(StandardCharsets.UTF_8));
		
		System.out.println(verified);
		
		System.out.println(ecPoint.getXCoord().toBigInteger().toString(16));
		System.out.println(ecPoint.getYCoord().toBigInteger().toString(16));*/
				
		/*ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");		
		ECNamedCurveSpec params = new ECNamedCurveSpec("secp256k1", spec.getCurve(), spec.getG(), spec.getN());
		java.security.spec.ECPoint point = ECPointUtil.decodePoint(params.getCurve(), "025f81956d5826bad7d30daed2b5c8c98e72046c1ec8323da336445476183fb7ca".getBytes(StandardCharsets.UTF_8));
		*/
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
		
		/*for (int i = 0; i < 10; i++) {
			Transaction transaction = new Transaction();
			
			transaction.setFrom("111111");
			transaction.setTo("2222222");
			transaction.setValue(100);
			transaction.setFee(1);
			transaction.setDateCreated(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
			transaction.setData("Index: " + i);
			transaction.setSenderPubKey("333333333");
			transaction.setTransactionDataHash("" + i);
			transaction.setSenderSignature("66666666666666");
			transaction.setMinedInBlock(-1);
			
			PendingTransactions.addTransaction(transaction);
		}
		
		Transaction[] transactions = PendingTransactions.getTransactions();
		Gson gson = new Gson();
		String json = gson.toJson(transactions);
		System.out.println(json);*/
		
		/*TransactionData transactionData = TransactionFactory.getTransactionData(transaction);
		Gson gson = new Gson();
		String json = gson.toJson(transaction);
		System.out.println(json);
		
		//JsonObject jsonResponse = new JSONObject(resourceResponse);
		transaction = gson.fromJson(json, Transaction.class);
		json = gson.toJson(transaction);
		System.out.println(json);*/

		//BlockchainFactory.initialize();
		
		//System.out.println(Blockchain.getBlockchain().size());
		
		//Block block1 = MiningJobFactory.requestMiningJob("8c18502007646197095099cd78e087017857d7e2");
		//Block block2 = MiningJobFactory.requestMiningJob("b63407e789e83487fcdc6c5b9310dda3e3b5653e");
		
		/*Gson gson = new Gson();
		String json = gson.toJson(block1);
		System.out.println(json);
		json = gson.toJson(block2);
		System.out.println(json);*/
		
		/*String to = "b63407e789e83487fcdc6c5b9310dda3e3b5653e";
		String dateCreated = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
		Transaction transaction = TransactionFactory.getTransaction(Utils.FAUCET_ADDRESS, to, Utils.MICRO_COINS_RATE, 0L, dateCreated, "Requested free coins", Utils.FAUCET_PRIVATE_KEY);
		
		//Transaction transaction2 = TransactionFactory.getGenesisTransaction();
		try {
			TransactionFactory.validateTransaction(transaction);
		} catch (TransactionValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Gson gson = new Gson();
		String json = gson.toJson(transaction);
		System.out.println(json);*/
		
		
	}
	
	/*private static void testBlockchain() {
		Web3j web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/hjYOEvj1Io7oPrzu5t0p"));
		Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
		String clientVersion = web3ClientVersion.getWeb3ClientVersion();
		//EthGetBalance ethGetBalance = web3j.ethGetBalance("0xe5b5354838bc79f49c97ce0bb9b053e0aec0a877", DefaultBlockParameterName.LATEST).sendAsync().get();

		//BigInteger wei = ethGetBalance.getBalance();
		System.out.println(clientVersion);
		
		//String walletFileName = WalletUtils.generateFullNewWalletFile("123456",new File("."));
		//System.out.println(walletFileName);
	}*/
}
