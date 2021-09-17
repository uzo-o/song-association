package gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Observer;
import model.SongAssociationModel;

import java.util.*;

/**
 * The SongAssociationGUI application is the UI for Song Association
 *
 * @author Uzo Ukekwe
 */
public class SongAssociationGUI extends Application
       implements Observer<SongAssociationModel, Object> {
    /** All colors used in view */
    private final static ArrayList<String> THEME_COLORS = new ArrayList<>
                                                        (Arrays.asList(
                                                                "#ffddd1",
                                                                "#000000"
                                                        ));

    /** Font used for fancier text */
    private final static Font MAIN_FONT = Font.loadFont(SongAssociationGUI.class.
            getResourceAsStream("resources/EDITION_.TTF"), 45);

    /** Font used for plainer text */
    private final static Font PLAIN_FONT = Font.loadFont(SongAssociationGUI.class.
            getResourceAsStream("resources/MilfordCondensedBold-ldjw.ttf"), 45);

    /** Styling values */
    private final static int LARGE_FONT_SIZE = 200;
    private final static int MEDIUM_FONT_SIZE = 120;
    private final static int SMALL_FONT_SIZE = 40;
    private final static int MICRO_FONT_SIZE = 15;
    private final static int BUTTON_PADDING = 15;
    private final static int BUTTON_INSETS = 5;
    private final static int SPACING = 10;

    /** Number of seconds user has to think of a song each round */
    private final static int TIME_PER_WORD = 10;

    /** Number of seconds user has left to think of a song in current round */
    private IntegerProperty timeLeft = new SimpleIntegerProperty(TIME_PER_WORD);

    /** The model for the view and the controller */
    private final SongAssociationModel model;

    /** Stage where the game's scenes are presented */
    private final Stage stage = new Stage();

    /** Main color used in view, depending on light/dark mode */
    private String mainColor;

    /** Accent color used to complement main color, depending on mode */
    private String accentColor;

    /** Text on light/dark mode button */
    private String modeButtonText;

    /** Number of times the next button has been clicked in one scene */
    private int nextButtonClicks;

    /** Number of times the quit button has been clicked in one scene */
    private int quitButtonClicks;

    /**
     * Construct the GUI
     */
    public SongAssociationGUI() {
        this.model = new SongAssociationModel();
        this.mainColor = THEME_COLORS.get(0);
        this.accentColor = THEME_COLORS.get(1);
        this.modeButtonText = "DARK MODE";

        init();
    }

    /**
     * Pre-GUI setup
     */
    @Override
    public void init() {
        this.model.addObserver(this);
    }

    /**
     * Switch between light and dark mode
     */
    private void switchMode() {
        if (this.modeButtonText.equals("DARK MODE")) {
            this.modeButtonText = "LIGHT MODE";
            this.mainColor = THEME_COLORS.get(1);
            this.accentColor = THEME_COLORS.get(0);
        }
        else {
            this.modeButtonText = "DARK MODE";
            this.mainColor = THEME_COLORS.get(0);
            this.accentColor = THEME_COLORS.get(1);
        }
        Scene start = new Scene(makeStartPane());
        this.stage.setScene(start);
    }

    /**
     * Customize a text element
     * @param text the text element to be customized
     * @param font the font of the text
     * @param size the size of the text
     * @param bold true if text is bold, false otherwise
     * @param color the color of the text
     */
    private void styleText(Text text, Font font, int size,
                           boolean bold, String color) {
        text.setFont(font);
        text.setStyle("-fx-font-size:" + size +";");
        text.setFill(Paint.valueOf(color));
        if (bold) {
            text.setStyle("-fx-font-weight: bold;");
        }
    }

    /**
     * Customize a button
     * @param button the button to be customized
     * @param bgColor the background color of the button
     * @param textSize the size of the text on the button
     * @param textColor the color of the text on the button
     */
    private void styleButton(Button button, String bgColor,
                             int textSize, String textColor) {
        button.setStyle("-fx-background-color: " + bgColor + ";" +
                "-fx-font-size: " + textSize + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-family: " + PLAIN_FONT.getFamily() + ";" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 0;" +
                "-fx-padding: " + BUTTON_PADDING + "px;" +
                "-fx-border-insets: " + BUTTON_INSETS + "px;" +
                "-fx-background-insets: " + BUTTON_INSETS + "px;");
    }

    /**
     * Create the pane which displays the game instructions
     * @return BorderPane containing instructions
     */
    private BorderPane makeHelpPane() {
        BorderPane helpNode = new BorderPane();
        helpNode.setStyle("-fx-background-color:" + this.mainColor + ";");

        //title
        Text helpTitle = new Text("HOW TO PLAY:\n");
        this.styleText(helpTitle, MAIN_FONT, MEDIUM_FONT_SIZE, false, this.accentColor);
        helpNode.setTop(helpTitle);
        BorderPane.setAlignment(helpTitle, Pos.BOTTOM_CENTER);

        //body
        Text helpBody = new Text("· THERE ARE " + SongAssociationModel.ROUNDS_PER_GAME +
                                    " ROUNDS PER GAME\n" +
                                    "· EACH ROUND, YOU ARE GIVEN ONE WORD\n" +
                                    "· YOU HAVE "  + TIME_PER_WORD + " SECONDS TO RECALL A SONG\n" +
                                    "   WITH THAT WORD IN ITS LYRICS\n" +
                                    "· TO SCORE A POINT, SING THE LYRIC ON\n" +
                                    "   TIME, THEN IDENTIFY THE SONG\n" +
                                    "· IF YOU CAN'T REMEMBER BOTH THE\n" +
                                    "   ARTIST AND TITLE, YOU MUST FORFEIT\n" +
                                    "   THE POINT");
        this.styleText(helpBody, PLAIN_FONT, SMALL_FONT_SIZE, true, this.accentColor);
        helpNode.setCenter(helpBody);
        BorderPane.setAlignment(helpBody, Pos.BASELINE_CENTER);

        // back button (returns to start scene)
        Button backButton = new Button("BACK");
        this.styleButton(backButton, this.accentColor,
                         MICRO_FONT_SIZE, this.mainColor);
        backButton.setOnAction(event -> this.stage.setScene(new Scene(makeStartPane())));
        helpNode.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.BOTTOM_LEFT);

        return helpNode;
    }

    /**
     * Display the help pane
     */
    private void showHelp() {
        Scene help = new Scene(makeHelpPane());
        this.stage.setScene(help);
    }

    /**
     * Create the pane which starts the game
     * @return BorderPane containing starting graphics
     */
    private BorderPane makeStartPane() {
        BorderPane startNode = new BorderPane();
        startNode.setStyle("-fx-background-color:" + this.mainColor + ";");

        // title
        Text startTitle = new Text("SONG\n ASSOC\nIATION\n ");
        this.styleText(startTitle, MAIN_FONT, MEDIUM_FONT_SIZE, false, this.accentColor);
        startNode.setTop(startTitle);
        BorderPane.setAlignment(startTitle, Pos.BOTTOM_CENTER);

        // start button
        Button startButton = new Button("START");
        this.styleButton(startButton, this.accentColor,
                         SMALL_FONT_SIZE, this.mainColor);
        startNode.setCenter(startButton);
        BorderPane.setAlignment(startButton, Pos.BASELINE_CENTER);
        startButton.setOnAction(event -> this.model.startRound());

        // help button
        Button helpButton = new Button("HELP");
        this.styleButton(helpButton, this.accentColor,
                         MICRO_FONT_SIZE, this.mainColor);
        helpButton.setOnAction(event -> this.showHelp());

        // light/dark mode button
        Button modeButton = new Button(this.modeButtonText);
        this.styleButton(modeButton, this.accentColor,
                         MICRO_FONT_SIZE, this.mainColor);
        modeButton.setOnAction(event -> this.switchMode());

        //bottom menu
        FlowPane bottomMenu = new FlowPane();
        bottomMenu.getChildren().add(helpButton);
        bottomMenu.getChildren().add(modeButton);
        startNode.setBottom(bottomMenu);
        
        return startNode;
    }

    /**
     * Start the GUI
     * @param stage stage where the game occurs
     */
    @Override
    public void start(Stage stage) {
        stage = this.stage;
        Scene start = new Scene(makeStartPane());

        // stage setup
        stage.setTitle("Song Association");
        stage.setScene(start);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Stop the timer during a round of gameplay
     * @param timeline timeline used for the countdown
     * @param gameplayNode main node where user does song association
     * @param userWarning message explaining circumstances to user
     */
    private void stopTime(Timeline timeline, BorderPane gameplayNode, Label userWarning) {
        // stop the timer if the stop button was pressed in time
        if (timeline != null) {
            timeline.stop();
        }
        // stop button wasn't pressed in time -> next round
        if (timeLeft.getValue() == 0) {
            // next button
            Button nextButton = new Button("TOO LATE");
            this.styleButton(nextButton, this.accentColor,
                             SMALL_FONT_SIZE, this.mainColor);

            this.nextButtonClicks = 1;
            nextButton.setOnAction(event -> this.nextRound("","", null, TIME_PER_WORD));
            gameplayNode.setCenter(nextButton);
        }
        // stop button was pressed in time and user must enter more info
        else { this.promptAnswer(gameplayNode, userWarning); }
    }

    /**
     * Create the nodes related to the timing aspect of gameplay
     * @param gameplayNode  main node where user does song association
     * @param timingBox VBox containing the timing nodes
     * @param userWarning message explaining circumstances to user
     */
    private void makeTimingNodes(BorderPane gameplayNode, VBox timingBox, Label userWarning) {
        // timer created
        Label timerLabel = new Label();
        this.timeLeft = new SimpleIntegerProperty(TIME_PER_WORD);
        timerLabel.textProperty().bind(timeLeft.asString());
        timerLabel.setTextFill(Paint.valueOf(this.accentColor));
        timerLabel.setStyle("-fx-font-size: " + MEDIUM_FONT_SIZE + ";" +
                            "-fx-font-family: " + PLAIN_FONT.getFamily() + ";" +
                            "-fx-font-weight: bold;");
        timingBox.getChildren().add(timerLabel);

        // stop button
        Button stopButton = new Button("I SANG THE LYRIC!");
        this.styleButton(stopButton, this.accentColor,
                         SMALL_FONT_SIZE, this.mainColor);
        timingBox.getChildren().add(stopButton);

        // timer functionality
        Timeline timeline = new Timeline();
        stopButton.setOnAction(event -> this.stopTime(timeline, gameplayNode, userWarning));
        timeLeft.set(TIME_PER_WORD);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(TIME_PER_WORD+1), new KeyValue(timeLeft, 0)));
        timeline.playFromStart();
        gameplayNode.setCenter(timingBox);
    }

    /**
     * Create the nodes related to notifying the user
     * @param gameplayNode main node where user does song association
     * @param notifBox HBox containing the notif(ication) nodes
     * @return message explaining circumstances to user
     */
    private Label makeNotifNodes(BorderPane gameplayNode, HBox notifBox) {
        // current round
        Text currentRound = new Text("[ROUND " + this.model.getCurrentRound() + "] ");
        this.styleText(currentRound, PLAIN_FONT, MICRO_FONT_SIZE, false, this.accentColor);
        currentRound.setTextAlignment(TextAlignment.CENTER);
        notifBox.getChildren().add(currentRound);

        // warnings
        Label userWarning = new Label("");
        userWarning.setStyle("-fx-font-size: " + MICRO_FONT_SIZE + ";" +
                "-fx-text-fill: " + this.accentColor + ";");
        notifBox.getChildren().add(userWarning);

        // current score
        Text currentScore = new Text(" [SCORE: " + this.model.getCurrentScore() + "/15]");
        this.styleText(currentScore, PLAIN_FONT, MICRO_FONT_SIZE, false, this.accentColor);
        currentScore.setTextAlignment(TextAlignment.CENTER);
        notifBox.getChildren().add(currentScore);
        notifBox.setAlignment(Pos.BOTTOM_CENTER);
        gameplayNode.setBottom(notifBox);

        return userWarning;
    }

    /**
     * Create the main node where the user does song association
     * @param currentWord word which must be found in a song's lyrics
     * @return BorderPane containing the main gameplay elements
     */
    private BorderPane makeGameplayPane(String currentWord) {
        BorderPane gameplayNode = new BorderPane();
        gameplayNode.setStyle("-fx-background-color:" + this.mainColor + ";");

        // word display
        VBox wordDisplay = new VBox();
        // word header
        Text wordHeader = new Text("WORD:");
        this.styleText(wordHeader, PLAIN_FONT, SMALL_FONT_SIZE, true, this.accentColor);
        wordDisplay.getChildren().add(wordHeader);
        // given word
        Text givenWord = new Text(currentWord.toUpperCase(Locale.ROOT));
        this.styleText(givenWord, MAIN_FONT, LARGE_FONT_SIZE, false, this.accentColor);
        givenWord.setUnderline(true);
        wordDisplay.getChildren().add(givenWord);
        wordDisplay.setAlignment(Pos.BOTTOM_CENTER);
        gameplayNode.setTop(wordDisplay);
        BorderPane.setAlignment(wordDisplay, Pos.BOTTOM_CENTER);

        // user notifications
        HBox userNotifs = new HBox();
        Label userWarning = makeNotifNodes(gameplayNode, userNotifs);

        // timing
        VBox timingPane = new VBox(SPACING);
        this.makeTimingNodes(gameplayNode, timingPane, userWarning);

        // quit
        Button quitButton = new Button("QUIT");
        this.styleButton(quitButton, this.accentColor,
                         MICRO_FONT_SIZE, this.mainColor);
        this.quitButtonClicks = 0;
        timingPane.getChildren().add(quitButton);
        quitButton.setOnAction(event -> this.quit(userWarning));
        timingPane.setAlignment(Pos.TOP_CENTER);

        return gameplayNode;
    }

    /**
     * Customize a text field
     * @param textField the text field to be customized
     */
    private void styleTextField(TextField textField) {
        textField.setFont(PLAIN_FONT);
        textField.setStyle("-fx-background-color: " + this.accentColor + ";" +
                           "-fx-font-size: " + MICRO_FONT_SIZE + ";" +
                           "-fx-text-fill: " + this.mainColor + ";" +
                           "-fx-font-family: " + PLAIN_FONT.getFamily() + ";" +
                           "-fx-background-radius: 0;");
    }

    /**
     * Prompt the user for an artist name and song title
     * @param gameplayNode main node where user does song association
     * @param userWarning message explaining circumstances to user
     */
    private void promptAnswer(BorderPane gameplayNode, Label userWarning) {
        FlowPane answerNodes = new FlowPane();

        // artist name field
        TextField artistName = new TextField();
        artistName.setPromptText("ARTIST NAME");
        this.styleTextField(artistName);
        answerNodes.getChildren().add(artistName);

        // song name field
        TextField songName = new TextField();
        songName.setPromptText("SONG NAME");
        this.styleTextField(songName);
        answerNodes.getChildren().add(songName);

        // next button
        Button nextButton = new Button("NEXT");
        this.styleButton(nextButton, this.accentColor,
                         MICRO_FONT_SIZE, this.mainColor);
        answerNodes.getChildren().add(nextButton);
        this.nextButtonClicks = 0;
        int answerTime = TIME_PER_WORD - timeLeft.getValue();
        nextButton.setOnAction(event -> nextRound(artistName.getText(), songName.getText(), userWarning, answerTime));
        answerNodes.setAlignment(Pos.BOTTOM_CENTER);

        gameplayNode.setCenter(answerNodes);
    }

    /**
     * Create the nodes related to the user's metrics
     * @param endPane pane which displays info in between games
     * @param metricsBox VBox containing the user's metrics
     */
    private void makeMetricsNodes(BorderPane endPane, VBox metricsBox) {
        // metrics title
        Text metricsTitle = new Text("YOUR METRICS");
        this.styleText(metricsTitle, MAIN_FONT, SMALL_FONT_SIZE, false, this.accentColor);
        metricsTitle.setTextAlignment(TextAlignment.CENTER);
        metricsBox.getChildren().add(metricsTitle);

        // metric values
        Text metricsValues = new Text("NEW SCORE: " + this.model.getCurrentScore() + "\n" +
                                         "AVERAGE SCORE: " + this.model.getAverageScore() + "\n" +
                                         "NEW TIME: " + this.model.getTotalAnswerTime() + " s\n" +
                                         "AVERAGE TIME: " + this.model.getAverageTotalTime() + " s\n\n");
        this.styleText(metricsValues, PLAIN_FONT, SMALL_FONT_SIZE, true, this.accentColor);
        metricsBox.getChildren().add(metricsValues);

        metricsBox.setAlignment(Pos.TOP_CENTER);
        endPane.setLeft(metricsBox);
        BorderPane.setAlignment(metricsBox, Pos.BOTTOM_CENTER);
        BorderPane.setMargin(metricsBox, new Insets(SPACING));
    }

    /**
     * Create the nodes relating to the user's mix
     * @param endPane pane which displays info in between games
     * @param mixBox VBox containing a playlist based on the user's answers
     */
    private void makeMixNodes(BorderPane endPane, VBox mixBox) {
        // mix title
        Text mixTitle = new Text("YOUR MIX");
        this.styleText(mixTitle, MAIN_FONT, SMALL_FONT_SIZE, false, this.accentColor);
        mixTitle.setTextAlignment(TextAlignment.CENTER);
        mixBox.getChildren().add(mixTitle);

        // mix songs
        for (int i = 0; i < this.model.getSongAnswers().size(); i++) {
            // song text
            String currentSong = this.model.getSongAnswers().get(i);
            Text songText = new Text(currentSong);
            this.styleText(songText, PLAIN_FONT, SMALL_FONT_SIZE, true, this.accentColor);
            // youtube button
            Button youtubeButton = new Button();
            youtubeButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("resources/youtube.png"))));
            youtubeButton.setStyle("-fx-border-color: transparent;" + "-fx-background-color: transparent;");
            String youtubeUrl = "https://www.youtube.com/results?search_query=" + currentSong.replace(" ", "+");
            youtubeButton.setOnAction(event -> getHostServices().showDocument(youtubeUrl));
            // spotify button
            Button spotifyButton = new Button();
            spotifyButton.setGraphic(new ImageView(new Image(getClass(). getResourceAsStream("resources/spotify.png"))));
            spotifyButton.setStyle("-fx-border-color: transparent;" + "-fx-background-color: transparent;");
            String spotifyUrl = "https://open.spotify.com/search/" + currentSong.replace(" ", "%20");
            spotifyButton.setOnAction(event -> getHostServices().showDocument(spotifyUrl));
            // add all 3 to a flow pane which becomes its own row
            HBox songBox = new HBox(SPACING, songText, youtubeButton, spotifyButton);
            songBox.setAlignment(Pos.CENTER_LEFT);
            mixBox.getChildren().add(songBox);
        }

        // pane containing playlist
        ScrollPane userMixView = new ScrollPane(mixBox);
        userMixView.setStyle("-fx-background: transparent; -fx-background-color: transparent; ");
        userMixView.setMinViewportWidth(mixBox.getWidth() + SPACING * 2);
        mixBox.setAlignment(Pos.TOP_CENTER);
        endPane.setRight(userMixView);
        BorderPane.setAlignment(userMixView, Pos.TOP_CENTER);
        BorderPane.setMargin(userMixView, new Insets(SPACING));
    }

    /**
     * Create the pane which is displayed at the end of every game
     * @return BorderPane containing the info output at the end of every game
     */
    private BorderPane makeEndPane() {
        BorderPane endPane = new BorderPane();
        endPane.setStyle("-fx-background-color:" + this.mainColor + ";");

        // title
        Text title = new Text("GAME OVER");
        this.styleText(title, MAIN_FONT, MEDIUM_FONT_SIZE, false, this.accentColor);
        title.setTextAlignment(TextAlignment.CENTER);
        endPane.setTop(title);
        BorderPane.setAlignment(title, Pos.BOTTOM_CENTER);

        // user's metrics
        VBox userMetrics = new VBox();
        this.makeMetricsNodes(endPane, userMetrics);

        // new game button
        Button newGameButton = new Button("NEW GAME");
        this.styleButton(newGameButton, this.accentColor,
                         SMALL_FONT_SIZE, this.mainColor);
        userMetrics.getChildren().add(newGameButton);
        newGameButton.setOnAction(event -> this.newGame());

        // home button
        Button homeButton = new Button("HOME");
        this.styleButton(homeButton, this.accentColor,
                         SMALL_FONT_SIZE, this.mainColor);
        userMetrics.getChildren().add(homeButton);
        homeButton.setOnAction(event -> this.goBackHome());

        // user's mix
        if (!this.model.getSongAnswers().isEmpty()) {
            VBox userMix = new VBox();
            this.makeMixNodes(endPane, userMix);
        }

        return endPane;
    }

    /**
     * Progress to the next round
     * @param artistName artist of song entered by user
     * @param songName title of song entered by user
     * @param userWarning message explaining circumstances to user
     * @param answerTime time taken to think of song
     */
    private void nextRound(String artistName, String songName, Label userWarning, int answerTime) {
        this.nextButtonClicks += 1;
        // user entered an artist name and song title
        if (!artistName.equals("") && !songName.equals("")) {
            String song = artistName + " - " + songName;
            this.model.endRound(song, answerTime);
            if (this.model.getCurrentRound() <= SongAssociationModel.ROUNDS_PER_GAME) {
                this.model.startRound();
            }
        }
        // user left a field blank and clicked next once (perhaps accidentally)
        else if (this.nextButtonClicks == 1){
            userWarning.setText("Finish typing or forfeit.");
        }
        // user left a field blank and chooses to continue regardless
        else {
            this.model.endRound(null, answerTime);
            if (this.model.getCurrentRound() <= SongAssociationModel.ROUNDS_PER_GAME) {
                this.model.startRound();
            }
        }
    }

    /**
     * Start new game
     */
    private void newGame() {
        this.model.reset();
        this.model.startRound();
    }

    /**
     * Return to start screen
     */
    private void goBackHome() {
        this.model.reset();
        this.stage.setScene(new Scene(makeStartPane()));
    }

    /**
     * Quit game to return to start screen
     * @param userWarning message explaining circumstances to user
     */
    private void quit(Label userWarning) {
        this.quitButtonClicks += 1;
        if (this.quitButtonClicks == 1) {
            userWarning.setText("Click again to confirm.");
        }
        else {
            this.goBackHome();
        }
    }

    /**
     * Update the view
     * @param songAssociationModel model of this view
     * @param o object passed by model
     */
    @Override
    public void update(SongAssociationModel songAssociationModel, Object o) {
        if (String.valueOf(o).contains("word:")) {
            String currentWord = String.valueOf(o).substring(5);
            this.stage.setScene(new Scene(makeGameplayPane(currentWord)));
        }
        else {
            this.stage.setScene(new Scene(makeEndPane()));
        }
    }

    /**
     * main entry point launches the JavaFX GUI.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
