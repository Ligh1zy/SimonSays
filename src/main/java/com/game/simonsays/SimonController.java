package com.game.simonsays;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

public class SimonController {
    @FXML
    private Label welcomeText;

    @FXML
    private Pane menuPanel; // Primary Panel

    @FXML
    private Pane gamePanel; // Game Panel

    @FXML
    private ListView<String> highscoreList; // HighScore list Panel

    @FXML
    public void initializePane()
    {
// Make sure the game pane is off when the main menu is on
        menuPanel.setVisible(true);
        gamePanel.setVisible(false);
    }
    @FXML
    protected void onPLAYButtonClick() {
        menuPanel.setVisible(false);
        gamePanel.setVisible(true);
        welcomeText.setText("You are here!.............");
    }
    @FXML
    protected void onHighScoresButtonClick()
    {
        welcomeText.setText("It worked!!!!");
    }
    @FXML
    protected void onExitButtonClick()
    {
        System.exit(0);
    }

}