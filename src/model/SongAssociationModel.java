package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Definition for the model of a song association game.
 *
 * @author Uzo Ukekwe
 */
public class SongAssociationModel {
    /** The objects that are watching this object */
    private final List< Observer< SongAssociationModel, Object > > observers;

    /** Number of rounds played during each game */
    public final static int ROUNDS_PER_GAME = 15;

    /** File containing words used during game */
    private final static String WORDS_FILE = "data/words.txt";

    /** Words used to prompt the user during each round */
    private ArrayList<String> words;

    /** Total amount of time taken to answer each prompt in one game */
    private int totalAnswerTime;

    /** Total amounts of times taken to play each game in one session */
    private final ArrayList<Integer> allTotalAnswerTimes;

    /** Current amount of points scored in current game */
    private int pointsScored;

    /** All final scores from the current session */
    private final ArrayList<Integer> allScores;

    /** Average of all final scores from the current session */
    private int averageScore;

    /** Average of all total game times from the current session */
    private int averageTotalTime;

    /** Number of games played in the current session */
    private int gamesPlayed;

    /** Songs input by user */
    private ArrayList<String> songAnswers;

    /** Current round being played by the user */
    private int currentRound;

    /**
     * Construct a SongAssociationModel.
     */
    public SongAssociationModel() {
        this.observers = new LinkedList<>();
        this.words = generateWords();
        this.allTotalAnswerTimes = new ArrayList<>();
        this.allScores = new ArrayList<>();
        this.gamesPlayed = 0;
        this.reset();
    }

    /**
     * Generate the list of words that will be randomly revealed to the user
     * @return the list of words to be used
     */
    private ArrayList<String> generateWords() {
        ArrayList<String> words = new ArrayList<>();

        // read in words from resource file line by line
        try (BufferedReader in = new BufferedReader(new FileReader(WORDS_FILE))) {
            String newWord = in.readLine();
            while (newWord != null) {
                words.add(newWord);
                newWord = in.readLine();
            }
            Collections.shuffle(words);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            System.exit(1);
        }

        return words;
    }

    /**
     * Reset values that only apply to the current game.
     */
    public void reset() {
        this.totalAnswerTime = 0;
        this.pointsScored = 0;
        this.songAnswers =  new ArrayList<>();
        this.currentRound = 1;
    }

    /**
     * Start a new round.
     */
    public void startRound() {
        if (this.words.isEmpty()) {
            this.words = this.generateWords();
        }
        String currentWord = this.words.remove(0);
        announce("word:" + currentWord);
    }

    /**
     * End the current round.
     * @param song song entered by the user for the last word revealed
     * @param answerTime seconds it took for the user to end the round
     */
    public void endRound(String song, int answerTime) {
        // values that are incremented regardless of the user's success
        this.currentRound += 1;
        this.totalAnswerTime += answerTime;

        // user entered an artist and song title in time
        if (song != null) {
            this.songAnswers.add(song);
            this.pointsScored += 1;
        }

        // current game is over
        if (this.currentRound > ROUNDS_PER_GAME) {
            // values that will also be used to calculate averages
            this.allScores.add(this.pointsScored);
            this.allTotalAnswerTimes.add(this.totalAnswerTime);
            this.gamesPlayed += 1;

            // user metrics that involve multiple calculations
            // average score
            int totalPointsScored = 0;
            for (int score : this.allScores) {
                totalPointsScored += score;
            }
            this.averageScore = totalPointsScored / this.gamesPlayed;
            // average time
            int sumOfTotalAnswerTimes = 0;
            for (int totalAnswerTime : this.allTotalAnswerTimes) {
                sumOfTotalAnswerTimes += totalAnswerTime;
            }
            this.averageTotalTime = sumOfTotalAnswerTimes / this.gamesPlayed;

            announce(null);
        }
    }

    public int getCurrentRound() {
        return this.currentRound;
    }

    public int getCurrentScore()  {
        return this.pointsScored;
    }

    public int getAverageScore() {
        return this.averageScore;
    }

    public int getTotalAnswerTime() {
        return this.totalAnswerTime;
    }

    public int getAverageTotalTime() { return this.averageTotalTime; }

    public ArrayList<String> getSongAnswers() {
        return this.songAnswers;
    }

    /**
     * Add a new observer to the list for this model
     * @param obs an object that an update when something changes here
     */
    public void addObserver(Observer<SongAssociationModel, Object> obs) {
        this.observers.add(obs);
    }

    /**
     * Announce to observers the model has changed
     * @param arg if applicable, the change
     */
    private void announce (Object arg) {
        for (var obs : this.observers) {
            obs.update(this, arg);
        }
    }
}
