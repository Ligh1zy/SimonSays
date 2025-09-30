package com.game.simonsays;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.Node;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.shape.Line;
import javafx.animation.FadeTransition;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimonController {
    @FXML
    private Label finalScoreLabel;

    @FXML
    private Label savePromptLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Pane menuPanel; // Primary Panel

    @FXML
    private Pane gamePanel; // Game Panel

    @FXML
    private Pane highscorePanel; // HighScore Panel

    @FXML
    private ListView<String> highscoreListView;

    @FXML
    private Pane gameOverPanel; // Game over Panel
// Every Arc with the colors
    @FXML private Arc arcGreen;
    @FXML private Arc arcRed;
    @FXML private Arc arcYellow;
    @FXML private Arc arcBlue;
// Every line for the 3 letters
    @FXML private Line line1;
    @FXML private Line line2;
    @FXML private Line line3;
// Game variables
    private boolean scoreSaved = false;
    private List<Arc>  colorSequence = new ArrayList<>();
    private int currentStep = 0;
    private int score = 0;
    private Random random = new Random();
    private boolean isPlayingSequence = false;
    private int currentLetterIndex = 0;
    private List<String> highScores = new ArrayList<>();
// Name of player using the common 3 letter username
    @FXML private Label letter1;
    @FXML private Label letter2;
    @FXML private Label letter3;
// Audio Variables
    private AudioClip greenSound;
    private AudioClip redSound;
    private AudioClip yellowSound;
    private AudioClip blueSound;
    private AudioClip gameOverSound;
    private AudioClip replaySound;
    private AudioClip exitSound;
    private MediaPlayer backgroundMusic;
// Every variable for the animations of every button, letter and lines
    @FXML private Text titleS;
    @FXML private Text titleI;
    @FXML private Text titleM;
    @FXML private Text titleO;
    @FXML private Text titleN;
    @FXML private Text titleSS;
    @FXML private Text titleA;
    @FXML private Text titleY;
    @FXML private Text titleSSS;
    @FXML private Text titleExcl;

    @FXML private Button playButton;
    @FXML private Button exitButton;
    @FXML private Button highScoresButton;
    @FXML private Button mainMenuButton;
    @FXML private Button replayButton;
    @FXML private Button exit2Button;

    @FXML private Line lineRed;
    @FXML private Line lineYellow;
    @FXML private Line lineBlue;
    @FXML private Line lineGreen;

    private Timeline titleAnimation;
    private FadeTransition blinkTransition;
// Method for the title animations
    private void startTitleAnimation() {
        List<Node> titleLetters = List.of(titleS, titleI, titleM, titleO, titleN, titleSS, titleA,
                titleY, titleSSS, titleExcl, lineBlue, lineRed, lineYellow, lineGreen);

        titleAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.5), event -> {
                    int randomIndex = random.nextInt(titleLetters.size());
                    Node randomLetter = titleLetters.get(randomIndex);

                    Bloom bloom = new Bloom();
                    bloom.setThreshold(0.2);
                    randomLetter.setEffect(bloom);

                    PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
                    pause.setOnFinished(e -> randomLetter.setEffect(null));
                    pause.play();
                })
        );

        titleAnimation.setCycleCount(Timeline.INDEFINITE);
        titleAnimation.play();
    }
// Methods for the blinking animation
    private void startBlinking(Line line) {
        stopBlinking(); // Stop previous blinking
        blinkTransition = new FadeTransition(Duration.seconds(0.5), line);
        blinkTransition.setFromValue(1.0);
        blinkTransition.setToValue(0.0);
        blinkTransition.setCycleCount(FadeTransition.INDEFINITE);
        blinkTransition.setAutoReverse(true);
        blinkTransition.play();
    }
    private void stopBlinking() {
        if (blinkTransition != null) {
            blinkTransition.stop();
            blinkTransition.getNode().setOpacity(1.0); // Reset to visible
        }
    }
// Method for setting the letters
    private void setLetter(int index, String letter) {
        switch (index) {
            case 0 -> letter1.setText(letter);
            case 1 -> letter2.setText(letter);
            case 2 -> letter3.setText(letter);
        }
    }
// Class for the Hover effect on each button
    private void buttonHoverEffect() {
        List<Button> buttons = new ArrayList<>();
        if (playButton != null) buttons.add(playButton);
        if (highScoresButton != null) buttons.add(highScoresButton);
        if (exitButton != null) buttons.add(exitButton);
        if (mainMenuButton != null) buttons.add(mainMenuButton);
        if (replayButton != null) buttons.add(replayButton);
        if (exit2Button != null) buttons.add(exit2Button);

        for (Button button : buttons) {
            button.setOnMouseEntered(event -> {
                Bloom bloom = new Bloom();
                bloom.setThreshold(0.3);
                button.setEffect(bloom);
            });

            button.setOnMouseExited(event -> {
                button.setEffect(null);
            });
        }
    }
// Method for saving highscores
    private void saveHighScore(String playerName, int score) {
        String entry = playerName + "     " + score;
        highScores.add(entry);

        try {
            FileWriter writer = new FileWriter("highscores.txt", true);
            writer.write(entry + System.lineSeparator());
            writer.close();
            System.out.println("Successfully wrote file");

        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
    @FXML
    public void initialize() {
        initializeSounds();
        initializePane();
        buttonHoverEffect();
// Link button presses to effects
        arcGreen.setOnMouseClicked(event -> handlePlayerAction(arcGreen));
        arcRed.setOnMouseClicked(event -> handlePlayerAction(arcRed));
        arcYellow.setOnMouseClicked(event -> handlePlayerAction(arcYellow));
        arcBlue.setOnMouseClicked(event -> handlePlayerAction(arcBlue));
// Capture typing when Game Over panel is focused
        gameOverPanel.setOnKeyPressed(event -> {
            if (scoreSaved) return;
            switch (event.getCode()) {
                case ENTER -> {
                    if (currentLetterIndex > 0) {
                        String playerName = letter1.getText().trim() + letter2.getText().trim() +
                                letter3.getText().trim();
                        saveHighScore(playerName, score);
                        stopBlinking();
                        savePromptLabel.setText("Score saved!");
                        scoreSaved = true;
                    }
                }
                case BACK_SPACE -> {
                    if (currentLetterIndex > 0) {
                        currentLetterIndex--;
                        setLetter(currentLetterIndex, " ");
                        switch (currentLetterIndex) {
                            case 0 -> startBlinking(line1);
                            case 1 -> startBlinking(line2);
                            case 2 -> startBlinking(line3);
                        }
                    }
                }
            }
        });
        gameOverPanel.setOnKeyTyped(event -> {
            if (scoreSaved) return;
            String input = event.getCharacter().toUpperCase();
            if (input.matches("[A-Z]") && currentLetterIndex < 3) {
                setLetter(currentLetterIndex, input);
                currentLetterIndex++;
                switch (currentLetterIndex) {
                    case 1 -> startBlinking(line2);
                    case 2 -> startBlinking(line3);
                    case 3 -> stopBlinking();
                }
            }
        });
        gameOverPanel.setFocusTraversable(true);
    }
// Methods for handling the sounds of the game
    public void initializeSounds() {
        try {
            System.out.println("Starting to load sounds...");
            greenSound = new AudioClip(getClass().getResource("/sounds/green_sound.mp3").toString());
            redSound = new AudioClip(getClass().getResource("/sounds/red_sound.mp3").toString());
            yellowSound = new AudioClip(getClass().getResource("/sounds/yellow_sound.mp3").toString());
            blueSound = new AudioClip(getClass().getResource("/sounds/blue_sound.mp3").toString());
            gameOverSound = new AudioClip(getClass().getResource("/sounds/incorrect_sound.mp3").toString());
            replaySound = new AudioClip(getClass().getResource("/sounds/replay_sound.mp3").toString());
            exitSound = new AudioClip(getClass().getResource("/sounds/shutdown_sound.mp3").toString());

            String musicPath = getClass().getResource("/sounds/background_music.mp3").toString();
            Media backgroundMedia = new Media(musicPath);
            backgroundMusic = new MediaPlayer(backgroundMedia);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // Loooooop
            backgroundMusic.setVolume(1.0);

            System.out.println("Sounds loaded.");
        } catch (Exception e) {
            System.out.println("Error loading sounds: " + e.getMessage());
            // runs even if failed
        }
    }
    private void playBackgroundMusic() {
        if (backgroundMusic != null) {
            PauseTransition delay = new PauseTransition(Duration.seconds(0.1));
            delay.setOnFinished(event -> {
                backgroundMusic.stop();
                backgroundMusic.play();
            });
            delay.play();
        }
    }
    private void pauseBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.pause();
        }
    }
    private void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
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

        if (arc == arcGreen && greenSound != null) {
            greenSound.play();
        } else if (arc == arcRed && redSound != null) {
            redSound.play();
        } else if (arc == arcYellow && yellowSound != null) {
            yellowSound.play();
        } else if (arc == arcBlue && blueSound != null) {
            blueSound.play();
        }
// Pause for 0.3 seconds, then remove effect
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
        }
        else {
            isPlayingSequence = true;
            Arc arc = colorSequence.get(index);
            flashArc(arc);
// Wait and show next Color
            PauseTransition pause = new PauseTransition(Duration.seconds(0.7));
            pause.setOnFinished(event -> playSequence(index + 1));
            pause.play();
        }
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
// Display the highscores
    private void showHighscores()
    {
// Change list style
        highscoreListView.setStyle("""
     -fx-background-color: transparent;
    -fx-control-inner-background: transparent;
    -fx-text-fill: white;
    -fx-font-family: 'Showcard Gothic';
    -fx-font-size: 15px;
    -fx-text-alignment: center;
    """);
        highscoreListView.getItems().clear();
        sortHighscores();
        try {
            List<String> lines = Files.readAllLines(Paths.get("highscores.txt"));
            highscoreListView.getItems().addAll(lines);
        } catch (IOException e) {
            highscoreListView.getItems().add("No highScores added.");
        }
    }
// Method for handling the ajustment of the highscores
    private void sortHighscores()
    {
        try {
            List<String> lines = Files.readAllLines(Paths.get("highscores.txt"));
            lines.sort((line1, line2) -> {
                int score1 = Integer.parseInt(line1.replaceAll("\\D", ""));
                int score2 = Integer.parseInt(line2.replaceAll("\\D", "")); // Only compare numbers
                return Integer.compare(score2, score1); // Descending order
            });
// Keep only 10 highcores
            if (lines.size() > 10) {
                lines = lines.subList(0, 10);
            }
            Files.write(Paths.get("highscores.txt"), lines);
        } catch (IOException e) {
            System.out.println("Error sorting highscores: " + e.getMessage());
        }
        catch (NumberFormatException e) {
            System.out.println("Error parsing scores: " + e.getMessage());
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
        if (gameOverSound != null) {
            gameOverSound.play();
        }
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(event -> {
            finalScoreLabel.setText("Score: " + score);   // <- Display score
            savePromptLabel.setText("");                  // <- Reset saved prompt
        menuPanel.setVisible(false);
        gamePanel.setVisible(false);
        highscorePanel.setVisible(false);
        gameOverPanel.setVisible(true);
// Reset letter slots
        setLetter(0, " ");
        setLetter(1, " ");
        setLetter(2, " ");
        currentLetterIndex = 0;

        scoreSaved = false;

        // Ensure panel gets keyboard focus
        gameOverPanel.requestFocus();

        // Start blinking first line
        startBlinking(line1);
    });
        pause.play();
    }
    @FXML
    public void initializePane()
    {
// Make sure the game pane is off when the main menu is on
        menuPanel.setVisible(true);
        gamePanel.setVisible(false);
        highscorePanel.setVisible(false);
        gameOverPanel.setVisible(false);
        startTitleAnimation();
        playBackgroundMusic();
    }
    @FXML
    protected void onPLAYButtonClick() { // To Start the game
        stopBackgroundMusic();
        menuPanel.setVisible(false);
        gamePanel.setVisible(true);
        gameOverPanel.setVisible(false);
        startNewGame();
    }
    @FXML
    protected void onHighScoresButtonClick()
    {
        menuPanel.setVisible(false);
        gamePanel.setVisible(false);
        highscorePanel.setVisible(true);
        gameOverPanel.setVisible(false);
        showHighscores();
    }
    @FXML
    protected void onMainMenuButtonClick()
    {
        menuPanel.setVisible(true);
        gamePanel.setVisible(false);
        highscorePanel.setVisible(false);
        gameOverPanel.setVisible(false);
// Restart title animation and music when returning to menu
        startTitleAnimation();
        playBackgroundMusic();

        // Reset any game over state
        scoreSaved = false;
        stopBlinking();
    }
    @FXML
    protected void onExitButtonClick()
    {
        if (exitSound != null) {
            exitSound.play();

            // Give sound a moment before exiting
            PauseTransition delay = new PauseTransition(Duration.seconds(1.0));
            delay.setOnFinished(e -> System.exit(0));
            delay.play();
        } else {
            System.exit(0);
        }
    }
    @FXML
    protected void onReplayButtonClick() {
        if (replaySound != null) {
            replaySound.play();
        }

        PauseTransition delay = new PauseTransition(Duration.seconds(0.4));
        delay.setOnFinished(e -> {
            stopBackgroundMusic();
            menuPanel.setVisible(false);
            gamePanel.setVisible(true);
            gameOverPanel.setVisible(false);
            startNewGame();
        });
        delay.play();
    }

}