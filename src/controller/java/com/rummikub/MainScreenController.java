package com.rummikub;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class MainScreenController implements Initializable {

	/*
	 * main Pane
	 */
	@FXML
	private AnchorPane root;

	/*
	 * player variables
	 */
	@FXML
	private FlowPane player0_pane;
	@FXML
	private FlowPane player1_pane;
	@FXML
	private FlowPane player2_pane;
	@FXML
	private FlowPane player3_pane;
	@FXML
	private Label player0_label;
	@FXML
	private Label player1_label;
	@FXML
	private Label player2_label;
	@FXML
	private Label player3_label;
	@FXML
	private Rectangle player0_rectangle;
	@FXML
	private Rectangle player1_rectangle;
	@FXML
	private Rectangle player2_rectangle;
	@FXML
	private Rectangle player3_rectangle;

	/*
	 * board variables
	 */
	@FXML
	private Pane table_pane;
	@FXML
	private Rectangle startGameRectangle;
	@FXML
	private Button startGameButton;
	@FXML
	private Button nextTurnButton;
	@FXML
	private Rectangle background;
	@FXML
	private Rectangle table_rect;
	@FXML
	private Rectangle stock_rect;
	@FXML
	private FlowPane stock_pane;

	private List<FlowPane> playerPanes = new ArrayList<FlowPane>();
	private List<Label> playerLabels = new ArrayList<Label>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		init_background_images();

		List<Rectangle> playerRectangles = new ArrayList<Rectangle>();

		for (FlowPane flowPane : playerPanes) {
			flowPane.setPadding(new Insets(10, 10, 10, 10));
			flowPane.setVgap(4);
			flowPane.setHgap(4);
		}

		playerPanes.add(player0_pane);
		playerPanes.add(player1_pane);
		playerPanes.add(player2_pane);
		playerPanes.add(player3_pane);
		playerLabels.add(player0_label);
		playerLabels.add(player1_label);
		playerLabels.add(player2_label);
		playerLabels.add(player3_label);

		playerRectangles.add(player0_rectangle);
		playerRectangles.add(player1_rectangle);
		playerRectangles.add(player2_rectangle);
		playerRectangles.add(player3_rectangle);

		Rummy.game.start();
		playerLabels.get(Rummy.game.currentPlayer.getNumber()).setStyle("-fx-font-weight: bold");
		int max = Rummy.game.players.size();
		while (playerPanes.size() > max) {
			playerPanes.get(max).setVisible(false);
			playerPanes.remove(max);
			playerLabels.get(max).setVisible(false);
			playerLabels.remove(max);
			playerRectangles.get(max).setVisible(false);
			playerRectangles.remove(max);
		}

		for (int i = 0; i < playerPanes.size(); i++) {
			viewTiles(Rummy.game.players.get(i), playerPanes.get(i));
		}
	}

	@FXML
	public void handleNextTurn(ActionEvent event) throws Exception {
		if (startGameButton.isVisible()) {
			startGameButton.setVisible(false);
			startGameRectangle.setVisible(false);
			nextTurnButton.setDisable(false);
		}

		takeTurn();
	}

	public void takeTurn() throws Exception {
		if (Rummy.game.rigDraw && Rummy.game.shouldDraw) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Choose a tile");
			alert.setContentText("Would you like to choose a tile from the stock to draw?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				stock_rect.setVisible(true);
				stock_pane.setVisible(true);
				viewTiles(stock_pane);

			} else {
				Print.println(Rummy.game.previousPlayer.getName() + " draws a tile from the stock: "
						+ Rummy.game.previousPlayer.getPlayerRack().takeTile(Rummy.game.stock).toString());
				Rummy.game.shouldDraw = false;
				nextTurnButton.setText("Next Turn");
			}
		} else {
			nextTurnButton.setDisable(true);
			if (Rummy.game.gameRunning) {
				Rummy.game.takeTurn();
				playerLabels.get(Rummy.game.currentPlayer.getNumber()).setStyle("-fx-font-weight: bold");
				playerLabels.get(Rummy.game.previousPlayer.getNumber()).setStyle("-fx-font-weight: normal");

				if (Rummy.game.rigDraw && Rummy.game.shouldDraw) {
					nextTurnButton.setText("Draw Tile");
				}
			}
		}
		viewTiles(Rummy.game.previousPlayer, playerPanes.get(Rummy.game.previousPlayer.getNumber()));
		viewTiles(Rummy.game.table, table_pane);
		nextTurnButton.setDisable(false);
	}

	/*
	 * tile images for player rack
	 */
	public void viewTiles(Player currPlayer, FlowPane pane) {
		pane.getChildren().clear();

		for (Tile tile : currPlayer.getPlayerRack().getRackArray()) {
			Image img = tile.getTileImage();
			if (!Rummy.game.printRackMeld && !currPlayer.isHuman()) {
				img = new Image(Constants.BACK_CARD);
			}

			ImageView tileImg = new ImageView(img);
			tileImg.setPreserveRatio(true);
			tileImg.setFitWidth(35);
			pane.getChildren().add(tileImg);
		}
	}

	/*
	 * tile/meld images for table
	 */
	public void viewTiles(Table table, Pane pane) {
		pane.getChildren().clear();
		double x_axis = 0;
		double y_axis = 0;
		double imgWidth = 35;

		for (Meld meld : table.getAllMelds()) {
			if (x_axis + meld.getTiles().size() * imgWidth >= pane.getWidth()) {
				y_axis += 50;
				x_axis = 0;
			}
			for (Tile tile : meld.getMeld()) {
				Image img = tile.getTileImage();
				ImageView tileImg = new ImageView(img);
				tileImg.setPreserveRatio(true);
				tileImg.setFitWidth(imgWidth);

				tileImg.relocate(x_axis, y_axis);
				pane.getChildren().add(tileImg);
				x_axis += imgWidth;

			}
			x_axis += (imgWidth + 5);
		}
	}

	/*
	 * tile images of stock
	 */
	public void viewTiles(FlowPane pane) {
		pane.getChildren().clear();

		int i = 0;
		for (Tile tile : Rummy.game.stock.getStockArray()) {
			Image img = tile.getTileImage();
			ImageView tileImg = new ImageView(img);

			tileImg.setPreserveRatio(true);
			tileImg.setFitWidth(35);
			tileImg.setId(Integer.toString(i));
			tileImg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					Rummy.game.stock.getStockArray().remove(tile);

					Rummy.game.previousPlayer.getPlayerRack().addTile(tile);
					Print.println(
							Rummy.game.previousPlayer.getName() + " draws a tile from the stock: " + tile.toString());

					stock_pane.getChildren().clear();
					stock_pane.setVisible(false);
					stock_rect.setVisible(false);

					nextTurnButton.setText("Next Turn");
					Rummy.game.shouldDraw = false;
					viewTiles(Rummy.game.previousPlayer, playerPanes.get(Rummy.game.previousPlayer.getNumber()));
					viewTiles(Rummy.game.table, table_pane);

				}
			});
			pane.getChildren().add(tileImg);
			i++;
		}
	}

	public void init_background_images() {
		background.setFill(new ImagePattern(new Image(Constants.BACKGROUND_IMG)));
		player0_rectangle.setFill(new ImagePattern(new Image(Constants.PLAYER_TABLE_IMG)));
		player2_rectangle.setFill(new ImagePattern(new Image(Constants.PLAYER_TABLE_IMG)));
		player1_rectangle.setFill(new ImagePattern(new Image(Constants.PLAYER_TABLE_IMG)));
		player3_rectangle.setFill(new ImagePattern(new Image(Constants.PLAYER_TABLE_IMG)));
		table_rect.setFill(new ImagePattern(new Image(Constants.TABLE_IMG)));
	}

}