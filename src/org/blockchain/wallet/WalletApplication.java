package org.blockchain.wallet;
	
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.blockchain.crypto.Cryptography;
import org.blockchain.node.factory.TransactionFactory;
import org.blockchain.node.model.Balance;
import org.blockchain.node.model.Crypto;
import org.blockchain.node.model.Kdfparams;
import org.blockchain.node.model.Transaction;
import org.blockchain.node.model.Wallet;
import org.blockchain.node.model.WalletData;
import org.blockchain.util.HttpResponse;
import org.blockchain.util.Utils;
import org.bouncycastle.util.encoders.Hex;

import com.google.gson.Gson;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


public class WalletApplication extends Application {
	private String[] nodes = new String[] {"http://localhost:5555", "http://localhost:5556", "http://localhost:5557", "http://localhost:5558"};
	private List<Wallet> wallets = new ArrayList<>(); 
	
	NumberFormat format = NumberFormat.getInstance();
	private Gson gson = new Gson();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			/*Wallet wallet = new Wallet();
			wallet.setAddress("8c18502007646197095099cd78e087017857d7e2");
			wallet.setPrivateKey("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08");
			wallets.add(wallet);
			
			wallet = new Wallet();
			wallet.setAddress("b63407e789e83487fcdc6c5b9310dda3e3b5653e");
			wallet.setPrivateKey("1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014");
			wallets.add(wallet);
			*/
			
			format.setMaximumFractionDigits(0);
			
			BorderPane root = new BorderPane();
		    Scene scene = new Scene(root, 800, 600, Color.WHITE);

		    MenuBar menuBar = new MenuBar();
		    menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		    root.setTop(menuBar);

		    Menu menuWallet = new Menu("Wallet");
		    Menu menuTransactions = new Menu("Transactions");
		    
		    MenuItem menuItemCreate = new MenuItem("Create Wallet");
		    MenuItem menuItemUnlock = new MenuItem("Unlock Wallet");
		    MenuItem menuItemBalance = new MenuItem("Check Balance");
		    MenuItem menuItemTransaction = new MenuItem("Send Transaction");
		    
		    menuItemUnlock.setOnAction(new EventHandler<ActionEvent>() {
		    	@Override
				public void handle(ActionEvent event) {		
		    		GridPane gridpane = new GridPane();
		    		gridpane.setPadding(new Insets(10));
				    gridpane.setHgap(10);
				    gridpane.setVgap(3);
				    
				    Stage stage = new Stage();
				    
				    TextField textFile = new TextField();
				    textFile.setMinWidth(500);
				    
				    Button button = new Button("Select File");
				    GridPane.setHalignment(button, HPos.RIGHT);
				    			    
				    Label labelPassword = new Label("Password");
				    GridPane.setHalignment(labelPassword, HPos.RIGHT);
				    
				    PasswordField textPassword = new PasswordField();
				    textPassword.setMinWidth(200);
				    
				    Button buttonUnlock = new Button("Unlock Wallet");
				    Button buttonExit = new Button("Exit");
				    
				    gridpane.add(button, 0, 0);
					gridpane.add(textFile, 1, 0);
					
					gridpane.add(labelPassword, 0, 1);
					gridpane.add(textPassword, 1, 1);
					
					gridpane.add(new Label(""), 0, 2);
					gridpane.add(new Label(""), 1, 2);
					
					HBox hBox = new HBox(10);
					hBox.getChildren().add(buttonUnlock);
					hBox.getChildren().add(buttonExit);
					hBox.setAlignment(Pos.CENTER);
					
					VBox vBox = new VBox();
					vBox.getChildren().add(gridpane);
					vBox.getChildren().add(hBox);
					
					Scene scene = new Scene(vBox,600,135);
							    		   
	    		    stage.setScene(scene);
	    		    stage.setTitle("Unlock Wallet");
	    		    stage.initModality(Modality.APPLICATION_MODAL);
	    		    
	    		    buttonExit.setOnAction(new EventHandler<ActionEvent>() {
				    	@Override
						public void handle(ActionEvent event) {
							stage.close();							
						}
					});
	    		    
	    		    button.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							FileChooser fileChooser = new FileChooser();
				            fileChooser.setTitle("Open File");
				            fileChooser.setInitialDirectory(new File("C:/Users/x/Desktop/wallets"));
				            
				            File file = fileChooser.showOpenDialog(stage);
				            textFile.setText(file.getAbsolutePath());
						}
					});
	    		    
	    		    buttonUnlock.setOnAction(new EventHandler<ActionEvent>() {
				    	@Override
						public void handle(ActionEvent event) {
				    		
				    		Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Error");
							
				    		if ( textPassword.getText().length() == 0 ) {
				    			alert.setContentText("Please enter wallet password");
								alert.showAndWait();
								return;
				    		} else if ( textPassword.getText().length() < 8 ) {
				    			alert.setContentText("Wallet password is too short");
								alert.showAndWait();
								return;
				    		} 
				    		
				    		String pass = textPassword.getText();
				    		
				    		
				    		try {
				    			String json = "";
				    			String line;
					    		BufferedReader bufferedReader = new BufferedReader(new FileReader(textFile.getText()));
					    		while ( (line = bufferedReader.readLine()) != null ) {
					    			json += line;
					    		}
					    		bufferedReader.close();
					    					
				    			Gson gson = new Gson();
				    			
				    			WalletData walletData = gson.fromJson(json, WalletData.class);
				    			
					    		String mac = Hex.toHexString(Cryptography.SCrypt(pass, walletData.getCrypto().getKdfparams()));
					    		if ( !mac.equals(walletData.getCrypto().getMac()) ) {
					    			alert.setContentText("Invalid wallet password");
									alert.showAndWait();
									return;
					    		}
					    		
					    		String password = pass;
					    		while ( password.length() < 16 ) {
					    			password += pass;
					    		}
					    		password = password.substring(0, 16);
					    		
					    		byte[] privateKey = Cryptography.decryptTwofish(Hex.decode(walletData.getCrypto().getCiphertext()), password, Hex.decode(walletData.getCrypto().getIv()));
					    		
					    		Wallet wallet = new Wallet();
					    		wallet.setAddress(walletData.getAddress());
					    		wallet.setPrivateKey(Hex.toHexString(privateKey));
					    		
					    		boolean exists = false;
					    		for (Wallet wallet2 : wallets) {
									if (wallet.getAddress().equals(wallet2.getAddress())) {
										exists = true;
										break;
									}
								}
					    		if ( !exists ) {
					    			wallets.add(wallet);
					    		}
					    		
					    		Alert alert2 = new Alert(AlertType.INFORMATION);
					    		alert2.setTitle("Unlock wallet");
					    		alert2.setContentText("Wallet successfully unlocked!");
					    		alert2.showAndWait();
								//byte[] encrypted = Cryptography.encryptTwofish(privateKey, password, iv);
								
								stage.close();
							} catch (Exception e) {
								alert.setContentText(e.getMessage());
								alert.showAndWait();
							} 
						}
					});
	    		    
	    		    stage.show();
				}
			});
		    
		    menuItemCreate.setOnAction(new EventHandler<ActionEvent>() {
		    	@Override
				public void handle(ActionEvent event) {		
		    		GridPane gridpane = new GridPane();
		    		gridpane.setPadding(new Insets(10));
				    gridpane.setHgap(10);
				    gridpane.setVgap(3);
				    
				    Label labelPassword = new Label("Wallet Password");
				    GridPane.setHalignment(labelPassword, HPos.RIGHT);
				    
				    PasswordField textPassword = new PasswordField();
				    textPassword.setMinWidth(200);
				    
				    Button buttonCreate = new Button("Create Wallet");
				    Button buttonExit = new Button("Exit");
				    
					gridpane.add(labelPassword, 0, 0);
					gridpane.add(textPassword, 1, 0);					
					
					gridpane.add(buttonCreate, 0, 1);
					gridpane.add(buttonExit, 1, 1);
										
					HBox hBox = new HBox(10);
					hBox.getChildren().add(buttonCreate);
					hBox.getChildren().add(buttonExit);
					hBox.setAlignment(Pos.CENTER);
					
					VBox vBox = new VBox();
					vBox.getChildren().add(gridpane);
					vBox.getChildren().add(hBox);
					
					Scene scene = new Scene(vBox,350,80);
					Stage stage = new Stage();
		    		   
	    		    stage.setScene(scene);
	    		    stage.setTitle("Create Wallet");
	    		    stage.initModality(Modality.APPLICATION_MODAL);
	    		    
	    		    buttonExit.setOnAction(new EventHandler<ActionEvent>() {
				    	@Override
						public void handle(ActionEvent event) {
							stage.close();							
						}
					});
	    		    
	    		    buttonCreate.setOnAction(new EventHandler<ActionEvent>() {
				    	@Override
						public void handle(ActionEvent event) {
				    		
				    		Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Error");
							
				    		if ( textPassword.getText().length() == 0 ) {
				    			alert.setContentText("Please enter wallet password");
								alert.showAndWait();
								return;
				    		} else if ( textPassword.getText().length() < 8 ) {
				    			alert.setContentText("Wallet password is too short");
								alert.showAndWait();
								return;
				    		} 
				    		
				    		String pass = textPassword.getText();
				    						    						    						    		
				    		byte[] salt = new byte[32];
				    		byte[] iv = new byte[16];
				    		byte[] privateKey = new byte[32];
				    		
				    		SecureRandom random = new SecureRandom();
				    		random.nextBytes(salt);
				    		random.nextBytes(iv);
				    		random.nextBytes(privateKey);
				    		
				    		Kdfparams kdfparams = new Kdfparams();
				    		kdfparams.setDklen(32);
				    		kdfparams.setN(16384);
				    		kdfparams.setP(1);
				    		kdfparams.setR(16);
				    		kdfparams.setSalt(Hex.toHexString(salt));
				    		
				    		String mac = Hex.toHexString(Cryptography.SCrypt(pass, kdfparams));
				    		
				    		String password = pass;
				    		while ( password.length() < 16 ) {
				    			password += pass;
				    		}
				    		password = password.substring(0, 16);
				    		
				    		try {
								byte[] encrypted = Cryptography.encryptTwofish(privateKey, password, iv);
								
								Crypto crypto = new Crypto();
								crypto.setCipher("twofish-128-cbc");
								crypto.setCiphertext(Hex.toHexString(encrypted));
								crypto.setIv(Hex.toHexString(iv));
								crypto.setKdf("scrypt");
								crypto.setKdfparams(kdfparams);
								crypto.setMac(mac);
								
								WalletData walletData = new WalletData();
								walletData.setAddress(Cryptography.getAddress(Cryptography.getPublicKeyCompressed(Hex.toHexString(privateKey))));
								walletData.setId(UUID.randomUUID().toString());
								walletData.setVersion("1.0");
								walletData.setCrypto(crypto);
								
								Gson gson = new Gson();
																
								String json = gson.toJson(walletData);
								String filename = "UTC--" + ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT) + "--" + Cryptography.RipeMD160(json) + ".json";
								filename = filename.replaceAll(":", "-");
								
								FileChooser fileChooser = new FileChooser();
					            fileChooser.setTitle("Save File");
					            fileChooser.setInitialDirectory(new File("C:/Users/x/Desktop/wallets"));
					            fileChooser.setInitialFileName(filename);
					            
					            File file = fileChooser.showSaveDialog(stage);
								
								FileWriter fileWriter = new FileWriter(file);
								fileWriter.write(json);
								fileWriter.flush();
								fileWriter.close();
								
								Alert alert2 = new Alert(AlertType.INFORMATION);
					    		alert2.setTitle("Create wallet");
					    		alert2.setContentText("Wallet created successfully!");
					    		alert2.showAndWait();
								
								stage.close();
							} catch (Exception e) {
								alert.setContentText(e.getMessage());
								alert.showAndWait();
							} 
						}
					});
	    		    
	    		    stage.show();
				}
			});

		    menuItemTransaction.setOnAction(new EventHandler<ActionEvent>() {
		    	@Override
				public void handle(ActionEvent event) {		
		    		GridPane gridpane = new GridPane();
		    		gridpane.setPadding(new Insets(10));
				    gridpane.setHgap(10);
				    gridpane.setVgap(3);
				    
				    Label labelNode = new Label("Node");
				    GridPane.setHalignment(labelNode, HPos.RIGHT);
				   
					ObservableList<String> listNode = FXCollections.observableArrayList();
					for (String node : nodes) {
						listNode.add(node);
					}
					ComboBox<String> comboNode = new ComboBox<>(listNode);
					comboNode.setValue(nodes[0]);
					comboNode.setMinWidth(420);

				    Label labelAddress = new Label("From");
				    GridPane.setHalignment(labelAddress, HPos.RIGHT);
				   
					ObservableList<String> listAddress = FXCollections.observableArrayList();
					for (Wallet wallet : wallets) {
						listAddress.add(wallet.getAddress());
					}
					ComboBox<String> comboAddress = new ComboBox<>(listAddress);
					comboAddress.setMinWidth(420);
					if (wallets.size() > 0) {
						comboAddress.setValue(wallets.get(0).getAddress());
					}
					
					Label labelTo = new Label("To");
				    GridPane.setHalignment(labelTo, HPos.RIGHT);
				    
				    TextField textTo = new TextField();
				    
				    Label labelValue = new Label("Value (Microcoins)");
				    GridPane.setHalignment(labelValue, HPos.RIGHT);
				    
				    TextField textValue = new TextField();
				    
				    Label labelData = new Label("Data");
				    GridPane.setHalignment(labelData, HPos.RIGHT);
				    
				    TextField textData = new TextField();
				    
				    Label labelTx = new Label("Tx");
				    GridPane.setHalignment(labelTx, HPos.RIGHT);
				    
				    TextField textTx = new TextField();
				    textTx.setEditable(false);
				    textTx.setMinWidth(420);
				    
				    Button buttonSend = new Button("Send Transaction");
				    Button buttonExit = new Button("Exit");
				    				    					
					gridpane.add(labelNode, 0, 0);
					gridpane.add(comboNode, 1, 0);
					gridpane.add(labelAddress, 0, 1);
					gridpane.add(comboAddress, 1, 1);
					
					gridpane.add(labelTo, 0, 2);
					gridpane.add(textTo, 1, 2);
					
					gridpane.add(labelValue, 0, 3);
					gridpane.add(textValue, 1, 3);
					
					gridpane.add(labelData, 0, 4);
					gridpane.add(textData, 1, 4);
					
					gridpane.add(labelTx, 0, 5);
					gridpane.add(textTx, 1, 5);		
					
					gridpane.add(new Label(""), 0, 6);
					gridpane.add(new Label(""), 1, 6);
															
					HBox hBox = new HBox(10);
					hBox.getChildren().add(buttonSend);
					hBox.getChildren().add(buttonExit);
					hBox.setAlignment(Pos.CENTER);
					
					VBox vBox = new VBox();
					vBox.getChildren().add(gridpane);
					vBox.getChildren().add(hBox);
					
					Scene scene = new Scene(vBox,570,250);
					Stage stage = new Stage();
		    		   
	    		    stage.setScene(scene);
	    		    stage.setTitle("Send Transaction");
	    		    stage.initModality(Modality.APPLICATION_MODAL);
	    		    
	    		    buttonExit.setOnAction(new EventHandler<ActionEvent>() {
				    	@Override
						public void handle(ActionEvent event) {
							stage.close();							
						}
					});
	    		    
	    		    buttonSend.setOnAction(new EventHandler<ActionEvent>() {
				    	@Override
						public void handle(ActionEvent event) {
				    		textTx.setText("");
				    		
				    		Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Error");
							
				    		if ( textTo.getText().length() != 40 ) {
				    			alert.setContentText("Invalid receipient address");
								alert.showAndWait();
								return;
				    		} 
				    		try {
				    			new BigInteger(textTo.getText(),16);
				    		} catch (Exception e) {
				    			alert.setContentText("Invalid receipient address");
								alert.showAndWait();
								return;
							}
				    		try {
				    			Long.parseLong(textValue.getText());
				    		} catch (Exception e) {
				    			alert.setContentText("Invalid value");
								alert.showAndWait();
								return;
							}
				    		
				    		String url = comboNode.getValue() + "/transactions/send/";
				    		
				    		String from = comboAddress.getValue();
				    		String to = textTo.getText();
				    		long value = Long.parseLong(textValue.getText());
				    		int fee = Utils.TRANSACTION_FEE;
				    		String dateCreated = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
				    		String data = textData.getText();
				    		String senderPrivateKey = "";
				    		for (Wallet wallet : wallets) {
								if (wallet.getAddress().equals(from)) {
									senderPrivateKey = wallet.getPrivateKey();
									break;
								}
							}
				    		Map<String, String> map = new HashMap<>();
							try {
								Transaction transaction = TransactionFactory.generateTransaction(from, to, value, fee, dateCreated, data, senderPrivateKey);
								/*
								String s = transaction.getSenderSignature()[0].substring(0, 1);
								if (s.equals("0")) {
									s = "1";
								} else {
									s = "0";
								}
								transaction.setSenderSignature(new String[]{s+transaction.getSenderSignature()[0].substring(1),transaction.getSenderSignature()[1]});
								*/
								HttpResponse httpResponse = Utils.sendHttpRequest(url, gson.toJson(transaction));
								if (httpResponse.getResponseCode()/100 != 2) {
									System.out.println("Response:" + httpResponse.getResponse());
									String errorMsg = null;
									try {
										map = gson.fromJson(httpResponse.getResponse(), HashMap.class);
										errorMsg = map.get(errorMsg);
									} catch (Exception e) {}
									alert.setContentText((errorMsg == null) ? httpResponse.getResponseMessage() : errorMsg);
									alert.showAndWait();
								} else {
									
									map = gson.fromJson(httpResponse.getResponse(), HashMap.class);
									if ( map.get("transactionDataHash") != null ) {
										textTx.setText(map.get("transactionDataHash"));
									} else {
										alert.setContentText(map.get("errorMsg"));
										alert.showAndWait();
									}
								}
							} catch (IOException e) {								
								alert.setContentText(e.getMessage());
								alert.showAndWait();
							}
						}
					});
	    		    
	    		    stage.show();
				}
			});
		    
		    menuItemBalance.setOnAction(new EventHandler<ActionEvent>() {
		    	@Override
				public void handle(ActionEvent event) {		
		    		GridPane gridpane = new GridPane();
		    		gridpane.setPadding(new Insets(10));
				    gridpane.setHgap(10);
				    gridpane.setVgap(3);
				    
				    Label labelNode = new Label("Node");
				    GridPane.setHalignment(labelNode, HPos.RIGHT);
				   
					ObservableList<String> listNode = FXCollections.observableArrayList();
					for (String node : nodes) {
						listNode.add(node);
					}
					ComboBox<String> comboNode = new ComboBox<>(listNode);
					comboNode.setValue(nodes[0]);
					comboNode.setMinWidth(300);

				    Label labelAddress = new Label("Address");
				    GridPane.setHalignment(labelAddress, HPos.RIGHT);
				   
					ObservableList<String> listAddress = FXCollections.observableArrayList();					
					for (Wallet wallet : wallets) {
						listAddress.add(wallet.getAddress());
					}
					ComboBox<String> comboAddress = new ComboBox<>(listAddress);
					comboAddress.setMinWidth(300);
					if (wallets.size() > 0) {
						comboAddress.setValue(wallets.get(0).getAddress());
					}
					
					Label labelSafe = new Label("Safe Balance (Microcoins)");
				    GridPane.setHalignment(labelSafe, HPos.RIGHT);
				    
				    TextField textSafe = new TextField();
				    textSafe.setEditable(false);
				    
				    Label labelConfirmed = new Label("Confirmed Balance (Microcoins)");
				    GridPane.setHalignment(labelConfirmed, HPos.RIGHT);
				    
				    TextField textConfirmed = new TextField();
				    textConfirmed.setEditable(false);
				    
				    Label labelPending = new Label("Pending Balance (Microcoins)");
				    GridPane.setHalignment(labelPending, HPos.RIGHT);
				    
				    TextField textPending = new TextField();
				    textPending.setEditable(false);
				    
				    Button buttonCheck = new Button("Check Balance");
				    Button buttonExit = new Button("Exit");
				    				    					
					gridpane.add(labelNode, 0, 0);
					gridpane.add(comboNode, 1, 0);
					gridpane.add(labelAddress, 0, 1);
					gridpane.add(comboAddress, 1, 1);
					
					gridpane.add(labelSafe, 0, 2);
					gridpane.add(textSafe, 1, 2);
					
					gridpane.add(labelConfirmed, 0, 3);
					gridpane.add(textConfirmed, 1, 3);
					
					gridpane.add(labelPending, 0, 4);
					gridpane.add(textPending, 1, 4);
					
					gridpane.add(new Label(""), 0, 5);
					gridpane.add(new Label(""), 1, 5);
					
					HBox hBox = new HBox(10);
					hBox.getChildren().add(buttonCheck);
					hBox.getChildren().add(buttonExit);
					hBox.setAlignment(Pos.CENTER);
					
					VBox vBox = new VBox();
					vBox.getChildren().add(gridpane);
					vBox.getChildren().add(hBox);
					
					Scene scene = new Scene(vBox,500,220);
					Stage stage = new Stage();
		    		   
	    		    stage.setScene(scene);
	    		    stage.setTitle("Check Balance");
	    		    stage.initModality(Modality.APPLICATION_MODAL);
	    		    
	    		    buttonExit.setOnAction(new EventHandler<ActionEvent>() {
				    	@Override
						public void handle(ActionEvent event) {
							stage.close();
							
						}
					});
	    		    
	    		    buttonCheck.setOnAction(new EventHandler<ActionEvent>() {
				    	@Override
						public void handle(ActionEvent event) {
				    		textSafe.setText("");
							textConfirmed.setText("");
							textPending.setText("");
							
							String url = comboNode.getValue() + "/address/" + comboAddress.getValue() + "/balance/";
							
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Error");
							
							try {
								HttpResponse httpResponse = Utils.sendHttpRequest(url, "");
								if (httpResponse.getResponseCode()/100 != 2) {
									alert.setContentText(httpResponse.getResponseMessage());
									alert.showAndWait();									
								} else {
									Balance balance = gson.fromJson(httpResponse.getResponse(), Balance.class);
									
									textSafe.setText(format.format(balance.getSafeBalance()));
									textConfirmed.setText(format.format(balance.getConfirmedBalance()));
									textPending.setText(format.format(balance.getPendingBalance()));
								}
							} catch (IOException e) {								
								alert.setContentText(e.getMessage());
								alert.showAndWait();
							}
						}
					});
	    		    
	    		    stage.show();
				}
			});
		    
		    menuWallet.getItems().addAll(menuItemCreate, menuItemUnlock);
		    menuTransactions.getItems().addAll(menuItemBalance, menuItemTransaction);

		    menuBar.getMenus().addAll(menuWallet);
		    menuBar.getMenus().addAll(menuTransactions);

		    primaryStage.setTitle("My Wallet");
		    primaryStage.setScene(scene);
		    primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
