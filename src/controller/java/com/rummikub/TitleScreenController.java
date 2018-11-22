package com.rummikub;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.sun.javafx.geom.AreaOp.AddOp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class TitleScreenController implements Initializable {
	@FXML
	private VBox root;
	@FXML
	private ComboBox<String> cb_PlayerCount;
	@FXML
	private ComboBox<String> cb_Player1;
	@FXML
	private ComboBox<String> cb_Player2;
	@FXML
	private ComboBox<String> cb_Player3;
	@FXML
	private ComboBox<String> cb_Player4;
	@FXML
	private VBox vb_PlayerStrategies;
	@FXML
	private Button btn_Play;

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		cb_PlayerCount.getItems().addAll("2", "3", "4");
		cb_Player1.getItems().addAll("Human", "Strategy 1", "Strategy 2", "Strategy 3");
		cb_Player2.getItems().addAll("Human", "Strategy 1", "Strategy 2", "Strategy 3");
		cb_Player3.getItems().addAll("Human", "Strategy 1", "Strategy 2", "Strategy 3");
		cb_Player4.getItems().addAll("Human", "Strategy 1", "Strategy 2", "Strategy 3");
		
		for (int i = 0; i < vb_PlayerStrategies.getChildren().size(); i++) {
			vb_PlayerStrategies.getChildren().get(i).setVisible(false);
		}
	}
	
	@FXML
	public void handlePlayerCountCB(ActionEvent event) {
		int numPlayers = Integer.parseInt(cb_PlayerCount.getValue());	
		
		for (int i = 0; i < vb_PlayerStrategies.getChildren().size(); i++) {
			ComboBox<String> currNode = (ComboBox<String>) vb_PlayerStrategies.getChildren().get(i); 
			
			if (i < numPlayers) {
				currNode.setVisible(true);
			}
			else {
				currNode.setVisible(false);
			}
			currNode.setValue("Select");
			
		}

		btn_Play.setDisable(true);
	}
	
	@FXML
	public void handleStrategyCB(ActionEvent event) {
		boolean check = true;
		int numPlayers = Integer.parseInt(cb_PlayerCount.getValue());	
		for (int i = 0; i < numPlayers; i++) {
			ComboBox<String> currNode = (ComboBox<String>) vb_PlayerStrategies.getChildren().get(i);
			if (currNode.getValue() == "Select") check = false;
		}
		
		if (check) {
			btn_Play.setDisable(false);
		}
		else {
			btn_Play.setDisable(true);
		}
	}
	
	@FXML
	public void handlePlayBtn(ActionEvent event) throws Exception {
		int numPlayers = Integer.parseInt(cb_PlayerCount.getValue());	
		List<Player> players = new ArrayList<Player>(); 
		boolean error = false;

		for (int i = 0; i < numPlayers; i++) {
			ComboBox<String> currNode = (ComboBox<String>) vb_PlayerStrategies.getChildren().get(i);
			
			switch (currNode.getValue()) {
			case "Human":
				players.add(new Player("p" + i, new Strategy0())); 
				break;
			case "Strategy 1":
				players.add(new Player("p" + i, new Strategy1())); 
				break;
			case "Strategy 2":
				players.add(new Player("p" + i, new Strategy2())); 
				break;
			case "Strategy 3":
				players.add(new Player("p" + i, new Strategy3())); 
				break;
				//case "Strategy 4":
				//players.add(new Player("p" + i, new Strategy4())); 
				//break;
			default:
				error = true;
			}
		}

		if (!error) {
			Game game = new Game(players);
			
			// Get the event's source stage, and set the scene to be the game.
			Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
			stage.setScene(Rummy.loadScene("MainScreen.fxml"));
			
			try {
				game.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			Print.print("Unknown strategy selected.");
		}

	}
	
	
}