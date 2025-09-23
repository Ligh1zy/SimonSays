package com.game.simonsays;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimonController {
    @FXML
    private Label statusLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Pane menuPanel; // Primary Panel

    @FXML
    private Pane gamePanel; // Game Panel

    @FXML
    private ListView<String> highscoreList; // HighScore list Panel
// Every Arc with the colors
    @FXML private Arc arcGreen;
    @FXML private Arc arcRed;
    @FXML private Arc arcYellow;
    @FXML private Arc arcBlue;
// Game variables
    private List<Arc>  colorSequence = new ArrayList<>();
    private int currentStep = 0;
    private int score = 0;
    private Random random = new Random();
    private boolean isPlayingSequence = false;

    @FXML
    public void initialize() {
// Link button presses to effects
        arcGreen.setOnMouseClicked(event -> handlePlayerAction(arcGreen));
        arcRed.setOnMouseClicked(event -> handlePlayerAction(arcRed));
        arcYellow.setOnMouseClicked(event -> handlePlayerAction(arcYellow));
        arcBlue.setOnMouseClicked(event -> handlePlayerAction(arcBlue));
    }
// Method for handling the players moves(Including clicks and mistakes)
    private void handlePlayerAction(Arc clickedArc)
    {
        if (isPlayingSequence) return;
        flashArc(clickedArc);
// Input Validation for right move
        if (clickedArc == colorSequence.get(currentStep))
        {
            currentStep++;
// Input Validation for right sequence
            if (currentStep >= colorSequence.size())
            {
                score++;
                scoreLabel.setText(String.valueOf(score));
                addNewColorToSequence();
                PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
                pause.setOnFinished(event -> playSequence(0));
                pause.play();
            }
        } else {
            gameOver();
        }
    }
// Method to make an arc "flash" with Bloom effect
    private void flashArc(Arc arc) {
        Bloom bloom = new Bloom();
        bloom.setThreshold(0.1); // lower threshold = brighter effect
        arc.setEffect(bloom);

// Pause for 0.5 seconds, then remove effect
        PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
        pause.setOnFinished(event -> arc.setEffect(null));
        pause.play();
    }
// Method to play the color sequences(Via recursion)
private void playSequence(int index) {
// If placed to check if player can interact
    if (index >= colorSequence.size()) {
        isPlayingSequence = false;
        currentStep = 0;
        return;
    }
    isPlayingSequence = true;
    Arc arc = colorSequence.get(index);
    flashArc(arc);
// Wait and show next Color
    PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
    pause.setOnFinished(event -> playSequence(index + 1));
    pause.play();
}
// Add the color to the sequence
    private void addNewColorToSequence() {
        int randomColor = random.nextInt(4);
        switch (randomColor) {
            case 0: colorSequence.add(arcGreen);
            break;
            case 1: colorSequence.add(arcRed);
            break;
            case 2: colorSequence.add(arcYellow);
            break;
            case 3: colorSequence.add(arcBlue);
            break;
        }
    }
    // Game start
    private void startNewGame() {
        colorSequence.clear();
        score = 0;
        currentStep = 0;
        scoreLabel.setText("0");

// Start with 1 color
        addNewColorToSequence();

// Short pause before starting
        PauseTransition pause = new PauseTransition(Duration.seconds(1.0));
        pause.setOnFinished(event -> playSequence(0));
        pause.play();
    }
// If the player gets it wrong
    private void gameOver() {
        statusLabel.setText("Game Over! Score: " + score);
        menuPanel.setVisible(true);
        gamePanel.setVisible(false);
    }
    @FXML
    public void initializePane()
    {
// Make sure the game pane is off when the main menu is on
        menuPanel.setVisible(true);
        gamePanel.setVisible(false);
    }
    @FXML
    protected void onPLAYButtonClick() { // To Start the game
        menuPanel.setVisible(false);
        gamePanel.setVisible(true);
        startNewGame();
    }
    @FXML
    protected void onHighScoresButtonClick()
    {
        statusLabel.setText("It worked!!!!");
    }
    @FXML
    protected void onExitButtonClick()
    {
        System.exit(0);
    }
}